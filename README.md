![Logo](./ras-logo.png)

# Water Tariff API

Water Tariff API é uma aplicação REST para cadastro de tabelas tarifárias de água e cálculo progressivo do valor a pagar por categoria de consumidor.

A API permite parametrizar categorias, faixas de consumo e valores no banco de dados, sem necessidade de alterar o código da aplicação.

# Appendix

O projeto do Water-Tariff foi implementado como uma API RESTful em Spring Boot, seguindo o desafio de cálculo de tarifa de água.

## 1. Tecnologias e arquitetura

- Backend: Spring Boot 4, Java 21, Gradle.
- Persistência: Spring Data JPA.
- Banco de dados: PostgreSQL 16.3.
- Migrations: Flyway.
- Validação: Jakarta Validation.
- Redução de boilerplate: Lombok.
- Orquestração de containers: Docker Compose.

##### Estrutura principal:

- `src/main/java/com/cleyxds/water_tariff/modules/tariff`: cadastro, listagem, consulta e desativação de tabelas tarifárias.
- `src/main/java/com/cleyxds/water_tariff/modules/calculation`: cálculo progressivo da tarifa.
- `src/main/java/com/cleyxds/water_tariff/shared/exception`: tratamento global de erros.
- `src/main/resources/db/migration`: migrations do banco de dados.
- `dev.compose.yaml`: sobe os serviços **postgresql** e **api**.

## 2. Pre-requisitos

- Docker e Docker Compose instalados.

###### Para execução sem Docker:

- Java 21
- PostgreSQL 16+

## 3. Configuração de ambiente

A aplicação importa um arquivo `.env` opcional:

```yaml
spring:
  config:
    import: optional:file:.env[.properties]
```

##### Variáveis suportadas:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`

##### Valores padrão para execução local:

```txt
DB_URL=jdbc:postgresql://localhost:5432/watertariff
DB_USERNAME=watertariffadmin
DB_PASSWORD=supersecretpassword
```

##### No Docker Compose:

A API usa o hostname do serviço PostgreSQL:

```txt
DB_URL=jdbc:postgresql://postgresql:5432/watertariff
```

## 4. Como rodar localmente com Docker (recomendado)

Na raiz do projeto:

```bash
docker compose -f dev.compose.yaml up --build
```

Serviços:

- API: http://localhost:8080
- Health check: http://localhost:8080/health
- PostgreSQL: localhost:5432

Para desligar:

```bash
docker compose -f dev.compose.yaml stop
```

Para recriar o banco local do zero:

```bash
docker compose -f dev.compose.yaml down -v
docker compose -f dev.compose.yaml up --build
```

## 5. Execução sem Docker (opcional)

Suba um PostgreSQL local com:

```txt
database: watertariff
username: watertariffadmin
password: supersecretpassword
```

Depois execute:

```bash
./gradlew bootRun
```

## 6. Endpoints

##### Health check

```http
GET /health
```

Resposta:

```txt
Water Tariff API is running
```

##### Criar tabela tarifária

```http
POST /api/tabelas-tarifarias
Content-Type: application/json
```

Request:

```json
{
  "name": "Tabela 2026",
  "effectiveDate": "2026-01-01",
  "categories": [
    {
      "code": "INDUSTRIAL",
      "name": "Industrial",
      "ranges": [
        {
          "startM3": 0,
          "endM3": 10,
          "unitPrice": 1.0
        },
        {
          "startM3": 11,
          "endM3": 20,
          "unitPrice": 2.0
        },
        {
          "startM3": 21,
          "endM3": 30,
          "unitPrice": 3.0
        },
        {
          "startM3": 31,
          "endM3": 99999,
          "unitPrice": 4.0
        }
      ]
    }
  ]
}
```

Resposta: `201 Created`

##### Listar tabelas tarifárias ativas

```http
GET /api/tabelas-tarifarias
```

##### Buscar tabela tarifária ativa por ID

```http
GET /api/tabelas-tarifarias/{id}
```

##### Desativar tabela tarifária

```http
DELETE /api/tabelas-tarifarias/{id}
```

Resposta: `204 No Content`

##### Calcular tarifa progressiva

