# 🏥 Pharmacy POS System (Backend)

Enterprise-grade backend for pharmaceutical retail management.
Implements a secure POS (Point of Sale) system with inventory tracking, batch management (FEFO), and role-based access control.

> **Stack:** Java 21, Spring Boot 3, MariaDB, Docker, JWT.

---

## ⚡ Quick Start (Docker)

**Prerequisites:** [Docker Desktop](https://www.docker.com/products/docker-desktop/) or Docker Engine.

You don't need to install Java or MariaDB manually. Just run:

```bash
docker-compose up --build
````

The system will start automatically:

* **Backend API:** `http://localhost:8080`
* **Database:** `localhost:3306` (inside container)

### 📖 API Documentation

Once started, open Swagger UI to explore and test endpoints:
👉 **[http://localhost:8080/swagger-ui.html](https://www.google.com/search?q=http://localhost:8080/swagger-ui.html)**

-----

## 🔐 Access & Security

The system is secured with **JWT (JSON Web Tokens)**.

### Default Admin Credentials

A bootstrap admin account is created on the first launch:

* **Username:** `admin`
* **Password:** `admin` (or check console logs if randomized via ENV)

### How to Authenticate

1.  **Login:** Send `POST /auth/login` with credentials.
2.  **Get Token:** Response contains `{ "token": "eyJ...", "role": "ADMIN" }`.
3.  **Use Token:** Add header to all protected requests:
    `Authorization: Bearer <your_token>`

> **Note:** The API implements **RBAC** (Role-Based Access Control).
>
>   * `ADMIN`: Full access (Manage Users, Inventory, Suppliers).
>   * `PHARMACIST`: Operational access (Sales, Customers, Read-only Medicine list).

-----

## 🛠️ Key Features

### 1\. Advanced Inventory Management (FEFO)

The system uses the **First Expired, First Out** strategy.

* Medicines are tracked by **Batches** (with expiration dates).
* When a sale occurs, the system automatically finds and deducts stock from the oldest batch first.
* Transactional integrity ensures data consistency during concurrent sales.

### 2\. Enterprise-Grade Validation

Strict data validation using **JSR-380 (Bean Validation)**.

* Prevents invalid data entry (e.g., negative prices, past expiration dates).
* Returns standardized `400 Bad Request` responses with detailed error messages.

### 3\. Localization (i18n)

Supports multi-language responses for errors and validation messages.

* Default: English
* Switchable via `Accept-Language` header (e.g., `ru`).

-----

## 🧪 Development & Testing

If you want to run the project manually (without Docker):

**Requirements:** Java 21, local MariaDB instance (created database `pharmacy_db`).

1.  **Configure Database:**
    Update `src/main/resources/application.properties` if needed.
2.  **Run Tests:**
    ```bash
    ./gradlew test
    ```
    *Includes 20+ Unit tests with Mockito covering critical business logic.*
3.  **Build JAR:**
    ```bash
    ./gradlew bootJar
    ```
4.  **Run App:**
    ```bash
    java -jar build/libs/pharmacy-0.0.1-SNAPSHOT.jar
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