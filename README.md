```bash
cd crm
./gradlew bootRun
```

Приложение доступно на:

```text
http://localhost:8080
```

### Запуск с PostgreSQL

Создайте базу данных `crm_db`, затем:

```bash
./gradlew bootRun --args='--spring.profiles.active=prod'
```

### H2 Console (только dev)

```text
URL: http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:crmdb
Username: sa
Password:
```

## API Endpoints

### Продавцы

```http
GET /api/sellers
```

Получить всех активных продавцов.

```http
GET /api/sellers/{id}
```

Получить продавца по ID.

```http
POST /api/sellers
```

Создать продавца.

```http
PUT /api/sellers/{id}
```

Обновить продавца.

```http
DELETE /api/sellers/{id}
```

Удалить продавца мягко.

---

### Транзакции

```http
GET /api/transactions
```

Получить все транзакции.

```http
GET /api/transactions/{id}
```

Получить транзакцию по ID.

```http
POST /api/transactions
```

Создать транзакцию.

```http
GET /api/transactions/seller/{sellerId}
```

Получить транзакции конкретного продавца.

---

### Аналитика

```http
GET /api/analytics/top-seller?start=...&end=...
```

Получить самого продуктивного продавца за период.

```http
GET /api/analytics/sellers-below?start=...&end=...&maxAmount=...
```

Получить продавцов с суммой транзакций меньше указанной.

```http
GET /api/analytics/sellers/{id}/best-period
```

Получить лучший период продавца.

---

## Примеры использования API

### Создание продавца

```http
POST /api/sellers
Content-Type: application/json
```

Body:

```json
{
  "name": "Иван Петров",
  "contactInfo": "ivan@email.com"
}
```

Пример успешного ответа:

```json
{
  "id": 1,
  "name": "Иван Петров",
  "contactInfo": "ivan@email.com",
  "registrationDate": "2026-05-17T12:00:00"
}
```

---

### Получение всех продавцов

```http
GET /api/sellers
```

Пример успешного ответа:

```json
[
  {
    "id": 1,
    "name": "Иван Петров",
    "contactInfo": "ivan@email.com",
    "registrationDate": "2026-05-17T12:00:00"
  },
  {
    "id": 2,
    "name": "Петр Иванов",
    "contactInfo": "petr@email.com",
    "registrationDate": "2026-05-17T12:10:00"
  }
]
```

---

### Получение продавца по ID

```http
GET /api/sellers/1
```

Пример успешного ответа:

```json
{
  "id": 1,
  "name": "Иван Петров",
  "contactInfo": "ivan@email.com",
  "registrationDate": "2026-05-17T12:00:00"
}
```

---

### Обновление продавца

```http
PUT /api/sellers/1
Content-Type: application/json
```

Body:

```json
{
  "name": "Иван Кузьмин",
  "contactInfo": "new@email.com"
}
```

Пример успешного ответа:

```json
{
  "id": 1,
  "name": "Иван Кузьмин",
  "contactInfo": "new@email.com",
  "registrationDate": "2026-05-17T12:00:00"
}
```

---

### Удаление продавца

```http
DELETE /api/sellers/2
```

Пример успешного ответа:

```text
204 No Content
```

---

### Создание транзакции

```http
POST /api/transactions
Content-Type: application/json
```

Body:

```json
{
  "sellerId": 1,
  "amount": 5000.00,
  "paymentType": "CASH"
}
```

Пример успешного ответа:

```json
{
  "id": 1,
  "sellerId": 1,
  "sellerName": "Иван Кузьмин",
  "amount": 5000.00,
  "paymentType": "CASH",
  "transactionDate": "2026-05-17T12:30:00"
}
```

---

### Получение всех транзакций

```http
GET /api/transactions
```

Пример успешного ответа:

```json
[
  {
    "id": 1,
    "sellerId": 1,
    "sellerName": "Иван Кузьмин",
    "amount": 5000.00,
    "paymentType": "CASH",
    "transactionDate": "2026-05-17T12:30:00"
  }
]
```

---

### Получение транзакции по ID

```http
GET /api/transactions/1
```

Пример успешного ответа:

```json
{
  "id": 1,
  "sellerId": 1,
  "sellerName": "Иван Кузьмин",
  "amount": 5000.00,
  "paymentType": "CASH",
  "transactionDate": "2026-05-17T12:30:00"
}
```

---

### Получение транзакций продавца

```http
GET /api/transactions/seller/1
```

Пример успешного ответа:

```json
[
  {
    "id": 1,
    "sellerId": 1,
    "sellerName": "Иван Кузьмин",
    "amount": 5000.00,
    "paymentType": "CASH",
    "transactionDate": "2026-05-17T12:30:00"
  },
  {
    "id": 2,
    "sellerId": 1,
    "sellerName": "Иван Кузьмин",
    "amount": 3000.00,
    "paymentType": "CARD",
    "transactionDate": "2026-05-17T13:00:00"
  }
]
```

---

### Самый продуктивный продавец

```http
GET /api/analytics/top-seller?start=2020-01-01T00:00:00&end=2030-01-01T00:00:00
```

Пример успешного ответа:

```json
{
  "sellerId": 1,
  "sellerName": "Иван Кузьмин",
  "totalAmount": 8000.00
}
```

---

### Продавцы с суммой меньше 10000

```http
GET /api/analytics/sellers-below?start=2020-01-01T00:00:00&end=2030-01-01T00:00:00&maxAmount=10000
```

Пример успешного ответа:

```json
[
  {
    "sellerId": 1,
    "sellerName": "Иван Кузьмин",
    "totalAmount": 8000.00
  },
  {
    "sellerId": 2,
    "sellerName": "Петр Иванов",
    "totalAmount": 3000.00
  }
]
```

---

### Лучший период продавца

```http
GET /api/analytics/sellers/1/best-period
```

Пример успешного ответа:

```json
{
  "sellerId": 1,
  "sellerName": "Иван Кузьмин",
  "startDate": "2026-05-17T12:30:00",
  "endDate": "2026-05-17T13:00:00",
  "transactionCount": 2
}
```

---

## Примеры ошибок

### Ошибка 404

```http
GET /api/sellers/999
```

Пример ответа:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Seller not found with id: 999",
  "timestamp": "2026-05-17T12:00:00"
}
```

---

### Ошибка 400

```http
POST /api/sellers
Content-Type: application/json
```

Body:

```json
{
  "name": ""
}
```

Пример ответа:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "name: must not be blank",
  "timestamp": "2026-05-17T12:00:00"
}
```

---

## Запуск тестов

```bash
./gradlew test
```

## Особенности реализации

- Мягкое удаление продавцов (active = false)
- Транзакции сохраняются после удаления продавца
- Алгоритм Кадане для поиска лучшего периода
- Централизованная обработка ошибок (404, 400)
- DTO для разделения Entity и API моделей
- Паттерн Builder через Lombok
