# Bank Credit Risk Monitor

A full-stack **Java 17 + Spring Boot 3.2 + MySQL + Thymeleaf** application for monitoring bank credit risk, built on Indian banking standards (RBI, CIBIL, NPA/IRACP norms).

---

## Tech Stack

| Layer       | Technology                          |
|-------------|-------------------------------------|
| Backend     | Java 17, Spring Boot 3.2.3          |
| ORM         | Spring Data JPA / Hibernate         |
| Database    | MySQL 8.x                           |
| Templates   | Thymeleaf 3                         |
| Charts      | Chart.js 4.4 (CDN)                  |
| Build       | Maven 3.9+                          |
| Testing     | JUnit 5, Mockito                    |

---

## Project Structure

```
credit-monitor/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/com/bank/creditmonitor/
    │   │   ├── CreditMonitorApplication.java
    │   │   ├── config/
    │   │   │   └── GlobalExceptionHandler.java
    │   │   ├── controller/
    │   │   │   ├── DashboardController.java   ← UI pages
    │   │   │   └── ApiController.java         ← REST API
    │   │   ├── dto/
    │   │   │   ├── DashboardDTO.java
    │   │   │   ├── BorrowerSummaryDTO.java
    │   │   │   ├── BorrowerDetailDTO.java
    │   │   │   ├── CreditAccountDTO.java
    │   │   │   ├── CibilScoreHistoryDTO.java
    │   │   │   ├── AlertDTO.java
    │   │   │   └── NpaTrendDTO.java
    │   │   ├── model/
    │   │   │   ├── Borrower.java
    │   │   │   ├── CreditAccount.java
    │   │   │   ├── CibilScore.java
    │   │   │   ├── CreditAlert.java
    │   │   │   ├── NpaTrend.java
    │   │   │   └── PortfolioSummary.java
    │   │   ├── repository/
    │   │   │   ├── BorrowerRepository.java
    │   │   │   ├── CreditAccountRepository.java
    │   │   │   ├── CibilScoreRepository.java
    │   │   │   ├── CreditAlertRepository.java
    │   │   │   ├── NpaTrendRepository.java
    │   │   │   └── PortfolioSummaryRepository.java
    │   │   └── service/
    │   │       ├── DashboardService.java
    │   │       ├── BorrowerService.java
    │   │       └── AlertService.java
    │   └── resources/
    │       ├── application.properties
    │       ├── data.sql                       ← Schema + seed data
    │       ├── static/
    │       │   ├── css/main.css
    │       │   └── js/
    │       │       ├── main.js
    │       │       └── dashboard.js
    │       └── templates/
    │           ├── dashboard.html
    │           ├── borrowers.html
    │           ├── borrower-detail.html
    │           ├── alerts.html
    │           └── error.html
    └── test/
        └── java/com/bank/creditmonitor/
            └── DashboardServiceTest.java
```

---

## Prerequisites

- **Java 17+** — [Download](https://adoptium.net/)
- **Maven 3.9+** — [Download](https://maven.apache.org/)
- **MySQL 8.x** — [Download](https://dev.mysql.com/downloads/)

---

## Setup & Run

### 1. Create MySQL database

```sql
CREATE DATABASE credit_monitor_db;
CREATE USER 'credituser'@'localhost' IDENTIFIED BY 'yourpassword';
GRANT ALL PRIVILEGES ON credit_monitor_db.* TO 'credituser'@'localhost';
FLUSH PRIVILEGES;
```

### 2. Update application.properties

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/credit_monitor_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Kolkata
spring.datasource.username=credituser
spring.datasource.password=yourpassword
```

### 3. Build & Run

```bash
# Clone / unzip the project
cd credit-monitor

# Build
mvn clean install -DskipTests

# Run
mvn spring-boot:run
```

The app starts on **http://localhost:8080**

> On first run, Spring auto-creates all tables and seeds data via `data.sql`.

---

## Pages

| URL                    | Description                          |
|------------------------|--------------------------------------|
| `http://localhost:8080/`         | Dashboard — KPIs, charts, watch list |
| `http://localhost:8080/borrowers` | All borrowers with filter & search   |
| `http://localhost:8080/borrowers/{id}` | Borrower detail — accounts, CIBIL history, alerts |
| `http://localhost:8080/alerts`   | Active alerts — filter by severity, resolve |

---

## REST API Endpoints

| Method | URL                              | Description                    |
|--------|----------------------------------|--------------------------------|
| GET    | `/api/v1/dashboard`              | Full dashboard data (JSON)     |
| GET    | `/api/v1/borrowers`              | All borrowers                  |
| GET    | `/api/v1/borrowers?segment=MSME` | Filter by segment              |
| GET    | `/api/v1/borrowers?search=Apex`  | Search by name                 |
| GET    | `/api/v1/borrowers/{id}`         | Borrower detail                |
| GET    | `/api/v1/alerts`                 | All open alerts                |
| GET    | `/api/v1/alerts?severity=CRITICAL` | Filter by severity           |
| PUT    | `/api/v1/alerts/{id}/resolve`    | Resolve an alert               |
| GET    | `/api/v1/npa-trend`              | 12-month NPA trend data        |

---

## Database Schema

### Tables

| Table               | Purpose                                    |
|---------------------|--------------------------------------------|
| `borrowers`         | Borrower master — PAN, GSTIN, segment      |
| `credit_accounts`   | Loan/CC/OD accounts per borrower           |
| `cibil_scores`      | CIBIL score history per borrower           |
| `credit_alerts`     | Risk alerts — severity, type, resolution   |
| `npa_trend`         | Monthly NPA ratio snapshots                |
| `portfolio_summary` | Overall portfolio KPIs per snapshot date   |

### Seed Data

- **15 borrowers** across Corporate, MSME, Housing Loan, Trade Finance, Startup segments
- **16 credit accounts** — Term Loans, CC, OD, LC, BG, Home Loan
- **23 CIBIL score records** — latest + history per borrower
- **8 alerts** — CRITICAL, WARNING, INFO, OK severities
- **12 months** of NPA trend data (Apr 2025 – Mar 2026)
- **1 portfolio summary** snapshot for Mar 2026

---

## Running Tests

```bash
mvn test
```

---

## Configuration Reference

```properties
# Change port
server.port=9090

# Disable SQL logging in production
spring.jpa.show-sql=false
logging.level.com.bank=INFO

# Turn off auto-seeding after first run
spring.sql.init.mode=never
```

---

## Indian Banking Standards

This application is built around RBI guidelines:

- **CIBIL Score** bands: Prime (750+), Near-prime (650–749), Subprime (550–649), High Risk (<550)
- **NPA classification** per IRACP norms — 90 DPD = NPA
- **PCR** (Provision Coverage Ratio) — minimum 70% per RBI mandate
- **CRAR** (Capital Adequacy Ratio) — minimum 9% per Basel III / RBI
- **Segments**: Corporate, MSME, Housing Loan, Trade Finance, Personal Loan
- Currency in **₹ Crore** throughout

---

## License

MIT — Free for educational and commercial use.
