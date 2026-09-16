# E-Lixo Zero

Sistema web para facilitar o descarte correto de resíduos eletrônicos em Santa Rita do Sapucaí - MG e região.

## Sobre o Projeto

O E-Lixo Zero conecta cidadãos, pontos de coleta e gestores para promover o descarte consciente de resíduos eletrônicos e contribuir com a preservação ambiental.

A plataforma permite localizar pontos de coleta (inclusive ordenados pela distância até o usuário), solicitar coletas residenciais, acompanhar agendamentos e receber notificações sobre ações de descarte sustentável.

## Objetivos

- Incentivar o descarte correto de resíduos eletrônicos
- Facilitar o acesso a pontos de coleta
- Permitir o agendamento de coletas residenciais
- Promover ações de conscientização ambiental
- Centralizar informações sobre resíduos eletrônicos

## Funcionalidades

### Áreas públicas

- Página inicial
- Página "Sobre"
- Página "Como Funciona"
- Visualização de pontos de coleta, com busca por cidade e ordenação por proximidade ("Usar minha localização")
- Consulta de tipos de resíduos
- Cadastro de usuários
- Login de usuários

### Áreas restritas

- Dashboard personalizado
- Solicitação de coleta
- Visualização das coletas realizadas
- Consulta de resíduos
- Central de notificações (inclui notificação de boas-vindas automática no cadastro)
- Perfil do usuário

## Tecnologias Utilizadas

- **Front-end:** Angular 21, TypeScript, SCSS, HTML5
- **Back-end:** Java 17, Spring Boot 3.5+, Spring Security (BCrypt), JWT
- **Banco de dados:** PostgreSQL
- **Ferramentas:** Maven, Node.js, npm, Git

## Estrutura do Projeto

```
E-Lixo-Zero/
├── e-lixo-zero/
│   ├── BackEnd/
│   │   ├── src/main/java/br/fai/lds/e_lixo_zero/
│   │   │   ├── configuration/
│   │   │   ├── controller/
│   │   │   ├── domain/
│   │   │   ├── dto/
│   │   │   ├── exceptions/
│   │   │   ├── ports_and_adapters/
│   │   │   └── security/
│   │   └── src/main/resources/
│   │       └── lds-db-scripts/   (scripts de criação e carga inicial)
│   └── FrontEnd/
│       ├── src/app/
│       │   ├── core/
│       │   ├── layouts/
│       │   ├── models/
│       │   ├── pages/
│       │   ├── services/
│       │   └── shared/
│       ├── public/images/
│       └── angular.json
└── docs/                          (documentação do projeto)
```

## Pré-requisitos

- Java 17+
- Maven
- PostgreSQL 14+
- Node.js 20+
- npm
- Angular CLI

## Como Executar

### 1. Configurar o banco de dados

1. Crie um banco de dados chamado `elixozero` no PostgreSQL.
2. Ajuste as credenciais em `e-lixo-zero/BackEnd/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/elixozero
spring.datasource.username=postgres
spring.datasource.password=SUA_SENHA
```

3. Execute os scripts SQL em `src/main/resources/lds-db-scripts/`, nesta ordem:
   - `create-tables-postgres.sql` — cria as tabelas (recria do zero, apaga dados anteriores)
   - `insert-data-postgres-basic.sql` — carga inicial: usuários de exemplo, tipos de resíduo, pontos de coleta em Santa Rita do Sapucaí e Cachoeira de Minas, coletas e notificações

Usuários de exemplo (senha `123456` para ambos):

- `joao@gmail.com` — Santa Rita do Sapucaí
- `maria@gmail.com` — Cachoeira de Minas

### 2. Iniciar o back-end

```bash
cd e-lixo-zero/BackEnd
./mvnw spring-boot:run
```

A API estará disponível em `http://localhost:8087`.

### 3. Iniciar o front-end

```bash
cd e-lixo-zero/FrontEnd
npm install
ng serve
```

A aplicação estará disponível em `http://localhost:4200`.

## Endpoints Principais

- `POST /api/users` — cadastro de usuários (gera notificação de boas-vindas)
- `POST /api/users/login` — autenticação (retorna JWT)
- `GET /api/collection-points` — lista pontos de coleta
- `GET /api/collection-points/city/{city}` — busca pontos por cidade/bairro/nome
- `GET /api/collection-points/nearby?lat=..&lng=..` — pontos ordenados por distância
- `GET /api/waste-types` — lista tipos de resíduos
- `GET /api/pickups` — lista coletas do usuário autenticado
- `POST /api/pickups` — solicita uma nova coleta
- `PUT /api/pickups/{id}/status` — atualiza o status de uma coleta
- `GET /api/notifications` — lista notificações do usuário
- `PUT /api/notifications/{id}/mark-read` — marca notificação como lida

## Licença

Este projeto foi desenvolvido exclusivamente para fins acadêmicos.
