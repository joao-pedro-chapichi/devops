package br.com.fatecads.fatecads;

import br.com.fatecads.fatecads.controller.*;
import br.com.fatecads.fatecads.entity.Produto;
import br.com.fatecads.fatecads.entity.Curso;
import br.com.fatecads.fatecads.entity.Professor;
import br.com.fatecads.fatecads.service.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.format.support.DefaultFormattingConversionService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Requisições reais ao controller: a validação do HTML sozinha não impediria esses POSTs. */
class EndpointValidationTests {
    private LocalValidatorFactoryBean validator;
    private MockMvc mvc;
    private final AlunoService alunos = mock(AlunoService.class);
    private final ProfessorService professores = mock(ProfessorService.class);
    private final CursoService cursos = mock(CursoService.class);
    private final DisciplinaService disciplinas = mock(DisciplinaService.class);
    private final UsuarioService usuarios = mock(UsuarioService.class);
    private final ProdutoService produtos = mock(ProdutoService.class);
    private final PedidoService pedidos = mock(PedidoService.class);

    @BeforeEach void preparar() {
        validator = new LocalValidatorFactoryBean(); validator.afterPropertiesSet();
        AlunoController ac = new AlunoController();
        ProfessorController pc = new ProfessorController();
        CursoController cc = new CursoController();
        DisciplinaController dc = new DisciplinaController();
        UsuarioController uc = new UsuarioController();
        ProdutoController prc = new ProdutoController();
        PedidoController pec = new PedidoController();
        LojaController lc = new LojaController(produtos, pedidos, usuarios);
        ReflectionTestUtils.setField(ac, "alunoService", alunos);
        ReflectionTestUtils.setField(ac, "cursoService", cursos);
        ReflectionTestUtils.setField(pc, "professorService", professores);
        ReflectionTestUtils.setField(cc, "cursoService", cursos);
        ReflectionTestUtils.setField(dc, "disciplinaService", disciplinas);
        ReflectionTestUtils.setField(dc, "cursoService", cursos);
        ReflectionTestUtils.setField(dc, "professorService", professores);
        ReflectionTestUtils.setField(uc, "usuarioService", usuarios);
        ReflectionTestUtils.setField(prc, "produtoService", produtos);
        ReflectionTestUtils.setField(pec, "pedidoService", pedidos);
        ReflectionTestUtils.setField(pec, "alunoService", alunos);
        ReflectionTestUtils.setField(pec, "produtoService", produtos);
        var conversao = new DefaultFormattingConversionService();
        conversao.addConverter(String.class, Curso.class, valor -> {
            Curso c = new Curso(); c.setIdCurso(Integer.valueOf(valor)); return c;
        });
        conversao.addConverter(String.class, Professor.class, valor -> {
            Professor p = new Professor(); p.setIdProfessor(Integer.valueOf(valor)); return p;
        });
        mvc = MockMvcBuilders.standaloneSetup(ac, pc, cc, dc, uc, prc, pec, lc)
                .setControllerAdvice(new FormBindingConfig()).setValidator(validator)
                .setConversionService(conversao).build();
    }
    @AfterEach void fechar() { validator.close(); }

    @Test void rejeitaPayloadDeAtaqueEmTodosOsFormulariosSemSalvar() throws Exception {
        mvc.perform(post("/alunos/salvar").param("nomeAluno", "<script>alert(1)</script>")
                .param("emailAluno", "ana@example.com").param("telefoneAluno", "17999999999")
                .param("cpfAluno", "12345678901").param("enderecoAluno", "Rua A, 12")
                .param("raAluno", "RA-123").param("curso", "1"))
                .andExpect(status().isOk()).andExpect(view().name("aluno/formularioAluno"))
                .andExpect(model().attributeHasFieldErrors("aluno", "nomeAluno"));
        mvc.perform(post("/professores/salvar").param("nomeProfessor", "<img src=x>")
                .param("telefoneProfessor", "17999999999").param("graduacaoProfessor", "Mestrado")
                .param("rmProfessor", "RM-123"))
                .andExpect(status().isOk()).andExpect(view().name("professor/formularioProfessor"))
                .andExpect(model().attributeHasFieldErrors("professor", "nomeProfessor"));
        mvc.perform(post("/cursos/salvar").param("nomeCurso", "<svg onload=alert(1)>")
                .param("periodoCurso", "Noturno").param("cargaHorariaCurso", "2800"))
                .andExpect(status().isOk()).andExpect(view().name("curso/formularioCurso"))
                .andExpect(model().attributeHasFieldErrors("curso", "nomeCurso"));
        mvc.perform(post("/disciplinas/salvar").param("nomeDisciplina", "<script>")
                .param("siglaDisciplina", "POO").param("cargaHorariaDisciplina", "80")
                .param("curso", "1").param("professor", "2"))
                .andExpect(status().isOk()).andExpect(view().name("disciplina/formularioDisciplina"))
                .andExpect(model().attributeHasFieldErrors("disciplina", "nomeDisciplina"));
        mvc.perform(post("/usuarios/salvar").param("nomeUsuario", "<script>")
                .param("emailUsuario", "joao@example.com").param("loginUsuario", "joao.pedro")
                .param("senhaUsuario", "Senha#12345"))
                .andExpect(status().isOk()).andExpect(view().name("usuario/formularioUsuario"))
                .andExpect(model().attributeHasFieldErrors("usuario", "nomeUsuario"));
        mvc.perform(multipart("/produtos/salvar")
                .file(new MockMultipartFile("imagem", new byte[0]))
                .param("descricaoProduto", "<script>").param("valorProduto", "12.50")
                .param("unidadeProduto", "Unidade").param("marcaProduto", "Marca"))
                .andExpect(status().isOk()).andExpect(view().name("produto/formularioProduto"))
                .andExpect(model().attributeHasFieldErrors("produto", "descricaoProduto"));
        verify(alunos, never()).save(any()); verify(professores, never()).save(any());
        verify(cursos, never()).save(any()); verify(disciplinas, never()).save(any());
        verify(usuarios, never()).save(any()); verify(produtos, never()).save(any());
    }

