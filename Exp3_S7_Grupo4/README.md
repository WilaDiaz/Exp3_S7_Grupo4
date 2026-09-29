# Exp3_S7_Grupo4 - Desarrollo Backend III

## Configurando tolerancia a fallos y arquitectura de eventos con microservicios en la nube

Proyecto correspondiente a la **Experiencia 3 - Semana 7** de la asignatura **Desarrollo Backend III (PBY2203)**.

---

## 📌 Objetivo del proyecto

El objetivo del proyecto es fortalecer una arquitectura basada en microservicios mediante la implementación de mecanismos de **tolerancia a fallos con Resilience4j** y la incorporación de **mensajería asíncrona utilizando Apache Kafka**.

La solución permite registrar transacciones mediante `bank-api` y generar eventos asociados a cada nueva operación. Estos eventos son publicados en Apache Kafka y posteriormente procesados de manera asíncrona mediante un Consumer.

---

## 🏗️ Arquitectura de la solución

El proyecto utiliza una arquitectura de microservicios compuesta por:

- **Config Server**: centraliza la configuración de los microservicios.
- **Eureka Server**: permite el registro y descubrimiento de servicios.
- **bank-api**: administra las operaciones bancarias y el flujo de eventos de las transacciones.
- **cuentas-api**: microservicio encargado de las operaciones relacionadas con cuentas.
- **intereses-api**: microservicio encargado de las operaciones relacionadas con intereses.
- **Apache Kafka**: broker utilizado para la comunicación asíncrona basada en eventos.
- **H2**: base de datos en memoria utilizada por los microservicios.
- **Resilience4j**: proporciona mecanismos de tolerancia a fallos mediante Circuit Breaker y Retry.

---

## 🔄 Arquitectura orientada a eventos

Para el procesamiento asíncrono de transacciones se implementó el siguiente flujo:

```text
Cliente / Postman
       |
       | POST /api/v1/transacciones
       v
   BankController
       |
       v
    BankService
      /     \
     v       v
    H2   TransaccionProducer
              |
              | TransaccionCreadaEvent
              v
        Apache Kafka
              |
              | Topic:
              | transacciones-creadas
              v
      TransaccionConsumer
```

Cuando se registra una nueva transacción:

1. `BankController` recibe la solicitud.
2. `BankService` procesa y almacena la transacción en H2.
3. Se genera un `TransaccionCreadaEvent`.
4. `TransaccionProducer` publica el evento en Apache Kafka.
5. El evento se envía al tópico `transacciones-creadas`.
6. `TransaccionConsumer` recibe y procesa el evento de manera asíncrona.

> **Nota:** la arquitectura de eventos con Kafka se implementa únicamente en `bank-api`, que es el microservicio que gestiona transacciones. Los microservicios `cuentas-api` e `intereses-api` mantienen su comportamiento síncrono, ya que no manejan eventos de transacciones.

---

## 📨 Evento implementado

El evento utilizado para representar una nueva transacción es:

```text
TransaccionCreadaEvent
```

Contiene los siguientes datos:

- Identificador de la transacción.
- Fecha.
- Monto.
- Tipo de transacción.

El tópico utilizado en Kafka es:

```text
transacciones-creadas
```

El grupo utilizado por el Consumer es:

```text
bank-api-group
```

---

## 🛡️ Tolerancia a fallos

La solución utiliza **Resilience4j** para fortalecer la resiliencia de los microservicios.

Se implementaron los patrones:

### Circuit Breaker

Permite controlar fallos durante la ejecución de las operaciones y utilizar métodos fallback cuando una operación no puede completarse correctamente.

### Retry

Permite realizar reintentos automáticos ante determinados errores antes de considerar que una operación ha fallado definitivamente.

En `bank-api`, estos mecanismos se encuentran aplicados sobre las operaciones administradas por `BankService`.

---

## 🔐 Seguridad

Los endpoints protegidos utilizan autenticación **HTTP Basic** y control de acceso basado en roles.

Credenciales utilizadas para las pruebas:

```text
Usuario: admin
Contraseña: admin123
Rol: ADMIN
```

Los endpoints `/api/v1/**` requieren un usuario con rol `ADMIN`.

---

## ⚙️ Tecnologías utilizadas

- Java 17
- Spring Boot 4.1.0
- Spring Cloud 2025.1.3
- Spring Cloud Config
- Netflix Eureka
- Spring Security
- Spring Data JPA
- Apache Kafka
- Spring Kafka
- Resilience4j
- H2 Database
- Maven
- Docker
- Postman

---

## 📂 Estructura general

