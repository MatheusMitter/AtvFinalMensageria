# Atividade Final - Sistema de Envio de E-mails com Java e RabbitMQ

Sistema de envio de e-mails em lote utilizando **Java + Spring Boot + RabbitMQ**, com persistência em banco e front-end web simples.

## Integrantes

- Matheus Oliveira Mitter
- Felipe Milhomem Rocha

## Arquitetura

```
[Front-end HTML/JS] --> [REST Controller] --> [Service] --> [Producer]
                                                                |
                                                                v
                                            [RabbitMQ: exchange -> routing key -> fila]
                                                                |
                                                                v
                                                  [Consumer @RabbitListener]
                                                                |
                                                                v
                                              [JavaMailSender / Log no console]
                                                                |
                                                                v
                                                       [Banco de dados H2]
```

A requisição de envio é desacoplada do processamento real. O endpoint REST apenas publica a mensagem na fila e responde imediatamente com `202 Accepted`. Um consumidor escuta a fila e dispara os e-mails em lote, gravando logs de cada envio no banco.

## Tecnologias

- Java 17
- Spring Boot 3.3
- Spring Web, Spring Data JPA, Spring AMQP, Spring Mail
- RabbitMQ (local ou CloudAMQP)
- Banco H2 (arquivo local em `./data/emaildb`)
- HTML, CSS e JavaScript puro no front-end

## Estrutura de pacotes

```
com.atvfinal.mensageria
├── MensageriaEmailApplication.java   # bootstrap Spring Boot
├── config
│   └── RabbitConfig.java             # exchange, fila, binding, RabbitTemplate
├── controller
│   ├── DestinatarioController.java   # CRUD de destinatários
│   ├── EnvioController.java          # solicitação de envio (publica na fila)
│   └── LogController.java            # listagem de logs de envio
├── dto
│   ├── EmailMensagemDTO.java         # payload da requisição
│   └── EmailLoteMensagem.java        # payload publicado na fila
├── exception
│   └── GlobalExceptionHandler.java
├── model
│   ├── Destinatario.java
│   └── EnvioLog.java
├── producer
│   └── EmailProducer.java            # publica na exchange usando RabbitTemplate
├── consumer
│   └── EmailConsumer.java            # @RabbitListener -> envia e-mails
├── repository
│   ├── DestinatarioRepository.java
│   └── EnvioLogRepository.java
└── service
    ├── DestinatarioService.java
    └── EnvioEmailService.java
```

## Configuração do RabbitMQ

A aplicação cria automaticamente:

- Exchange: `email.exchange` (Direct, durable)
- Fila: `email.envio.queue` (durable)
- Routing key: `email.envio`

### RabbitMQ local

Subir com Docker:

```bash
docker run -d --name rabbit -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```

Painel: http://localhost:15672 (guest / guest)

### CloudAMQP

Definir as variáveis de ambiente antes de subir a aplicação:

```bash
export RABBITMQ_HOST=seu-host.cloudamqp.com
export RABBITMQ_PORT=5672
export RABBITMQ_USER=seu-usuario
export RABBITMQ_PASS=sua-senha
export RABBITMQ_VHOST=seu-vhost
```

## Configuração do envio de e-mails

Por padrão a aplicação roda em modo `simulate=true`, gravando os envios no console e no banco sem disparar SMTP real. Para usar Mailtrap (ou outro SMTP):

```bash
export MAIL_SIMULATE=false
export MAIL_HOST=sandbox.smtp.mailtrap.io
export MAIL_PORT=2525
export MAIL_USER=seu_usuario_mailtrap
export MAIL_PASS=sua_senha_mailtrap
export MAIL_FROM=no-reply@atvfinal.com
```

## Executando

```bash
./mvnw spring-boot:run
```

ou

```bash
mvn spring-boot:run
```

A aplicação sobe em `http://localhost:8080`.

- Front-end: http://localhost:8080/
- Console H2: http://localhost:8080/h2 (JDBC URL `jdbc:h2:file:./data/emaildb`, usuário `sa`, senha em branco)

## Endpoints REST

| Método | Endpoint                  | Descrição                                         |
|--------|---------------------------|---------------------------------------------------|
| GET    | `/api/destinatarios`      | Lista destinatários                               |
| POST   | `/api/destinatarios`      | Cadastra destinatário (nome, email)               |
| DELETE | `/api/destinatarios/{id}` | Remove destinatário                               |
| POST   | `/api/envios`             | Publica mensagem na fila (assunto, corpo)         |
| GET    | `/api/logs`               | Lista logs de processamento                       |

### Exemplo de cadastro

```bash
curl -X POST http://localhost:8080/api/destinatarios \
  -H "Content-Type: application/json" \
  -d '{"nome":"Matheus","email":"matheus@example.com"}'
```

### Exemplo de envio em lote

```bash
curl -X POST http://localhost:8080/api/envios \
  -H "Content-Type: application/json" \
  -d '{"assunto":"Teste","corpo":"Olá! Mensagem de teste."}'
```

A resposta é imediata (`202 Accepted`). O consumidor processa o lote em background e grava os logs em `envio_logs`.

## Principais classes

- **Configuração**: `RabbitConfig`
- **Producer**: `EmailProducer`
- **Consumer**: `EmailConsumer` (`@RabbitListener`)
- **Service**: `DestinatarioService`, `EnvioEmailService`
- **Controller**: `DestinatarioController`, `EnvioController`, `LogController`
- **Modelo**: `Destinatario`, `EnvioLog`

## Fluxo resumido

1. Usuário cadastra destinatários pelo front-end (persistidos em H2).
2. Usuário cria a mensagem (assunto + corpo) e clica em "Publicar na fila".
3. `EnvioController` chama `EnvioEmailService`, que monta o `EmailLoteMensagem` e chama `EmailProducer`.
4. `EmailProducer` publica via `RabbitTemplate` na exchange `email.exchange` com routing key `email.envio`.
5. A fila `email.envio.queue` recebe a mensagem.
6. `EmailConsumer` (`@RabbitListener`) consome a mensagem e itera os destinatários, enviando o e-mail (real ou simulado) e gravando um `EnvioLog` para cada um.
7. O front-end consulta `/api/logs` para visualizar o resultado do processamento.
