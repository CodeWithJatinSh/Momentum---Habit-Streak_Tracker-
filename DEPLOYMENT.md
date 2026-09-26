# Momentum Deployment Guide

This project is a full-stack application composed of:
1. **Frontend**: React + Vite SPA
2. **Backend**: Java 17 + Spring Boot 3 + MySQL + JWT

---

## Architecture Overview for Cloud Deployment

| Component | Recommended Host | Why? |
|---|---|---|
| **Frontend** (`frontend/`) | **Vercel** | Ultra-fast global Edge CDN, automatic previews, free SSL |
| **Backend & DB** (`src/`) | **Render / Railway / Fly.io** | Long-running Java JVM processes and managed MySQL databases (which Vercel does not host) |

---

## Part 1: Deploying the Frontend to Vercel

You can deploy the frontend to Vercel either via the **Vercel Web Dashboard** (recommended) or the **Vercel CLI**.

### Option A: Via Vercel Web Dashboard (Recommended)

1. **Commit and Push your changes to GitHub**:
   ```bash
   git add .
   git commit -m "Configure Vercel deployment and CORS"
   git push origin main
   ```

2. **Import into Vercel**:
   - Go to [vercel.com](https://vercel.com) and log in.
   - Click **Add New...** > **Project**.
   - Select your GitHub repository: `CodeWithJatinSh/Momentum---Habit-Streak_Tracker-`.

3. **Configure Project Settings**:
   - **Framework Preset**: `Vite`
   - **Root Directory**: Click `Edit` and select `frontend` (or leave as `./` if using the root `vercel.json`).
   - **Build Command**: `npm run build`
   - **Output Directory**: `dist`

4. **Environment Variables**:
   Under the **Environment Variables** section, add:
   - **Key**: `VITE_API_BASE_URL`
   - **Value**: `https://your-spring-boot-backend-url.com` *(Leave empty during initial testing if using mock data, or point to your deployed backend)*.

5. Click **Deploy**. Vercel will build and assign you a live URL like `https://momentum-xxxx.vercel.app`.

---

### Option B: Via Vercel CLI

Run the following commands in your terminal:

```bash
cd frontend
npx vercel
```
- Link to your Vercel account when prompted.
- Select defaults (`Vite` framework, output `dist`).
- To deploy to production:
```bash
npx vercel --prod
```

---

## Part 2: Deploying the Backend & Database (Render Example)

Vercel hosts static sites and serverless Node.js/Python functions, but does **not** host Java Spring Boot services or MySQL. The simplest free/low-cost way to host the backend is [Render](https://render.com) or [Railway](https://railway.app).

### 1. Create a Managed MySQL Database (e.g. on Render or Aiven)
- On Render, create a new **MySQL** database (or use **Aiven** / **Railway**).
- Note down:
  - Database Host
  - Port (usually 3306)
  - Database Name (`momentum_db`)
  - Username
  - Password

### 2. Deploy Spring Boot as a Web Service on Render
1. Create a new **Web Service** on Render and connect your GitHub repository.
2. Select **Java** environment (or **Docker**).
3. **Build Command**:
   ```bash
   ./mvnw clean package -DskipTests
   ```
4. **Start Command**:
   ```bash
   java -jar target/momentum-0.0.1-SNAPSHOT.jar
   ```
5. **Environment Variables**:
   - `SPRING_DATASOURCE_URL`: `jdbc:mysql://<db-host>:<port>/<db-name>?useSSL=true&requireSSL=false`
   - `SPRING_DATASOURCE_USERNAME`: `<db-user>`
   - `SPRING_DATASOURCE_PASSWORD`: `<db-password>`
   - `SPRING_JPA_HIBERNATE_DDL_AUTO`: `update`

6. Once deployed, Render will provide your backend URL:
   `https://momentum-backend.onrender.com`

---

## Part 3: Connect Frontend to Backend

1. In your **Vercel Dashboard**, go to **Settings** > **Environment Variables**.
2. Add or update:
   ```env
   VITE_API_BASE_URL=https://momentum-backend.onrender.com
   ```
3. Trigger a **Redeploy** on Vercel so the frontend picks up the new backend URL.
4. Your habit tracker is now fully live and accessible globally!
