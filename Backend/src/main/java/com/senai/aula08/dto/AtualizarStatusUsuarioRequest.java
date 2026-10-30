package com.senai.aula08.dto;

import com.senai.aula08.models.StatusUsuario;
import jakarta.validation.constraints.NotNull;

public record AtualizarStatusUsuarioRequest(@NotNull StatusUsuario status) {}
