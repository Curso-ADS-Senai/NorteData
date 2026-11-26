package com.senai.aula08.config;

import com.senai.aula08.models.Perfil;
import com.senai.aula08.models.StatusUsuario;
import com.senai.aula08.models.Usuario;
import com.senai.aula08.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

// Banco novo nasce sem nenhum usuário, e isso trava o sistema de vez: quem se
// cadastra fica PENDENTE e só um ADMIN aprova, então ninguém entra nunca.
// Este seed quebra o impasse criando o primeiro ADMIN a partir de ADMIN_EMAIL /
// ADMIN_PASSWORD. Roda a cada subida, mas só cria o que falta.
@Configuration
public class AdminInicial {
    private static final Logger log = LoggerFactory.getLogger(AdminInicial.class);

    @Bean
    ApplicationRunner criarAdminInicial(UsuarioRepository usuarios,
                                        PasswordEncoder passwordEncoder,
                                        @Value("${app.admin.email:}") String email,
                                        @Value("${app.admin.password:}") String senha) {
        return args -> {
            if (email.isBlank() || senha.isBlank()) {
                return; // Variáveis não cadastradas: nada a fazer (caso da máquina local).
            }
            if (usuarios.existsByEmailIgnoreCase(email)) {
                log.info("Admin inicial {} já existe; seed ignorado.", email);
                return; // Idempotente: nunca sobrescreve a senha de um admin existente.
            }

            Usuario admin = new Usuario();
            admin.setNome("Administrador");
            admin.setEmail(email);
            admin.setSenhaHash(passwordEncoder.encode(senha)); // Nunca a senha original.
            admin.setPerfil(Perfil.ADMIN);
            admin.setStatus(StatusUsuario.ATIVO); // Já aprovado, senão não conseguiria logar.
            usuarios.save(admin);

            log.info("Admin inicial {} criado.", email);
        };
    }
}
