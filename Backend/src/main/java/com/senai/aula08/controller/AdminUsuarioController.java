package com.senai.aula08.controller;

import com.senai.aula08.dto.AtualizarStatusUsuarioRequest;
import com.senai.aula08.dto.UsuarioResponse;
import com.senai.aula08.models.StatusUsuario;
import com.senai.aula08.service.AdminUsuarioService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUsuarioController {
    private final AdminUsuarioService service;

    public AdminUsuarioController(AdminUsuarioService service) {
        this.service = service;
    }

    @GetMapping
    public List<UsuarioResponse> listar(@RequestParam(required = false) StatusUsuario status) {
        return service.listar(status);
    }

    @PatchMapping("/{id}/status")
    public UsuarioResponse atualizarStatus(@PathVariable Long id, @Valid @RequestBody AtualizarStatusUsuarioRequest dados) {
        return service.atualizarStatus(id, dados.status());
    }
}
