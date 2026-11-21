package br.com.fatecads.fatecads.entity;

import java.util.List;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Produto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer idProduto;

    @Column(nullable = false, length = 40)
    @NotBlank(message = "Informe a descrição.")
    @Size(max = 40, message = "Use no máximo 40 caracteres.")
    @Pattern(regexp = "[\\p{L}\\p{N} .,'/()&\\-]+", message = "A descrição contém caracteres inválidos.")
    private String descricaoProduto;

    @NotNull(message = "Informe o valor.")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero.")
    @DecimalMax(value = "1000000.00", message = "O valor é muito alto.")
    private Double valorProduto;

    @Column(nullable = false, length = 10)
    @NotBlank(message = "Informe a unidade.")
    @Pattern(regexp = "[\\p{L}\\p{N} .-]{1,10}", message = "Use até 10 letras ou números.")
    private String unidadeProduto;

    @Column(nullable = false, length = 30)
    @NotBlank(message = "Informe a marca.")
    @Size(max = 30, message = "Use no máximo 30 caracteres.")
    @Pattern(regexp = "[\\p{L}\\p{N} .&'-]+", message = "A marca contém caracteres inválidos.")
    private String marcaProduto;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] imagemProduto;

    private String tipoImagem;

    @OneToMany(mappedBy = "produto")
    private List<ItemDoPedido> itens;
}
