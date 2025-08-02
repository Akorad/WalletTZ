# 💳 Wallet Service

<div align="center">

![Java](https://img.shields.io/badge/Java-22-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.3-brightgreen?style=for-the-badge&logo=spring)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue?style=for-the-badge&logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?style=for-the-badge&logo=docker)

**Современный финансовый REST API-сервис для управления кошельками**

*Пополнение • Списание • История транзакций • JWT авторизация*

</div>

---

## ✨ Особенности

- 🔐 **JWT Authentication** - безопасная авторизация
- 💰 **Управление кошельками** - создание, пополнение, списание
- 📊 **История транзакций** - полная отчетность
- 🔄 **Автоматические миграции** - Liquibase для управления БД
- 🐳 **Docker Ready** - запуск одной командой
- 📝 **Swagger UI** - интерактивная документация API
- 🔒 **Шифрование данных** - защита чувствительной информации

---

## 🚀 Быстрый старт

### Способ 1: Docker Compose (Рекомендуется)

> 💡 **Всё запускается одной командой**  
> PostgreSQL + приложение автоматически настроятся

```bash
# 1. Клонируем репозиторий
git clone https://github.com/your-username/wallet-service.git
cd wallet-service

# 2. Собираем JAR
./mvnw clean package -DskipTests

# 3. Запускаем всё в Docker
docker-compose up --build
```

### Способ 2: Локальная разработка

> 🛠️ **Для разработки с hot-reload**

```bash
# 1. Запускаем только PostgreSQL в Docker
docker-compose up postgres -d

# 2. Настраиваем .env для локального запуска
cp .env.example .env

# 3. Запускаем приложение локально
./mvnw spring-boot:run
```

---

## ⚙️ Конфигурация

### Docker окружение

Создайте файл `.env.docker`:

```env
# Database Configuration
DB_HOST=postgres
DB_PORT=5432
DB_NAME=testdb
DB_USER=root
DB_PASSWORD=1234

# Application Configuration
SPRING_PORT=8080

# Security Configuration
JWT_SECRET=QMXJ5e+VceqEj+erF7s1//xb6rkeGtVm8M2J8NRwYYA=
JWT_EXPIRATION=3600000
ENCRYPTION_SECRET=1234567890123456
```

### Локальное окружение

Создайте файл `.env`:

```env
# Database Configuration (локальный PostgreSQL)
DB_HOST=localhost
DB_PORT=5432
DB_NAME=testdb
DB_USER=root
DB_PASSWORD=1234

# Application Configuration
SPRING_PORT=8080

# Security Configuration
JWT_SECRET=QMXJ5e+VceqEj+erF7s1//xb6rkeGtVm8M2J8NRwYYA=
JWT_EXPIRATION=3600000
ENCRYPTION_SECRET=1234567890123456
```

---

## 🔧 Требования

- **Java 22** или выше
- **Maven 3.6+**
- **Docker & Docker Compose** (для Docker запуска)
- **PostgreSQL 15** (для локального запуска)

---

## 📚 API Документация

После запуска приложения API документация доступна по адресам:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

### Основные эндпоинты

| Метод | Эндпоинт | Описание |
|-------|----------|----------|
| `POST` | `/api/auth/register` | Регистрация пользователя |
| `POST` | `/api/auth/login` | Авторизация |
| `GET` | `/api/wallets` | Получить кошельки пользователя |
| `POST` | `/api/wallets/{id}/deposit` | Пополнить кошелек |
| `POST` | `/api/wallets/{id}/withdraw` | Списать с кошелька |
| `GET` | `/api/wallets/{id}/transactions` | История транзакций |

---



---

## 🛠️ Разработка

### Полезные команды

```bash
# Запуск тестов
./mvnw test

# Запуск с профилем разработки
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Сборка без тестов
./mvnw clean package -DskipTests

# Просмотр логов Docker
docker-compose logs -f wallet-app

# Остановка всех контейнеров
docker-compose down
```

### База данных

Миграции выполняются автоматически при запуске через **Liquibase**.

Файлы миграций: `src/main/resources/db/migration/`

---

## 🚦 Статусы сервисов

После запуска проверьте статус:

- ✅ **Приложение**: http://localhost:8080/actuator/health
- ✅ **База данных**: Подключение проверяется автоматически
- ✅ **Swagger**: http://localhost:8080/swagger-ui.html

---