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
import java.util.Arrays;
import java.math.BigDecimal;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Controller
@RequestMapping("/produtos")
public class ProdutoController {
    @InitBinder("produto")
    void camposPermitidos(WebDataBinder binder) {
        binder.setAllowedFields("idProduto", "descricaoProduto", "valorProduto", "unidadeProduto", "marcaProduto");
    }

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
            @RequestParam("imagem") MultipartFile imagem, Model model) {

        if (produto.getValorProduto() != null && !Double.isFinite(produto.getValorProduto())) {
            erros.rejectValue("valorProduto", "invalido", "Informe um valor numérico válido.");
        } else if (produto.getValorProduto() != null
                && BigDecimal.valueOf(produto.getValorProduto()).stripTrailingZeros().scale() > 2) {
            erros.rejectValue("valorProduto", "casasDecimais", "Use no máximo duas casas decimais.");
        }
        byte[] dadosImagem = null;
        if (!imagem.isEmpty()) {
            if (imagem.getSize() > 5 * 1024 * 1024) {
                model.addAttribute("erroImagem", "Envie uma imagem PNG, JPEG ou GIF de até 5 MB.");
            } else {
                try {
                    dadosImagem = imagem.getBytes();
                    if (tipoImagem(dadosImagem) == null) {
                        model.addAttribute("erroImagem", "Envie uma imagem PNG, JPEG ou GIF de até 5 MB.");
                    }
                } catch (IOException e) {
                    model.addAttribute("erroImagem", "Não foi possível ler a imagem. Envie o arquivo novamente.");
                }
            }
        }
        if (erros.hasErrors() || model.containsAttribute("erroImagem")) {
            return "produto/formularioProduto";
        }

        if (produto.getIdProduto() != null) {
            Produto existente = produtoService.findById(produto.getIdProduto());
            if (existente == null) {
                erros.rejectValue("idProduto", "invalido", "Produto não encontrado.");
                return "produto/formularioProduto";
            }
            produto.setImagemProduto(existente.getImagemProduto());
            produto.setTipoImagem(existente.getTipoImagem());
        }

        if (dadosImagem != null) {
            produto.setImagemProduto(dadosImagem);
            produto.setTipoImagem(tipoImagem(dadosImagem));
        }

        produtoService.save(produto);

        return "redirect:/produtos/listar";
    }

    private String tipoImagem(byte[] bytes) {
        if (bytes.length >= 8 && Arrays.equals(Arrays.copyOf(bytes, 8),
                new byte[]{(byte) 137, 80, 78, 71, 13, 10, 26, 10})) return "image/png";
        if (bytes.length >= 3 && (bytes[0] & 255) == 255 && (bytes[1] & 255) == 216 && (bytes[2] & 255) == 255) return "image/jpeg";
        if (bytes.length >= 6 && (new String(bytes, 0, 6, java.nio.charset.StandardCharsets.US_ASCII).equals("GIF87a")
                || new String(bytes, 0, 6, java.nio.charset.StandardCharsets.US_ASCII).equals("GIF89a"))) return "image/gif";
        return null;
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String imagemMuitoGrande(Model model) {
        model.addAttribute("produto", new Produto());
        model.addAttribute("erroImagem", "Envie uma imagem de até 5 MB.");
        return "produto/formularioProduto";
    }

    @GetMapping("/imagem/{id}")
    public ResponseEntity<byte[]> mostrarImagem(@PathVariable Integer id) {

        Produto produto = produtoService.findById(id);

        if (produto == null || produto.getImagemProduto() == null || tipoImagem(produto.getImagemProduto()) == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(tipoImagem(produto.getImagemProduto())))
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
