package br.com.fatecads.fatecads;

import br.com.fatecads.fatecads.controller.FormBindingConfig;
import br.com.fatecads.fatecads.controller.ItemRequest;
import br.com.fatecads.fatecads.controller.LojaController;
import br.com.fatecads.fatecads.controller.PedidoController;
import br.com.fatecads.fatecads.controller.RecuperacaoSenhaController;
import br.com.fatecads.fatecads.entity.*;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.web.bind.WebDataBinder;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/** Demonstra valor válido, campo vazio, markup, tamanho, formato e limites de cada cadastro. */
class InputValidationTests {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private Curso curso() {
        Curso c = new Curso(); c.setNomeCurso("Análise e Desenvolvimento de Sistemas");
        c.setPeriodoCurso("Noturno"); c.setCargaHorariaCurso(2800); return c;
    }
    private Professor professor() {
        Professor p = new Professor(); p.setNomeProfessor("João da Silva");
        p.setTelefoneProfessor("17999999999"); p.setGraduacaoProfessor("Mestrado");
        p.setRmProfessor("RM-123"); return p;
    }
    private Aluno aluno() {
        Aluno a = new Aluno(); a.setNomeAluno("Ana Maria"); a.setEmailAluno("ana@example.com");
        a.setTelefoneAluno("17999999999"); a.setEnderecoAluno("Rua A, 12");
        a.setCpfAluno("12345678901"); a.setRaAluno("RA-123"); a.setCurso(curso()); return a;
    }
    private Disciplina disciplina() {
        Disciplina d = new Disciplina(); d.setNomeDisciplina("Programação");
        d.setSiglaDisciplina("POO"); d.setCargaHorariaDisciplina(80);
        d.setCurso(curso()); d.setProfessor(professor()); return d;
    }
    private Produto produto() {
        Produto p = new Produto(); p.setDescricaoProduto("Caderno grande");
        p.setValorProduto(29.90); p.setUnidadeProduto("Unidade"); p.setMarcaProduto("Marca A"); return p;
    }
    private Usuario usuario() {
        Usuario u = new Usuario(); u.setNomeUsuario("João Pedro"); u.setEmailUsuario("joao@example.com");
        u.setLoginUsuario("joao.pedro"); u.setSenhaUsuario("Senha#12345"); return u;
    }
    private ItemDoPedido item() {
        ItemDoPedido i = new ItemDoPedido(); i.setProduto(produto()); i.setQuantidade(2); return i;
    }
    private void valido(Object o) { assertTrue(validator.validate(o).isEmpty(), () -> validator.validate(o).toString()); }
    private void invalido(Object o, String campo) {
        assertTrue(validator.validate(o).stream().anyMatch(v -> v.getPropertyPath().toString().equals(campo)), campo);
    }

    @Test void alunoValidaTodosOsCampos() {
        valido(aluno());
        Aluno a = aluno(); a.setNomeAluno("<script>alert(1)</script>"); invalido(a, "nomeAluno");
        a = aluno(); a.setNomeAluno(" "); invalido(a, "nomeAluno");
        a = aluno(); a.setEmailAluno("email-errado"); invalido(a, "emailAluno");
        a = aluno(); a.setTelefoneAluno("(17)99999-9999"); invalido(a, "telefoneAluno");
        a = aluno(); a.setCpfAluno("123<script>"); invalido(a, "cpfAluno");
        a = aluno(); a.setEnderecoAluno("<img src=x>"); invalido(a, "enderecoAluno");
        a = aluno(); a.setRaAluno("RA 123!"); invalido(a, "raAluno");
        a = aluno(); a.setCurso(null); invalido(a, "curso");
        a = aluno(); a.setNomeAluno("A".repeat(41)); invalido(a, "nomeAluno");
    }

    @Test void professorValidaTodosOsCampos() {
        valido(professor());
        Professor p = professor(); p.setNomeProfessor("<svg onload=alert(1)>"); invalido(p, "nomeProfessor");
        p = professor(); p.setTelefoneProfessor("123"); invalido(p, "telefoneProfessor");
        p = professor(); p.setGraduacaoProfessor("<script>"); invalido(p, "graduacaoProfessor");
        p = professor(); p.setRmProfessor("RM; DROP TABLE"); invalido(p, "rmProfessor");
        p = professor(); p.setGraduacaoProfessor("X".repeat(51)); invalido(p, "graduacaoProfessor");
    }

    @Test void cursoEDisciplinaValidamTodosOsCampos() {
        valido(curso()); valido(disciplina());
        Curso c = curso(); c.setNomeCurso("<script>"); invalido(c, "nomeCurso");
        c = curso(); c.setPeriodoCurso("\nNoturno"); invalido(c, "periodoCurso");
        c = curso(); c.setCargaHorariaCurso(0); invalido(c, "cargaHorariaCurso");
        c = curso(); c.setCargaHorariaCurso(100001); invalido(c, "cargaHorariaCurso");
        Disciplina d = disciplina(); d.setNomeDisciplina("<script>"); invalido(d, "nomeDisciplina");
        d = disciplina(); d.setSiglaDisciplina("POO!"); invalido(d, "siglaDisciplina");
        d = disciplina(); d.setCargaHorariaDisciplina(-1); invalido(d, "cargaHorariaDisciplina");
        d = disciplina(); d.setCurso(null); invalido(d, "curso");
        d = disciplina(); d.setProfessor(null); invalido(d, "professor");
    }

