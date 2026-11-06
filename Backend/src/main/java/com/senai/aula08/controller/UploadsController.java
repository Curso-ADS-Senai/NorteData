package com.senai.aula08.controller;

import com.senai.aula08.dto.UploadValidadoRequest;
import com.senai.aula08.dto.UploadValidadoResponse;
import com.senai.aula08.service.UploadValidacaoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/uploads")
public class UploadsController {
    private final UploadValidacaoService service;

    public UploadsController(UploadValidacaoService service) {
        this.service = service;
    }

    @PostMapping("/validadas")
    public UploadValidadoResponse validar(@Valid @RequestBody UploadValidadoRequest dados) {
        return service.validar(dados);
    }
}
