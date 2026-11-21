package br.com.fatecads.fatecads.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.fatecads.fatecads.entity.ItemDoPedido;
import br.com.fatecads.fatecads.entity.Produto;
import br.com.fatecads.fatecads.service.AlunoService;
import br.com.fatecads.fatecads.service.PedidoService;
import br.com.fatecads.fatecads.service.ProdutoService;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {
    private final PedidoService pedidoService;
    private final AlunoService alunoService;
    private final ProdutoService produtoService;

    public PedidoController(PedidoService pedidoService, AlunoService alunoService, ProdutoService produtoService) {
        this.pedidoService = pedidoService;
        this.alunoService = alunoService;
        this.produtoService = produtoService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> salvarPedido(@RequestBody PedidoRequest entrada) {
        if (entrada == null || entrada.alunoId() == null || entrada.alunoId() < 1 || alunoService.findById(entrada.alunoId()) == null) {
            return erro("aluno", "Selecione um aluno válido.");
        }
        if (entrada.itens() == null || entrada.itens().isEmpty() || entrada.itens().size() > 100) {
            return erro("itens", "Adicione de 1 a 100 produtos ao pedido.");
        }
        List<ItemDoPedido> itens = new ArrayList<>();
        for (ProdutoPedidoRequest item : entrada.itens()) {
            if (item == null || item.produtoId() == null || item.produtoId() < 1
                    || produtoService.findById(item.produtoId()) == null) {
                return erro("produto", "Selecione um produto válido.");
            }
            if (item.quantidade() == null || item.quantidade() < 1 || item.quantidade() > 1000) {
                return erro("quantidade", "Informe uma quantidade entre 1 e 1000.");
            }
            ItemDoPedido novo = new ItemDoPedido();
            Produto produto = new Produto();
            produto.setIdProduto(item.produtoId());
            novo.setProduto(produto);
            novo.setQuantidade(item.quantidade());
            itens.add(novo);
        }
        try {
            var pedido = pedidoService.salvarPedido(entrada.alunoId(), itens);
            return ResponseEntity.ok(Map.of("mensagem", "Pedido salvo com sucesso.", "pedidoId", pedido.getIdPedido()));
        } catch (IllegalArgumentException e) {
            return erro("itens", "Verifique os itens do pedido e tente novamente.");
        }
    }

    private ResponseEntity<Map<String, Object>> erro(String campo, String mensagem) {
        return ResponseEntity.badRequest().body(Map.of("campo", campo, "erro", mensagem));
    }

    @GetMapping("/criar")
    public String criarForm(Model model) {
        model.addAttribute("alunos", alunoService.findAll());
        model.addAttribute("produtos", produtoService.findAll());
        return "pedido/formularioPedido";
    }

    public record PedidoRequest(Integer alunoId, List<ProdutoPedidoRequest> itens) { }
    public record ProdutoPedidoRequest(Integer produtoId, Integer quantidade) { }
}
