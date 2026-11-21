package br.com.fatecads.fatecads;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import br.com.fatecads.fatecads.repository.CursoRepository;

@SpringBootTest
@AutoConfigureMockMvc
class ValidacaoHttpIntegrationTests {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private CursoRepository cursos;

    @Test
    @Transactional
    void cursoValidoESalvoNoBancoDeTeste() throws Exception {
        long antes = cursos.count();
        mvc.perform(post("/cursos/salvar").with(user("admin").roles("ADMIN"))
                .param("nomeCurso", "Análise de Sistemas")
                .param("periodoCurso", "Noturno")
                .param("cargaHorariaCurso", "2800"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/cursos/listar"));
        assertEquals(antes + 1, cursos.count());
    }

    @Test
    void alunoInvalidoVoltaAoFormularioComErroNoCpf() throws Exception {
        MvcResult resultado = mvc.perform(post("/alunos/salvar").with(user("admin").roles("ADMIN"))
                .param("nomeAluno", "Ana D'Ávila")
                .param("emailAluno", "ana@example.com")
                .param("telefoneAluno", "11999999999")
                .param("cpfAluno", "11111111111")
                .param("enderecoAluno", "Rua São João, 12")
                .param("raAluno", "RA123"))
                .andExpect(status().isOk())
                .andExpect(view().name("aluno/formularioAluno"))
                .andExpect(model().attributeHasFieldErrors("aluno", "cpfAluno", "curso"))
                .andReturn();
        erroAbaixoDoCampo(resultado, "cpfAluno", "Informe um CPF válido.");
    }

    @Test
    void cursoInvalidoMostraMensagensSemSalvar() throws Exception {
        MvcResult resultado = mvc.perform(post("/cursos/salvar").with(user("admin").roles("ADMIN"))
                .param("nomeCurso", "ADS<script>")
                .param("periodoCurso", "Noturno")
                .param("cargaHorariaCurso", "0"))
                .andExpect(status().isOk())
                .andExpect(view().name("curso/formularioCurso"))
                .andExpect(model().attributeHasFieldErrors("curso", "nomeCurso", "cargaHorariaCurso"))
                .andReturn();
        erroAbaixoDoCampo(resultado, "nomeCurso", "O nome contém caracteres inválidos.");
        erroAbaixoDoCampo(resultado, "cargaHorariaCurso", "A carga horária deve ser maior que zero.");
    }

    @Test
    void numeroNaoNumericoRecebeMensagemEspecifica() throws Exception {
        MvcResult resultado = mvc.perform(post("/cursos/salvar").with(user("admin").roles("ADMIN"))
                .param("nomeCurso", "ADS")
                .param("periodoCurso", "Noturno")
                .param("cargaHorariaCurso", "abc"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("curso", "cargaHorariaCurso"))
                .andReturn();
        erroAbaixoDoCampo(resultado, "cargaHorariaCurso", "Informe uma carga horária numérica válida.");
    }

    @Test
    void disciplinaInvalidaMostraErrosDeSiglaESelecoes() throws Exception {
        MvcResult resultado = mvc.perform(post("/disciplinas/salvar").with(user("admin").roles("ADMIN"))
                .param("nomeDisciplina", "Programação")
                .param("siglaDisciplina", "<POO>")
                .param("cargaHorariaDisciplina", "80"))
                .andExpect(status().isOk())
                .andExpect(view().name("disciplina/formularioDisciplina"))
                .andExpect(model().attributeHasFieldErrors("disciplina", "siglaDisciplina", "curso", "professor"))
                .andReturn();
        erroAbaixoDoCampo(resultado, "siglaDisciplina", "Use de 2 a 10 letras ou números.");
    }

    @Test
    void professorInvalidoMostraErroNoTelefone() throws Exception {
        MvcResult resultado = mvc.perform(post("/professores/salvar").with(user("admin").roles("ADMIN"))
                .param("nomeProfessor", "José Silva")
                .param("telefoneProfessor", "telefone")
                .param("graduacaoProfessor", "Computação")
                .param("rmProfessor", "RM123"))
                .andExpect(status().isOk())
                .andExpect(view().name("professor/formularioProfessor"))
                .andExpect(model().attributeHasFieldErrors("professor", "telefoneProfessor"))
                .andReturn();
        erroAbaixoDoCampo(resultado, "telefone", "Digite 10 ou 11 números, sem pontuação.");
    }

    @Test
    void produtoRejeitaArquivoQueNaoEImagem() throws Exception {
        MockMultipartFile arquivo = new MockMultipartFile("imagem", "ataque.svg", "image/svg+xml",
                "<svg onload=alert(1)>".getBytes(StandardCharsets.UTF_8));
        MvcResult resultado = mvc.perform(multipart("/produtos/salvar").with(user("admin").roles("ADMIN"))
                .file(arquivo)
                .param("descricaoProduto", "Caneta Azul")
                .param("valorProduto", "2.50")
                .param("unidadeProduto", "Unidade")
                .param("marcaProduto", "Marca"))
                .andExpect(status().isOk())
                .andExpect(view().name("produto/formularioProduto"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Envie uma imagem PNG, JPEG ou GIF de até 5 MB.")))
                .andReturn();
        erroAbaixoDoCampo(resultado, "imagem", "Envie uma imagem PNG, JPEG ou GIF de até 5 MB.");
    }

    @Test
    void pedidoRejeitaAlunoInvalidoComCampoNaResposta() throws Exception {
        mvc.perform(post("/pedidos").with(user("admin").roles("ADMIN"))
                .contentType("application/json")
                .content("{\"alunoId\":-1,\"itens\":[{\"produtoId\":1,\"quantidade\":1}]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campo").value("aluno"));
    }

    @Test
    void compraRejeitaCarrinhoVazio() throws Exception {
        mvc.perform(post("/compras/finalizar").with(user("admin").roles("ADMIN"))
                .contentType("application/json")
                .content("{\"itens\":[]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("O carrinho deve ter de 1 a 100 itens."));
    }

    @Test
    void recuperacaoRejeitaEmailMalicioso() throws Exception {
        mvc.perform(post("/recuperacao/solicitar").param("email", "<script>@example.com"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campo").value("email"));
    }

    private void erroAbaixoDoCampo(MvcResult resultado, String id, String mensagem) throws Exception {
        String html = resultado.getResponse().getContentAsString();
        int posicaoCampo = html.indexOf("id=\"" + id + "\"");
        int posicaoMensagem = html.indexOf(mensagem);
        assertTrue(posicaoCampo >= 0 && posicaoMensagem > posicaoCampo,
                "A mensagem deve aparecer depois do campo " + id);
        String entreCampoEMensagem = html.substring(posicaoCampo, posicaoMensagem);
        assertTrue(entreCampoEMensagem.contains("<small class=\"erro-campo\""),
                "A mensagem deve estar no elemento de erro abaixo do campo " + id);
    }
}
