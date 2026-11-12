package com.senai.aula08.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "upload_lote")
public class UploadLote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lote")
    private Long idLote;

    @Column(name = "arquivo_nome", nullable = false, length = 255)
    private String arquivoNome;

    @Column(name = "enviado_em", nullable = false)
    private LocalDateTime enviadoEm;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StatusLote status;

    @Column(name = "total_linhas", nullable = false)
    private Integer totalLinhas = 0;

    @Column(name = "linhas_validas", nullable = false)
    private Integer linhasValidas = 0;

    @Column(name = "linhas_invalidas", nullable = false)
    private Integer linhasInvalidas = 0;

    @Column(name = "linhas_com_aviso", nullable = false)
    private Integer linhasComAviso = 0;

    @Column(name = "mensagem", length = 500)
    private String mensagem;

    public UploadLote() {
    }

    public UploadLote(String arquivoNome, StatusLote status) {
        this.arquivoNome = arquivoNome;
        this.status = status;
        this.enviadoEm = LocalDateTime.now();
    }

    public Long getIdLote() {
        return idLote;
    }

    public String getArquivoNome() {
        return arquivoNome;
    }

    public void setArquivoNome(String arquivoNome) {
        this.arquivoNome = arquivoNome;
    }

    public LocalDateTime getEnviadoEm() {
        return enviadoEm;
    }

    public void setEnviadoEm(LocalDateTime enviadoEm) {
        this.enviadoEm = enviadoEm;
    }

    public StatusLote getStatus() {
        return status;
    }

    public void setStatus(StatusLote status) {
        this.status = status;
    }

    public Integer getTotalLinhas() {
        return totalLinhas;
    }

    public void setTotalLinhas(Integer totalLinhas) {
        this.totalLinhas = totalLinhas;
    }

    public Integer getLinhasValidas() {
        return linhasValidas;
    }

    public void setLinhasValidas(Integer linhasValidas) {
        this.linhasValidas = linhasValidas;
    }

    public Integer getLinhasInvalidas() {
        return linhasInvalidas;
    }

    public void setLinhasInvalidas(Integer linhasInvalidas) {
        this.linhasInvalidas = linhasInvalidas;
    }

    public Integer getLinhasComAviso() {
        return linhasComAviso;
    }

    public void setLinhasComAviso(Integer linhasComAviso) {
        this.linhasComAviso = linhasComAviso;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
