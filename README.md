# AI-Powered Career & Placement Management Platform

A full-stack, enterprise-grade university placement and career management system built with **Java 21 Spring Boot**, **React (Vite)**, **MySQL**, and official **Google Gemini AI API** integration.

---

## 1. Project Overview
A centralized campus platform designed for:
- **Students**: Manage verified placement profiles, view transparent eligibility evaluations, create ATS-compliant single-page resumes, analyze uploaded PDFs with AI, file grievances, and consult **Teximo** (the AI placement chatbot).
- **Placement Administrators**: Track registration metrics, manage corporate partner companies, schedule placement drives, approve recruiter openings, review grievance pipelines, and export student reports to CSV.
- **Corporate Recruiters**: Maintain verified company profiles, publish campus openings with mandatory skills & CGPA cutoffs, and view eligible candidates.

> **Policy Compliance Note**: In accordance with university placement guidelines, automatic web scraping and third-party automated job application uploads (LinkedIn / Naukri) are strictly disallowed.

---

## 2. Technology Stack

### Frontend
- **Framework**: React 18 with Vite
- **Styling**: Vanilla CSS3 Custom Design System (Responsive, Modern, Accessible)
- **Routing**: React Router v6 (Role-Based Protected Routes)
- **API Client**: Axios (with Bearer Token Interceptor)
- **Analytics Charts**: Recharts
- **Icons**: Lucide React

### Backend
- **Language**: Java 21 (JDK 21+)
- **Framework**: Spring Boot 3.3.4
- **Security**: Spring Security 6 with stateless JWT authentication
- **ORM / Persistence**: Spring Data JPA & Hibernate
- **Build Tool**: Apache Maven
- **PDF Extraction**: Apache PDFBox 3.x (Max 500 MB upload support)
- **PDF Generation**: OpenPDF (Clean single-page ATS-formatted resume export)

### Database
- **Database**: MySQL 8.0+ Community Server
- **Management**: MySQL Workbench / SQL CLI

### AI Integration
- **Engine**: Official Google Gemini API (Model: `gemini-1.5-flash`)
- **Security**: Secret key resides exclusively in backend environment variables.
- **Fail-Safe Heuristic Engine**: Intelligent fallback ensures resume analysis and Teximo chatbot function smoothly even before configuring an external API key.

---

## 3. Architecture & Design

```
+---------------------------------------------------------------------------------+
|                                 CLIENT TIER                                     |
|    React 18 + Vite SPA | Axios Interceptor | RBAC Routes | Teximo AI Widget     |
+---------------------------------------------------------------------------------+
                                       |
                              HTTPS / REST + JWT
                                       v
+---------------------------------------------------------------------------------+
|                                 BACKEND TIER                                    |
|   Spring Security (OncePerRequestFilter) -> REST Controllers -> Services Layer  |
|   - EligibilityService: Transparent skill & CGPA cutoff evaluations             |
|   - PdfParserService: Apache PDFBox safe text extraction                        |
|   - PdfGeneratorService: OpenPDF formatted resume generation                    |
|   - AiService: Official Gemini API client + Heuristic Placement Engine          |
+---------------------------------------------------------------------------------+
                                       |
                                     JDBC
                                       v
+---------------------------------------------------------------------------------+
|                                 DATABASE TIER                                   |
|   MySQL 8 (placement_db) InnoDB: Users, Profiles, Opportunities, Resumes, etc.   |
+---------------------------------------------------------------------------------+
```

---

## 4. Complete Project Directory Structure

