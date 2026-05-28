# Atividade Final - Sistema de Envio de E-mails com Java e RabbitMQ

Sistema que recebe um cadastro de destinatários e envia e-mails em lote para esse grupo de forma assíncrona. A requisição feita pelo usuário não dispara o envio diretamente: ela publica uma mensagem em uma fila do RabbitMQ, e um consumidor processa essa mensagem fora da requisição principal, fazendo o envio dos e-mails.

## Integrantes

- Matheus Oliveira Mitter
- Felipe Milhomem Rocha

## Tecnologias

- Java 17
- Spring Boot 3.3 (Web, Data JPA, AMQP, Mail, Validation)
- RabbitMQ
- Banco H2
- HTML, CSS e JavaScript no front-end

## Arquitetura

```
Front-end  -->  Controller  -->  Service  -->  Producer
                                                  |
                                                  v
                       Exchange  ->  Routing Key  ->  Fila
                                                  |
                                                  v
                                      Consumer (@RabbitListener)
                                                  |
                                                  v
                                  JavaMailSender / log no console
                                                  |
                                                  v
                                            Banco H2
```

O `EnvioController` recebe a solicitação de envio e devolve `202 Accepted` imediatamente. O `EmailProducer` publica a mensagem em uma exchange Direct usando `RabbitTemplate`. A fila recebe a mensagem e o `EmailConsumer`, anotado com `@RabbitListener`, processa o lote, envia os e-mails e grava um registro em `envio_logs` para cada destinatário.

## Recursos do RabbitMQ usados

- Exchange: `email.exchange` (direct, durable)
- Fila: `email.envio.queue` (durable)
- Routing key: `email.envio`
- Binding entre a exchange e a fila pela routing key

Esses recursos são declarados em `RabbitConfig` e criados automaticamente ao subir a aplicação.

## Estrutura de pacotes

```
com.atvfinal.mensageria
├── MensageriaEmailApplication.java
├── config
│   └── RabbitConfig.java
├── controller
│   ├── DestinatarioController.java
│   ├── EnvioController.java
│   └── LogController.java
├── dto
│   ├── EmailLoteMensagem.java
│   └── EmailMensagemDTO.java
├── exception
│   └── GlobalExceptionHandler.java
├── model
│   ├── Destinatario.java
│   └── EnvioLog.java
├── producer
│   └── EmailProducer.java
├── consumer
│   └── EmailConsumer.java
├── repository
│   ├── DestinatarioRepository.java
│   └── EnvioLogRepository.java
└── service
    ├── DestinatarioService.java
    └── EnvioEmailService.java
```

## Principais classes

- Configuração: `RabbitConfig`
- Producer: `EmailProducer`
- Consumer: `EmailConsumer`
- Services: `DestinatarioService`, `EnvioEmailService`
- Controllers: `DestinatarioController`, `EnvioController`, `LogController`
- Entidades: `Destinatario`, `EnvioLog`

## Banco de dados

Persistência em H2 com arquivo local em `./data/emaildb`. Tabelas:

- `destinatarios` — armazena nome, e-mail e data de cadastro
- `envio_logs` — armazena destinatário, assunto, status e detalhe de cada envio processado pelo consumer

Console H2: `http://localhost:8080/h2` (JDBC URL `jdbc:h2:file:./data/emaildb`, usuário `sa`, senha em branco).

## Endpoints REST

| Método | Endpoint                  | Descrição                                  |
|--------|---------------------------|--------------------------------------------|
| GET    | `/api/destinatarios`      | Lista destinatários                        |
| POST   | `/api/destinatarios`      | Cadastra destinatário                      |
| DELETE | `/api/destinatarios/{id}` | Remove destinatário                        |
| POST   | `/api/envios`             | Publica solicitação de envio na fila       |
| GET    | `/api/logs`               | Lista logs de processamento                |

## Front-end

Front-end servido pelo próprio Spring Boot em `http://localhost:8080`. Permite cadastrar destinatários, listar e remover, criar a mensagem (assunto e corpo), publicar a solicitação de envio na fila e visualizar os logs de processamento.

## Como executar

Pré-requisito: RabbitMQ acessível em `localhost:5672`. Para subir com Docker:

```bash
docker run -d --name rabbit -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```

Painel do RabbitMQ: `http://localhost:15672` (usuário `guest`, senha `guest`).

Subir a aplicação:

```bash
mvn spring-boot:run
```

Aplicação disponível em `http://localhost:8080`.

## Envio de e-mails

A aplicação suporta dois modos, definidos pela variável `MAIL_SIMULATE`:

- `MAIL_SIMULATE=true` (padrão) — o consumer registra o envio no console e em `envio_logs` sem chamar SMTP.
- `MAIL_SIMULATE=false` — o consumer envia via SMTP usando as variáveis `MAIL_HOST`, `MAIL_PORT`, `MAIL_USER`, `MAIL_PASS` e `MAIL_FROM`.

## Fluxo resumido

1. O usuário cadastra os destinatários pelo front-end e os dados são persistidos em H2.
2. O usuário preenche assunto e corpo da mensagem e clica em "Publicar na fila".
3. `EnvioController` recebe a requisição, `EnvioEmailService` monta um `EmailLoteMensagem` com a lista de e-mails e chama `EmailProducer`.
4. `EmailProducer` publica a mensagem na exchange `email.exchange` com a routing key `email.envio`. A resposta volta imediatamente para o cliente.
5. A mensagem chega à fila `email.envio.queue`.
6. `EmailConsumer` consome a mensagem, percorre os destinatários, envia o e-mail (real ou simulado) e grava um `EnvioLog` para cada um.
7. O front-end consulta `/api/logs` para mostrar o resultado.

## Evidências

Prints da execução do sistema estão em [`docs/prints/`](docs/prints/), cobrindo o front-end em uso, o console H2 com as tabelas `destinatarios` e `envio_logs`, o painel do RabbitMQ com a exchange, a fila e o binding, e os logs do Spring Boot mostrando o producer publicando e o consumer processando o lote.

## Vídeo

Link do vídeo demonstrativo:
