# Haru Pharmacy: Enterprise Management System

![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.1-brightgreen?style=for-the-badge&logo=springboot)
![Angular](https://img.shields.io/badge/Angular-21.1.0-dd0031?style=for-the-badge&logo=angular)
![Java](https://img.shields.io/badge/Java-21_(LTS)-orange?style=for-the-badge&logo=openjdk)
![Docker](https://img.shields.io/badge/Deployment-Docker-2496ed?style=for-the-badge&logo=docker)

> **A Next-Generation Point of Sale (POS) & Inventory System.**
> Built to demonstrate a strict **Contract-First** architecture using the bleeding edge of the Java ecosystem.

---

## 🏗 Key Architectural Decisions

This project implements strict engineering patterns to ensure scalability, type safety, and maintainability.

### 1. Contract-First Development (OpenAPI)
Instead of manually matching frontend and backend types, the project treats the **API Specification** as the absolute source of truth.
* **Why:** To eliminate regression bugs caused by silent API changes (e.g., renamed fields).
* **How:** The Backend exposes a live OpenAPI v3 spec. The Frontend client is **auto-generated** (`npm run generate-api`), ensuring 100% synchronization at compile time.

### 2. Deterministic Data State
* **Database Migrations:** Schema changes are versioned via **Flyway** (`V1__init_schema.sql`). This guarantees identical database structures across Development, Testing, and Production environments.
* **DTO Mapping:** Usage of **MapStruct** for compile-time, zero-overhead mapping between Entities and Data Transfer Objects.

### 3. Stateless Security Model
* **Mechanism:** JWT (JSON Web Token) with per-request filters (`JwtFilter`).
* **Access Control:** Role-Based Access Control (RBAC) separating Admin and Pharmacist privileges.

---

## ⚡ Tech Stack (Bleeding Edge 2026)

The system leverages the latest stable and rolling releases available as of Q1 2026.

| Layer        | Technology         | Version      | Rationale                                                 |
|:-------------|:-------------------|:-------------|:----------------------------------------------------------|
| **Backend**  | **Spring Boot**    | **4.0.1**    | Virtual Threads, Spring Framework 7, Structured Logging.  |
| **Language** | **Java**           | **21 (LTS)** | Records, Pattern Matching, Sequenced Collections.         |
| **Frontend** | **Angular**        | **21.1.0**   | **Zoneless** Change Detection, Signals-based Reactivity.  |
| **Database** | **MariaDB**        | **12.1.2**   | Rolling release with advanced vector search capabilities. |
| **DevOps**   | **Docker Compose** | **Spec v2**  | Full infrastructure instantiation (Database + API).       |

---

## 🛠 Developer Workflow

### Prerequisites
* Java 21 JDK
* Node.js 22+
* Docker & Docker Compose

### 🚀 Quick Start (Full Environment)

The fastest way to get the system running (Database + Backend + Frontend):

```bash
# 1. Clone the repository
git clone https://github.com/Kitty-Hivens/pharmacy.git

# 2. Spin up Infrastructure (Database)
docker-compose up -d

# 3. Start Backend (Terminal 1)
cd backend
./gradlew bootRun

# 4. Start Frontend (Terminal 2)
cd frontend
npm install
ng serve

```

* **App URL:** `http://localhost:4200`
* **Swagger API Docs:** `http://localhost:8080/swagger-ui/index.html`

### 🔄 Regenerating API Client

If you modify the Backend DTOs/Controllers, run this to sync the Frontend:

```bash
cd frontend
npm run generate-api

```

---

## 🔮 Project Roadmap

* [x] **Core:** User Management, Security, JWT, Database Migrations.
* [x] **Inventory:** SKU tracking, Stock Management, Supplier Relations.
* [x] **POS Logic:** Sale processing, Transaction Atomicity.
* [ ] **Dashboard UI:** Advanced Charts & Analytics (Active Development).
* [ ] **Notification Service:** Low-stock alerts via WebSocket.

## ⚖️ License

**MIT License**.
Developed by **Haru (Vitalii)** as a professional portfolio project.
