# Plano de testes E2E — E-Lixo Zero

## Objetivo

Preparar e automatizar os fluxos de login e cadastro de cidadão do sistema E-Lixo Zero, seguindo o fluxo do Plano de Testes do PID: selecionar a funcionalidade, executar o cenário, comparar o resultado com o esperado, registrar o resultado e consolidar a cobertura.

## Escopo

Os cenários percorrem as telas Angular em um navegador Chromium e verificam a comunicação HTTP esperada com a API de usuários. As respostas da API são isoladas por cenário para tornar os resultados repetíveis e evitar alterações no banco de dados local. Este conjunto valida os fluxos de interface e o contrato HTTP do front-end; não mede cobertura de linhas Java nem substitui testes de integração do back-end com PostgreSQL.

## Pré-condições

- Node.js compatível com Angular 21 e npm instalados.
- Dependências do front-end instaladas com `npm ci`.
- Navegador Chromium do Playwright instalado com `npx playwright install chromium`.
- Execução a partir da pasta `e-lixo-zero/FrontEnd`.

## Casos de teste planejados

| ID | Fluxo | Cenário | Resultado esperado |
| --- | --- | --- | --- |
| E2E-LOGIN-01 | Login | Informar credenciais válidas e enviar | A API retorna sucesso, a sessão é armazenada e a aplicação abre o dashboard do cidadão. |
| E2E-LOGIN-02 | Login | Informar credenciais inválidas e enviar | A aplicação permanece na tela de login e informa que o e-mail ou a senha são inválidos. |
| E2E-CAD-01 | Cadastro | Preencher os dados e cadastrar com senhas iguais | A API confirma a criação com HTTP 201 sem corpo; a aplicação encaminha para o login e informa que o cadastro foi realizado. |
| E2E-CAD-02 | Cadastro | Preencher senhas diferentes | A aplicação informa que as senhas não coincidem e não envia a solicitação de cadastro. |
| E2E-CAD-03 | Cadastro | Tentar cadastrar um e-mail já utilizado | A aplicação informa a falha de cadastro e permanece no formulário. |

## Resultados da execução

Execução realizada em 17/11/2026 com Playwright 1.65.1 e Chromium, pelo comando `npm run e2e`.

| ID | Resultado | Evidência |
| --- | --- | --- |
| E2E-LOGIN-01 | Aprovado | Envio do e-mail e senha conferido; dashboard exibido e token de sessão armazenado. |
| E2E-LOGIN-02 | Aprovado | Mensagem de credenciais inválidas exibida; permaneceu na tela de login. |
| E2E-CAD-01 | Aprovado | Campos do cadastro conferidos; resposta HTTP 201 sem corpo levou ao login com confirmação. |
| E2E-CAD-02 | Aprovado | Mensagem de senhas diferentes exibida sem envio da requisição. |
| E2E-CAD-03 | Aprovado | Falha por e-mail já cadastrado exibida; permaneceu no formulário. |

**Resumo:** 5 casos aprovados, 0 reprovados, em aproximadamente 16,5 segundos.

## Análise da cobertura

A cobertura dos cenários planejados foi de **5/5 (100%)**: login, 2/2 casos; cadastro de cidadão, 3/3 casos. Essa métrica representa cobertura dos cenários selecionados, não cobertura de linhas de código. A API foi simulada nos testes; portanto, a suíte não mede o comportamento de controladores, regras de negócio Java, JWT ou PostgreSQL. Esses componentes devem ser cobertos separadamente pelos testes do back-end.
