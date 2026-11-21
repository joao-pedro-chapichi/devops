package br.com.fatecads.fatecads.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.*;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idUsuario;

    @Column(nullable = false, length = 80)
    @NotBlank(message = "Informe o nome.")
    @Size(max = 80, message = "Use no máximo 80 caracteres.")
    @Pattern(regexp = "[\\p{L}][\\p{L} .'-]*", message = "Use apenas letras e pontuação de nome.")
    private String nomeUsuario;

    @Column(nullable = false, length = 80)
    @NotBlank(message = "Informe o email.")
    @Email(message = "Informe um email válido.")
    @Pattern(regexp = "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}", message = "Informe um email válido.")
    @Size(max = 80, message = "Use no máximo 80 caracteres.")
    private String emailUsuario;

    @Column(nullable = false, length = 50)
    @NotBlank(message = "Informe o login.")
    @Pattern(regexp = "[A-Za-z0-9._-]{3,50}", message = "Use de 3 a 50 letras, números, ponto, _ ou hífen.")
    private String loginUsuario;

    @Column(nullable = false, length = 150)
    @NotBlank(message = "Informe a senha.")
    @Size(min = 8, max = 72, message = "A senha deve ter entre 8 e 72 caracteres.")
    private String senhaUsuario;

    @Column(length = 20)
    @NotBlank(message = "Informe o telefone para recuperação de senha.")
    @Pattern(regexp = "\\d{10,11}", message = "Digite 10 ou 11 números, sem pontuação.")
    private String telefoneUsuario;

    @Column(length = 6)
    private String codigoRecuperacao;

    private LocalDateTime expiracaoCodigo;

    private String role = "ROLE_USER";
}
