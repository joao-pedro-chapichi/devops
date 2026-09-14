package br.com.fatecads.fatecads.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.fatecads.fatecads.entity.ItemDoPedido;
import br.com.fatecads.fatecads.entity.Pedido;
import br.com.fatecads.fatecads.entity.Produto;
import br.com.fatecads.fatecads.entity.Usuario;
import br.com.fatecads.fatecads.repository.PedidoRepository;
import br.com.fatecads.fatecads.repository.ProdutoRepository;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    // Método para criar um pedido
    public Pedido salvarPedido(Pedido pedido) {
        pedido.setDataPedido(LocalDate.now());
        for (ItemDoPedido item : pedido.getItens()) {
            Produto produto = produtoRepository.findById(item.getProduto().getIdProduto()).orElseThrow(() -> new RuntimeException("Produto não encontrado"));

            item.setProduto(produto);
            item.setPreco(produto.getValorProduto());
            item.atualizarSubtotal();
            item.setPedido(pedido);
        }
        pedido.atualizarTotal();
        return pedidoRepository.save(pedido);
    }

    // A compra sempre usa preço e produto vindos do banco, nunca valores enviados pelo navegador.
    public Pedido salvarCompra(Usuario usuario, java.util.List<ItemDoPedido> itensDaCompra) {
        Pedido pedido = new Pedido();
        pedido.setDataPedido(LocalDate.now());
        pedido.setUsuario(usuario);

        for (ItemDoPedido item : itensDaCompra) {
            if (item.getProduto() == null || item.getProduto().getIdProduto() == null
                    || item.getQuantidade() == null || item.getQuantidade() < 1) {
                throw new IllegalArgumentException("Há um item inválido no carrinho.");
            }

            Produto produto = produtoRepository.findById(item.getProduto().getIdProduto())
                    .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));
            item.setProduto(produto);
            item.setPreco(produto.getValorProduto());
            item.atualizarSubtotal();
            item.setPedido(pedido);
        }

        pedido.setItens(itensDaCompra);
        pedido.atualizarTotal();
        return pedidoRepository.save(pedido);
    }
}
