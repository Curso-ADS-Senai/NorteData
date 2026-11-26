package com.senai.aula08.controller;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Diagnóstico público de "a aplicação subiu?": é o que a plataforma de deploy
// consulta e o link pedido na entrega. Liberado sem token no SecurityConfig.
@RestController
@RequestMapping("/api/health")
public class HealthController {
    @GetMapping
    public Map<String, String> health() {
        return Map.of("status", "ok", "servico", "norte-data-api");
    }
}
