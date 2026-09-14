package br.com.fatecads.fatecads.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.fatecads.fatecads.entity.Usuario;
import br.com.fatecads.fatecads.service.UsuarioService;



@Controller
@RequestMapping("/usuarios")
public class UsuarioController {
    
    // Injeção de dependêccia da service de alunos
    @Autowired
    private UsuarioService usuarioService;

    // Método para salvar um aluno
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Usuario usuario, Authentication authentication) {
        boolean admin = ehAdmin(authentication);

        // Cadastro público nunca pode escolher privilégios administrativos.
        if (!admin) {
            if (usuario.getIdUsuario() != null) {
                return "redirect:/login";
            }
            usuario.setRole("ROLE_USER");
        } else if (!"ROLE_ADMIN".equals(usuario.getRole())) {
            usuario.setRole("ROLE_USER");
        }
        usuarioService.save(usuario);
        return admin ? "redirect:/usuarios/listar" : "redirect:/login";
    }   

    // Método para listar todos os alunos
    @GetMapping("/listar")
    public String listar(Model model) {
        
        model.addAttribute("usuarios", usuarioService.findAll());
        return "usuario/listarUsuario";
    }

    // Método para criar um novo aluno e abrir um novo formulário
    @GetMapping("/criar")
    public String criarForm(Model model, Authentication authentication) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("podeDefinirRole", ehAdmin(authentication));
        return "usuario/formularioUsuario";
    }

    // Método para excluir um aluno pelo ID
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Integer id) {
        usuarioService.deleteById(id);
        return "redirect:/usuarios/listar";
    }
    
    // Método para editar um aluno pelo ID
    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Integer id, Model model, Authentication authentication) {
        Usuario usuario = usuarioService.findById(id);
        model.addAttribute("usuario", usuario);
        model.addAttribute("podeDefinirRole", ehAdmin(authentication));
        return "usuario/formularioUsuario";
    }

    private boolean ehAdmin(Authentication authentication) {
        return authentication != null && authentication.isAuthenticated()
                && authentication.getAuthorities().stream()
                        .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }
    
}
