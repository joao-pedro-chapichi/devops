package br.com.fatecads.fatecads;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import br.com.fatecads.fatecads.controller.PedidoController;
import br.com.fatecads.fatecads.controller.PedidoController.PedidoRequest;
import br.com.fatecads.fatecads.controller.PedidoController.ProdutoPedidoRequest;
import br.com.fatecads.fatecads.entity.*;
import br.com.fatecads.fatecads.service.*;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import br.com.fatecads.fatecads.validation.CpfValidator;

class ValidacaoEntradaTests {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private boolean erroEm(Object objeto, String campo) {
        return validator.validate(objeto).stream().anyMatch(erro -> erro.getPropertyPath().toString().equals(campo));
    }

    @Test
    void rejeitaConteudoPerigosoEValoresIncorretosEmTodosOsCadastros() {
        Aluno aluno = new Aluno();
        aluno.setNomeAluno("<script>alert(1)</script>");
        aluno.setCpfAluno("123.456.789-00");
        aluno.setTelefoneAluno("abc");
        assertTrue(erroEm(aluno, "nomeAluno"));
        assertTrue(erroEm(aluno, "cpfAluno"));
        assertTrue(erroEm(aluno, "telefoneAluno"));

        Curso curso = new Curso();
        curso.setNomeCurso("<img src=x>");
        curso.setCargaHorariaCurso(-1);
        assertTrue(erroEm(curso, "nomeCurso"));
        assertTrue(erroEm(curso, "cargaHorariaCurso"));

        Disciplina disciplina = new Disciplina();
        disciplina.setSiglaDisciplina("<POO>");
        assertTrue(erroEm(disciplina, "siglaDisciplina"));

        Professor professor = new Professor();
        professor.setTelefoneProfessor("(11) 99999-9999");
        professor.setGraduacaoProfessor("<script>");
        assertTrue(erroEm(professor, "telefoneProfessor"));
        assertTrue(erroEm(professor, "graduacaoProfessor"));

        Usuario usuario = new Usuario();
        usuario.setEmailUsuario("<script>@example.com");
        usuario.setLoginUsuario("admin<script>");
        usuario.setSenhaUsuario("curta");
        assertTrue(erroEm(usuario, "emailUsuario"));
        assertTrue(erroEm(usuario, "loginUsuario"));
        assertTrue(erroEm(usuario, "senhaUsuario"));

        Produto produto = new Produto();
        produto.setDescricaoProduto("<script>");
        produto.setValorProduto(-10.0);
        assertTrue(erroEm(produto, "descricaoProduto"));
        assertTrue(erroEm(produto, "valorProduto"));
    }

    @Test
    void aceitaCaracteresLegitimosEmNomeEEndereco() {
        Aluno aluno = new Aluno();
        aluno.setNomeAluno("Ana D'Ávila");
        aluno.setEnderecoAluno("Rua São João, 12");
        aluno.setEmailAluno("ana+teste@example.com");
        assertFalse(erroEm(aluno, "nomeAluno"));
        assertFalse(erroEm(aluno, "enderecoAluno"));
        assertFalse(erroEm(aluno, "emailAluno"));
        Produto produto = new Produto();
        produto.setDescricaoProduto("Pão & Cia");
        produto.setMarcaProduto("H&M");
        assertFalse(erroEm(produto, "descricaoProduto"));
        assertFalse(erroEm(produto, "marcaProduto"));
    }

    @Test
    void validaDigitosVerificadoresDoCpf() {
        assertTrue(CpfValidator.valido("52998224725"));
        assertFalse(CpfValidator.valido("52998224726"));
        assertFalse(CpfValidator.valido("11111111111"));
    }

    @Test
    void pedidoRejeitaAlunoInexistenteEQuantidadeForaDoLimite() {
        PedidoService pedidos = mock(PedidoService.class);
        AlunoService alunos = mock(AlunoService.class);
        ProdutoService produtos = mock(ProdutoService.class);
        PedidoController controller = new PedidoController(pedidos, alunos, produtos);

        var semAluno = controller.salvarPedido(new PedidoRequest(999, List.of(new ProdutoPedidoRequest(1, 1))));
        assertEquals(HttpStatus.BAD_REQUEST, semAluno.getStatusCode());
        assertEquals("aluno", semAluno.getBody().get("campo"));

        when(alunos.findById(1)).thenReturn(new Aluno());
        when(produtos.findById(1)).thenReturn(new Produto());
        var quantidade = controller.salvarPedido(new PedidoRequest(1, List.of(new ProdutoPedidoRequest(1, -5))));
        assertEquals(HttpStatus.BAD_REQUEST, quantidade.getStatusCode());
        assertEquals("quantidade", quantidade.getBody().get("campo"));
        verifyNoInteractions(pedidos);
    }
}
