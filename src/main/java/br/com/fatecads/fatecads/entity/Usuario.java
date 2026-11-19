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
    @NotBlank @Size(max = 80) @Pattern(regexp = "^[^<>\\p{Cntrl}]+$")
    private String nomeUsuario;

    @Column(nullable = false, length = 80)
    @NotBlank @Email @Size(max = 80)
    private String emailUsuario;

    @Column(nullable = false, length = 50)
    @NotBlank @Pattern(regexp = "^[A-Za-z0-9._-]{3,50}$")
    private String loginUsuario;

    @Column(nullable = false, length = 150)
    @NotBlank @Size(min = 8, max = 72)
    private String senhaUsuario;

    @Column(length = 20)
    @Pattern(regexp = "^$|^[0-9]{10,11}$")
    private String telefoneUsuario;

    @Column(length = 6)
    private String codigoRecuperacao;

    private LocalDateTime expiracaoCodigo;

    private String role = "ROLE_USER";
}
