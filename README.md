# CRM Система

Упрощенная CRM-система для управления продавцами и транзакциями с функциями аналитики.

## Технологии

- Java 17
- Spring Boot 4.0.6
- Spring Data JPA (Hibernate 7.2)
- PostgreSQL / H2
- Gradle 9.4
- Lombok
- JUnit 5 + Mockito

## Функциональность

### Продавцы (Sellers)
- Создание, чтение, обновление, удаление (CRUD)
- Мягкое удаление (active = false) — сохраняется историчность

### Транзакции (Transactions)
- Создание с привязкой к продавцу
- Поддержка типов оплат: CASH, CARD, TRANSFER
- Получение всех транзакций или транзакций конкретного продавца

### Аналитика
- Самый продуктивный продавец за период (день/месяц/квартал/год)
- Продавцы с суммой меньше указанной за период
- Лучший период продавца — алгоритм Кадане

## Установка и запуск

### Требования
- JDK 17+
- PostgreSQL (для продакшен режима)

### Запуск в режиме разработки (H2 в памяти)

cd crm
./gradlew bootRun

Приложение доступно на http://localhost:8080

### Запуск с PostgreSQL

Создайте базу данных crm_db, затем:

./gradlew bootRun --args='--spring.profiles.active=prod'

### H2 Console (только dev)

URL: http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:crmdb
Username: sa
Password: (пусто)

## API Endpoints

### Продавцы

GET /api/sellers - Все активные продавцы
GET /api/sellers/{id} - Продавец по ID
POST /api/sellers - Создать продавца
PUT /api/sellers/{id} - Обновить продавца
DELETE /api/sellers/{id} - Удалить (мягкое)

### Транзакции

GET /api/transactions - Все транзакции
GET /api/transactions/{id} - Транзакция по ID
POST /api/transactions - Создать транзакцию
GET /api/transactions/seller/{sellerId} - Транзакции продавца

### Аналитика

GET /api/analytics/top-seller?start=...&end=... - Самый продуктивный продавец
GET /api/analytics/sellers-below?start=...&end=...&maxAmount=... - Продавцы с суммой меньше
GET /api/analytics/sellers/{id}/best-period - Лучший период продавца

## Примеры использования

Создание продавца:
curl -X POST http://localhost:8080/api/sellers -H "Content-Type: application/json" -d '{"name": "Иван Петров", "contactInfo": "ivan@email.com"}'

Все продавцы:
curl http://localhost:8080/api/sellers

Обновление продавца:
curl -X PUT http://localhost:8080/api/sellers/1 -H "Content-Type: application/json" -d '{"name": "Иван Кузьмин", "contactInfo": "new@email.com"}'

Удаление продавца:
curl -X DELETE http://localhost:8080/api/sellers/2

Создание транзакции:
curl -X POST http://localhost:8080/api/transactions -H "Content-Type: application/json" -d '{"sellerId": 1, "amount": 5000.00, "paymentType": "CASH"}'

Транзакции продавца:
curl http://localhost:8080/api/transactions/seller/1

Самый продуктивный продавец:
curl "http://localhost:8080/api/analytics/top-seller?start=2020-01-01T00:00:00&end=2030-01-01T00:00:00"

Продавцы с суммой меньше 10000:
curl "http://localhost:8080/api/analytics/sellers-below?start=2020-01-01T00:00:00&end=2030-01-01T00:00:00&maxAmount=10000"

Лучший период продавца:
curl "http://localhost:8080/api/analytics/sellers/1/best-period"

Ошибка 404:
curl http://localhost:8080/api/sellers/999

Ошибка 400:
curl -X POST http://localhost:8080/api/sellers -H "Content-Type: application/json" -d '{"name": ""}'

## Запуск тестов

./gradlew test

## Структура проекта

src/main/java/com/example/crm/
├── CrmApplication.java
├── controller/
│   ├── SellerController.java
│   ├── TransactionController.java
│   └── AnalyticsController.java
├── service/
│   ├── SellerService.java
│   ├── TransactionService.java
│   └── AnalyticsService.java
├── repository/
│   ├── SellerRepository.java
│   └── TransactionRepository.java
├── model/
│   ├── entity/
│   │   ├── Seller.java
│   │   └── Transaction.java
│   └── enums/
│       └── PaymentType.java
├── dto/
│   ├── request/
│   │   ├── CreateSellerRequest.java
│   │   └── CreateTransactionRequest.java
│   └── response/
│       ├── SellerResponse.java
│       ├── TransactionResponse.java
│       ├── TopSellerResponse.java
│       ├── SellerBelowResponse.java
│       └── BestPeriodResponse.java
└── exception/
    ├── ResourceNotFoundException.java
    └── GlobalExceptionHandler.java

## Особенности реализации

- Мягкое удаление продавцов (active = false)
- Транзакции сохраняются после удаления продавца
- Алгоритм Кадане для поиска лучшего периода
- Централизованная обработка ошибок (404, 400)
- DTO для разделения Entity и API моделей
- Паттерн Builder через Lombok