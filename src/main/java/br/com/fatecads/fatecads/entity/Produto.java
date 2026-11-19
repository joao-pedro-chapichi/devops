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
    @NotBlank @Size(max = 40) @Pattern(regexp = "^[^<>\\p{Cntrl}]+$")
    private String descricaoProduto;

    @NotNull @DecimalMin("0.01") @DecimalMax("1000000.00")
    private Double valorProduto;

    @Column(nullable = false, length = 10)
    @NotBlank @Size(max = 10) @Pattern(regexp = "^[^<>\\p{Cntrl}]+$")
    private String unidadeProduto;

    @Column(nullable = false, length = 30)
    @NotBlank @Size(max = 30) @Pattern(regexp = "^[^<>\\p{Cntrl}]+$")
    private String marcaProduto;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] imagemProduto;

    private String tipoImagem;

    @OneToMany(mappedBy = "produto")
    private List<ItemDoPedido> itens;
}
