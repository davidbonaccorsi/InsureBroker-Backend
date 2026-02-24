# InsureBroker - Backend Documentation & API 🛡️

The core engine powering the InsureBroker platform. Built with Spring Boot 3 and Java 21, this REST API handles everything from dynamic premium calculations to on-the-fly PDF policy generation and stateless RBAC security.

## 🛠️ Tech Stack

- **Java 21**
- **Spring Boot 3** (Web, Data JPA, Security)
- **MySQL** (Relational Data & Hibernate ORM)
- **JWT** (JSON Web Tokens for stateless auth)
- **OpenPDF** (Dynamic document generation)
- **Lombok** (Because life is too short for getters and setters)

## 🧠 Under the Hood

This isn't just a basic CRUD app. Here's what makes it tick:

- **Dynamic Pricing Engine**: Calculates insurance premiums on the fly. It evaluates a hybrid formula `Base Premium + (Sum Insured * Daily Rate * Coverage Days)` and supports exponential custom multipliers (e.g., custom risk factors defined by admins).
- **Extensible Product Models**: Admins can attach custom fields (dropdowns, checkboxes, numbers) to insurance products dynamically. The backend parses these JSON configurations and applies mathematical conditions to the final premium.
- **On-the-Fly PDF Generation**: Uses `OpenPDF` to draw and stream official insurance policies directly to the client, complete with dynamic tables, transaction UUIDs, and drawn approval stamps.
- **Activity Audit Trail**: Every critical action (policy creation, document download, client updates, etc.) is logged via the `ActivityLogService` for complete system traceability.
- **File Management**: Secure, restricted endpoints for uploading and downloading proofs of payment.

## 🚀 Running Locally

### Prerequisites
- JDK 21
- MySQL Server

### Setup

1. **Database Setup**
   Create a local MySQL database (e.g., `insurebroker`).

2. **Configure Environment**
   Update the `src/main/resources/application.properties` file with your database credentials:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/insurebroker
   spring.datasource.username=root
   spring.datasource.password=your_password


*Note: `spring.jpa.hibernate.ddl-auto=update` is set by default, so the schema will generate automatically on the first run.*

3. **Fire it up**
Use the included Maven wrapper:
```bash
./mvnw spring-boot:run
```


*The API will start on `http://localhost:8081`.*

## 📡 Key API Routes

* `POST /api/auth/login` - Authenticate and get your JWT.
* `POST /api/premium/calculate` - The brain of the app. Takes coverage data, evaluates custom field rules, and returns a detailed premium breakdown.
* `GET /api/policies/{id}/pdf` - Generates and streams the official PDF policy.
* `POST /api/policies/{id}/upload-proof` - Handles multipart file uploads for payment validation.
* `GET /api/products` - Returns insurance products along with their mapped dynamic custom fields.

---
