package com.senai.aula08.repository;

import com.senai.aula08.models.StatusUsuario;
import com.senai.aula08.models.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// O Spring Data JPA implementa os métodos de acesso ao banco.
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
    List<Usuario> findByStatus(StatusUsuario status);
}
