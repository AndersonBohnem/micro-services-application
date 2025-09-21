# Greeting API

Uma API simples em **Spring Boot** que retorna mensagens de saudação.  
Ela suporta diferentes formas de fornecer o nome da pessoa e demonstra boas práticas em endpoints REST.

---

## Funcionalidades

- **GET /greeting**
    - Retorna uma saudação com o **nome default** se nenhum nome for fornecido.

- **GET /greeting/{name}**
    - Retorna uma saudação personalizada usando o **path variable**.

- **GET /greeting?name=Lucas**
    - Retorna uma saudação personalizada usando o **query parameter**.

- **POST /greeting**
    - Recebe um JSON no corpo da requisição:
      ```json
      {
          "name": "Pedro"
      }
      ```
    - Retorna a saudação usando o nome fornecido ou o default se o corpo estiver vazio.

---

## Exemplo de Respostas

| Requisição                             | Resposta                |
|----------------------------------------|-------------------------|
| GET `/greeting`                        | `Olá, Usuário!!!`       |
| GET `/greeting/Lucas`                  | `Olá, Lucas!!!`         |
| GET `/greeting?name=Maria`             | `Olá, Maria!!!`         |
| POST `/greeting` `{ "name": "Pedro" }` | `Olá, Pedro!!!`         |
| POST `/greeting` `{ }`                 | `Olá, Usuário!!!`       |

---

## Tecnologias

- Java 24
- Spring Boot 3.5.5
- Maven
