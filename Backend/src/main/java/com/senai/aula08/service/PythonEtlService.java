package com.senai.aula08.service;

import com.senai.aula08.dto.LinhaValidadaResponse;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

/**
 * A ponte entre o Java e o serviço Python (analytics-python).
 *
 * O Java valida e ENCAMINHA; quem cria o lote, limpa, calcula e GRAVA no banco é o Python.
 * Por isso aqui não há repositório nem entidade: só um POST /etl.
 *
 * A chamada é SÍNCRONA de propósito — se o Python falhar, o usuário vê o erro na hora, na
 * resposta do próprio upload, em vez de ficar com um lote preso em PROCESSANDO.
 */
@Service
public class PythonEtlService {
    private static final Logger log = LoggerFactory.getLogger(PythonEtlService.class);

    private final String url;
    private final String chave;
    // Conectar é rápido; processar a planilha não. Daí os dois tempos serem bem diferentes.
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    private final ObjectMapper json;

    public PythonEtlService(
            @Value("${app.etl.url:http://localhost:8000/etl}") String url,
            @Value("${app.etl.key:}") String chave,
            ObjectMapper json) {
        this.url = url.isBlank() ? "http://localhost:8000/etl" : url.trim();
        this.chave = chave == null ? "" : chave.trim();
        this.json = json;
    }

    /**
     * Envia as linhas válidas. O Python grava tudo no banco e só responde {"ok": true}.
     *
     * As linhas vão em camelCase; o `tratar()` do limpeza.py converte para snake_case na
     * primeira coisa que faz, então não é preciso traduzir os nomes aqui.
     */
    public void enviar(String arquivoNome, int recebidas, int invalidas, int comAviso,
                       List<LinhaValidadaResponse> linhas) {
        Map<String, Object> pedido = new LinkedHashMap<>();
        pedido.put("arquivoNome", arquivoNome);
        pedido.put("recebidas", recebidas);   // tudo que a planilha tinha (boas + ruins)
        pedido.put("invalidas", invalidas);   // descartadas no front + rejeitadas aqui
        pedido.put("comAviso", comAviso);
        pedido.put("linhas", linhas);

        log.info("Enviando {} linhas ao Python: POST {}", linhas.size(), url);

        byte[] corpo;
        try {
            corpo = json.writeValueAsBytes(pedido);
        } catch (JacksonException e) {
            // Jackson 3 (Spring Boot 4): JacksonException é unchecked e substitui o
            // JsonProcessingException do Jackson 2.
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erro ao montar o JSON para o ETL: " + e.getMessage());
        }

        HttpRequest requisicao = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofMinutes(5))
                .header("Content-Type", "application/json; charset=utf-8")
                .header("X-ETL-Key", chave)
                .POST(HttpRequest.BodyPublishers.ofByteArray(corpo))
                .build();

        try {
            HttpResponse<String> resposta = http.send(requisicao, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (resposta.statusCode() != 200) {
                log.error("Python respondeu {}: {}", resposta.statusCode(), resposta.body());
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                        "O serviço Python respondeu " + resposta.statusCode() + ": " + resposta.body());
            }
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Não foi possível falar com o serviço Python. Ele está ligado? "
                            + "(cd analytics-python && .venv\\Scripts\\python.exe -m uvicorn api:app --port 8000)");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "A chamada ao Python foi interrompida.");
        }
    }
}
