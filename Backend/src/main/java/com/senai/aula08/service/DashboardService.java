package com.senai.aula08.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
// Spring Boot 4 usa Jackson 3: o pacote é tools.jackson.*, não com.fasterxml.jackson.*
// (só jackson-annotations continua no pacote antigo).
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

// Só LÊ: quem grava upload_lote e estatistica_lote é o serviço Python (analytics-python).
// Por isso usa JdbcTemplate em vez de JPA — não há entidade mapeada para estatistica_lote,
// e o conteúdo de `resultado` é um JSON opaco calculado pelo pandas.
@Service
public class DashboardService {

    // A PK aqui é id_lote; o front espera o campo `id` (contrato do GET /api/dashboard),
    // por isso o alias no SELECT. Sem ele o cabeçalho e o seletor de lote da tela quebram.
    private static final String SELECT_LOTE = """
            SELECT id_lote AS id, arquivo_nome, enviado_em, status, total_linhas,
                   linhas_validas, linhas_invalidas, linhas_com_aviso, mensagem
              FROM upload_lote
            """;

    private final JdbcTemplate jdbc;
    private final ObjectMapper json;

    public DashboardService(JdbcTemplate jdbc, ObjectMapper json) {
        this.jdbc = jdbc;
        this.json = json;
    }

    /** Devolve { lote, geradoEm, estatisticas }. Sem loteId: o último PROCESSADO (ou o último enviado). */
    public Map<String, Object> obter(Long loteId) {
        List<Map<String, Object>> achados;
        if (loteId != null) {
            achados = jdbc.query(SELECT_LOTE + " WHERE id_lote = ?", this::lerLote, loteId);
        } else {
            achados = jdbc.query(SELECT_LOTE + " WHERE status = 'PROCESSADO' ORDER BY id_lote DESC LIMIT 1", this::lerLote);
            if (achados.isEmpty()) {
                // Nenhum deu certo ainda: mostra o último enviado, que pode estar PROCESSANDO ou ERRO.
                achados = jdbc.query(SELECT_LOTE + " ORDER BY id_lote DESC LIMIT 1", this::lerLote);
            }
        }
        if (achados.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, loteId == null
                    ? "Nenhuma planilha foi processada ainda. Envie uma planilha para ver os gráficos."
                    : "Lote " + loteId + " não encontrado.");
        }
        Map<String, Object> lote = achados.get(0);

        Map<String, Object> saida = new LinkedHashMap<>();
        saida.put("lote", lote);
        // null = o Python ainda não terminou. A tela usa lote.status para decidir se insiste.
        saida.put("geradoEm", null);
        saida.put("estatisticas", null);

        List<Map<String, Object>> contas = jdbc.query(
                "SELECT gerado_em, resultado FROM estatistica_lote WHERE id_lote = ?",
                (rs, n) -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("geradoEm", data(rs.getTimestamp("gerado_em")));
                    m.put("resultado", rs.getString("resultado"));
                    return m;
                }, lote.get("id"));
        if (!contas.isEmpty()) {
            saida.put("geradoEm", contas.get(0).get("geradoEm"));
            saida.put("estatisticas", lerJson((String) contas.get(0).get("resultado")));
        }
        return saida;
    }

    /** Os 20 lotes mais recentes, para o seletor de planilha da tela. */
    public List<Map<String, Object>> listarLotes() {
        return jdbc.query(SELECT_LOTE + " ORDER BY id_lote DESC LIMIT 20", this::lerLote);
    }

    // Uma linha de upload_lote vira um mapa em camelCase, que é o que o front consome.
    private Map<String, Object> lerLote(ResultSet rs, int numeroLinha) throws SQLException {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", rs.getLong("id"));
        m.put("arquivoNome", rs.getString("arquivo_nome"));
        m.put("enviadoEm", data(rs.getTimestamp("enviado_em")));
        m.put("status", rs.getString("status"));
        m.put("totalLinhas", rs.getInt("total_linhas"));
        m.put("linhasValidas", rs.getInt("linhas_validas"));
        m.put("linhasInvalidas", rs.getInt("linhas_invalidas"));
        m.put("linhasComAviso", rs.getInt("linhas_com_aviso"));
        m.put("mensagem", rs.getString("mensagem"));
        return m;
    }

    private static Object data(Timestamp t) {
        return t == null ? null : t.toLocalDateTime();
    }

    private JsonNode lerJson(String texto) {
        try {
            return json.readTree(texto);
        } catch (JacksonException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "O resultado gravado pelo Python não é um JSON válido.");
        }
    }
}
