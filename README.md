# 📊 Sistema de Processamento de Folha de Pagamento em Lote

**Autor:** Alexandre Henrique N. V. A. Ramos  
**Projeto:** Submetido como avaliação técnica para a vaga de Desenvolvedor Java.  
**Local:** Fortaleza, CE  
**Data:** 04 de setembro de 2025  

---

## 📌 Resumo

Este projeto consiste no desenvolvimento de um **serviço backend** para o processamento em lote de folhas de pagamento.  
A aplicação, construída com o ecossistema **Spring**, expõe uma **API RESTful segura** para disparar, monitorar e consultar relatórios financeiros consolidados por funcionário.  

A arquitetura foi projetada para ser **robusta, escalável e segura**, utilizando tecnologias modernas como:

- ⚙️ **Spring Batch** → Processamento em lote assíncrono.  
- 🔐 **JWT (JSON Web Tokens)** → Autenticação e autorização.  
- 🛢️ **PostgreSQL** → Banco de dados relacional.  
- 🐳 **Docker** → Portabilidade e facilidade de deploy.  

---

## 📂 Estrutura do Projeto

```
folha-pagamento-batch/
│── src/main/java/...    # Código-fonte principal
│── src/main/resources/  # Configurações (application.properties)
│── docker-compose.yml   # Orquestração com Docker
│── Dockerfile           # Build da aplicação
│── pom.xml              # Dependências Maven
```

---

## 🎯 Objetivo

Desenvolver um **serviço backend** capaz de processar rubricas (vantagens e descontos) de múltiplos funcionários de forma **assíncrona**, consolidando os dados em um **relatório financeiro detalhado por competência (mês/ano)**.

---

## 🧠 Minha Implementação

Optei por usar dependências como Lombok para reduzir a quantidade de código repetitivo (boilerplate) que normalmente escrevemos em Java, tornando o código mais limpo, legível e fácil de manter.

Também utilizei o Flyway PostgreSQL Database Support, para automatizar a gestão da estrutura do banco de dados e facilitar o controle das migrações.

Como solicitado, implementei autenticação e autorização usando Spring Security para garantir a segurança dos endpoints com  um JWT.

O PostgreSQL foi definido como banco de dados principal, e o uso de Docker trouxe consistência ao ambiente e simplificou a configuração, permitindo que qualquer desenvolvedor consiga rodar o projeto sem complicações. Além disso usei o Spring Data JPA com Hibernate para auxiliar no mapemento das tabelas 

Na parte do processamento e relatórios, optei por definir o formato no endpoint /relatorios/{executionId}, separando melhor as responsabilidades, já que esse endpoint é responsável por consultar ou baixar o relatório.

Fiz questão de implementar um tratamento de exceções, cobrindo os erros mais comuns como o 401 (Não Autorizado), 403 (Acesso Negado) e 404 (Não Encontrado). Porque queria garantir que, em caso de falha, a minha aplicação sempre se comunique de forma clara com o cliente.

Por fim, segui a arquitetura de Controller, Service e Entity, separando bem as camadas para melhorar a organização e a manutenibilidade do sistema.


## 🚀 Como Executar o Projeto

### 1. Pré-requisitos
- 🐳 Docker e Docker Compose  

### 1. Passos
```bash
# Clone o repositório
git clone https://github.com/alexandreH-dev/desafio-folha-pagamento
cd folha-pagamento-batch

# Suba os containers (API + Banco)
docker-compose up --build
```

A aplicação estará disponível em:  
👉 **http://localhost:8080**

---

## 📡 Como Utilizar a API

Use ferramentas como **Postman** ou **Insomnia** para testar.

### 1. Registrar um Usuário
```http
POST /auth/register
Content-Type: application/json

{
  "username": "admin",
  "password": "password123"
}
```

### 2. Realizar Login
```http
POST /auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "password123"
}
```
🔑 Resposta: token JWT

### 3. Disparar Processamento em Lote
```http
POST /processamentos/relatorios?competenciaMes=8&competenciaAno=2025
Authorization: Bearer <TOKEN>
```

### 4. Consultar Status
```http
GET /processamentos/{executionId}/status
Authorization: Bearer <TOKEN>
```

### 5. Consultar Relatório
```http
GET /relatorios/{executionId}?formato=TABLE | CSV
Authorization: Bearer <TOKEN>
```
