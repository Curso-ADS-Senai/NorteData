package com.senai.aula08.service;

import com.senai.aula08.dto.LinhaUploadRequest;
import com.senai.aula08.dto.LinhaValidadaResponse;
import com.senai.aula08.dto.UploadValidadoRequest;
import com.senai.aula08.dto.UploadValidadoResponse;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Service;

// Revalida o que o front (validacaoPlanilha.js) já validou e padroniza segmento/nível
// antes de devolver JSON limpo. Mesmo escopo da Aula 13: sem banco, sem Python ainda.
@Service
public class UploadValidacaoService {
    // Espelha SEGMENTOS_PADRAO de src/stores/validacaoPlanilha.js — os 7 segmentos reais
    // do projeto, não só os 3 do exemplo didático da Aula 13.
    private static final Map<String, String> SEGMENTOS_PADRAO = Map.of(
            "ind", "Indústria",
            "industria", "Indústria",
            "comercio", "Comércio",
            "servicos", "Serviços",
            "tecnologia", "Tecnologia",
            "saude", "Saúde",
            "educacao", "Educação");

    public UploadValidadoResponse validar(UploadValidadoRequest requisicao) {
        List<String> rejeitadas = new ArrayList<>();
        List<LinhaValidadaResponse> aceitas = new ArrayList<>();
        Map<String, Integer> codigosVistos = new HashMap<>();

        for (LinhaUploadRequest linha : requisicao.linhas()) {
            String chave = normalizarChave(linha.codigoCliente());
            Integer linhaAnterior = codigosVistos.get(chave);
            if (linhaAnterior != null) {
                rejeitadas.add("Linha %d: código de cliente \"%s\" duplicado (já aparece na linha %d)."
                        .formatted(linha.numeroLinha(), linha.codigoCliente(), linhaAnterior));
                continue;
            }
            codigosVistos.put(chave, linha.numeroLinha());
            aceitas.add(padronizar(linha));
        }

        String status = rejeitadas.isEmpty() ? "OK" : "PARCIAL";
        return new UploadValidadoResponse(status, requisicao.linhas().size(), aceitas.size(), rejeitadas, aceitas);
    }

    private LinhaValidadaResponse padronizar(LinhaUploadRequest linha) {
        String segmentoPadrao = SEGMENTOS_PADRAO.getOrDefault(normalizarChave(linha.segmento()), linha.segmento().trim());
        String nivel = linha.nivelCliente().trim().toUpperCase(Locale.ROOT);

        return new LinhaValidadaResponse(
                linha.numeroLinha(),
                linha.codigoCliente().trim(),
                linha.nomeCliente().trim(),
                linha.consultor().trim(),
                segmentoPadrao,
                nivel,
                linha.faturamentoAnual(),
                linha.servicosContratados(),
                linha.dataContratacao(),
                linha.cidade(),
                linha.uf());
    }

    // Mesma normalização de normalizarChave() em validacaoPlanilha.js: minúsculo, sem
    // acento, só letras/números — pra "IND.", "Indústria" e "industria" caírem na mesma chave.
    private String normalizarChave(String valor) {
        String semAcento = Normalizer.normalize(valor.trim().toLowerCase(Locale.ROOT), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return semAcento.replaceAll("[^a-z0-9]", "");
    }
}
