package br.com.fatecads.fatecads.controller;

import br.com.fatecads.fatecads.entity.ItemDoPedido;
import br.com.fatecads.fatecads.entity.Produto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public record ItemRequest(@NotNull @Valid ProdutoId produto, @NotNull @Min(1) @Max(1000) Integer quantidade) {
    public record ProdutoId(@NotNull @Positive Integer idProduto) { }

    public ItemDoPedido paraEntidade() {
        ItemDoPedido item = new ItemDoPedido();
        Produto p = new Produto();
        p.setIdProduto(produto.idProduto());
        item.setProduto(p);
        item.setQuantidade(quantidade);
        return item;
    }
}
