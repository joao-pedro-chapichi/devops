package br.com.fatecads.fatecads.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.fatecads.fatecads.entity.Aluno;
import br.com.fatecads.fatecads.service.AlunoService;

@Controller
@RequestMapping("/alunos")
public class AlunoController {
    
    // Injeção de dependêccia da service de alunos
    private AlunoService alunoService;

    // Método para salvar um aluno
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Aluno aluno) {
        alunoService.save(aluno);
        return "redirect:/alunos/listar";
    }

    // Método para listar todos os alunos
    @GetMapping("/listar")
    public String listar(Model model) {
        
        model.addAttribute("alunos", alunoService.findAll());
        return "aluno/listarALunos";
    }
}
