
# ⚙️ Internship WithPutul (IWP) 2.0 - AI Backend Engine

This is the backend for **Internship WithPutul (Version 2.0)**. What started as a standard REST API has been upgraded into a fully autonomous, AI-powered web scraping engine built with **Java 21** and **Spring Boot 3**. 

Designed to serve a React frontend, this stateless backend not only handles JWT authentication and content management, but it also autonomously hunts for new tech jobs and internships, parses them using Google Gemini AI, and emails users automatically.

### ✨ Key Features

* 🤖 **Autonomous Web Scraping:** Uses **Selenium WebDriver** running in headless mode to scrape targeted job boards (e.g., freshersrecruitment.co.in) automatically every 4 hours using Spring `@Scheduled` tasks.
* 🧠 **AI Data Parsing:** Integrates the **Google Gemini 2.5 Flash API** to read messy, unstructured website text and format it into clean, structured JSON objects.
* 📧 **Automated Email Alerts:** A built-in batch notification system that automatically sends HTML emails to subscribed users whenever new jobs or internships are found.
* 🔐 **Stateless Authentication:** Secure API endpoints protected by Spring Security 6 and JSON Web Tokens (JWT).
* 👥 **Role-Based Authorization:** Distinct permissions for `ADMIN` and `USER` roles.
* 🐳 **Production-Ready Docker:** Includes a custom multi-stage `Dockerfile` configured with Alpine Linux and Chromium to seamlessly run Selenium in cloud environments like Render.

### 🛠️ Tech Stack

* **Framework:** Spring Boot 3.2.5
* **Language:** Java 21
* **Database:** MySQL (Aiven Cloud) / Spring Data JPA
* **AI Integration:** Google Gemini API
* **Web Scraping:** Selenium WebDriver
* **Security:** Spring Security 6 + JJWT
* **Mail:** Spring Boot Starter Mail
* **Build Tool:** Maven
* **Deployment:** Docker (Render)

---

### 📡 API Endpoints

**Authentication & Users**
| Method | Endpoint | Protection | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/login` | Public | Authenticates a user and returns a JWT. |
| `POST` | `/api/auth/signup` | Public | Registers a new user account. |

**Jobs & Internships (AI Generated)**
| Method | Endpoint | Protection | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/internships` | Public | Retrieves all AI-scraped internships. |
| `GET` | `/api/jobs` | Public | Retrieves all AI-scraped jobs. |
| `POST` | `/api/internships` | Admin | Manually creates a new internship. |
| `DELETE` | `/api/internships/{id}` | Admin | Deletes an internship. |

**Admin Management**
| Method | Endpoint | Protection | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/management/users` | Admin | Retrieves a list of all registered users. |
| `DELETE` | `/api/management/users/{id}`| Admin | Deletes a user. |
| `GET` | `/api/management/messages`| Admin | Retrieves all contact form messages. |

**Public Services**
| Method | Endpoint | Protection | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/contact` | Public | Submits a message from the contact form. |
| `GET/POST`| `/api/visits` | Public | Gets or increments the live visitor counter. |

---

### 💻 Local Setup

To run this project on your local machine:

**1. Prerequisites:**
* Java 21 JDK
* Maven
* Google Chrome installed locally (for Selenium)
* A MySQL server (Local or Cloud like Aiven)

**2. Configure Local Properties:**
Open `src/main/resources/application.properties` and add your local or cloud MySQL credentials, along with your Gemini API key:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/your_db_name
spring.datasource.username=root (Aiven database )
spring.datasource.password=your_password

gemini.api.key=YOUR_GEMINI_API_KEY
jwt.secret=YOUR_LOCAL_SECRET_KEY
```

**4. Run the application:**
Run the application using your IDE or the Maven wrapper:
```bash
./mvnw spring-boot:run
```
The API will start on `http://localhost:8080`.

---

### 🚀 Configuration for Production Deployment (Render / Docker)

This application is configured to run inside a Docker container using a `prod` profile. When deploying to platforms like Render, the `Dockerfile` will automatically install Chromium for the scraper.

You **must** set the following Environment Variables in your hosting dashboard:

* `PORT` : `8080` (Required for Render port binding)
* `SERVER_PORT` : `8080`
* `SPRING_PROFILES_ACTIVE` : `prod`
* `SPRING_DATASOURCE_URL` : Full JDBC connection string (e.g., Aiven MySQL URL).
* `SPRING_DATASOURCE_USERNAME` : Production DB username.
* `SPRING_DATASOURCE_PASSWORD` : Production DB password.
* `SPRING_DATASOURCE_DRIVER_CLASS_NAME` : `com.mysql.cj.jdbc.Driver`
* `SPRING_JPA_DATABASE_PLATFORM` : `org.hibernate.dialect.MySQLDialect`
* `jwtSecret` : A long, secure random string.
* `GEMINI_API_KEY` : Your active Google AI Studio key.
* `SPRING_MAIL_USERNAME` : Gmail address for sending batch notifications.
* `SPRING_MAIL_PASSWORD` : Gmail App Password.

---
*Architected and developed by Siddhant Kumar*
