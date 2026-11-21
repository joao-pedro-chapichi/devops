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
import br.com.fatecads.fatecads.repository.AlunoRepository;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private AlunoRepository alunoRepository;

    // Método para criar um pedido
    public Pedido salvarPedido(Integer alunoId, java.util.List<ItemDoPedido> itens) {
        Pedido pedido = new Pedido();
        pedido.setAluno(alunoRepository.findById(alunoId)
                .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado.")));
        pedido.setItens(itens);
        pedido.setDataPedido(LocalDate.now());
        for (ItemDoPedido item : itens) {
            Produto produto = produtoRepository.findById(item.getProduto().getIdProduto()).orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));

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
                    || item.getQuantidade() == null || item.getQuantidade() < 1 || item.getQuantidade() > 1000) {
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
