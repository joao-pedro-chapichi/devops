package br.com.fatecads.fatecads.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import br.com.fatecads.fatecads.entity.ItemDoPedido;
import br.com.fatecads.fatecads.entity.Produto;
import br.com.fatecads.fatecads.security.UserDetailsImpl;
import br.com.fatecads.fatecads.service.PedidoService;
import br.com.fatecads.fatecads.service.ProdutoService;
import br.com.fatecads.fatecads.service.UsuarioService;

@Controller
public class LojaController {

    private final ProdutoService produtoService;
    private final PedidoService pedidoService;
    private final UsuarioService usuarioService;

    public LojaController(ProdutoService produtoService, PedidoService pedidoService, UsuarioService usuarioService) {
        this.produtoService = produtoService;
        this.pedidoService = pedidoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/loja")
    public String loja(Model model) {
        List<Produto> produtos = produtoService.findAll();
        model.addAttribute("produtos", produtos);
        return "loja";
    }

    @PostMapping("/compras/finalizar")
    public ResponseEntity<Map<String, Object>> finalizar(@RequestBody CompraRequest compra,
            @AuthenticationPrincipal UserDetailsImpl usuarioAutenticado) {
        if (compra.itens() == null || compra.itens().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("erro", "O carrinho está vazio."));
        }

        try {
            var usuario = usuarioService.findByLogin(usuarioAutenticado.getUsername());
            var pedido = pedidoService.salvarCompra(usuario, compra.itens());
            return ResponseEntity.ok(Map.of("mensagem", "Compra realizada com sucesso!", "pedidoId", pedido.getIdPedido()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    public record CompraRequest(List<ItemDoPedido> itens) {
    }
}
