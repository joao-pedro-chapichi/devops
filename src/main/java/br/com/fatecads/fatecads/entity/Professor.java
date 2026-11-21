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
    @NotBlank(message = "Informe o nome.")
    @Size(max = 40, message = "Use no máximo 40 caracteres.")
    @Pattern(regexp = "[\\p{L}][\\p{L} .'-]*", message = "Use apenas letras e pontuação de nome.")
    private String nomeProfessor;

    @Column(nullable = false, length = 20)
    @NotBlank(message = "Informe o telefone.")
    @Pattern(regexp = "\\d{10,11}", message = "Digite 10 ou 11 números, sem pontuação.")
    private String telefoneProfessor;

    @Column(nullable = false, length = 50)
    @NotBlank(message = "Informe a graduação.")
    @Size(max = 50, message = "Use no máximo 50 caracteres.")
    @Pattern(regexp = "[\\p{L}\\p{N} .'-]+", message = "A graduação contém caracteres inválidos.")
    private String graduacaoProfessor;

    @Column(nullable = false, length = 50)
    @NotBlank(message = "Informe o RM.")
    @Pattern(regexp = "[A-Za-z0-9-]{1,50}", message = "Use apenas letras, números e hífen.")
    private String rmProfessor;
}
