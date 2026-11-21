package br.com.fatecads.fatecads;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FatecadsApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void contextLoads() {
	}

	@Test
	void cadastroInvalidoMostraErroAbaixoDoCampo() throws Exception {
		mockMvc.perform(post("/usuarios/salvar")
				.param("nomeUsuario", "<script>alert(1)</script>")
				.param("emailUsuario", "teste@example.com")
				.param("loginUsuario", "teste_seguro")
				.param("telefoneUsuario", "11999999999")
				.param("senhaUsuario", "senha-forte-123")
				.param("role", "ROLE_ADMIN"))
				.andExpect(status().isOk())
				.andExpect(view().name("usuario/formularioUsuario"))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Use apenas letras e pontuação de nome.")))
				.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("value=\"<script>"))))
				.andExpect(model().attribute("usuario", org.hamcrest.Matchers.hasProperty("role", org.hamcrest.Matchers.is("ROLE_USER"))));
	}

}
