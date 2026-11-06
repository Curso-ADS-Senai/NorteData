package com.senai.aula08.dto;

import java.util.List;

// Formato esperado por RelatorioValidacao.vue: status, totalLinhas, linhasValidas e
// rejeitadas (mensagens legíveis) são lidos direto no template.
public record UploadValidadoResponse(
    String status,
    int totalLinhas,
    int linhasValidas,
    List<String> rejeitadas,
    List<LinhaValidadaResponse> linhas
) {}
