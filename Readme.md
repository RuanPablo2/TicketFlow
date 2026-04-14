# 🎫 TicketFlow Microservices API

![Java](https://img.shields.io/badge/Java-21-blue?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.x-brightgreen?style=for-the-badge&logo=spring)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-Event_Driven-FF6600?style=for-the-badge&logo=rabbitmq)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-4169E1?style=for-the-badge&logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker)
![JWT](https://img.shields.io/badge/Security-JWT_Auth-black?style=for-the-badge&logo=jsonwebtokens)

Sistema robusto de gerenciamento de chamados de suporte (Help Desk) construído sob uma **Arquitetura de Microsserviços**. O projeto simula um ambiente corporativo real, utilizando mensageria assíncrona para envio de notificações, roteamento centralizado via API Gateway e segurança granular baseada em níveis de acesso (RBAC).

## 📐 Ecossistema e Arquitetura

A aplicação foi desenhada para ser escalável, resiliente e seguir o padrão de responsabilidade única.

```text
📦 ticketflow-ecosystem
 ┣ 📂 api-gateway          # [Porta 8081] Ponto de entrada único. Roteia requisições e oculta as portas internas.
 ┣ 📂 ticket-service       # [Porta 8080] Core da aplicação. Gere JWT, usuários, tickets, regras e banco de dados.
 ┣ 📂 notification-service # [Porta 8082] Consumer assíncrono. Ouve filas do RabbitMQ e dispara e-mails via SMTP.
 ┗ 📜 docker-compose.yml   # Orquestração da infraestrutura (PostgreSQL, RabbitMQ, pgAdmin).
```

## 🛠 Tecnologias e Boas Práticas

- **Ecosistema Spring:** Spring Boot, Spring Web, Spring Data JPA, Spring Security, Spring Cloud Gateway.
- **Mensageria:** RabbitMQ (Comunicação _Event-Driven_).
- **Segurança:** Autenticação Stateless com JWT e controle de permissões por roles (`CLIENT`, `SUPPORT`, `ADMIN`).
- **Banco de Dados:** PostgreSQL (ORM via Hibernate).
- **Templates:** Thymeleaf (Renderização dinâmica de e-mails em HTML).
- **Documentação:** Springdoc OpenAPI (Swagger UI).
- **Infraestrutura:** Docker & Docker Compose.

---

## 🔐 Regras de Acesso e Perfis (Roles)

O sistema garante Isolamento de Dados (Tenant Isolation).

- 🧑‍💻 **CLIENT:** Só visualiza e interage com os **próprios** tickets. Pode abrir, adicionar mensagens públicas e retomar tickets. Limite de 3 tickets abertos simultaneamente.
- 🎧 **SUPPORT:** Visualiza a fila global. Pode assumir tickets, alterar status/prioridade e adicionar notas internas.
- 👑 **ADMIN:** Acesso irrestrito. Único perfil que pode cadastrar novos agentes de suporte na plataforma.

---

## 🔗 Documentação Completa da API (Endpoints)

Todas as requisições devem ser feitas apontando para o **API Gateway** (`http://localhost:8081`).
_Nota: Para rotas protegidas (🔒), envie o Header `Authorization: Bearer <seu_token_jwt>`._

### 👤 Autenticação e Identidade (`/api/auth`)

| Método | Rota                             | Descrição                                        | Permissão  |
| ------ | -------------------------------- | ------------------------------------------------ | ---------- |
| `POST` | `/api/auth/login`                | Autentica o usuário e retorna o Token JWT.       | 🌐 Público |
| `POST` | `/api/auth/register`             | Cria uma nova conta pública com a role `CLIENT`. | 🌐 Público |
| `POST` | `/api/auth/admin/register-staff` | Cadastra novos agentes de suporte ou admins.     | 🔒 `ADMIN` |

### 🎫 Gestão de Tickets (`/api/tickets`)

| Método | Rota                         | Descrição                                                                | Permissão             |
| ------ | ---------------------------- | ------------------------------------------------------------------------ | --------------------- |
| `POST` | `/api/tickets`               | Abre um novo chamado (dispara e-mail assíncrono via RabbitMQ).           | 🔒 Autenticado        |
| `GET`  | `/api/tickets`               | Lista paginada. Clientes veem os próprios; Staff vê a fila global.       | 🔒 Autenticado        |
| `GET`  | `/api/tickets/{id}`          | Retorna detalhes de um ticket específico.                                | 🔒 Autenticado        |
| `GET`  | `/api/tickets/my-queue`      | Retorna a fila de trabalho (tickets atribuídos ao atendente logado).     | 🔒 `SUPPORT`, `ADMIN` |
| `PUT`  | `/api/tickets/{id}/assign`   | Atendente assume a autoria/responsabilidade pelo ticket.                 | 🔒 `SUPPORT`, `ADMIN` |
| `PUT`  | `/api/tickets/{id}/priority` | Atualiza prioridade (`LOW`, `MEDIUM`, `HIGH`, `URGENT`).                 | 🔒 `SUPPORT`, `ADMIN` |
| `PUT`  | `/api/tickets/{id}/status`   | Atualiza status (`OPEN`, `IN_PROGRESS`, `WAITING_CUSTOMER`, `RESOLVED`). | 🔒 `SUPPORT`, `ADMIN` |
| `PUT`  | `/api/tickets/{id}/resume`   | Cliente devolve o ticket ao suporte (volta para `IN_PROGRESS`).          | 🔒 `CLIENT`           |

### 💬 Mensagens e Chat (`/api/tickets/{id}/messages`)

| Método | Rota           | Descrição                                                               | Permissão      |
| ------ | -------------- | ----------------------------------------------------------------------- | -------------- |
| `GET`  | `.../messages` | Histórico do chat. Clientes não enxergam notas internas.                | 🔒 Autenticado |
| `POST` | `.../messages` | Adiciona interação ao chamado. Suporte pode marcar como `internalNote`. | 🔒 Autenticado |

---

## 🚦 Status e Prioridades

O ciclo de vida de um ticket obedece regras estritas:

- **Categorias:** `BUG`, `QUESTION`, `FINANCE`, `ACCESS`.
- **Prioridades:** `LOW`, `MEDIUM`, `HIGH`, `URGENT`. (Tickets resolvidos não podem ter a prioridade alterada).
- **Status:**
  1. `OPEN` (Nasce aqui)
  2. `IN_PROGRESS` (Após um atendente assumir)
  3. `WAITING_CUSTOMER` (Suporte solicita info ao cliente)
  4. `RESOLVED` (Registra data de fechamento. Exige atendente atribuído).

---

## 🛠 Tratamento Global de Exceções

O sistema implementa o padrão `@RestControllerAdvice`, padronizando todas as respostas de erro JSON e evitando o vazamento de stack traces para o Front-end.

| Código Interno            | HTTP Status       | Descrição comum                                                  |
| ------------------------- | ----------------- | ---------------------------------------------------------------- |
| `VALIDATION_ERROR`        | `400 Bad Request` | Falha no Bean Validation (ex: campos nulos ou em branco).        |
| `BUSINESS_RULE_VIOLATION` | `409 Conflict`    | Violação de regra (ex: cliente tentar abrir > 3 tickets).        |
| `UNAUTHORIZED_ACCESS`     | `403 Forbidden`   | Credenciais inválidas ou tentativa de burla de Tenant Isolation. |
| `RESOURCE_NOT_FOUND`      | `404 Not Found`   | ID do Ticket ou Usuário inexistente.                             |

---

## 🚀 Como Rodar o Projeto Localmente

### Pré-requisitos

- Docker e Docker Compose instalados.
- JDK 21+
- Maven

### Passos para Inicialização

**1. Clone o repositório**

```bash
git clone [https://github.com/RuanPablo2/TicketFlow.git](https://github.com/RuanPablo2/TicketFlow.git)
cd TicketFlow
```

**2. Configuração de Infraestrutura**

Na raiz do projeto, suba os serviços de suporte (Banco de Dados, RabbitMQ e pgAdmin):

```bash
docker-compose up -d
```

- **PostgreSQL:** Porta 5432.
- **RabbitMQ:** Porta 5672 (Painel em `http://localhost:15672` com guest/guest).
- **pgAdmin:** Porta 5050 (Acesse com `admin@ticketflow.com` / `admin`).

**3. Configuração de Notificações**

No `notification-service`, configure as variáveis de ambiente para envio de e-mail (SMTP Gmail):

```bash
MAIL_USERNAME=seu-email@gmail.com
MAIL_PASSWORD=sua-senha-de-app-gmail
```

**4. Executar os Microsserviços**

Execute cada um na seguinte ordem:

Ticket Service: `mvn spring-boot:run`

Notification Service: `mvn spring-boot:run`

API Gateway: `mvn spring-boot:run`

## 👨‍💻 Autor

Desenvolvido por Ruan Pablo (https://github.com/RuanPablo2). Feedbacks e contribuições são bem-vindos!
