package com.senai.aula08.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;

// Corpo enviado por RelatorioValidacao.vue (services/uploads.js) após o upload
// já ter sido validado e tratado no front.
public record UploadValidadoRequest(
    @NotBlank String arquivoNome,
    @NotNull @PositiveOrZero Integer descartadas,
    @NotEmpty @Valid List<LinhaUploadRequest> linhas
) {}
