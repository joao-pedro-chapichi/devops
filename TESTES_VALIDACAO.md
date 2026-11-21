# Testes de validação de entradas

## Como executar

No Windows, na pasta do projeto:

```powershell
.\mvnw.cmd verify
```

Esse comando executa os testes e gera um relatório HTML em `target/reports/surefire.html`. Abra esse arquivo no navegador para ver o total de testes, falhas e erros, além dos resultados de cada classe e método. Os resultados detalhados também ficam em `target/surefire-reports/` nos formatos TXT e XML.

Os testes usam o banco H2 em memória configurado em `src/test/resources/application.properties`. Não precisam do PostgreSQL local e não gravam dados no banco de produção. O teste de cadastro válido usa uma transação revertida ao terminar.

## O que foi verificado

| Área | Casos automatizados | Resultado esperado |
| --- | --- | --- |
| Alunos | CPF com dígitos verificadores inválidos e curso ausente | Formulário retornado com erro abaixo do CPF |
| Cursos | HTML no nome, carga horária zero e texto onde deveria haver número | Erros específicos abaixo dos campos |
| Cursos | Dados válidos | Cadastro salvo no banco de teste |
| Disciplinas | Sigla com HTML e seleções ausentes | Erros na sigla, curso e professor |
| Professores | Telefone com letras | Erro abaixo do telefone |
| Usuários | HTML no nome e tentativa de enviar `ROLE_ADMIN` pelo formulário | Erro no nome; perfil continua `ROLE_USER` |
| Produtos | Arquivo SVG com código no lugar de imagem permitida | Upload rejeitado com erro abaixo do arquivo |
| Pedidos | Aluno inválido | HTTP 400 com indicação do campo `aluno` |
| Loja | Carrinho vazio | HTTP 400 com mensagem clara |
| Recuperação de senha | Email com conteúdo malicioso | HTTP 400 com indicação do campo `email` |
| Validação unitária | Conteúdo como `<script>`, limites numéricos, CPF e caracteres legítimos | Entradas perigosas são rejeitadas; nomes com acento e marcas como `H&M` são aceitos |

Os testes de integração estão em `ValidacaoHttpIntegrationTests.java` e `FatecadsApplicationTests.java`. Eles enviam requisições HTTP simuladas pelo MockMvc e verificam a resposta e o HTML produzido pelo Thymeleaf. Os testes unitários estão em `ValidacaoEntradaTests.java`.

Na última execução, **16 testes passaram, sem falhas ou erros (100% de sucesso)**.
