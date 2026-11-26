package com.senai.aula08.security;

import com.senai.aula08.models.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.io.Decoders;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service // Responsável por assinar e validar JWTs.
public class JwtService {
    private final SecretKey chave;
    private final long expiracaoMinutos;

    public JwtService(@Value("${app.jwt.secret}") String segredo,
                      @Value("${app.jwt.expiration-minutes:60}") long expiracaoMinutos) {
        this.chave = Keys.hmacShaKeyFor(bytesDoSegredo(segredo));
        this.expiracaoMinutos = expiracaoMinutos;
    }

    // Aceita as duas formas de segredo em uso: Base64 (como o JWT_SECRET_BASE64 da
    // máquina local) e texto puro — o roteiro de deploy gera a chave com
    // secrets.token_urlsafe(48), que usa "-" e "_" e não é Base64 padrão.
    // HS256 exige ao menos 256 bits (32 bytes) de chave aleatória.
    private static byte[] bytesDoSegredo(String segredo) {
        try {
            byte[] decodificado = Decoders.BASE64.decode(segredo);
            if (decodificado.length >= 32) {
                return decodificado;
            }
        } catch (DecodingException naoEhBase64) {
            // Cai no texto puro abaixo.
        }
        return segredo.getBytes(StandardCharsets.UTF_8);
    }

    public String gerar(Usuario usuario) {
        Instant agora = Instant.now();
        return Jwts.builder()
                .subject(usuario.getEmail()) // Identidade incluída no token.
                .issuedAt(Date.from(agora))
                .expiration(Date.from(agora.plusSeconds(expiracaoMinutos * 60)))
                .signWith(chave, Jwts.SIG.HS256) // Assinatura HMAC SHA-256.
                .compact();
    }

    public String extrairEmailValidado(String token) {
        // Rejeita assinaturas inválidas e tokens expirados; obtém o subject.
        Claims claims = Jwts.parser().verifyWith(chave).build()
                .parseSignedClaims(token).getPayload();
        return claims.getSubject();
    }
}
