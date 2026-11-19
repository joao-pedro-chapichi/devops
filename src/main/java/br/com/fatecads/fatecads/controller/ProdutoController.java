package br.com.fatecads.fatecads.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.fatecads.fatecads.entity.Produto;
import br.com.fatecads.fatecads.service.ProdutoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    // Método para listar todos os produtos
    @GetMapping("/listar")
    public String listar(Model model) {
        List<Produto> produtos = produtoService.findAll();
        model.addAttribute("produtos", produtos);
        return "produto/listarProdutos";
    }

    // Método para abrir o formulário de criação de produtos
    @GetMapping("/criar")
    public String criarForm(Model model) {
        model.addAttribute("produto", new Produto());
        return "produto/formularioProduto";
    }

    @PostMapping("/salvar")
    public String salvar(
            @Valid @ModelAttribute Produto produto, BindingResult erros,
            @RequestParam("imagem") MultipartFile imagem) throws IOException {

        if (!imagem.isEmpty()) {
            if (imagem.getSize() > 5_000_000) {
                erros.reject("imagem.invalida", "Imagem deve ser PNG ou JPEG com até 5 MB.");
            } else {
                byte[] bytes = imagem.getBytes();
                boolean png = bytes.length >= 8 && bytes[0] == (byte) 0x89 && bytes[1] == 0x50
                    && bytes[2] == 0x4e && bytes[3] == 0x47 && bytes[4] == 0x0d
                    && bytes[5] == 0x0a && bytes[6] == 0x1a && bytes[7] == 0x0a;
                boolean jpeg = bytes.length >= 3 && bytes[0] == (byte) 0xff
                    && bytes[1] == (byte) 0xd8 && bytes[2] == (byte) 0xff;
                if (!(png || jpeg)) {
                    erros.reject("imagem.invalida", "Imagem deve ser PNG ou JPEG com até 5 MB.");
                }
            }
        }
        if (erros.hasErrors()) return "produto/formularioProduto";

        if (!imagem.isEmpty()) {
            produto.setImagemProduto(imagem.getBytes());
            produto.setTipoImagem(imagem.getBytes()[0] == (byte) 0x89 ? "image/png" : "image/jpeg");
        } else if (produto.getIdProduto() != null) {
            Produto existente = produtoService.findById(produto.getIdProduto());
            if (existente == null) return "redirect:/produtos/listar";
            produto.setImagemProduto(existente.getImagemProduto());
            produto.setTipoImagem(existente.getTipoImagem());
        }

        produtoService.save(produto);

        return "redirect:/produtos/listar";
    }

    @GetMapping("/imagem/{id}")
    public ResponseEntity<byte[]> mostrarImagem(@PathVariable Integer id) {

        Produto produto = produtoService.findById(id);

        if (produto == null || produto.getImagemProduto() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(produto.getTipoImagem()))
                .body(produto.getImagemProduto());
    }

    // Método para abrir o formulário de edição de produtos
    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Integer id, Model model) {
        Produto produto = produtoService.findById(id);
        model.addAttribute("produto", produto);
        return "produto/formularioProduto";
    }

    // Método para excluir um produto pelo ID
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Integer id) {
        produtoService.deleteById(id);
        return "redirect:/produtos/listar";
    }

}
