package br.com.fatecads.fatecads.controller;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.fatecads.fatecads.entity.Usuario;
import br.com.fatecads.fatecads.repository.UsuarioRepository;
import br.com.fatecads.fatecads.service.WhatsappService;

@RestController
@RequestMapping("/recuperacao")
public class RecuperacaoSenhaController {
    private final UsuarioRepository usuarioRepository;
    private final WhatsappService whatsappService;
    private final PasswordEncoder passwordEncoder;

    public RecuperacaoSenhaController(UsuarioRepository usuarioRepository, WhatsappService whatsappService,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.whatsappService = whatsappService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/solicitar")
    public ResponseEntity<Map<String, String>> solicitarCodigo(@RequestParam String email) {
        if (!emailValido(email)) return erro("email", "Informe um email válido.");
        Usuario usuario = usuarioRepository.findByEmailUsuario(email).orElse(null);
        if (usuario != null && usuario.getTelefoneUsuario() != null) {
            String codigo = String.format("%06d", new SecureRandom().nextInt(1000000));
            usuario.setCodigoRecuperacao(codigo);
            usuario.setExpiracaoCodigo(LocalDateTime.now().plusMinutes(10));
            usuarioRepository.save(usuario);
            whatsappService.enviarCodigo(usuario.getTelefoneUsuario(), codigo);
        }
        return ResponseEntity.ok(Map.of("mensagem", "Se o email estiver cadastrado, o código será enviado."));
    }

    @PostMapping("/redefinir")
    public ResponseEntity<Map<String, String>> redefinirSenha(@RequestParam String email,
            @RequestParam String codigo, @RequestParam String novaSenha) {
        if (!emailValido(email)) return erro("email", "Informe um email válido.");
        if (codigo == null || !codigo.matches("\\d{6}")) return erro("codigo", "Informe os seis números do código.");
        if (novaSenha == null || novaSenha.length() < 8 || novaSenha.length() > 72)
            return erro("novaSenha", "A senha deve ter entre 8 e 72 caracteres.");

        Usuario usuario = usuarioRepository.findByEmailUsuario(email).orElse(null);
        if (usuario == null || usuario.getCodigoRecuperacao() == null || usuario.getExpiracaoCodigo() == null
                || usuario.getExpiracaoCodigo().isBefore(LocalDateTime.now())
                || !usuario.getCodigoRecuperacao().equals(codigo)) {
            return erro("codigo", "Código inválido ou expirado.");
        }
        usuario.setSenhaUsuario(passwordEncoder.encode(novaSenha));
        usuario.setCodigoRecuperacao(null);
        usuario.setExpiracaoCodigo(null);
        usuarioRepository.save(usuario);
        return ResponseEntity.ok(Map.of("mensagem", "Senha redefinida com sucesso."));
    }

    private boolean emailValido(String email) {
        return email != null && email.length() <= 80
                && email.matches("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}");
    }

    private ResponseEntity<Map<String, String>> erro(String campo, String mensagem) {
        return ResponseEntity.badRequest().body(Map.of("campo", campo, "erro", mensagem));
    }
}
