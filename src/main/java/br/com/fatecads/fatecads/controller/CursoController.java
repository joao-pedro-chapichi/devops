package br.com.fatecads.fatecads.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.fatecads.fatecads.entity.Curso;
import br.com.fatecads.fatecads.service.CursoService;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;



@Controller
@RequestMapping("/cursos")
public class CursoController {
    @InitBinder("curso")
    void camposPermitidos(WebDataBinder binder) {
        binder.setAllowedFields("idCurso", "nomeCurso", "periodoCurso", "cargaHorariaCurso");
    }
    
    @Autowired
    private CursoService cursoService;

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute Curso curso, BindingResult erros) {
        if (erros.hasErrors()) {
            return "curso/formularioCurso";
        }
        cursoService.save(curso);
        return "redirect:/cursos/listar";
    }

    @GetMapping("/listar")
    public String listar(Model model) {
        
        model.addAttribute("cursos", cursoService.findAll());
        return "curso/listarCurso";
    }
    
    @GetMapping("/criar")
    public String criarForm(Model model) {
        model.addAttribute("curso", new Curso());
        return "curso/formularioCurso";
    }
    
    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Integer id) {
        cursoService.deleteById(id);
        return "redirect:/cursos/listar";
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Integer id, Model model) {
        Curso curso = cursoService.findById(id);
        model.addAttribute("curso", curso);
        return "curso/formularioCurso";
    }
}
