package com.senai.aula08.service;

import com.senai.aula08.dto.*;
import com.senai.aula08.models.Perfil;
import com.senai.aula08.models.StatusUsuario;
import com.senai.aula08.models.Usuario;
import com.senai.aula08.repository.UsuarioRepository;
import com.senai.aula08.security.JwtService;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service // Regras de negócio do cadastro e do login.
public class AuthService {
    private final UsuarioRepository repository;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthService(UsuarioRepository repository, PasswordEncoder encoder, JwtService jwt) {
        this.repository = repository;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    public UsuarioResponse cadastrar(CadastroRequest dados) {
        String email = dados.email().trim().toLowerCase(Locale.ROOT);
        // BCrypt considera no máximo 72 bytes; comprimento não equivale a bytes em UTF-8.
        if (dados.senha().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Senha muito longa para BCrypt");
        }
        if (repository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado");
        }
        Usuario usuario = new Usuario();
        usuario.setNome(dados.nome().trim());
        usuario.setEmail(email);
        usuario.setSenhaHash(encoder.encode(dados.senha())); // Armazena apenas o hash.
        usuario.setPerfil(Perfil.USER); // Cadastro público nunca define ADMIN.
        usuario.setStatus(StatusUsuario.PENDENTE); // Só entra depois de um admin aprovar.
        return UsuarioResponse.de(repository.save(usuario));
    }

    public LoginResponse entrar(LoginRequest dados) {
        Usuario usuario = repository.findByEmailIgnoreCase(dados.email().trim())
                .orElse(null);
        // Mesmo erro para usuário inexistente e senha inválida.
        if (usuario == null || !encoder.matches(dados.senha(), usuario.getSenhaHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos");
        }
        if (usuario.getStatus() == StatusUsuario.PENDENTE) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cadastro pendente de aprovação do administrador.");
        }
        if (usuario.getStatus() == StatusUsuario.BLOQUEADO) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuário bloqueado. Contate o administrador.");
        }
        return new LoginResponse(jwt.gerar(usuario), "Bearer", UsuarioResponse.de(usuario));
    }
}
