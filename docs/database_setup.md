# MySQL Database Setup & Workbench Guide

## 1. Database Requirements
- MySQL 8.0+ Community Server (running on localhost:3306)
- MySQL Workbench

## 2. Creating Database via MySQL Workbench
1. Open **MySQL Workbench**.
2. Connect to your Local Instance (`root@localhost:3306`).
3. Open a new SQL Query tab (`File` > `New Query Tab`).
4. Paste the contents of `database/schema.sql` and click the **Lightning Bolt** execution icon.
5. Next, open `database/seed_data.sql` and run it to populate skills, sample companies, and announcements.
6. In the left **Schemas** panel, right-click and choose **Refresh All**. You will see `placement_db` with all tables.

## 3. Alternative: Setup via Command Prompt / PowerShell
Run the following in Windows PowerShell or CMD:

```powershell
# Open MySQL CLI
mysql -u root -p

# Inside MySQL prompt:
source c:/Users/ssgir/OneDrive/Desktop/Projects/FSD/Placement management System1/database/schema.sql;
source c:/Users/ssgir/OneDrive/Desktop/Projects/FSD/Placement management System1/database/seed_data.sql;
exit;
```

## 4. Configuring Backend Credentials
The backend reads datasource settings from environment variables or defaults to:
- URL: `jdbc:mysql://localhost:3306/placement_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC`
- User: `root`
- Password: defined in `SPRING_DATASOURCE_PASSWORD` (or configured in `backend/src/main/resources/application.yml`).
