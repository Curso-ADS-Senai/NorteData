package com.senai.aula08.models;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="servico")
public class Servico {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name= "id_servico")
    private  Long idServico; // id do servico

    @Column (name = "descricao",nullable = false,length = 150)
    private  String descricao;

    @Column (name = "valor", nullable = false, precision = 15, scale=2)
    private  BigDecimal valor;

}