    @Test void aceitaCadastroValidoEPreservaPerfilMesmoComParametroForjado() throws Exception {
        mvc.perform(post("/usuarios/salvar")
                .param("nomeUsuario", "João Pedro").param("emailUsuario", "joao@example.com")
                .param("loginUsuario", "joao.pedro").param("senhaUsuario", "Senha#12345")
                .param("role", "ROLE_ADMIN"))
                .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/login"));
        org.mockito.ArgumentCaptor<br.com.fatecads.fatecads.entity.Usuario> captor =
                org.mockito.ArgumentCaptor.forClass(br.com.fatecads.fatecads.entity.Usuario.class);
        verify(usuarios).save(captor.capture());
        org.junit.jupiter.api.Assertions.assertEquals("ROLE_USER", captor.getValue().getRole());
    }

    @Test void rejeitaImagensFalsasESvgMesmoComMimeDeclaradoComoPng() throws Exception {
        String[] campos = {"descricaoProduto", "valorProduto", "unidadeProduto", "marcaProduto"};
        String[] valores = {"Caderno", "12.50", "Unidade", "Marca"};
        mvc.perform(multipart("/produtos/salvar")
                .file(new MockMultipartFile("imagem", "ataque.png", "image/png", "<svg onload=alert(1)>".getBytes()))
                .param(campos[0], valores[0]).param(campos[1], valores[1])
                .param(campos[2], valores[2]).param(campos[3], valores[3]))
                .andExpect(status().isOk()).andExpect(view().name("produto/formularioProduto"))
                .andExpect(model().hasErrors());
        verify(produtos, never()).save(any());
    }

    @Test void aceitaPngERejeitaImagemAcimaDeCincoMegabytes() throws Exception {
        byte[] png = {(byte) 0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a};
        mvc.perform(multipart("/produtos/salvar")
                .file(new MockMultipartFile("imagem", "foto.png", "image/png", png))
                .param("descricaoProduto", "Caderno").param("valorProduto", "12.50")
                .param("unidadeProduto", "Unidade").param("marcaProduto", "Marca"))
                .andExpect(status().is3xxRedirection());
        verify(produtos).save(any(Produto.class));
        byte[] grande = new byte[5_000_001];
        mvc.perform(multipart("/produtos/salvar")
                .file(new MockMultipartFile("imagem", "grande.png", "image/png", grande))
                .param("descricaoProduto", "Caderno").param("valorProduto", "12.50")
                .param("unidadeProduto", "Unidade").param("marcaProduto", "Marca"))
                .andExpect(status().isOk()).andExpect(model().hasErrors());
        verify(produtos, times(1)).save(any());
    }

    @Test void rejeitaJsonDePedidosEComprasSemChamarServico() throws Exception {
        mvc.perform(post("/pedidos").contentType("application/json")
                .content("{\"aluno\":{\"idAluno\":1},\"itens\":[{\"produto\":{\"idProduto\":1},\"quantidade\":0}]}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/compras/finalizar").contentType("application/json")
                .content("{\"itens\":[{\"produto\":{\"idProduto\":1},\"quantidade\":1001}]}"))
                .andExpect(status().isBadRequest());
        verify(pedidos, never()).salvarPedido(any());
        verify(pedidos, never()).salvarCompra(any(), any());
    }
}
