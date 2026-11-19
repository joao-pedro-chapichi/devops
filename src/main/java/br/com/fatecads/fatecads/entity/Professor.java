package br.com.fatecads.fatecads.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
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
public class Professor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idProfessor;

    @Column(nullable = false, length = 40)
    @NotBlank @Size(max = 40) @Pattern(regexp = "^[^<>\\p{Cntrl}]+$")
    private String nomeProfessor;

    @Column(nullable = false, length = 20)
    @NotBlank @Pattern(regexp = "^[0-9]{10,11}$", message = "Informe 10 ou 11 dígitos, sem pontuação")
    private String telefoneProfessor;

    @Column(nullable = false, length = 50)
    @NotBlank @Size(max = 50) @Pattern(regexp = "^[^<>\\p{Cntrl}]+$")
    private String graduacaoProfessor;

    @Column(nullable = false, length = 50)
    @NotBlank @Pattern(regexp = "^[A-Za-z0-9-]{1,50}$")
    private String rmProfessor;
}
