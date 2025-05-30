
---


# 🔒 Sistema de Logística e Autenticação - Backend

![Java](https://img.shields.io/badge/Java-17%2B-007396?logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.0-6DB33F?logo=spring)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15%2B-4169E1?logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-20.10%2B-2496ED?logo=docker)
![Maven](https://img.shields.io/badge/Maven-3.8%2B-EF3B24?logo=apache-maven)
![License](https://img.shields.io/badge/Licença-MIT-blue)

Um backend robusto, escalável e seguro, construído com **Spring Boot** e **Spring Security**, projetado para gerenciar autenticação de usuários, operações logísticas, carteiras digitais, notificações e integrações empresariais. O sistema utiliza **JWT** para autenticação, **PostgreSQL** para persistência de dados e **Docker** para implantação containerizada. Ele suporta funcionalidades como gerenciamento de usuários, endereços, remessas, cupons, notificações por e-mail e integração com serviços de pagamento (Stripe).

---- O sistema está em desenvolvimento, pode ocorrer mudanças na estrutura/projeto e haver bugs! ----

## 📑 Índice

- [Visão Geral](#-visão-geral)
- [Funcionalidades](#-funcionalidades)
- [Arquitetura](#-arquitetura)
- [Pré-requisitos](#-pré-requisitos)
- [Instalação](#-instalação)
- [Configuração de Ambiente](#⚙️-configuração-de-ambiente)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [Endpoints da API](#-endpoints-da-api)
- [Fluxo de Segurança](#🛡️-fluxo-de-segurança)
- [Melhores Práticas](#-melhores-práticas)
- [Monitoramento e Logging](#-monitoramento-e-logging)
- [Testes](#-testes)
- [Implantação](#-implantação)
- [Integrações Externas](#-integrações-externas)
- [Exemplos de Uso](#-exemplos-de-uso)
- [Contribuição](#-contribuição)
- [FAQ](#-perguntas-frequentes-faq)
- [Licença](#-licença)
- [Contato](#-contato)

## 🌟 Visão Geral

O **Sistema de Logística e Autenticação** é uma solução backend projetada para suportar aplicações de e-commerce e logística, com foco em segurança, escalabilidade e facilidade de integração. Ele oferece uma API RESTful para autenticação de usuários e empresas, gerenciamento de endereços, carteiras digitais, remessas, cupons e notificações. A arquitetura modular, combinada com práticas modernas de desenvolvimento, garante robustez e manutenção simplificada.

## 🚀 Funcionalidades

- **Autenticação Segura**: Suporte a login de usuários e empresas com JWT, incluindo logout com blacklist de tokens.
- **Gerenciamento de Usuários**: Registro, atualização e consulta de perfis com controle de acesso baseado em papéis (ROLE_USER, ROLE_ADMIN).
- **Gerenciamento de Endereços**: Criação, atualização e validação de endereços com suporte a CEP.
- **Carteira Digital**: Depósitos e gerenciamento de saldo com validação de transações.
- **Remessas**: Criação e rastreamento de remessas com status (e.g., PENDING, SHIPPED, DELIVERED).
- **Notificações**: Envio de e-mails para recuperação de senha e comunicações personalizadas.
- **Cupons**: Geração e validação de cupons promocionais com tipos configuráveis.
- **Integração com Pagamentos**: Suporte a pagamentos via Stripe para transações seguras.
- **Gerenciamento Empresarial**: Endpoints dedicados para registro e autenticação de empresas.
- **Logs Detalhados**: Logging estruturado com SLF4J para monitoramento e depuração.
- **Containerização**: Implantação facilitada com Docker e Docker Compose.
- **Cache**: Configuração de cache para otimizar desempenho em consultas frequentes.

## 🏛 Arquitetura

O sistema segue uma **arquitetura em camadas**, garantindo separação de responsabilidades e escalabilidade:

- **Controllers**: Gerenciam requisições HTTP, delegando lógica para serviços.
- **Services**: Contêm a lógica de negócios, interagindo com repositórios e serviços externos.
- **Repositories**: Abstraem o acesso a dados com Spring Data JPA.
- **Entities**: Mapeiam tabelas do banco de dados com anotações JPA.
- **DTOs**: Objetos de transferência de dados para encapsulamento e validação.
- **Mappers**: Conversão entre entidades e DTOs usando MapStruct.
- **Security**: Autenticação e autorização baseadas em JWT com Spring Security.
- **Configuration**: Configurações centralizadas para cache, segurança e integrações externas (Stripe, WebClient).
- **Utilities**: Funções auxiliares, como geração de códigos de cupom e formatação de e-mails.

O projeto utiliza **Docker Compose** para orquestrar o banco de dados PostgreSQL e o contêiner da aplicação, garantindo consistência entre ambientes.

## 📋 Pré-requisitos

- **Java JDK 17+**
- **Apache Maven 3.8+**
- **Docker 20.10+**
- **Docker Compose 2.12+**
- **PostgreSQL 15+**
- **IDE**: IntelliJ IDEA, VS Code ou equivalente
- **Ferramentas de Teste**: Postman, curl ou Insomnia
- **Opcional**: pgAdmin ou DBeaver para gerenciamento do banco de dados

## 🛠 Instalação

1. **Clonar o Repositório**:
   ```bash
   git clone https://github.com/seu-usuario/shipping-system.git
   cd shipping-system
   ```

2. **Configurar Variáveis de Ambiente**:
   Copie o arquivo de exemplo e edite conforme necessário:
   ```bash
   cp shipping/src/main/resources/application.properties.example shipping/src/main/resources/application.properties
   ```

3. **Iniciar o Banco de Dados com Docker**:
   ```bash
   cd infrastructure/docker
   docker-compose up -d
   ```

4. **Construir e Executar a Aplicação**:
   ```bash
   cd ../../shipping
   mvn clean install
   mvn spring-boot:run
   ```

A aplicação estará disponível em `http://localhost:8080`.

## ⚙️ Configuração de Ambiente

As configurações são definidas em `shipping/src/main/resources/application.properties`. Exemplo:

```properties
# Banco de Dados
spring.datasource.url=jdbc:postgresql://localhost:5432/shipping_db
spring.datasource.username=authuser
spring.datasource.password=securepassword
spring.jpa.hibernate.ddl-auto=update

# JWT
jwt.secret=SuaChaveSecretaMuitoLongaESegura12345!
jwt.expiration=86400000

# E-mail
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=seu-email@gmail.com
spring.mail.password=sua-senha-de-aplicativo
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# Stripe
stripe.api.key=sua-chave-stripe

# Cache
spring.cache.type=redis
spring.redis.host=localhost
spring.redis.port=6379

# Logging
logging.level.org.springframework.security=DEBUG
logging.level.redirex.shipping=DEBUG
```

| Variável                          | Descrição                                  | Valor Padrão                          |
|-----------------------------------|--------------------------------------------|---------------------------------------|
| `spring.datasource.url`           | URL de conexão com o PostgreSQL            | `jdbc:postgresql://localhost:5432/shipping_db` |
| `spring.datasource.username`      | Usuário do banco de dados                  | `authuser`                            |
| `spring.datasource.password`      | Senha do banco de dados                    | `securepassword`                      |
| `jwt.secret`                      | Chave secreta para assinatura JWT          | (Obrigatório, deve ser seguro)        |
| `jwt.expiration`                  | Tempo de expiração do token (ms)           | `86400000` (24 horas)                 |
| `spring.mail.*`                   | Configurações do servidor de e-mail        | (Configurações SMTP, e.g., Gmail)     |
| `stripe.api.key`                  | Chave da API Stripe para pagamentos        | (Obrigatório para funcionalidades de pagamento) |
| `spring.cache.*`                  | Configurações do Redis para cache          | `localhost:6379`                      |

## 🏗 Estrutura do Projeto

```plaintext
├── infrastructure/
│   └── docker/
│       ├── docker-compose.yml       # Configuração do PostgreSQL e aplicação
│       ├── docker-compose.override.yml # Sobrescrita de configurações locais
│       └── Dockerfile              # Definição do contêiner da aplicação
├── shipping/
│   ├── mvnw                        # Wrapper do Maven
│   ├── mvnw.cmd                    # Wrapper do Maven para Windows
│   ├── pom.xml                     # Dependências do Maven
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/redirex/shipping/
│   │   │   │   ├── config/         # Configurações (Segurança, Cache, WebClient)
│   │   │   │   ├── controller/     # Controladores REST (Auth, User, Enterprise, Email)
│   │   │   │   ├── dto/            # Objetos de transferência de dados
│   │   │   │   │   ├── request/    # DTOs para requisições
│   │   │   │   │   └── response/   # DTOs para respostas
│   │   │   │   ├── entity/         # Entidades JPA
│   │   │   │   ├── enums/          # Enums (e.g., CouponType, ShipmentStatus)
│   │   │   │   ├── exception/      # Exceções personalizadas
│   │   │   │   ├── mapper/         # Mapeadores MapStruct
│   │   │   │   ├── repositories/   # Repositórios JPA
│   │   │   │   ├── security/       # Configurações de segurança (JWT, Spring Security)
│   │   │   │   ├── service/        # Lógica de negócios
│   │   │   │   │   └── email/      # Serviços de e-mail
│   │   │   │   └── util/           # Utilitários (e.g., CouponCodeGenerator)
│   │   │   └── resources/
│   │   │       ├── application.properties  # Configurações da aplicação
│   │   │       └── META-INF/
│   │   │           └── spring.factories    # Configurações do Spring
│   │   └── test/                   # Testes unitários e de integração
│   └── target/                     # Classes compiladas e fontes geradas
└── LICENSE                         # Arquivo de licença MIT
```

## 🌐 Endpoints da API

### Autenticação (`/public/auth`)
| Método | Endpoint                    | Descrição                           | Corpo da Requisição              | Resposta                        |
|--------|-----------------------------|-------------------------------------|----------------------------------|---------------------------------|
| `POST` | `/public/auth/login`        | Autentica um usuário                | `AuthRequestDTO`                 | `AuthResponseDTO` (token JWT)   |
| `POST` | `/public/auth/login/enterprise` | Autentica uma empresa           | `AuthRequestDTO`                 | `AuthResponseDTO` (token JWT)   |
| `POST` | `/public/auth/logout`       | Invalida o token JWT                | Nenhum (cabeçalho Authorization) | Mensagem de sucesso             |

### Gerenciamento de Usuários (`/public/user` e `/api/user`)
| Método | Endpoint                              | Descrição                           | Corpo da Requisição              | Resposta                        |
|--------|---------------------------------------|-------------------------------------|----------------------------------|---------------------------------|
| `POST` | `/public/user/register`               | Registra um novo usuário            | `RegisterUserDTO`                | `UserResponse`                  |
| `POST` | `/public/user/forgot-password`        | Solicita redefinição de senha       | `ForgotPasswordDTO`              | Mensagem de sucesso             |
| `POST` | `/public/user/reset-password`         | Redefine a senha do usuário         | `ResetPasswordDTO`               | Mensagem de sucesso             |
| `POST` | `/public/user/created-address`        | Cria um novo endereço               | `CreateAddressRequest`           | `AddressResponse`               |
| `PUT`  | `/public/user/update-address/{zipcode}` | Atualiza um endereço              | `AddressDTO`                     | `AddressResponse`               |
| `GET`  | `/api/user/{id}`                     | Consulta usuário por ID             | Nenhum                           | `UserResponse`                   |
| `PUT`  | `/api/user/{id}/profile`             | Atualiza perfil do usuário          | `RegisterUserDTO`                | `UserResponse`                   |

### Notificações por E-mail (`/email`)
| Método | Endpoint         | Descrição                           | Corpo da Requisição              | Resposta                        |
|--------|------------------|-------------------------------------|----------------------------------|---------------------------------|
| `POST` | `/email/send`    | Envia notificação por e-mail        | `UserEmailDetailsUtil`           | Mensagem de sucesso ou erro     |

### Exemplo de Requisição (Registro de Usuário)
```http
POST /public/user/register
Content-Type: application/json

{
  "fullname": "João Silva",
  "email": "joao.silva@exemplo.com",
  "password": "SenhaSegura123!",
  "cpf": "12345678901",
  "phone": "11987654321",
  "address": "Rua Principal, 123",
  "complement": "Apto 4B",
  "city": "São Paulo",
  "state": "SP",
  "zipcode": "12345678",
  "country": "Brasil",
  "occupation": "Desenvolvedor"
}

# Resposta
HTTP/1.1 201 Created
{
  "id": 1,
  "fullname": "João Silva",
  "email": "joao.silva@exemplo.com",
  "cpf": "12345678901",
  "phone": "11987654321",
  "address": "Rua Principal, 123",
  "complement": "Apto 4B",
  "city": "São Paulo",
  "state": "SP",
  "zipcode": "12345678",
  "country": "Brasil",
  "occupation": "Desenvolvedor",
  "role": "ROLE_USER"
}
```

## 🛡️ Fluxo de Segurança

1. **Registro**: Usuários ou empresas registram-se com validação de campos e senhas criptografadas (BCrypt).
2. **Autenticação**: Credenciais são validadas, gerando um token JWT com expiração configurável.
3. **Autorização**: Endpoints protegidos exigem um token JWT válido no cabeçalho `Authorization: Bearer <token>`. O `JwtAuthenticationFilter` verifica a validade do token.
4. **Logout**: Tokens são adicionados a uma blacklist (via `TokenBlacklistService`) para impedir reutilização.
5. **Redefinição de Senha**: Usuários recebem um token de redefinição por e-mail, válido por um período limitado.

## 🔐 Melhores Práticas

- **Segurança**:
  - Senhas criptografadas com `BCryptPasswordEncoder`.
  - Tokens JWT assinados com chave segura e expiração configurável.
  - Sessões stateless com `SessionCreationPolicy.STATELESS`.
  - Controle de acesso baseado em papéis com `@PreAuthorize`.
- **Qualidade de Código**:
  - Arquitetura modular com separação de responsabilidades.
  - Uso de DTOs e MapStruct para mapeamento de dados.
  - Exceções personalizadas com respostas HTTP detalhadas.
- **Desempenho**:
  - Cache configurado com Redis para consultas frequentes.
  - Consultas otimizadas com Spring Data JPA.
- **Validação**:
  - Validação de entrada com Jakarta Bean Validation.
  - Respostas de erro estruturadas com timestamp e códigos de status.
- **Documentação**:
  - Endpoints documentados com exemplos claros.
  - README detalhado com instruções completas.

## 📊 Monitoramento e Logging

- **Logging**: O sistema usa SLF4J com logs detalhados em nível `DEBUG` para `org.springframework.security` e `redirex.shipping`. Logs incluem:
  - Tentativas de login (sucesso e falha).
  - Erros de validação e exceções personalizadas.
  - Operações de banco de dados e chamadas externas (e.g., Stripe, e-mail).
- **Monitoramento**: Integração recomendada com ferramentas como **Prometheus** e **Grafana** para métricas de desempenho e saúde da API.
- **Configuração de Logs**:
  ```properties
  logging.level.org.springframework.security=DEBUG
  logging.level.redirex.shipping=DEBUG
  logging.file.name=logs/shipping-system.log
  ```

## 🧪 Testes

O projeto inclui testes iniciais em `GlobalApplicationTests.java`. Para executar:

```bash
mvn test
```

Para testes manuais de API, use Postman ou curl:

```bash
# Testar registro de usuário
curl -X POST http://localhost:8080/public/user/register \
  -H "Content-Type: application/json" \
  -d '{"fullname":"João Silva","email":"joao.silva@exemplo.com","password":"SenhaSegura123!","cpf":"12345678901","phone":"11987654321","address":"Rua Principal, 123","complement":"Apto 4B","city":"São Paulo","state":"SP","zipcode":"12345678","country":"Brasil","occupation":"Desenvolvedor"}'

# Testar login
curl -X POST http://localhost:8080/public/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"joao.silva@exemplo.com","password":"SenhaSegura123!"}'
```

Para testes automatizados, recomenda-se expandir com **JUnit**, **Mockito** e **Testcontainers** para simular o banco de dados.

## 🚀 Implantação

1. **Construir a Imagem Docker**:
   ```bash
   cd shipping
   docker build -t shipping-system:latest .
   ```

2. **Executar com Docker Compose**:
   ```bash
   cd infrastructure/docker
   docker-compose up -d
   ```

3. **Verificar Implantação**:
   Confirme que a aplicação está rodando em `http://localhost:8080` e que o banco de dados está acessível.

4. **Implantação em Produção**:
   - Configure um servidor de CI/CD (e.g., Jenkins, GitHub Actions) para builds automatizadas.
   - Use um orquestrador como **Kubernetes** para escalabilidade.
   - Monitore com ferramentas como **Prometheus** e **Grafana**.

## 🔗 Integrações Externas

- **Stripe**: Integração para pagamentos via `StripeService`, configurado com `stripe.api.key`.
- **E-mail**: Suporte a envio de e-mails via SMTP (e.g., Gmail) para notificações e redefinição de senha.
- **Redis**: Cache configurado para melhorar o desempenho de consultas frequentes.
- **WebClient**: Configurado em `WebClientConfig` para chamadas a APIs externas.

## 📚 Exemplos de Uso

### Registrar um Usuário
```bash
curl -X POST http://localhost:8080/public/user/register \
  -H "Content-Type: application/json" \
  -d '{"fullname":"Maria Oliveira","email":"maria.oliveira@exemplo.com","password":"SenhaSegura456!","cpf":"98765432100","phone":"11912345678","address":"Avenida Central, 456","complement":"Casa 2","city":"Rio de Janeiro","state":"RJ","zipcode":"87654321","country":"Brasil","occupation":"Gerente"}'
```

### Autenticar e Obter Token
```bash
curl -X POST http://localhost:8080/public/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"maria.oliveira@exemplo.com","password":"SenhaSegura456!"}'
```

### Criar Endereço
```bash
curl -X POST http://localhost:8080/public/user/created-address \
  -H "Content-Type: application/json" \
  -d '{"street":"Rua Nova, 789","complement":"Bloco B","city":"Curitiba","state":"PR","zipcode":"54321098","country":"Brasil"}'
```

## 🤝 Contribuição

1. Faça um fork do repositório.
2. Crie uma branch para sua feature (`git checkout -b feature/nova-funcionalidade`).
3. Commit suas alterações (`git commit -m 'Adiciona nova funcionalidade'`).
4. Push para a branch (`git push origin feature/nova-funcionalidade`).
5. Abra um Pull Request com uma descrição detalhada.

## ❓ Perguntas (FAQ)

**P: Como configurar o envio de e-mails?**  
R: Configure as propriedades `spring.mail.*` em `application.properties` com as credenciais do seu provedor SMTP (e.g., Gmail). Use uma senha de aplicativo para serviços como Gmail.

**P: Como lidar com erros de autenticação?**  
R: Verifique os logs em `logs/shipping-system.log` e as respostas HTTP, que incluem detalhes como `timestamp`, `status` e `message`.

**P: Posso usar outro banco de dados?**  
R: Sim, mas será necessário ajustar o `spring.datasource.url` e o driver correspondente no `pom.xml`.

**P: Como testar endpoints protegidos?**  
R: Obtenha um token JWT via `/public/auth/login` e inclua-o no cabeçalho `Authorization: Bearer <token>`.

## 📄 Licença

Distribuído sob a licença MIT. Veja o arquivo `LICENSE` para mais detalhes.

## ✉️ Contato

**Mantenedor**: Felipe Panosso  
**E-mail**: panossodev@gmail.com  
**LinkedIn**: [linkedin.com/in/felipe-panosso](https://linkedin.com/in/felipe-panosso)  

---
