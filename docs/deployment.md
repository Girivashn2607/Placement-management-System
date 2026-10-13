# Deployment Guide: Production Architecture

## 1. Frontend Deployment (Vercel)
1. Push the code to a Git repository (GitHub/GitLab).
2. Go to [Vercel Dashboard](https://vercel.com) and click **Add New Project**.
3. Select the `frontend` directory as the Root Directory.
4. Set Build Command: `npm run build`
5. Set Output Directory: `dist`
6. Add Environment Variable:
   - `VITE_API_BASE_URL` = `https://your-backend-domain.com/api`
7. Click **Deploy**.

## 2. Backend Deployment (Java Hosting: Render / Railway / AWS Elastic Beanstalk)
1. Select a provider that supports Java 21 / Docker.
2. In the deployment configuration, set Root Directory to `backend`.
3. Set Build Command: `mvn clean package -DskipTests`
4. Set Run Command: `java -jar target/placement-system-backend-1.0.0.jar`
5. Configure Environment Variables:
   - `SPRING_DATASOURCE_URL` = `jdbc:mysql://<host>:<port>/<dbname>?useSSL=true`
   - `SPRING_DATASOURCE_USERNAME` = `<user>`
   - `SPRING_DATASOURCE_PASSWORD` = `<password>`
   - `JWT_SECRET` = `<your-secure-base64-secret>`
   - `CORS_ALLOWED_ORIGINS` = `https://your-frontend.vercel.app`
   - `GEMINI_API_KEY` = `<your-google-ai-api-key>`

## 3. Database Deployment (Aiven / PlanetScale / AWS RDS MySQL)
1. Provision a MySQL 8.x managed instance.
2. Whitelist your backend hosting IP or allow secure SSL connections.
3. Apply `database/schema.sql` via MySQL Workbench using connection parameters.
