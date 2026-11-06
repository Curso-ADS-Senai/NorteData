package com.senai.aula08.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

// Uma linha da planilha já filtrada e tratada pelo front (validacaoPlanilha.js) — revalida
// de novo no backend porque a tela não é a única porta de entrada possível da API.
public record LinhaUploadRequest(
    @NotNull @Positive Integer numeroLinha,
    @NotBlank @Size(max = 50) String codigoCliente,
    @NotBlank @Size(max = 180) String nomeCliente,
    @NotBlank @Size(max = 150) String consultor,
    @NotBlank @Size(max = 100) String segmento,
    @NotBlank @Pattern(regexp = "A|B|C", message = "Nível deve ser A, B ou C") String nivelCliente,
    @NotNull @PositiveOrZero BigDecimal faturamentoAnual,
    @Size(max = 255) String servicosContratados,
    @NotBlank String dataContratacao,
    @Size(max = 100) String cidade,
    @Pattern(regexp = "^$|[A-Z]{2}$", message = "UF deve ter 2 letras") String uf
) {}
