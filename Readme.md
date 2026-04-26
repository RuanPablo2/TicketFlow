# 🎫 TicketFlow Microservices API

![Java](https://img.shields.io/badge/Java-21-blue?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.x-brightgreen?style=for-the-badge&logo=spring)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-Event_Driven-FF6600?style=for-the-badge&logo=rabbitmq)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-4169E1?style=for-the-badge&logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker)
![JWT](https://img.shields.io/badge/Security-JWT_Auth-black?style=for-the-badge&logo=jsonwebtokens)
![CI/CD](https://img.shields.io/badge/CI%2FCD-GitHub_Actions-2088FF?style=for-the-badge&logo=github-actions)
![Oracle Cloud](https://img.shields.io/badge/Cloud-Oracle_OCI-F80000?style=for-the-badge&logo=oracle)

Sistema robusto de gerenciamento de chamados de suporte (Help Desk) construído sob uma **Arquitetura de Microsserviços**. O projeto simula um ambiente corporativo real, utilizando mensageria assíncrona para envio de notificações, roteamento centralizado via API Gateway e segurança granular baseada em níveis de acesso (RBAC).

🔗 **Acesse o sistema em Produção:** https://ticketflow-web.netlify.app

## ☁️ Arquitetura de Nuvem e DevOps (Produção)

O TicketFlow foi implantado em um ambiente de produção real, garantindo alta disponibilidade, segurança e deploy automatizado.

- **Infraestrutura em Nuvem:** Hospedado em um servidor Linux (Ubuntu) na **Oracle Cloud Infrastructure (OCI)**.
- **Orquestração de Contêineres:** Todo o ecossistema (Bancos de dados, Brokers e Microsserviços) roda isolado e orquestrado pelo **Docker Compose** utilizando redes internas (Bridge Network) para comunicação segura.
- **Proxy Reverso e Segurança:** \* O tráfego de internet é recebido por um servidor **Nginx**, que atua como proxy reverso roteando as requisições para o API Gateway.
  - Criptografia de ponta a ponta (HTTPS) garantida via certificado SSL/TLS gerado automaticamente pelo **Certbot (Let's Encrypt)**.
- **Integração e Entrega Contínuas (CI/CD):**
  - Pipeline automatizado construído com **GitHub Actions**.
  - A cada _push_ na branch `main`, um _runner_ acessa o servidor remoto via SSH, realiza o pull do código, recompila os artefatos com o Maven, recria as imagens Docker e reinicia os serviços sem a necessidade de intervenção manual.

## 📐 Estrutura dos Microsserviços

A aplicação foi desenhada para ser escalável, resiliente e seguir o padrão de responsabilidade única.

```text
📦 ticketflow-ecosystem
 ┣ 📂 api-gateway          # Ponto de entrada único. Resolve CORS e roteia requisições dinâmicas.
 ┣ 📂 ticket-service       # Core da aplicação. Gere JWT, usuários, tickets, regras e banco de dados.
 ┣ 📂 notification-service # Consumer assíncrono. Ouve filas do RabbitMQ e dispara e-mails.
 ┣ 📜 docker-compose.yml   # Orquestração do ambiente de desenvolvimento.
 ┗ 📜 docker-compose.prod.yml # Orquestração otimizada para o ambiente de Produção.
```

## 🛠 Tecnologias e Boas Práticas

- **Ecosistema Spring:** Spring Boot, Spring Web, Spring Data JPA, Spring Security, Spring Cloud Gateway.
- **Mensageria:** RabbitMQ (Comunicação _Event-Driven_).
- **Segurança:** Autenticação Stateless com JWT e controle de permissões por roles (`CLIENT`, `SUPPORT`, `ADMIN`).
- **Banco de Dados:** PostgreSQL (ORM via Hibernate).
- **Templates:** Thymeleaf (Renderização dinâmica de e-mails em HTML).
- **Documentação:** Springdoc OpenAPI (Swagger UI).
- **Infraestrutura & DevOps**: Docker, Nginx, GitHub Actions, Oracle Cloud.

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
| `GET`  | `/api/tickets/my-queue`      | Retorna a fila de trabalho (tickets atribuídos ao atendente logado).     | 🔒 `SUPPORT`          |
| `PUT`  | `/api/tickets/{id}/assign`   | Atendente assume a autoria/responsabilidade pelo ticket.                 | 🔒 `SUPPORT`          |
| `PUT`  | `/api/tickets/{id}/priority` | Atualiza prioridade (`LOW`, `MEDIUM`, `HIGH`, `URGENT`).                 | 🔒 `SUPPORT`, `ADMIN` |
| `PUT`  | `/api/tickets/{id}/status`   | Atualiza status (`OPEN`, `IN_PROGRESS`, `WAITING_CUSTOMER`, `RESOLVED`). | 🔒 `SUPPORT`, `ADMIN` |
| `GET`  | `/api/tickets/stats`         | Retorna contagem global de chamados agrupados por status para dashboard. | 🔒 `ADMIN`            |
| `GET`  | `/api/tickets//stats/agents` | Retorna métricas de resolução e carga de trabalho por atendente.         | 🔒 `ADMIN`            |
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

**2. Configuração de Variáveis (Ambiente)**

Crie os arquivos .env na raiz do projeto com as credenciais do banco e configurações de SMTP.

**3. Executar via Docker Compose**

Para subir todo o ecossistema rapidamente (banco, rabbitMQ e as APIS) na sua máquina, execute:

```bash
docker compose up -d --build
```

O API Gateway estará disponível em `http://localhost:8081.`

O painel do RabbitMQ estará disponível em `http://localhost:15672` (guest/guest).

## 👨‍💻 Autor

Desenvolvido por Ruan Pablo (https://github.com/RuanPablo2). Feedbacks e contribuições são bem-vindos!
