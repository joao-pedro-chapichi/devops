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
public class Disciplina {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idDisciplina;

    @Column(nullable = false, length = 40)
    @NotBlank @Size(max = 40) @Pattern(regexp = "^[^<>\\p{Cntrl}]+$")
    private String nomeDisciplina;

    @Column(nullable = false, length = 10)  
    @NotBlank @Pattern(regexp = "^[A-Za-z0-9]{2,10}$")
    private String siglaDisciplina;

    @Column(nullable = false)
    @NotNull @Min(1) @Max(100000)
    private Integer cargaHorariaDisciplina;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "idCurso_fk")
    private Curso curso;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "idProfessor_fk")
    private Professor professor;
}
