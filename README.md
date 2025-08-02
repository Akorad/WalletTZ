# 💸 Wallet Service

Финансовый REST API-сервис для управления кошельками: пополнение, списание, история транзакций.  
Построен на **Java 17 + Spring Boot 3 + PostgreSQL + Liquibase + Docker Compose**.

---

## 🚀 Быстрый старт (через Docker)

> 💡 Всё запускается одной командой.  
> Конфигурации задаются через `.env` — без пересборки!

### 1. Склонировать репозиторий

```bash
git clone https://github.com/your-user/wallet-service.git
cd wallet-service
```
### 2. Собрать JAR
```bash
./mvnw clean package -DskipTests
```
### 3. Настроить .env
```bash
# .env
SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/walletdb
SPRING_DATASOURCE_USERNAME=wallet_user
SPRING_DATASOURCE_PASSWORD=secure_password

SPRING_JPA_HIBERNATE_DDL_AUTO=none
SPRING_JPA_SHOW_SQL=true

SERVER_PORT=8080

JWT_SECRET=some_very_secure_secret
JWT_EXPIRATION=3600000

ENCRYPTION_SECRET=1234567890123456
```
### 4. Запуск
```bash
docker compose up --build
```
## 📬 API-документация доступна по адресу:
```
http://localhost:8080/swagger-ui.html
```