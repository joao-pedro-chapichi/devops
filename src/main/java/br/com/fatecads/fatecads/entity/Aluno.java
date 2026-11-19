package br.com.fatecads.fatecads.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class Aluno {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idAluno;

    @Column(nullable = false, length = 40)
    @NotBlank @Size(max = 40) @Pattern(regexp = "^[^<>\\p{Cntrl}]+$")
    private String nomeAluno;

    @Column(nullable = false, length = 40)  
    @NotBlank @Email @Size(max = 40)
    private String emailAluno;

    @Column(nullable = false, length = 20)
    @NotBlank @Pattern(regexp = "^[0-9]{10,11}$", message = "Informe 10 ou 11 dígitos, sem pontuação")
    private String telefoneAluno;

    @Column(nullable = false, length = 50)
    @NotBlank @Size(max = 50) @Pattern(regexp = "^[^<>\\p{Cntrl}]+$")
    private String enderecoAluno;

    @Column(nullable = false, length = 11)
    @NotBlank @Pattern(regexp = "^[0-9]{11}$", message = "Informe 11 dígitos, sem pontuação")
    private String cpfAluno;

    @Column(nullable = false)
    @NotBlank @Size(max = 30) @Pattern(regexp = "^[A-Za-z0-9-]+$")
    private String raAluno;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "idCurso_fk")
    private Curso curso;
}