```http
POST /api/calculos
Content-Type: application/json
```

Request:

```json
{
  "category": "INDUSTRIAL",
  "consumption": 18
}
```

Resposta:

```json
{
  "category": "INDUSTRIAL",
  "totalConsumption": 18,
  "totalAmount": 26.0,
  "details": [
    {
      "range": {
        "start": 0,
        "end": 10
      },
      "chargedM3": 10,
      "unitPrice": 1.0,
      "subtotal": 10.0
    },
    {
      "range": {
        "start": 11,
        "end": 20
      },
      "chargedM3": 8,
      "unitPrice": 2.0,
      "subtotal": 16.0
    }
  ]
}
```

## 7. Regras de negócio

- As tabelas tarifárias são cadastradas com categorias e faixas de consumo.
- A exclusão de tabela tarifária é lógica, usando `active = false`.
- Tabelas desativadas não são usadas em consultas e cálculos.
- O cálculo é progressivo por faixa.
- A categoria usada no cálculo é buscada na tabela tarifária ativa mais recente.

##### Validações das faixas:

- A primeira faixa deve iniciar em `0`.
- `startM3` deve ser menor ou igual a `endM3`.
- As faixas devem ser contínuas.
- As faixas não podem se sobrepor.
- O consumo informado deve estar coberto pelas faixas cadastradas.

## 8. Banco de dados e migrations

O projeto usa Flyway para versionar a estrutura do banco.

Migration inicial:

```txt
src/main/resources/db/migration/V1__create_tariff_tables.sql
```

Tabelas principais:

- `tariff_tables`
- `consumer_categories`
- `consumption_ranges`

Em ambiente local, Hibernate está configurado com:

```yaml
ddl-auto: update
```

Em produção, use o profile `prod` para validar o schema sem alterá-lo automaticamente:

```bash
SPRING_PROFILES_ACTIVE=prod
```

## 9. Tratamento de erros

A API usa um handler global para padronizar respostas de erro.

Formato:

```json
{
  "timestamp": "2026-06-28T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "messages": ["Mensagem do erro"]
}
```

Casos tratados:

- Erros de validação: `400 Bad Request`.
- Regras de negócio: `400 Bad Request`.
- Recursos não encontrados: `404 Not Found`.
- JSON inválido: `400 Bad Request`.
- Erro inesperado: `500 Internal Server Error`.

## 10. Como testar rapidamente

1. Suba a aplicação:

```bash
docker compose -f dev.compose.yaml up --build
```

2. Verifique se a API subiu:

```bash
curl http://localhost:8080/health
```

3. Cadastre uma tabela tarifária:

```bash
curl -X POST http://localhost:8080/api/tabelas-tarifarias \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Tabela 2026",
    "effectiveDate": "2026-01-01",
    "categories": [
      {
        "code": "INDUSTRIAL",
        "name": "Industrial",
        "ranges": [
          { "startM3": 0, "endM3": 10, "unitPrice": 1.00 },
          { "startM3": 11, "endM3": 20, "unitPrice": 2.00 },
          { "startM3": 21, "endM3": 30, "unitPrice": 3.00 },
          { "startM3": 31, "endM3": 99999, "unitPrice": 4.00 }
        ]
      }
    ]
  }'
```

4. Calcule a tarifa:

```bash
curl -X POST http://localhost:8080/api/calculos \
  -H "Content-Type: application/json" \
  -d '{
    "category": "INDUSTRIAL",
    "consumption": 18
  }'
```

## 11. Decisões técnicas

- API REST com recursos em português para seguir os endpoints pedidos no desafio.
- Campos de request e response em inglês por decisão própria de padronização.
- DTOs para separar contrato HTTP das entidades JPA.
- Spring Data JPA para reduzir código de persistência.
- Flyway para versionar a estrutura do banco.
- Soft delete para impedir uso futuro de tabelas desativadas sem perder histórico.
- Validação centralizada das faixas antes da persistência.
- Cálculo progressivo separado em módulo próprio.
- Docker Compose com healthcheck para aguardar o PostgreSQL antes de iniciar a API.

## 12. Autor

- **Cleyson Barbosa**
