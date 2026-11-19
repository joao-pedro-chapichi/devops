package br.com.fatecads.fatecads.controller;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

// Impede que campos internos (role, preço calculado, código de recuperação etc.)
// sejam alterados com parâmetros extras enviados diretamente ao servidor.
@ControllerAdvice
public class FormBindingConfig {
    @InitBinder
    public void limitarCampos(WebDataBinder binder) {
        switch (binder.getObjectName()) {
            case "aluno" -> binder.setAllowedFields("idAluno", "nomeAluno", "emailAluno", "telefoneAluno",
                    "enderecoAluno", "cpfAluno", "raAluno", "curso");
            case "professor" -> binder.setAllowedFields("idProfessor", "nomeProfessor", "telefoneProfessor",
                    "graduacaoProfessor", "rmProfessor");
            case "curso" -> binder.setAllowedFields("idCurso", "nomeCurso", "periodoCurso", "cargaHorariaCurso");
            case "disciplina" -> binder.setAllowedFields("idDisciplina", "nomeDisciplina", "siglaDisciplina",
                    "cargaHorariaDisciplina", "curso", "professor");
            case "usuario" -> binder.setAllowedFields("idUsuario", "nomeUsuario", "emailUsuario",
                    "loginUsuario", "senhaUsuario", "telefoneUsuario");
            case "produto" -> binder.setAllowedFields("idProduto", "descricaoProduto", "valorProduto",
                    "unidadeProduto", "marcaProduto");
            default -> { }
        }
    }
}
