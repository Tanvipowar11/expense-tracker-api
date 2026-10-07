# Expense Tracker API

A secure RESTful backend built with **Java 17, Spring Boot 3, Spring Security (JWT), Spring Data JPA and MySQL**.
Users register, log in to receive a JWT, and manage their own expenses. Each user can only see and modify their own data.

## Features
- JWT authentication (stateless) with BCrypt password hashing
- CRUD for expenses, filter by category, and a per-category spending summary
- Request validation with clear field-level error messages (HTTP 400)
- Proper status codes: 201, 204, 401, 404, 409
- Layered design: controller / service / repository / DTOs
- Unit tests (JUnit 5, Mockito) and integration tests (MockMvc)
- Runs instantly on in-memory H2; switch to MySQL with one profile flag

## Run it
Requires JDK 17+ and Maven.

```bash
mvn test              # run all tests
mvn spring-boot:run   # start on http://localhost:8080 (H2 in-memory)
```

Using MySQL:
```bash
export DB_USER=root DB_PASSWORD=yourpassword JWT_SECRET=a-long-random-secret-of-32-plus-chars
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

## API

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/auth/register` | No | Create account (username 3-30 chars, password 8-64 chars) |
| POST | `/api/auth/login` | No | Returns `{ "token": "..." }` |
| POST | `/api/expenses` | Bearer | Create expense |
| GET | `/api/expenses?category=Food` | Bearer | List expenses (optional category filter) |
| GET | `/api/expenses/{id}` | Bearer | Get one expense |
| PUT | `/api/expenses/{id}` | Bearer | Update expense |
| DELETE | `/api/expenses/{id}` | Bearer | Delete expense |
| GET | `/api/expenses/summary` | Bearer | Total spend per category |

Expense body:
```json
{ "description": "Lunch", "amount": 12.50, "category": "Food", "date": "2025-01-15" }
```

### Example
```bash
curl -X POST localhost:8080/api/auth/register -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"password123"}'

TOKEN=$(curl -s -X POST localhost:8080/api/auth/login -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"password123"}' | sed -E 's/.*"token":"([^"]+)".*/\1/')

curl -X POST localhost:8080/api/expenses -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"description":"Lunch","amount":12.50,"category":"Food","date":"2025-01-15"}'

curl localhost:8080/api/expenses/summary -H "Authorization: Bearer $TOKEN"
```

## Project structure
```
controller/   REST endpoints and validation error handling
service/      business logic
repository/   Spring Data JPA repositories
model/        JPA entities (User, Expense)
dto/          request/response records with validation
security/     JWT service, auth filter, security config
```
