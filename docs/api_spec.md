# Placement Management System - REST API Specification

Base URL: `http://localhost:8080/api`

## Authentication (`/api/auth`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/auth/register` | Public | Register Student or Recruiter. Public ADMIN registration blocked. |
| `POST` | `/api/auth/login` | Public | Authenticate user with email and password, returns JWT token. |
| `GET` | `/api/auth/me` | Authenticated | Fetch current user session details. |
| `POST` | `/api/auth/change-password` | Authenticated | Change user account password. |

## Student (`/api/student`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/student/dashboard` | Student | Overview statistics: Profile completion %, eligible count, notices. |
| `GET` | `/api/student/profile` | Student | Fetch complete placement profile. |
| `PUT` | `/api/student/profile` | Student | Update academics, CGPA, graduation batch, skills, projects, internships. |

## Opportunities (`/api/opportunities`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/opportunities/public` | Public | Paginated public opportunities. |
| `GET` | `/api/opportunities` | Authenticated | Filtered opportunities with student-specific eligibility evaluations. |
| `GET` | `/api/opportunities/{id}` | Authenticated | Detailed opportunity breakdown, unmet criteria, skill match %. |
| `POST` | `/api/opportunities` | Admin / Recruiter | Create new placement or internship opening. |

## AI Resume Builder & Analyzer (`/api/resumes`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/resumes/builder/save` | Student | Save structured resume JSON to MySQL. |
| `POST` | `/api/resumes/upload` | Student | Upload PDF (up to 500 MB), parse via Apache PDFBox, run AI analysis. |
| `POST` | `/api/resumes/ai/suggest` | Student | AI rewrite using XYZ action formula. |
| `GET` | `/api/resumes/latest-analysis` | Student | Fetch latest advisory score and skill breakdown. |
| `GET` | `/api/resumes/{id}/download` | Student | Generate and download PDF rendered via OpenPDF. |

## Teximo AI Chatbot (`/api/chat`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/chat/teximo` | Authenticated | Placement, interview, and career guidance chatbot. Non-career queries redirected. |

## Grievances & Complaints (`/api/complaints`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/complaints` | Student | Submit complaint (Drive issue, Schedule clash, etc.). |
| `GET` | `/api/complaints/my` | Student | View own complaint history and admin responses. |
| `GET` | `/api/complaints` | Admin | View all complaints with status filtering. |
| `PATCH` | `/api/complaints/{id}/status` | Admin | Update complaint status and provide official response. |

## Administrator Console (`/api/admin`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/admin/overview` | Admin | KPIs: Total students, companies, drives, complaints pipeline. |
| `GET` | `/api/admin/students` | Admin | Filterable student records (by Dept, Batch, CGPA). |
| `GET` | `/api/admin/students/export-csv`| Admin | Download verified student records in CSV format. |
| `GET` | `/api/admin/companies` | Admin | List recruitment partners. |
| `POST` | `/api/admin/companies` | Admin | Register new recruitment company. |
| `PATCH` | `/api/admin/opportunities/{id}/status` | Admin | Approve recruiter-submitted openings. |
| `POST` | `/api/admin/announcements` | Admin | Post university placement bulletin. |
| `GET` | `/api/admin/drives` | Admin | List placement drives. |
| `POST` | `/api/admin/drives` | Admin | Schedule campus placement drive. |

## Recruiter Console (`/api/recruiter`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/recruiter/my-company` | Recruiter | View company details. |
| `PUT` | `/api/recruiter/my-company` | Recruiter | Update company details. |
| `GET` | `/api/recruiter/my-opportunities` | Recruiter | View company openings. |
| `GET` | `/api/recruiter/candidates` | Recruiter | View graduating candidates. |
