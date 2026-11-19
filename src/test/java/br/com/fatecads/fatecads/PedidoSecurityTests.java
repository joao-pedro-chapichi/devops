package br.com.fatecads.fatecads;

import br.com.fatecads.fatecads.entity.*;
import br.com.fatecads.fatecads.repository.*;
import br.com.fatecads.fatecads.service.PedidoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PedidoSecurityTests {
    private final PedidoRepository pedidos = mock(PedidoRepository.class);
    private final ProdutoRepository produtos = mock(ProdutoRepository.class);
    private final AlunoRepository alunos = mock(AlunoRepository.class);
    private final PedidoService service = new PedidoService();

    @BeforeEach void preparar() {
        ReflectionTestUtils.setField(service, "pedidoRepository", pedidos);
        ReflectionTestUtils.setField(service, "produtoRepository", produtos);
        ReflectionTestUtils.setField(service, "alunoRepository", alunos);
        when(pedidos.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }
    private ItemDoPedido item(int quantidade) {
        Produto p = new Produto(); p.setIdProduto(3);
        ItemDoPedido item = new ItemDoPedido(); item.setProduto(p); item.setQuantidade(quantidade);
        item.setPreco(0.01); item.setSubtotal(0.01); return item;
    }
    private void precoNoBanco() {
        Produto p = new Produto(); p.setIdProduto(3); p.setValorProduto(25.0);
        when(produtos.findById(3)).thenReturn(Optional.of(p));
    }

    @Test void compraIgnoraPrecoESubtotalForjados() {
        precoNoBanco();
        Usuario usuario = new Usuario();
        Pedido salvo = service.salvarCompra(usuario, List.of(item(2)));
        assertEquals(25.0, salvo.getItens().get(0).getPreco());
        assertEquals(50.0, salvo.getItens().get(0).getSubtotal());
        assertEquals(50.0, salvo.getTotalPedido());
        assertSame(usuario, salvo.getUsuario());
        verify(pedidos).save(any());
    }

    @Test void compraRecusaQuantidadeNulaNegativaOuExcessivaSemSalvar() {
        for (Integer qtd : new Integer[]{null, 0, -1, 1001}) {
            ItemDoPedido item = item(1); item.setQuantidade(qtd);
            assertThrows(IllegalArgumentException.class, () -> service.salvarCompra(new Usuario(), List.of(item)));
        }
        assertThrows(IllegalArgumentException.class, () -> service.salvarCompra(new Usuario(), List.of()));
        verify(pedidos, never()).save(any());
    }

    @Test void pedidoRecuperaAlunoEProdutoDoBancoEIgnoraPrecoInformado() {
        precoNoBanco();
        Aluno a = new Aluno(); a.setIdAluno(7);
        when(alunos.findById(7)).thenReturn(Optional.of(a));
        Pedido pedido = new Pedido(); pedido.setAluno(a); pedido.setItens(List.of(item(3)));
        Pedido salvo = service.salvarPedido(pedido);
        assertSame(a, salvo.getAluno());
        assertEquals(75.0, salvo.getTotalPedido());
        assertEquals(25.0, salvo.getItens().get(0).getPreco());
    }

    @Test void pedidoInvalidoNaoPersiste() {
        Pedido pedido = new Pedido(); pedido.setItens(List.of(item(2)));
        assertThrows(IllegalArgumentException.class, () -> service.salvarPedido(pedido));
        verify(pedidos, never()).save(any());
    }
}