```
Placement management System1/
├── backend/
│   ├── pom.xml
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/placement/
│   │   │   │   ├── PlacementApplication.java
│   │   │   │   ├── controller/      # Auth, Student, Opportunity, Resume, Chat, Admin, etc.
│   │   │   │   ├── dto/             # Data Transfer Objects
│   │   │   │   ├── entity/          # JPA Entities & Enums
│   │   │   │   ├── exception/       # GlobalExceptionHandler
│   │   │   │   ├── repository/      # Spring Data JPA Repositories
│   │   │   │   ├── security/        # JWT Utils, Filter, SecurityConfig
│   │   │   │   └── service/         # Business logic, AI, PDFBox, Eligibility
│   │   │   └── resources/
│   │   │       └── application.yml
│   │   └── test/
│   │       ├── java/com/placement/service/  # Unit & Mockito test suites
│   │       └── resources/application-test.yml
├── frontend/
│   ├── index.html
│   ├── package.json
│   ├── vite.config.js
│   └── src/
│       ├── main.jsx
│       ├── App.jsx
│       ├── components/    # Navbar, Sidebar, ProtectedRoute, TeximoWidget
│       ├── context/       # AuthContext (JWT session management)
│       ├── pages/         # Landing, Login, Register, Dashboards, Resume AI, etc.
│       ├── services/      # api.js Axios client
│       └── styles/        # index.css design system
├── database/
│   ├── schema.sql         # 18 normalized MySQL tables with constraints & indexes
│   └── seed_data.sql      # Skills, sample companies, announcements
├── docs/
│   ├── architecture.md
│   ├── api_spec.md
│   ├── database_setup.md
│   └── deployment.md
├── .gitignore
└── README.md
```

---

## 5. Prerequisites & Software Versions
Ensure the following tools are installed on your Windows computer:
1. **Java Development Kit**: JDK 21 or later (`java -version`)
2. **Apache Maven**: 3.9+ (`mvn -version`)
3. **Node.js**: v18+ or v20+ (`node -v`)
4. **MySQL Community Server**: 8.0+ running on port `3306`

---

## 6. Step-by-Step Setup on Windows

### Step 1: Create MySQL Database
Open **Windows PowerShell** or **MySQL Workbench** and execute:

```sql
CREATE DATABASE IF NOT EXISTS placement_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Run schema and initial seeds:
```powershell
mysql -u root -p placement_db < database/schema.sql
mysql -u root -p placement_db < database/seed_data.sql
```

### Step 2: Configure Backend Environment Variables
In `backend/src/main/resources/application.yml` (or via Windows PowerShell environment variables):

```powershell
$env:SPRING_DATASOURCE_USERNAME="root"
$env:SPRING_DATASOURCE_PASSWORD="YOUR_MYSQL_PASSWORD"
$env:GEMINI_API_KEY="YOUR_GEMINI_API_KEY" # Optional: AI falls back gracefully if left empty
```

### Step 3: Run Backend Tests & Start Server
In a terminal window:

```powershell
cd "backend"
# Run JUnit tests
mvn test

# Launch Spring Boot server
mvn spring-boot:run
```
Backend will start on: **`http://localhost:8080`**

### Step 4: Run Frontend Development Server
In a separate terminal window:

```powershell
cd "frontend"
npm install
npm run dev
```
Frontend will start on: **`http://localhost:5173`**

---

## 7. First Administrator Login & Default Credentials
For secure initial setup, the backend automatically seeds a default administrator account upon first startup if no administrator exists in MySQL:

- **Admin Email**: `admin@placement.edu`
- **Password**: `Admin@123`
- **Role**: `ADMIN`

> **Security Rule**: Public registration through `/register` permits only `STUDENT` and `RECRUITER` accounts. Public users can never assign themselves the `ADMIN` role.

---

## 8. Teximo AI Chatbot
The Teximo chatbot is located in the bottom-right corner of the website:
- Shows the official elephant avatar.
- Displays **"Ask me anything"** in its closed state.
- Answers placement procedure, interview, resume, and aptitude questions.
- Automatically and politely redirects off-topic questions.

---

## 9. Common Errors & Troubleshooting

| Error | Root Cause | Solution |
|---|---|---|
| `Access denied for user 'root'@'localhost'` | Incorrect MySQL password | Set `$env:SPRING_DATASOURCE_PASSWORD="your_password"` before starting Maven. |
| `Unknown database 'placement_db'` | Database not created | Run `CREATE DATABASE placement_db;` in MySQL Workbench. |
| `Port 8080 already in use` | Another process listening on port 8080 | Run `Get-Process -Id (Get-NetTCPConnection -LocalPort 8080).OwningProcess | Stop-Process -Force` in PowerShell. |
| `File size exceeds 500 MB` | Uploaded PDF larger than 500 MB limit | Upload a resume PDF under 500 MB. |

---

## 10. License
Academic CSE Project - Placement & Career Management Platform.
