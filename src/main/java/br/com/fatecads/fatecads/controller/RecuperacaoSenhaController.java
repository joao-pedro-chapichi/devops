package br.com.fatecads.fatecads.controller;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import br.com.fatecads.fatecads.entity.Usuario;
import br.com.fatecads.fatecads.repository.UsuarioRepository;
import br.com.fatecads.fatecads.service.WhatsappService;
import jakarta.validation.constraints.*;
import org.springframework.validation.annotation.Validated;

@RestController
@Validated
@RequestMapping("/recuperacao")
public class RecuperacaoSenhaController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private WhatsappService whatsappService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/solicitar")
    public String solicitarCodigo(
            @RequestParam @NotBlank @Email @Size(max = 80) String email) {

        Usuario usuario =
                usuarioRepository
                        .findByEmailUsuario(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Usuário não encontrado"));

        String codigo =
                String.format(
                        "%06d",
                        new SecureRandom()
                                .nextInt(1000000));

        usuario.setCodigoRecuperacao(codigo);

        usuario.setExpiracaoCodigo(
                LocalDateTime.now()
                        .plusMinutes(10));

        usuarioRepository.save(usuario);

        whatsappService.enviarCodigo(
                usuario.getTelefoneUsuario(),
                codigo);

        return "Código enviado";
    }

    @PostMapping("/redefinir")
    public String redefinirSenha(
            @RequestParam @NotBlank @Email @Size(max = 80) String email,
            @RequestParam @Pattern(regexp = "^[0-9]{6}$") String codigo,
            @RequestParam @NotBlank @Size(min = 8, max = 72) String novaSenha) {

        Usuario usuario =
                usuarioRepository
                        .findByEmailUsuario(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Usuário não encontrado"));

        if (usuario.getExpiracaoCodigo() == null || usuario.getCodigoRecuperacao() == null || usuario.getExpiracaoCodigo()
                .isBefore(LocalDateTime.now())) {

            throw new RuntimeException(
                    "Código expirado");
        }

        if (!usuario.getCodigoRecuperacao()
                .equals(codigo)) {

            throw new RuntimeException(
                    "Código inválido");
        }

        usuario.setSenhaUsuario(
                passwordEncoder.encode(novaSenha));

        usuario.setCodigoRecuperacao(null);
        usuario.setExpiracaoCodigo(null);

        usuarioRepository.save(usuario);

        return "Senha redefinida com sucesso";
    }
}
