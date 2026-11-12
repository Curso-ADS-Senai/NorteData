package com.senai.aula08.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.senai.aula08.models.Servico;

public interface ServicoRepository extends JpaRepository<Servico, Long> {

    Optional<Servico> findByDescricao(String descricao);

}
