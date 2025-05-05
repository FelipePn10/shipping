---


# 🔒 Shipping - Sistema de Autenticação e Gerenciamento de Usuários

![Java](https://img.shields.io/badge/Java-17%2B-007396?logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.0-6DB33F?logo=spring)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15%2B-4169E1?logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-20.10%2B-2496ED?logo=docker)
![License](https://img.shields.io/badge/License-MIT-blue)

Um sistema robusto para autenticação e gerenciamento de usuários, implementado com Spring Boot, Spring Security, JSON Web Tokens (JWT) e PostgreSQL. O projeto utiliza Docker para containerização do banco de dados, facilitando a implantação e testes.

## 📑 Índice

- [Recursos Principais](#-recursos-principais)
- [Pré-requisitos](#-pré-requisitos)
- [Instalação](#-instalação)
- [Configuração de Ambiente](#⚙️-configuração-de-ambiente)
- [Tecnologias Utilizadas](#-tecnologias-utilizadas)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [Endpoints da API](#-endpoints-da-api)
- [Fluxo de Segurança](#%EF%B8%8F-fluxo-de-segurança)
- [Melhores Práticas Implementadas](#-melhores-práticas-implementadas)
- [Executando Testes](#-executando-testes)
- [Contribuição](#-contribuição)
- [Licença](#-licença)
- [Contato](#-contato)

## 🚀 Recursos Principais

- ✅ Autenticação segura com JWT (JSON Web Tokens)
- ✅ Registro de usuários com validação de campos obrigatórios
- ✅ Listagem de todos os usuários cadastrados
- ✅ Armazenamento seguro de senhas com BCrypt
- ✅ Configuração de PostgreSQL containerizado com Docker
- ✅ Endpoints públicos para registro e autenticação
- ✅ Logs detalhados para debugging
- ✅ Configuração de CORS para integração com frontends

## 📋 Pré-requisitos

- Java JDK 17+
- Apache Maven 3.8+
- Docker 20.10+
- Docker Compose 2.12+
- PostgreSQL 15+
- IDE de sua preferência (IntelliJ IDEA, VS Code, etc.)
- Postman para testes de API

## 🛠 Instalação

```bash
# 1. Clone o repositório
git clone https://github.com/seu-usuario/shipping-system.git
cd shipping-system

# 2. Configure as variáveis de ambiente
# Crie um arquivo application.properties em shipping/src/main/resources/
cp shipping/src/main/resources/application.properties.example shipping/src/main/resources/application.properties

# 3. Inicie o container do PostgreSQL
cd infrastructure/docker
docker-compose up -d

# 4. Construa e execute a aplicação
mvn clean install
mvn spring-boot:run
```

## ⚙️ Configuração de Ambiente

As configurações são definidas no arquivo `shipping/src/main/resources/application.properties`. Exemplo:

```properties
# Banco de dados
spring.datasource.url=jdbc:postgresql://localhost:5432/shipping_db
spring.datasource.username=authuser
spring.datasource.password=securepassword
spring.jpa.hibernate.ddl-auto=update

# JWT
jwt.secret=SuperSegredoJWT12345!MuitoLongaESegura
jwt.expiration=86400000

# Logs
logging.level.org.springframework.security=DEBUG
logging.level.redirex.shipping=DEBUG
```

| Variável               | Descrição                              | Valor Padrão          |
|------------------------|----------------------------------------|-----------------------|
| `spring.datasource.url`| URL de conexão do PostgreSQL           | jdbc:postgresql://localhost:5432/shipping_db |
| `spring.datasource.username` | Usuário do banco de dados        | authuser              |
| `spring.datasource.password` | Senha do banco de dados          | securepassword        |
| `jwt.secret`           | Chave secreta para assinatura JWT      | (obrigatório)         |
| `jwt.expiration`       | Tempo de expiração do token (ms)       | 86400000 (24h)        |

## 🛠 Tecnologias Utilizadas

### Backend
- **Spring Boot 3.1** - Framework principal
- **Spring Security 6** - Autenticação e autorização
- **JJWT** - Geração e validação de tokens JWT
- **Spring Data JPA** - ORM para acesso ao banco
- **Lombok** - Redução de código boilerplate
- **PostgreSQL Driver** - Conexão com banco

### Banco de Dados
- **PostgreSQL 15** - Banco de dados relacional
- **Docker** - Containerização do banco

### Ferramentas
- **Postman** - Teste de endpoints
- **Maven** - Gerenciamento de dependências
- **pgAdmin/DBeaver** - Gerenciamento do banco de dados

## 🏗 Estrutura do Projeto

```plaintext
├── infrastructure/
│   └── docker/
│       └── docker-compose.yml       # Configuração do PostgreSQL
├── shipping/
│   ├── src/main/java/redirex/shipping/
│   │   ├── config/                 # Configurações do Spring
│   │   │   └── SecurityConfig.java # Configuração do Spring Security
│   │   ├── controllers/            # Controladores REST
│   │   │   ├── AuthController.java # Endpoints de autenticação
│   │   │   └── UserController.java # Endpoints de gerenciamento de usuários
│   │   ├── dto/                    # Objetos de transferência
│   │   │   └── RegisterUserDTO.java
│   │   ├── entity/                 # Entidades JPA
│   │   │   └── User.java
│   │   ├── repositories/           # Repositórios JPA
│   │   │   └── UserRepository.java
│   │   ├── security/               # Lógica de segurança
│   │   │   ├── JwtUtil.java
│   │   │   ├── JwtAuthenticationFilter.java
│   │   │   └── CustomUserDetailsService.java
│   │   └── service/                # Serviços de negócio
│   │       └── UserService.java
│   ├── src/main/resources/
│   │   └── application.properties  # Configurações de ambiente
└── pom.xml                         # Dependências Maven
```

## 🌐 Endpoints da API

### Autenticação
```http
POST /auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "senha": "SenhaSegura123!"
}

# Resposta
HTTP/1.1 200 OK
{
  "accessToken": "eyJhbGciOiJIUzUxMiJ9..."
}
```

### Registro de Usuário
```http
POST /usuarios
Content-Type: application/json

{
  "fullname": "Novo Usuário",
  "email": "novo@example.com",
  "password": "SenhaForte456@",
  "cpf": "12345678901",
  "phone": "11987654321",
  "address": "Rua Exemplo, 123",
  "complement": "Apto 45",
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
  "fullname": "Novo Usuário",
  "email": "novo@example.com",
  "password": "$2a$10$...",
  "cpf": "12345678901",
  "phone": "11987654321",
  "address": "Rua Exemplo, 123",
  "complement": "Apto 45",
  "city": "São Paulo",
  "state": "SP",
  "zipcode": "12345678",
  "country": "Brasil",
  "occupation": "Desenvolvedor",
  "role": "ROLE_USER"
}
```

### Listagem de Usuários
```http
GET /usuarios
Content-Type: application/json

# Resposta
HTTP/1.1 200 OK
[
  {
    "id": 1,
    "fullname": "Novo Usuário",
    "email": "novo@example.com",
    "password": "$2a$10$...",
    "cpf": "12345678901",
    "phone": "11987654321",
    "address": "Rua Exemplo, 123",
    "complement": "Apto 45",
    "city": "São Paulo",
    "state": "SP",
    "zipcode": "12345678",
    "country": "Brasil",
    "occupation": "Desenvolvedor",
    "role": "ROLE_USER"
  }
]
```

## 🛡️ Fluxo de Segurança

1. **Registro**: Cria um usuário com senha criptografada (BCrypt) e atribui o papel `ROLE_USER`.
2. **Login**: Valida as credenciais e retorna um token JWT.
3. **Acesso protegido**: Requisições a endpoints protegidos exigem um token JWT válido no cabeçalho `Authorization: Bearer <token>`.
4. **Autorização**: O Spring Security verifica o token e os papéis do usuário no `SecurityContext`.

## 🔐 Melhores Práticas Implementadas

- **Senhas seguras**: Uso de `BCryptPasswordEncoder` para criptografia de senhas.
- **JWT**: 
  - Chave secreta longa e segura.
  - Tokens com expiração de 24 horas.
  - Validação de assinatura no `JwtAuthenticationFilter`.
- **Segurança stateless**: Configuração de `SessionCreationPolicy.STATELESS` para APIs REST.
- **Logs detalhados**: Níveis `DEBUG` para `org.springframework.security` e `redirex.shipping`.
- **Validação de dados**: Campos obrigatórios validados na entidade `User`.

## 🧪 Executando Testes

Atualmente, o projeto não inclui testes unitários ou de integração configurados. Para testar os endpoints, use o Postman:

```bash
# Testar criação de usuário
POST http://localhost:8080/usuarios
Content-Type: application/json

# Testar listagem de usuários
GET http://localhost:8080/usuarios
Content-Type: application/json
```

Para adicionar testes unitários, use JUnit e Mockito:

```bash
mvn test
```

## 🤝 Contribuição

1. Faça um fork do projeto.
2. Crie uma branch para sua feature (`git checkout -b feature/nova-funcionalidade`).
3. Commit suas alterações (`git commit -m 'Adiciona nova funcionalidade'`).
4. Push para a branch (`git push origin feature/nova-funcionalidade`).
5. Abra um Pull Request.

## 📄 Licença

Distribuído sob a licença MIT. Veja `LICENSE` para mais informações.

## ✉️ Contato

**Desenvolvedor:** [Felipe Panosso]  
**Email:** [panossodev@gmail.com]  
**LinkedIn:** [linkedin.com/in/felipe-panosso]

---
