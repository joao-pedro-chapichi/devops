# Tema 19 — teste de segurança: validação de entradas

## Objetivo e conceito

O Tema 19 da atividade pede verificar se os formulários tratam entradas inesperadas, caracteres especiais e conteúdo potencialmente perigoso. Validar entradas significa conferir formato e limites (sintaxe), além de verificar se o valor faz sentido para a operação (semântica). A verificação precisa ocorrer no servidor, pois uma requisição pode ignorar as restrições do navegador. Ao devolver um valor ao HTML, ele deve aparecer como texto, sem ser executado como código.

Referências de pesquisa: [OWASP — Input Validation Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Input_Validation_Cheat_Sheet.html) e [OWASP — Cross Site Scripting Prevention Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Cross_Site_Scripting_Prevention_Cheat_Sheet).

## Ferramentas utilizadas

- **JUnit 5 (Jupiter):** organiza os casos com `org.junit.jupiter.api.Test` e verifica os resultados com asserções. [Documentação do JUnit 5](https://docs.junit.org/5.13.1/user-guide/index.html).
- **Spring Boot Test e MockMvc:** enviam requisições HTTP simuladas aos controllers e verificam status, modelo, redirecionamentos e HTML renderizado, sem abrir um navegador. [Documentação do MockMvc](https://docs.spring.io/spring-framework/reference/testing/mockmvc.html).
- **Bean Validation:** valida campos obrigatórios, formatos, tamanhos e limites no servidor.
- **H2 em memória:** isola os testes do PostgreSQL usado pela aplicação; a configuração está em `src/test/resources/application.properties`.
- **Maven Surefire Report:** transforma os resultados dos testes em um relatório HTML.

## Procedimento e evidências

Os testes unitários estão em `src/test/java/br/com/fatecads/fatecads/ValidacaoEntradaTests.java`. Os testes HTTP e de formulário estão em `ValidacaoHttpIntegrationTests.java` e `FatecadsApplicationTests.java`, na mesma pasta. Eles executam as seguintes verificações:

| Entrada enviada | Resultado esperado | Resultado obtido |
| --- | --- | --- |
| Nome de usuário ou curso contendo `<script>` | Recusar a entrada e mostrar o erro no formulário | Erro exibido; conteúdo não aparece como HTML executável |
| Descrição de produto contendo `<script>` | Recusar a descrição e indicar o campo | Formulário retornado com erro abaixo da descrição |
| Descrição com `&`, nomes acentuados e marca `H&M` | Aceitar caracteres legítimos e escapar `&` na saída HTML | Valores aceitos; `&` renderizado como `&amp;` no atributo HTML |
| Carga horária `0` ou `abc` | Rejeitar valor fora do limite ou não numérico | Mensagem específica abaixo da carga horária |
| CPF com dígitos verificadores incorretos | Rejeitar o cadastro de aluno | Erro abaixo do CPF |
| Sigla com HTML e curso/professor ausentes | Rejeitar a disciplina | Erros nos campos correspondentes |
| Telefone com letras | Rejeitar o cadastro de professor | Erro abaixo do telefone |
| Arquivo SVG contendo código enviado como imagem | Rejeitar o upload | Erro abaixo do campo de imagem |
| Pedido com aluno inválido, compra vazia ou email de recuperação malicioso | Recusar a requisição | HTTP 400 e mensagem indicando o problema |
| Curso com dados válidos | Salvar normalmente | Cadastro confirmado no banco H2 de teste |

O teste de cadastro válido é revertido ao terminar. Os testes não gravam dados no PostgreSQL da aplicação.

## Como executar e demonstrar

No Windows, na pasta do projeto:

```powershell
.\mvnw.cmd verify
```

O comando executa os testes e gera `target/reports/surefire.html`. Abra esse arquivo no navegador para mostrar o total de testes, falhas e erros, além do resultado de cada caso. Os arquivos detalhados em TXT e XML ficam em `target/surefire-reports/`.

**Resultado observado em 25/11/2026:** 17 testes executados, 0 falhas, 0 erros, 0 ignorados e 100% de sucesso. O relatório HTML gerado nesta execução é a evidência desse resultado.

Na apresentação, execute o comando, abra o relatório e mostre um formulário recebendo uma entrada inválida. Explique que a validação acontece no servidor e que a mensagem aparece abaixo do campo, permitindo corrigir o valor.

Para a entrega, publique o código atualizado e este relatório no GitHub, conforme as orientações da atividade. O HTML em `target/reports/` é gerado localmente a cada execução; execute `verify` novamente para demonstrar a evidência atual.

Esses testes exercitam as rotas, validações e templates da aplicação pelo MockMvc. Eles não simulam um navegador real; isso não é exigido pelo Tema 19.
