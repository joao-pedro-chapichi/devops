package br.com.fatecads.fatecads.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Curso {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idCurso;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Informe o nome.")
    @Size(max = 100, message = "Use no máximo 100 caracteres.")
    @Pattern(regexp = "[\\p{L}\\p{N} .'-]+", message = "O nome contém caracteres inválidos.")
    private String nomeCurso;

    @Column(nullable = false, length = 50)
    @NotBlank(message = "Informe o período.")
    @Size(max = 50, message = "Use no máximo 50 caracteres.")
    @Pattern(regexp = "[\\p{L}\\p{N} .'-]+", message = "O período contém caracteres inválidos.")
    private String periodoCurso;

    @Column(nullable = false, length = 50)
    @NotNull(message = "Informe a carga horária.")
    @Min(value = 1, message = "A carga horária deve ser maior que zero.")
    @Max(value = 100000, message = "A carga horária é muito alta.")
    private Integer cargaHorariaCurso;
}
