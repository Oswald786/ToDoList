# Ethan’s Code Guild – ToDo List Game 🎮✅

A **gamified To-Do List application** built with **Micronaut**, **Hibernate/JPA**, and **Oracle Database Free** (running in Docker).

This project demonstrates:

- Clean architecture with an **Adapter layer**
- **REST APIs**
- **SQL-backed persistence**
- Secure, role-based access using **JWT authentication**

Completing tasks isn’t just checking boxes — you earn **XP, levels, and rewards** as part of a productivity game.

---

## ✨ Features

- ✅ Create, update, delete tasks  
- 🎮 Gamified XP + level progression  
- 🗄 Oracle Free database in Docker (**service: FREEPDB1**)  
- 🔐 JWT authentication + role-based access
  - Tasks are scoped to the logged-in user (via JWT)
  - **ADMIN** users can see all tasks
  - Write operations are protected
- 🌐 RESTful API (GET / POST / PUT / DELETE)
- 🧩 Adapter layer to isolate database logic
- 🖥 Static frontend (no frameworks, no Node)

---

## 🛠 Tech Stack

**Backend**
- Java 17
- Micronaut
- Hibernate / JPA
- Oracle JDBC

**Infrastructure**
- Docker + Docker Compose

**Frontend**
- Static HTML / JavaScript

---

## ✅ Prerequisites

- Docker / Docker Desktop
- Git
- A modern web browser (Chrome, Edge, Firefox)

> Java is only required if you want to run the backend locally.

---

## 🚀 Getting Started (Full Stack)

### 1️⃣ Clone the repository

```bash
git clone https://github.com/Oswold786/todolist-db.git
cd todolist-db
2️⃣ Build & start services
This project runs full-stack via Docker Compose:

bash
Copy code
docker compose up --build -d
This starts:

Oracle DB (FREEPDB1)

Micronaut backend API

3️⃣ Verify everything is running
Backend API:

arduino
Copy code
http://localhost:8080
Check containers:

bash
Copy code
docker compose ps
View logs:

bash
Copy code
docker compose logs -f
🗄 Database Connection Info (optional)
If connecting via a DB client:

Host: localhost

Port: 1521

Service name: FREEPDB1

Username: DEMO_USER

Password: DEMO_PASSWORD

JDBC:

bash
Copy code
jdbc:oracle:thin:@//localhost:1521/FREEPDB1
Credentials come from the SQL init scripts inside /init.

🎨 Frontend (Access the App)
Frontend files live here:

Copy code
ToDoListFrontEnd/
Open:

TaskDashboard.html

LogInPage.html

RegisterPage.html

Simply open the files in your browser — no server required.

Ensure Docker containers are running before using the UI.

🌐 API Endpoints (High Level)
Authentication (JWT)
POST /login → returns a JWT

Include token as:

makefile
Copy code
Authorization: Bearer <token>
Tasks
Ownership rules:

Task owner is determined from the JWT username

USERS can only access their own tasks

ADMINS can see all tasks

Routes:

Get tasks: GET /api/tasks/getTasks

Create task: POST /api/tasks

Get by ID: GET /api/tasks/{id}

Update: PUT /api/tasks/{id}

Delete: DELETE /api/tasks/{id}

📦 Project Structure
rust
Copy code
ToDoList/demo/        -> Micronaut backend
ToDoListDB/           -> Oracle DB container
ToDoListDB/init/      -> SQL bootstrap scripts
ToDoListFrontEnd/     -> Static HTML / JS frontend
docker-compose.yml    -> Orchestration
README.md             -> Documentation
LICENSE               -> MIT License
🛑 Stopping & Resetting
Stop services:

bash
Copy code
docker compose down
Reset database (removes data):

bash
Copy code
docker compose down -v
⚠️ Troubleshooting
ORA-12505 / service not registered
Use service:

nginx
Copy code
FREEPDB1
(not XE)

Schema empty?
sql
Copy code
SELECT SYS_CONTEXT('USERENV','CURRENT_SCHEMA') FROM dual;
Should return:

nginx
Copy code
ETHAN
Init scripts didn’t run
bash
Copy code
docker compose down -v
docker compose up --build -d
🧠 Future Improvements
Rename GET /api/tasks/getTasks → GET /api/tasks

Add .env.example for environment variables

Optional lightweight web server for frontend hosting

📜 License
MIT License © 2025 — Ethan Slade

yaml
Copy code

---

### 👍 Want me to do one more pass?

We can also:

📌 add screenshots  
📌 add an architecture diagram  
📌 add a “How authentication works” section  
📌 make it recruiter-ready

Tell me what you want and we’ll polish it even further.
