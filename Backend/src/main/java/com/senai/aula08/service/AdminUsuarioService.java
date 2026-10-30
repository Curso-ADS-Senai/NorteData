package com.senai.aula08.service;

import com.senai.aula08.dto.UsuarioResponse;
import com.senai.aula08.models.StatusUsuario;
import com.senai.aula08.models.Usuario;
import com.senai.aula08.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service // Regras de negócio da tela de administração de usuários (perfil ADMIN).
public class AdminUsuarioService {
    private final UsuarioRepository repository;

    public AdminUsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    public List<UsuarioResponse> listar(StatusUsuario status) {
        List<Usuario> usuarios = status != null ? repository.findByStatus(status) : repository.findAll();
        return usuarios.stream().map(UsuarioResponse::de).toList();
    }

    @Transactional
    public UsuarioResponse atualizarStatus(Long id, StatusUsuario status) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
        usuario.setStatus(status);
        return UsuarioResponse.de(repository.save(usuario));
    }
}
