package com.senai.aula08.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

// Só LÊ a carteira de clientes para as telas de Relatórios e Detalhe do Cliente.
//
// Separado do ClienteService de propósito: aquele é o CRUD JPA das aulas 08/09 (criar,
// atualizar, excluir um cliente avulso) e este é consulta de leitura com junção e
// contagem. Usa JdbcTemplate pelo mesmo motivo do DashboardService — os getters da
// entidade Cliente não batem com o JSON que o front espera (família do caso idLong/id).
//
// Quem grava cliente/contrato/servico em lote é o ETL em Python, nunca o Java.
@Service
public class ClienteConsultaService {

    // LIMIT defensivo: a tela é uma tabela com busca, não um exportador. Uma planilha
    // absurda não deve conseguir derrubar o navegador.
    private static final int LIMITE = 500;

    private static final String SELECT_LISTA = """
            SELECT c.id_cliente AS id, c.codigo_cliente, c.nome_empresa, c.segmento, c.nivel,
                   c.status, c.faturamento_anual, c.cidade, c.uf, co.nome AS consultor,
                   (SELECT COUNT(*) FROM contrato ct WHERE ct.id_cliente = c.id_cliente) AS qtd_servicos
              FROM cliente c
              JOIN consultor co ON co.id_consultor = c.id_consultor
            """;

    private final JdbcTemplate jdbc;

    public ClienteConsultaService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * Lista a carteira de um lote. Sem loteId usa o último PROCESSADO — sem esse recorte a
     * tabela mostraria o mesmo cliente repetido uma vez por planilha enviada, já que cada
     * upload regrava a carteira inteira num lote novo.
     */
    public Map<String, Object> listar(String busca, String segmento, String nivel, Long loteId) {
        Long lote = loteId != null ? loteId : ultimoLoteProcessado();

        Map<String, Object> saida = new LinkedHashMap<>();
        saida.put("loteId", lote);

        // Nenhuma planilha processada ainda: devolve lista vazia em vez de 404. A tela
        // mostra o convite para enviar uma planilha, que não é um erro.
        if (lote == null) {
            saida.put("clientes", List.of());
            saida.put("total", 0);
            return saida;
        }

        StringBuilder sql = new StringBuilder(SELECT_LISTA).append(" WHERE c.id_lote = ?");
        List<Object> parametros = new ArrayList<>();
        parametros.add(lote);

        if (preenchido(busca)) {
            sql.append(" AND (LOWER(c.nome_empresa) LIKE ? OR LOWER(c.codigo_cliente) LIKE ?)");
            String termo = "%" + busca.trim().toLowerCase(Locale.ROOT) + "%";
            parametros.add(termo);
            parametros.add(termo);
        }
        if (preenchido(segmento)) {
            sql.append(" AND c.segmento = ?");
            parametros.add(segmento.trim());
        }
        if (preenchido(nivel)) {
            sql.append(" AND c.nivel = ?");
            parametros.add(nivel.trim().toUpperCase(Locale.ROOT));
        }

        sql.append(" ORDER BY c.faturamento_anual DESC LIMIT ").append(LIMITE);

        List<Map<String, Object>> clientes = jdbc.query(sql.toString(), this::lerCliente, parametros.toArray());
        saida.put("clientes", clientes);
        saida.put("total", clientes.size());
        return saida;
    }

    /** Um cliente com os serviços contratados e o lote de origem. */
    public Map<String, Object> obter(Long id) {
        List<Map<String, Object>> achados = jdbc.query(SELECT_LISTA + " WHERE c.id_cliente = ?", this::lerCliente, id);
        if (achados.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente " + id + " não encontrado.");
        }

        Map<String, Object> cliente = new LinkedHashMap<>(achados.get(0));

        cliente.put("contratos", jdbc.query("""
                SELECT s.descricao, s.valor, ct.data_contratacao
                  FROM contrato ct
                  JOIN servico s ON s.id_servico = ct.id_servico
                 WHERE ct.id_cliente = ?
                 ORDER BY s.descricao
                """, (rs, n) -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("servico", rs.getString("descricao"));
            m.put("valor", rs.getBigDecimal("valor"));
            m.put("dataContratacao", rs.getObject("data_contratacao", java.time.LocalDate.class));
            return m;
        }, id));

        // Cliente cadastrado à mão pelo CRUD tem id_lote null e não casa com nenhuma linha aqui.
        List<Map<String, Object>> lotes = jdbc.query("""
                SELECT l.id_lote AS id, l.arquivo_nome, l.enviado_em
                  FROM upload_lote l
                  JOIN cliente c ON c.id_lote = l.id_lote
                 WHERE c.id_cliente = ?
                """, (rs, n) -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", rs.getLong("id"));
            m.put("arquivoNome", rs.getString("arquivo_nome"));
            m.put("enviadoEm", data(rs.getTimestamp("enviado_em")));
            return m;
        }, id);
        cliente.put("lote", lotes.isEmpty() ? null : lotes.get(0));

        return cliente;
    }

    /** Os segmentos e níveis presentes no lote, para montar os filtros da tela. */
    public Map<String, Object> filtros(Long loteId) {
        Long lote = loteId != null ? loteId : ultimoLoteProcessado();
        Map<String, Object> saida = new LinkedHashMap<>();
        if (lote == null) {
            saida.put("segmentos", List.of());
            saida.put("niveis", List.of());
            return saida;
        }
        saida.put("segmentos", jdbc.queryForList(
                "SELECT DISTINCT segmento FROM cliente WHERE id_lote = ? ORDER BY segmento", String.class, lote));
        saida.put("niveis", jdbc.queryForList(
                "SELECT DISTINCT nivel FROM cliente WHERE id_lote = ? ORDER BY nivel", String.class, lote));
        return saida;
    }

    private Long ultimoLoteProcessado() {
        List<Long> ids = jdbc.queryForList(
                "SELECT id_lote FROM upload_lote WHERE status = 'PROCESSADO' ORDER BY id_lote DESC LIMIT 1", Long.class);
        return ids.isEmpty() ? null : ids.get(0);
    }

    private static boolean preenchido(String valor) {
        return valor != null && !valor.isBlank();
    }

    private Map<String, Object> lerCliente(ResultSet rs, int numeroLinha) throws SQLException {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", rs.getLong("id"));
        m.put("codigoCliente", rs.getString("codigo_cliente"));
        m.put("nomeEmpresa", rs.getString("nome_empresa"));
        m.put("segmento", rs.getString("segmento"));
        m.put("nivel", rs.getString("nivel"));
        m.put("status", rs.getString("status"));
        m.put("faturamentoAnual", rs.getBigDecimal("faturamento_anual"));
        m.put("cidade", rs.getString("cidade"));
        m.put("uf", rs.getString("uf"));
        m.put("consultor", rs.getString("consultor"));
        m.put("qtdServicos", rs.getInt("qtd_servicos"));
        return m;
    }

    private static Object data(Timestamp t) {
        return t == null ? null : t.toLocalDateTime();
    }
}
