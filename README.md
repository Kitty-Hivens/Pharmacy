# 🏥 Haru Pharmacy Management System

[![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.1-brightgreen?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Angular](https://img.shields.io/badge/Angular-21.2.5-dd0031?style=for-the-badge&logo=angular&logoColor=white)](https://angular.io/)
[![Java](https://img.shields.io/badge/Java-21_(LTS)-orange?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.9-blue?style=for-the-badge&logo=typescript&logoColor=white)](https://www.typescriptlang.org/)
[![Docker](https://img.shields.io/badge/Docker-Ready-2496ed?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)
[![CI/CD](https://img.shields.io/badge/CI%2FCD-GitHub_Actions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white)](https://github.com/Kitty-Hivens/Pharmacy/actions)
[![Live](https://img.shields.io/badge/Live-pharmacy.hivens.dev-success?style=for-the-badge&logo=googlechrome&logoColor=white)](https://pharmacy.hivens.dev)
[![License](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)](LICENSE)

> **Enterprise-Grade Point of Sale & Inventory Management System**  
> A production-ready fullstack application demonstrating modern software architecture, clean code practices, and advanced Spring Boot 4 / Angular 21 features.

🔗 **Live Demo:** [https://pharmacy.hivens.dev](https://pharmacy.hivens.dev)

---

## 🎯 Project Highlights

**Created by a Backend Developer** who decided to master the full stack — this project showcases end-to-end ownership from database design to pixel-perfect UI.

### Why This Project Stands Out

✨ **Bleeding-Edge Stack** — Built with Spring Boot 4.0.1 and Angular 21.2.5 (latest stable releases as of Q1 2026)  
🏗️ **Production Architecture** — Contract-First API design, CQRS patterns, optimistic locking, RBAC security  
🧪 **Test Coverage** — Comprehensive JUnit tests with Mockito for business-critical logic  
📦 **Docker Ready** — Full containerization with multi-stage builds and health checks  
🚀 **CI/CD Pipeline** — Automated deployment to VPS via GitHub Actions on every push  
🌍 **i18n Support** — Bilingual interface (English/Russian) with dynamic language switching  
🎨 **Professional UI** — Built with PrimeNG, responsive design, and modern UX patterns

---

## 📸 Screenshots

| Dashboard                                    | POS Terminal                     |
|----------------------------------------------|----------------------------------|
| ![Dashboard](docs/screenshots/dashboard.png) | ![POS](docs/screenshots/pos.png) |

| Inventory Management                         | Sales History                        |
|----------------------------------------------|--------------------------------------|
| ![Medicines](docs/screenshots/medicines.png) | ![Sales](docs/screenshots/sales.png) |

---

## 🚀 Key Features

### 🛒 Point of Sale (POS) Terminal
- **Real-time stock validation** — Server-side checks prevent overselling
- **Customer discount management** — Automatic price calculations
- **Receipt generation** — Print-ready transaction summaries
- **Cart persistence** — LocalStorage backup for session recovery

### 📦 Inventory Management
- **FEFO Algorithm** (First Expired, First Out) — Smart batch rotation for pharmaceutical compliance
- **Batch tracking** — Full traceability with expiration date monitoring
- **Low-stock alerts** — Proactive notifications for reordering
- **Supplier integration** — Manage supply chain relationships

### 📊 Business Intelligence
- **Sales analytics** — Revenue tracking with growth metrics
- **Customer insights** — Registration trends and loyalty tracking
- **Inventory health** — Stock status visualization
- **Historical reporting** — Paginated sales history with date filtering

### 🔐 Security & Access Control
- **JWT Authentication** — Stateless token-based security
- **Role-Based Access Control** — Admin vs Pharmacist permissions
- **Password encryption** — BCrypt hashing with Spring Security
- **CORS configuration** — Secure cross-origin request handling

### 🎨 Modern Frontend
- **Zoneless Angular 21** — Signals-based reactivity for optimal performance
- **PrimeNG UI Framework** — Enterprise-grade components
- **Auto-generated API Client** — TypeScript SDK from OpenAPI spec
- **Responsive Design** — Mobile-first approach with PrimeFlex

---

## 🛠️ Technology Stack

### Backend (Spring Boot 4 + Java 21)
```java
✓ Spring Boot 4.0.1         → Virtual Threads, Structured Logging
✓ Java 21 (LTS)             → Records, Pattern Matching, Sequenced Collections
✓ Spring Data JPA           → Database abstraction with Hibernate
✓ Spring Security 7         → JWT + RBAC implementation
✓ Flyway 10+                → Versioned database migrations
✓ MapStruct 1.5.5           → Compile-time DTO mapping
✓ SpringDoc OpenAPI 2.7     → API documentation generation
✓ MariaDB 10.11             → Relational database with advanced features
✓ JUnit 5 + Mockito         → Comprehensive unit testing
```

### Frontend (Angular 21 + TypeScript 5)
```typescript
✓ Angular 21.2.5            → Zoneless change detection, Signals API
✓ PrimeNG 21.1.3            → UI component library
✓ RxJS 7.8                  → Reactive programming
✓ OpenAPI Generator         → Auto-generated TypeScript client
✓ ngx-translate             → Internationalization (i18n)
✓ TypeScript 5.9            → Type safety with strict mode
```

### DevOps & Tools
```yaml
✓ Docker Compose            → Multi-container orchestration
✓ GitHub Actions            → CI/CD — auto deploy on push to main
✓ Nginx + Let's Encrypt     → Reverse proxy with HTTPS
✓ Gradle 8.14               → Build automation
✓ npm 11.7.0                → Frontend package management
✓ Git                       → Version control with conventional commits
```

---

## 🏛️ Architecture Decisions

### 1️⃣ Contract-First Development (OpenAPI)
Instead of manually syncing frontend/backend types, the **API specification is the single source of truth**.

**Benefits:**
- Zero type mismatches at runtime
- Breaking changes caught at compile time
- Auto-generated, always up-to-date client SDK

**Implementation:**
```bash
# Backend exposes /v3/api-docs
# Frontend generates client:
npm run generate-api
```

### 2️⃣ FEFO Inventory Algorithm
Pharmaceutical compliance requires selling items closest to expiration first.

**Query Strategy:**
```sql
SELECT * FROM inventory
WHERE medicine_id = :id
  AND stock_quantity > 0
  AND expiration_date >= CURRENT_DATE
ORDER BY expiration_date ASC
```

**Atomic Transaction:**
```java
@Transactional
public void createSale(SaleCreateDto dto, String username) {
    // 1. Fetch valid batches (FEFO)
    List<Inventory> batches = inventoryRepo.findValidBatchesForSale(medicineId, LocalDate.now());
    
    // 2. Validate total stock
    int totalStock = batches.stream().mapToInt(Inventory::getStockQuantity).sum();
    if (totalStock < requestedQty) throw new InsufficientStockException();
    
    // 3. Deduct from batches (oldest first)
    for (Inventory batch : batches) {
        // Optimistic locking (@Version) prevents race conditions
    }
}
```

### 3️⃣ Optimistic Locking for Concurrency
High-traffic POS scenarios require safe concurrent stock updates.

**Implementation:**
```java
@Entity
public class Inventory {
    @Version
    private Long version; // Hibernate automatic versioning
}
```

**Behavior:** If two cashiers sell the same item simultaneously, the second transaction will fail with `OptimisticLockingFailureException`, triggering a retry.

### 4️⃣ DTO Projection Queries
Avoid N+1 queries by fetching aggregated data in a single SQL query.

**Before (N+1 Problem):**
```java
// 1 query for medicines + N queries for stock
for (Medicine m : medicines) {
    int stock = inventoryRepo.sumByMedicine(m.getId());
}
```

**After (Single Query):**
```java
@Query("""
    SELECT new MedicineResponseDto(
        m.id, m.name, m.price, 
        COALESCE(SUM(i.stockQuantity), 0L)
    )
    FROM Medicine m
    LEFT JOIN Inventory i ON i.medicine.id = m.id
    GROUP BY m.id
""")
List<MedicineResponseDto> findAllSummarized();
```

### 5️⃣ Flyway Database Versioning
All schema changes are tracked in migration files.

**Example:**
```
backend/src/main/resources/db/migration/
├── V1__init_schema.sql       → Initial tables
├── V2__add_version_column.sql → Add optimistic locking
└── V3__create_indexes.sql     → Performance optimization
```

**Result:** Identical database structure across Dev/Test/Prod environments.

---

## 📦 Getting Started

### Prerequisites
```bash
Java 21 JDK       → https://adoptium.net/
Node.js 22+       → https://nodejs.org/
Docker & Compose  → https://www.docker.com/
```

### 🚀 Quick Start (Docker)
```bash
# Clone repository
git clone https://github.com/Kitty-Hivens/Pharmacy.git
cd Pharmacy

# Start all services (DB + Backend + Frontend)
docker-compose up -d

# Access application
🌐 Frontend → http://localhost
📚 API Docs → http://localhost:8080/swagger-ui.html
🗄️ Database → localhost:3307 (root/root)
```

### 🔑 Default Credentials
```
Username: admin
Password: Set via ADMIN_INITIAL_PASSWORD environment variable
```

### 💻 Local Development
```bash
# Terminal 1: Database
docker-compose up -d db

# Terminal 2: Backend
cd backend
./gradlew bootRun

# Terminal 3: Frontend
cd frontend
npm install
ng serve

# Docker: http://localhost
# Local dev: http://localhost:4200
```

### 🔄 Regenerate API Client
After modifying backend DTOs/Controllers:
```bash
cd frontend
npm run generate-api
```

---

## 📂 Project Structure

```
pharmacy-management/
├── backend/                    # Spring Boot 4 Application
│   ├── src/main/java/
│   │   └── haru/pharmacy/
│   │       ├── config/         # Security, OpenAPI, CORS
│   │       ├── controller/     # REST endpoints
│   │       ├── dto/            # Request/Response objects
│   │       ├── exception/      # Custom exceptions
│   │       ├── mapper/         # MapStruct interfaces
│   │       ├── model/          # JPA entities
│   │       ├── repository/     # Spring Data repositories
│   │       └── service/        # Business logic
│   ├── src/main/resources/
│   │   ├── db/migration/       # Flyway SQL scripts
│   │   ├── messages.properties # i18n (EN)
│   │   └── messages_ru.properties # i18n (RU)
│   └── src/test/java/          # JUnit + Mockito tests
│
├── frontend/                   # Angular 21 Application
│   ├── src/app/
│   │   ├── api/                # Auto-generated OpenAPI client
│   │   ├── core/               # Guards, Interceptors
│   │   ├── layout/             # Main layout component
│   │   └── pages/              # Feature modules
│   │       ├── dashboard/
│   │       ├── pos/            # Point of Sale
│   │       ├── medicines/
│   │       ├── inventory/
│   │       ├── sales-history/
│   │       ├── customers/
│   │       ├── suppliers/
│   │       └── users/
│   └── public/i18n/            # Translation files
│
├── .github/workflows/
│   └── deploy.yml              # GitHub Actions CI/CD
└── docker-compose.yml          # Multi-container setup
```

---

## 📚 API Documentation

### Live Interactive Docs
🔗 **Swagger UI:** `https://pharmacy.hivens.dev/swagger-ui.html`

### Sample Endpoints
```http
POST /auth/login                → Authenticate user
GET  /api/medicines             → List all medicines
POST /api/inventory/restock     → Add stock (ADMIN only)
POST /api/sales                 → Process sale transaction
GET  /api/sales?from=&to=       → Sales history with filters
POST /api/customers             → Register customer
GET  /api/users                 → List employees (ADMIN only)
```

### Authentication Flow
```javascript
// 1. Login
POST /auth/login
{
  "username": "admin",
  "password": "your_password"
}
// Response: { "token": "eyJhbGc...", "role": "ADMIN" }

// 2. Use token in subsequent requests
GET /api/medicines
Headers: { Authorization: "Bearer eyJhbGc..." }
```

---

## 🧪 Testing

### Backend Unit Tests
```bash
cd backend
./gradlew test

# Coverage Report
./gradlew test jacocoTestReport
open build/reports/jacoco/test/html/index.html
```

**Test Examples:**
- ✅ Medicine CRUD operations
- ✅ FEFO algorithm correctness
- ✅ Stock validation (insufficient stock scenarios)
- ✅ Optimistic locking conflict handling
- ✅ User authentication & authorization

### Frontend E2E Tests (Playwright)
```bash
cd frontend
npm run test:e2e
```

---

## 🚢 Deployment

### Production Build
```bash
# Backend JAR
cd backend
./gradlew bootJar
# Output: build/libs/Pharmacy-0.0.2-SNAPSHOT.jar

# Frontend (Static Assets)
cd frontend
npm run build
# Output: dist/frontend/browser/
```

### Environment Variables
```bash
# Backend (.env or docker-compose)
DB_URL=jdbc:mariadb://db:3306/pharmacy_db
DB_USERNAME=root
DB_PASSWORD=secure_password
JWT_SECRET=YourVeryLongSecretKey...
APP_CORS_ALLOWED_ORIGINS=https://yourdomain.com
ADMIN_INITIAL_PASSWORD=ChangeMe123!

# Frontend (environment.prod.ts)
apiUrl=https://api.yourdomain.com
```

### CI/CD Pipeline
Every push to the `Central-Workflow` branch triggers automatic deployment:

```yaml
# .github/workflows/deploy.yml
on:
  push:
    branches: [Central-Workflow]
jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: appleboy/ssh-action@v1
        with:
          script: |
            cd /root/Pharmacy
            git pull
            docker compose up --build -d
```

### Docker Production
```yaml
# docker-compose.yml
services:
  backend:
    image: pharmacy-backend:latest
    environment:
      - SPRING_PROFILES_ACTIVE=prod
  
  frontend:
    image: pharmacy-frontend:latest
    
  nginx:
    image: nginx:alpine
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf
```

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📧 Contact

**Vitalii (Haru)**  
🔗 GitHub: [@Kitty-Hivens](https://github.com/Kitty-Hivens)  
📧 Email: vitalii.vakar@proton.me  
💼 LinkedIn: [Vitalii Vakar](https://linkedin.com/in/vitalii-vakar)

---

## 📜 License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.

---

## 🙏 Acknowledgments

- **Spring Team** — For the incredible framework and documentation
- **Angular Team** — For pushing the boundaries of frontend development
- **PrimeNG** — For the professional UI component library
- **OpenAPI Initiative** — For standardizing API specifications

---

<div align="center">

### ⭐ If this project helped you, please consider giving it a star!

</div>
