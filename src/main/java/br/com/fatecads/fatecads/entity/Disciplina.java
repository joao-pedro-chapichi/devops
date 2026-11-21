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
    @NotBlank(message = "Informe o nome.")
    @Size(max = 40, message = "Use no máximo 40 caracteres.")
    @Pattern(regexp = "[\\p{L}\\p{N} .'-]+", message = "O nome contém caracteres inválidos.")
    private String nomeDisciplina;

    @Column(nullable = false, length = 10)  
    @NotBlank(message = "Informe a sigla.")
    @Pattern(regexp = "[A-Za-z0-9]{2,10}", message = "Use de 2 a 10 letras ou números.")
    private String siglaDisciplina;

    @Column(nullable = false)
    @NotNull(message = "Informe a carga horária.")
    @Min(value = 1, message = "A carga horária deve ser maior que zero.")
    @Max(value = 100000, message = "A carga horária é muito alta.")
    private Integer cargaHorariaDisciplina;

    @ManyToOne
    @NotNull(message = "Selecione um curso.")
    @JoinColumn(name = "idCurso_fk")
    private Curso curso;

    @ManyToOne
    @NotNull(message = "Selecione um professor.")
    @JoinColumn(name = "idProfessor_fk")
    private Professor professor;
}
