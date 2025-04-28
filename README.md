# 🔒 Shipping - Sistema de Autenticação Segura com Spring Security & JWT

![Java](https://img.shields.io/badge/Java-17%2B-007396?logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.0-6DB33F?logo=spring)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15+-4169E1?logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-20.10%2B-2496ED?logo=docker)
![License](https://img.shields.io/badge/License-MIT-blue)

Um sistema de autenticação robusto e escalável implementando as melhores práticas de segurança modernas com Spring Security 6, JWT (JSON Web Tokens) e PostgreSQL. Totalmente containerizado com Docker para implantação simplificada.

## 📑 Índice

- [Recursos Principais](#-recursos-principais)
- [Pré-requisitos](#-pré-requisitos)
- [Instalação](#-instalação)
- [Configuração de Ambiente](#⚙️-configuração-de-ambiente)
- [Tecnologias Utilizadas](#-tecnologias-utilizadas)
- [Arquitetura do Sistema](#-arquitetura-do-sistema)
- [Endpoints da API](#-endpoints-da-api)
- [Fluxo de Segurança](#%EF%B8%8F-fluxo-de-segurança)
- [Melhores Práticas Implementadas](#-melhores-práticas-implementadas)
- [Executando Testes](#-executando-testes)
- [Contribuição](#-contribuição)
- [Licença](#-licença)
- [Contato](#-contato)

## 🚀 Recursos Principais

- ✅ Autenticação JWT Stateless com refresh tokens
- ✅ Autorização baseada em roles (ROLE_USER, ROLE_ADMIN)
- ✅ Armazenamento seguro de senhas com BCrypt
- ✅ Docker Compose para PostgreSQL + Adminer
- ✅ Configurações multi-ambiente (dev/prod)
- ✅ Proteção contra CSRF e CORS configurável
- ✅ Validação de tokens com blacklist
- ✅ Rate limiting básico para prevenção de brute force
- ✅ Documentação de endpoints com exemplos

## 📋 Pré-requisitos

- Java JDK 17+
- Apache Maven 3.8+
- Docker 20.10+
- Docker Compose 2.12+
- PostgreSQL 15+
- IDE de sua preferência (IntelliJ, VS Code, etc.)

## 🛠 Instalação

```bash
# 1. Clone o repositório
git clone https://github.com/seu-usuario/authguard-system.git
cd authguard-system

# 2. Configure as variáveis de ambiente (crie um arquivo .env na raiz)
cp .env.example .env

# 3. Inicie os containers (PostgreSQL + Adminer)
docker-compose up -d

# 4. Construa e execute a aplicação (perfil dev)
mvn clean install
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## ⚙️ Configuração de Ambiente

| Variável               | Descrição                              | Valor Padrão          |
|------------------------|----------------------------------------|-----------------------|
| `JWT_SECRET_KEY`       | Chave secreta para assinatura JWT      | (obrigatório)         |
| `JWT_EXPIRATION_MS`    | Tempo de expiração do token (ms)       | 86400000 (24h)        |
| `DB_URL`               | URL de conexão do PostgreSQL           | jdbc:postgresql://localhost:5432/authdb |
| `DB_USERNAME`          | Usuário do banco de dados              | authuser              |
| `DB_PASSWORD`          | Senha do banco de dados                | securepassword        |
| `CORS_ALLOWED_ORIGINS` | Origins permitidos para CORS           | http://localhost:3000 |

## 🛠 Tecnologias Utilizadas

### Backend
- **Spring Boot 3.1** - Framework principal
- **Spring Security 6** - Autenticação e autorização
- **JJWT** - Implementação de JWT
- **Lombok** - Redução de boilerplate
- **PostgreSQL Driver** - Conexão com banco
- **Spring Data JPA** - ORM e repositórios

### Banco de Dados
- **PostgreSQL 15** - Banco de dados relacional
- **Docker** - Containerização do banco
- **Flyway** - Migrations controladas (opcional)

### Ferramentas
- **Postman/Insomnia** - Teste de endpoints
- **Adminer** - Interface web para PostgreSQL
- **Maven** - Gerenciamento de dependências

## 🏗 Arquitetura do Sistema

```plaintext
📦 authguard-system
└── src/main/java/com/authguard
    ├── config/            # Configurações globais
    │   ├── SecurityConfig.java     # Config Spring Security
    │   ├── JwtFilterConfig.java    # Filtros JWT
    │   └── CorsConfig.java         # Políticas CORS
    │
    ├── security/          # Lógica de segurança
    │   ├── JwtProvider.java       # Geração/validação de tokens
    │   └── UserPrincipal.java     # Implementação UserDetails
    │
    ├── domain/            # Entidades e DTOs
    │   ├── entities/      # Modelos JPA
    │   └── dtos/          # Objetos de transferência
    │
    ├── repositories/      # Interfaces Spring Data JPA
    ├── services/          # Lógica de negócio
    └── web/               # Camada de apresentação
        ├── auth/          # Endpoints de autenticação
        └── user/          # Gestão de usuários
```

## 🌐 Endpoints da API

### Autenticação
```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "SenhaSegura123!"
}

# Resposta
HTTP/1.1 200 OK
{
  "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
  "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
  "expiresIn": 86400000
}
```

### Registro de Usuário
```http
POST /api/v1/auth/register
Content-Type: application/json

{
  "name": "Novo Usuário",
  "email": "novo@example.com",
  "password": "SenhaForte456@",
  "role": "USER"
}

# Resposta
HTTP/1.1 201 Created
{
  "id": 3,
  "name": "Novo Usuário",
  "email": "novo@example.com",
  "role": "USER"
}
```

### Acesso Protegido
```http
GET /api/v1/users/me
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...

# Resposta
HTTP/1.1 200 OK
{
  "id": 1,
  "name": "Usuário Teste",
  "email": "user@example.com",
  "role": "ADMIN"
}
```

## 🛡️ Fluxo de Segurança

1. **Login**: Valida credenciais → Gera JWT
2. **Requests**: Verifica header Authorization → Valida token → Carrega UserDetails
3. **Autorização**: Verifica roles/permissões no SecurityContext
4. **Renovação**: Usa refresh token para gerar novo access token
5. **Logout**: Invalida token no client-side (blacklist opcional)

## 🔐 Melhores Práticas Implementadas

- **Armazenamento Seguro**: BCrypt para hashing de senhas
- **JWT Configurações**: 
  - Chave secreta de 512 bits
  - Tokens curtos (24h) + refresh tokens
  - Assinatura HMAC-SHA512
- **HTTPS**: Configuração pronta para produção
- **Proteção Básica**:
  - CSRF desabilitado para APIs stateless
  - Headers de segurança HTTP (CSP, HSTS)
  - Rate limiting básico (Spring Actuator)
- **CORS**: Configuração restritiva por padrão

## � Executando Testes

```bash
# Testes unitários
mvn test

# Testes de integração (perfil test)
mvn verify -Ptest

# Cobertura de testes (Jacoco)
mvn jacoco:report
```

## 🤝 Contribuição

1. Faça um fork do projeto
2. Crie sua branch (`git checkout -b feature/nova-funcionalidade`)
3. Commit suas mudanças (`git commit -m 'Adiciona nova funcionalidade'`)
4. Push para a branch (`git push origin feature/nova-funcionalidade`)
5. Abra um Pull Request

## 📄 Licença

Distribuído sob a licença MIT. Veja `LICENSE` para mais informações.

## ✉️ Contato

**Desenvolvedor:** [Seu Nome]  
**Email:** [seu-email@provedor.com]  
**LinkedIn:** [linkedin.com/in/seu-perfil]

---
