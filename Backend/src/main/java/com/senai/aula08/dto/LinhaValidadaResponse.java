package com.senai.aula08.dto;

import java.math.BigDecimal;

// Linha já padronizada pelo UploadValidacaoService (segmento/nível na grafia oficial),
// pronta para a próxima etapa (ETL em Python, ainda não existente).
public record LinhaValidadaResponse(
    Integer numeroLinha,
    String codigoCliente,
    String nomeCliente,
    String consultor,
    String segmento,
    String nivelCliente,
    BigDecimal faturamentoAnual,
    String servicosContratados,
    String dataContratacao,
    String cidade,
    String uf
) {}
