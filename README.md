# Ethan’s Code Guild – ToDo List Game 🎮✅

A **gamified To-Do List application** built with **Micronaut**, **Hibernate/JPA**, and **Oracle Database** (running in Docker).  
This project demonstrates clean architecture with an **Adapter layer**, **REST APIs**, and **SQL-backed persistence**.  

Completing tasks isn’t just checking boxes — you earn **points, levels, and rewards** as part of a productivity game.

---

## 🚀 Features
- ✅ Manage tasks: create, update, delete
- 🎮 Gamified scoring system with rewards
- 🗄️ Oracle XE database running locally in Docker
- 🔗 Hibernate + JPA for object–relational mapping
- 🌐 RESTful APIs (GET/POST/PUT/DELETE)
- 🧩 Adapter layer for database communication

---

## 🛠️ Prerequisites
- [Java 17+](https://adoptium.net/)  
- [IntelliJ IDEA](https://www.jetbrains.com/idea/) (Community works; Ultimate adds DB tools/HTTP client)  
- [Docker](https://www.docker.com/) + [Docker Compose](https://docs.docker.com/compose/)  
- [Git](https://git-scm.com/)  
- (Optional) Oracle SQL Developer / IntelliJ Database plugin  

---

## ⚙️ Setup Instructions

### 1. Clone the repository
git clone https://github.com/Oswold786/todolist-db.git
cd todolist-db
2. Start the Oracle Database with Docker
bash
Copy code
docker compose up --build -d
This will:

Pull the Oracle Free image

Run a container with the DB exposed on port 1521

Execute the init SQL scripts (/init) to create the schema, user, tables, and seed data

Check logs until DB is ready:

bash
Copy code
docker logs -f oracledb
3. Verify Database Connection
Host: localhost

Port: 1521

Service name: FREEPDB1 (⚠️ not SID XE)

User: ETHAN

Password: Ethan@9901

JDBC URL:

arduino
Copy code
jdbc:oracle:thin:@//localhost:1521/FREEPDB1
4. Run the Micronaut Application
From IntelliJ or terminal:

bash
Copy code
./gradlew run
Backend will run at:
👉 http://localhost:8080

📂 Project Structure
rust
Copy code
/init/                   -> SQL scripts (user, tables, seed data)
/app/                    -> Micronaut application code
docker-compose.yml       -> Oracle XE container config
Dockerfile               -> App container (if needed)
README.md                -> Setup + instructions
🌐 REST API Endpoints
List tasks
bash
Copy code
GET http://localhost:8080/api/tasks
Create a task
bash
Copy code
POST http://localhost:8080/api/tasks
Content-Type: application/json

{
  "name": "Write README",
  "type": "docs",
  "level": 1,
  "description": "Add setup instructions"
}
Get by ID
bash
Copy code
GET http://localhost:8080/api/tasks/{id}
Update a task
bash
Copy code
PUT http://localhost:8080/api/tasks/{id}
Content-Type: application/json

{
  "name": "Update README",
  "type": "docs",
  "level": 2,
  "description": "Expanded instructions"
}
Delete a task
bash
Copy code
DELETE http://localhost:8080/api/tasks/{id}
🧪 Testing
Run all tests:

bash
Copy code
./gradlew test
⚠️ Troubleshooting
ORA-12505 (SID not registered)
Use service name: FREEPDB1 instead of SID XE.

Invalid credentials
Ensure user ETHAN exists and you’re not connecting with SYSTEM.

Tables missing
Confirm schema is correct:

sql
Copy code
SELECT SYS_CONTEXT('USERENV','CURRENT_SCHEMA') FROM dual;
Should return ETHAN.

Init scripts didn’t run
They only run on first container startup. To reset:

bash
Copy code
docker compose down -v
docker compose up --build -d
📜 License
MIT License © 2025 Ethan Slade

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