```text
Exp3_S7_Grupo4/
|
|-- bank-api/
|-- cuentas-api/
|-- intereses-api/
|-- config-server/
|-- eureka-server/
|-- docker-compose.yml
|-- README.md
```

### bank-api

Contiene la implementación principal de la arquitectura orientada a eventos.

Entre sus componentes se encuentran:

```text
controller/
    BankController.java

service/
    BankService.java

event/
    TransaccionCreadaEvent.java

kafka/
    TransaccionProducer.java
    TransaccionConsumer.java

config/
    KafkaConfig.java
    SecurityConfig.java
```

### config-server

Contiene la configuración centralizada de los microservicios.

```text
config-server/
    src/main/resources/
        application.yml
        config-repo/
            bank-api.yml
            cuentas-api.yml
            intereses-api.yml
```

---

## 🔌 Puertos utilizados

| Componente | Puerto |
|---|---:|
| Eureka Server | 8761 |
| Config Server | 8888 |
| bank-api | 8081 |
| cuentas-api | 8082 |
| intereses-api | 8083 |
| Apache Kafka | 9092 |

---

## 🚀 Ejecución del proyecto

### 1. Levantar Apache Kafka

Desde la carpeta raíz del proyecto ejecutar:

```bash
docker compose up -d
```

Para verificar el contenedor:

```bash
docker ps
```

Debe aparecer el contenedor:

```text
banco-kafka
```

### 2. Ejecutar Config Server

Ingresar a:

```text
config-server
```

Ejecutar:

```bash
.\mvnw.cmd spring-boot:run
```

El servicio se ejecutará en:

```text
http://localhost:8888
```

### 3. Ejecutar Eureka Server

Ingresar a:

```text
eureka-server
```

Ejecutar:

```bash
.\mvnw.cmd spring-boot:run
```

Eureka estará disponible en:

```text
http://localhost:8761
```

### 4. Ejecutar bank-api

Ingresar a:

```text
bank-api
```

Ejecutar:

```bash
.\mvnw.cmd spring-boot:run
```

Puerto:

```text
8081
```

### 5. Ejecutar cuentas-api

Ingresar a:

```text
cuentas-api
```

Ejecutar:

```bash
.\mvnw.cmd spring-boot:run
```

Puerto:

```text
8082
```

### 6. Ejecutar intereses-api

Ingresar a:

```text
intereses-api
```

Ejecutar:

```bash
.\mvnw.cmd spring-boot:run
```

Puerto:

```text
8083
```

---

## 🧪 Prueba de creación de una transacción

Realizar la siguiente solicitud mediante Postman:

```text
POST http://localhost:8081/api/v1/transacciones
```

Utilizar autenticación **Basic Auth**:

```text
Username: admin
Password: admin123
```

Body:

```json
{
    "fecha": "2026-09-26",
    "monto": 150000.0,
    "tipo": "DEPOSITO"
}
```

Ejemplo de respuesta:

```json
{
    "fecha": "2026-09-26",
    "id": 1001,
    "monto": 150000.0,
    "tipo": "DEPOSITO"
}
```

---

## ✅ Validación del flujo de Kafka

Después de registrar la transacción, la consola de `bank-api` permite verificar la publicación del evento:

```text
Evento enviado a Kafka -> tópico: transacciones-creadas
```

Posteriormente se evidencia su procesamiento por el Consumer:

```text
EVENTO RECIBIDO DESDE KAFKA
```

Esto permite comprobar el flujo:

```text
POST -> bank-api -> H2 -> Producer -> Kafka -> Consumer
```

---

## 🎯 Resultado

La solución implementa una arquitectura asíncrona orientada a eventos utilizando **Apache Kafka** para el procesamiento de nuevas transacciones.

El sistema mantiene además mecanismos de tolerancia a fallos mediante **Resilience4j**, registro y descubrimiento mediante **Eureka Server** y configuración centralizada mediante **Config Server**.

---

## 👥 Integrantes del grupo

- [Nombre 1]
- [Nombre 2]

---

## 📚 Referencias

- [Spring Cloud Config](https://docs.spring.io/spring-cloud-config/docs/current/reference/html/)
- [Spring Cloud Netflix Eureka](https://docs.spring.io/spring-cloud-netflix/docs/current/reference/html/)
- [Spring for Apache Kafka](https://docs.spring.io/spring-kafka/docs/current/reference/html/)
- [Apache Kafka Documentation](https://kafka.apache.org/documentation/)
- [Resilience4j — Circuit Breaker](https://resilience4j.readme.io/docs/circuitbreaker)
- [Spring Security — Basic Auth](https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/basic.html)
- Repositorio de datos legacy: [bank_legacy_data](https://github.com/KariVillagran/bank_legacy_data)