    @Test void produtoValidaTodosOsCampos() {
        valido(produto());
        Produto p = produto(); p.setDescricaoProduto("<script>"); invalido(p, "descricaoProduto");
        p = produto(); p.setDescricaoProduto("X".repeat(41)); invalido(p, "descricaoProduto");
        p = produto(); p.setValorProduto(-1.0); invalido(p, "valorProduto");
        p = produto(); p.setValorProduto(1_000_001.0); invalido(p, "valorProduto");
        p = produto(); p.setUnidadeProduto("<img>"); invalido(p, "unidadeProduto");
        p = produto(); p.setMarcaProduto("<svg>"); invalido(p, "marcaProduto");
        p = produto(); p.setMarcaProduto("M".repeat(31)); invalido(p, "marcaProduto");
    }

    @Test void usuarioValidaTodosOsCamposSemProibirCaracteresEspeciaisNaSenha() {
        valido(usuario());
        Usuario u = usuario(); u.setNomeUsuario("<script>"); invalido(u, "nomeUsuario");
        u = usuario(); u.setEmailUsuario("não-é-email"); invalido(u, "emailUsuario");
        u = usuario(); u.setLoginUsuario("admin<script>"); invalido(u, "loginUsuario");
        u = usuario(); u.setSenhaUsuario("curta"); invalido(u, "senhaUsuario");
        u = usuario(); u.setTelefoneUsuario("telefone"); invalido(u, "telefoneUsuario");
        u = usuario(); u.setTelefoneUsuario("17999999999"); valido(u);
        u = usuario(); u.setSenhaUsuario("S3nh@<>!válida"); valido(u);
    }

    @Test void pedidoEJsonValidamItensEQuantidades() {
        Pedido pedido = new Pedido(); pedido.setAluno(aluno()); pedido.setItens(List.of(item())); valido(pedido);
        pedido.setItens(Collections.emptyList()); invalido(pedido, "itens");
        ItemDoPedido i = item(); i.setQuantidade(0); invalido(i, "quantidade");
        i = item(); i.setProduto(null); invalido(i, "produto");
        var itemValido = new ItemRequest(new ItemRequest.ProdutoId(1), 2);
        valido(itemValido);
        invalido(new ItemRequest(new ItemRequest.ProdutoId(1), 1001), "quantidade");
        invalido(new ItemRequest(new ItemRequest.ProdutoId(-1), 2), "produto.idProduto");
        valido(new LojaController.CompraRequest(List.of(itemValido)));
        invalido(new LojaController.CompraRequest(List.of()), "itens");
        valido(new PedidoController.PedidoRequest(new PedidoController.AlunoId(1), List.of(itemValido)));
        invalido(new PedidoController.PedidoRequest(new PedidoController.AlunoId(1), List.of()), "itens");
        invalido(new PedidoController.PedidoRequest(null, List.of(itemValido)), "aluno");
    }

    @Test void recuperacaoValidaParametrosAntesDeEnviarCodigoOuAlterarSenha() throws Exception {
        RecuperacaoSenhaController controller = new RecuperacaoSenhaController();
        Method solicitar = RecuperacaoSenhaController.class.getMethod("solicitarCodigo", String.class);
        Method redefinir = RecuperacaoSenhaController.class.getMethod("redefinirSenha", String.class, String.class, String.class);
        var executaveis = validator.forExecutables();
        assertFalse(executaveis.validateParameters(controller, solicitar, new Object[]{"<script>"}).isEmpty());
        assertFalse(executaveis.validateParameters(controller, redefinir,
                new Object[]{"joao@example.com", "123x56", "Senha#12345"}).isEmpty());
        assertFalse(executaveis.validateParameters(controller, redefinir,
                new Object[]{"joao@example.com", "123456", "curta"}).isEmpty());
        assertTrue(executaveis.validateParameters(controller, redefinir,
                new Object[]{"joao@example.com", "123456", "Senha#12345"}).isEmpty());
    }

    @Test void parametrosExtrasNaoAlteramCamposInternosEmNenhumFormulario() {
        Map<String, String> extras = Map.of("role", "ROLE_ADMIN", "codigoRecuperacao", "123456",
                "totalPedido", "0", "imagemProduto", "hack", "tipoImagem", "text/html");
        for (Object entidade : List.of(aluno(), professor(), curso(), disciplina(), usuario(), produto())) {
            String nome = entidade.getClass().getSimpleName().toLowerCase();
            WebDataBinder binder = new WebDataBinder(entidade, nome);
            new FormBindingConfig().limitarCampos(binder);
            binder.bind(new MutablePropertyValues(extras));
            assertEquals(extras.size(), binder.getBindingResult().getSuppressedFields().length, nome);
        }
        Usuario u = usuario(); assertEquals("ROLE_USER", u.getRole());
    }
}
