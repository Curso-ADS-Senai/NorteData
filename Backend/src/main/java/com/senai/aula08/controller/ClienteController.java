package com.senai.aula08.controller;

import com.senai.aula08.service.ClienteConsultaService;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// Leitura da carteira de clientes: telas de Relatórios e Detalhe do Cliente.
// Todas as rotas exigem JWT — SecurityConfig fecha com anyRequest().authenticated().
@RestController
@RequestMapping("/api/clientes")
public class ClienteController {
    private final ClienteConsultaService service;

    public ClienteController(ClienteConsultaService service) {
        this.service = service;
    }

    // Todos os filtros são opcionais. Sem loteId, o service usa o último lote PROCESSADO.
    @GetMapping
    public Map<String, Object> listar(
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) String segmento,
            @RequestParam(required = false) String nivel,
            @RequestParam(required = false) Long loteId) {
        return service.listar(busca, segmento, nivel, loteId);
    }

    @GetMapping("/filtros")
    public Map<String, Object> filtros(@RequestParam(required = false) Long loteId) {
        return service.filtros(loteId);
    }

    @GetMapping("/{id}")
    public Map<String, Object> obter(@PathVariable Long id) {
        return service.obter(id);
    }
}
