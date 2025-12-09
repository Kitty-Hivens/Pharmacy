# 🏥 Pharmacy POS System

Enterprise-grade backend for pharmaceutical retail management.
Designed for high reliability, strict data validation, and secure role-based access.

> **Stack:** Java 21, Spring Boot 3, MariaDB, Docker, JWT.
> **Status:** v1.0 (MVP Ready)

---

## ⚡ Quick Start (Docker)

**Prerequisites:** Docker & Docker Compose installed.

Run the full stack (Backend + Database) with a single command from the project root:

```bash
docker-compose up --build
````

The system will start automatically:

* **Backend API:** `http://localhost:8080`
* **Database:** `localhost:3306` (inside container)

### 📖 API Documentation

Interactive Swagger UI is available at:
👉 **[http://localhost:8080/swagger-ui/index.html#/](http://localhost:8080/swagger-ui.html)**

-----

## 📂 Project Structure

This is a monorepo containing:

* **`backend/`** - Spring Boot application (Java 21).
* **`frontend/`** - Vue.js POS interface (in development).
* **`docker-compose.yml`** - Orchestration for the entire system.

-----

## 🔐 Access & Credentials

The system is secured with **Stateless JWT**.

### 1\. Default Admin

Created automatically on first launch:

* **Login:** `admin`
* **Password:** `admin` (or check console logs if randomized)

### 2\. Authentication Flow

1.  **Login:** `POST /auth/login`
2.  **Get Token:** Response `{ "token": "eyJ...", "role": "ADMIN" }`
3.  **Use Token:** Add header to requests: `Authorization: Bearer <token>`

-----

## 🛠️ Key Features

### 📦 Inventory (FEFO Strategy)

Implements **First Expired, First Out** logic.

* Tracks goods by **Batches** (expiration dates).
* Automatically finds and deducts stock from the oldest batch during sales.

### 🛡️ Security (RBAC)

* **ADMIN:** Full control (Users, Inventory, Suppliers).
* **PHARMACIST:** Operational access (Sales, Customers).

### ✅ Reliability

* **Validation:** Strict `JSR-380` validation on all DTOs.
* **Error Handling:** Global handler returning standardized JSON errors.
* **i18n:** Localized error messages (EN/RU).

-----

## 🧪 Development (Manual Mode)

If you want to run the **Backend** manually (without Docker):

1.  **Navigate to backend directory:**
    ```bash
    cd backend
    ```
2.  **Configure Database:** Ensure MariaDB is running on `localhost:3306`.
3.  **Run Tests:**
    ```bash
    ./gradlew test
    ```
4.  **Run App:**
    ```bash
    ./gradlew bootRun
    ```

-----

## 📂 Project Structure

* `config/` - Security (JWT), Swagger, Global Exception Handler.
* `controller/` - REST Endpoints (secured via `@PreAuthorize`).
* `dto/` - Data Transfer Objects (Records) with Validation annotations.
* `mapper/` - MapStruct interfaces for DTO \<-\> Entity mapping.
* `service/` - Business Logic (Transactional).
* `repository/` - Spring Data JPA interfaces.
* `model/` - JPA Entities.

-----

© 2025 Haru Pharmacy Inc.