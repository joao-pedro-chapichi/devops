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
    @NotBlank(message = "Informe o nome.")
    @Size(max = 40, message = "Use no máximo 40 caracteres.")
    @Pattern(regexp = "[\\p{L}][\\p{L} .'-]*", message = "Use apenas letras e pontuação de nome.")
    private String nomeAluno;

    @Column(nullable = false, length = 40)  
    @NotBlank(message = "Informe o email.")
    @Email(message = "Informe um email válido.")
    @Pattern(regexp = "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}", message = "Informe um email válido.")
    @Size(max = 40, message = "Use no máximo 40 caracteres.")
    private String emailAluno;

    @Column(nullable = false, length = 11)
    @NotBlank(message = "Informe o telefone.")
    @Pattern(regexp = "\\d{10,11}", message = "Digite 10 ou 11 números, sem pontuação.")
    private String telefoneAluno;

    @Column(nullable = false, length = 50)
    @NotBlank(message = "Informe o endereço.")
    @Size(max = 50, message = "Use no máximo 50 caracteres.")
    @Pattern(regexp = "[\\p{L}\\p{N} ,.'ºª/\\-]+", message = "O endereço contém caracteres inválidos.")
    private String enderecoAluno;

    @Column(nullable = false, length = 11)
    @NotBlank(message = "Informe o CPF.")
    @Pattern(regexp = "\\d{11}", message = "Digite 11 números, sem pontuação.")
    private String cpfAluno;

    @Column(nullable = false)
    @NotBlank(message = "Informe o RA.")
    @Size(max = 30, message = "Use no máximo 30 caracteres.")
    @Pattern(regexp = "[A-Za-z0-9-]+", message = "Use apenas letras, números e hífen.")
    private String raAluno;

    @ManyToOne
    @NotNull(message = "Selecione um curso.")
    @JoinColumn(name = "idCurso_fk")
    private Curso curso;
}
