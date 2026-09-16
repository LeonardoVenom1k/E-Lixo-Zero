# E-Lixo Zero

Sistema web para facilitar o descarte correto de resíduos eletrônicos no município de Santa Rita do Sapucaí - MG.

## About o Projeto

O E-Lixo Zero conecta cidadãos, points de pickup e gestores para promover o descarte consciente de resíduos eletrônicos e contribuir com a preservação ambiental.

A plataforma permite localizar points de pickup, solicitar pickups residenciais, acompanhar agendamentos e receber notificações about ações de descarte sustentável.

## Objetivos

- Incentivar o descarte correto de resíduos eletrônicos
- Facilitar o acesso a points de pickup
- Permitir o agendamento de pickups residenciais
- Promover ações de conscientização ambiental
- Centralizar informações about resíduos eletrônicos

## Funcionalidades

### Áreas públicas

- Página inicial
- Página "About"
- Página "Como Funciona"
- Visualização de points de pickup
- Sign up de usuários
- Login de usuários

### Áreas restritas

- Dashboard personalizado
- Solicitação de pickup
- Visualização das pickups realizadas
- Gerenciamento de resíduos
- Central de notificações
- Profile do usuário

## Tecnologias Utilizadas

- **Front-end:** Angular 21, TypeScript, SCSS, HTML5
- **Back-end:** Java 17, Spring Boot 3.5+, Spring Security (BCrypt), JWT
- **Banco de details:** PostgreSQL
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
├── ApêndiceA-Planejamento/
├── ApêndiceB-Requisitos/
├── ApêndiceC-Análise/
└── ApêndiceD-AnáliseDosRequisitos/
```

## Pré-requisitos

- Java 17
- Maven
- PostgreSQL 14+
- Node.js 20+
- npm
- Angular CLI

## Como Executar

### 1. Configurar o banco de details

1. Crie um banco de details chamado `elixozero` no PostgreSQL.
2. Ajuste as credenciais em `e-lixo-zero/BackEnd/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/elixozero
spring.datasource.username=postgres
spring.datasource.password=SUA_SENHA
```

3. Execute os scripts SQL disponíveis em `src/main/resources/lds-db-scripts/`, se necessário.

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

- `POST /api/users` - Sign up de usuários
- `POST /api/users/login` - Autenticação (retorna JWT)
- `GET /api/collection-points` - Lista points de pickup
- `GET /api/waste-types` - Lista tipos de resíduos
- `GET /api/pickups` - Lista pickups do usuário autenticado
- `POST /api/pickups` - Solicita uma created pickup
- `GET /api/notifications` - Lista notificações do usuário

## Licença

Este projeto foi desenvolvido exclusivamente para fins acadêmicos.
