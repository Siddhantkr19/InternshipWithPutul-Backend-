## InternshipWithPutul - Backend API

This is the backend for a full-stack internship portal. It's a secure, stateless REST API built with Java and Spring Boot, designed to serve a React frontend. It handles user authentication, content management, and other core functionalities for the platform.

## Features

**Stateless Authentication:** Secure API using Spring Security and JSON Web Tokens (JWT).

**Role-Based Authorization:** Distinct permissions for ADMIN and USER roles protecting relevant endpoints.

**Internship Management:** Full CRUD (Create, Read, Update, Delete) API for internship listings.

**User Management:** Admin-only API for creating, viewing, and deleting user accounts.

**Contact & Visitor System:** Endpoints for handling public contact form submissions and a simple visitor counter.

**Profile-Based Configuration:** Separate configurations for local development (MySQL) and production (PostgreSQL) environments using Spring Profiles.

**Containerized:** Includes a Dockerfile for easy and consistent deployment.

## Tech Stack

**Framework:** Spring Boot 3

**Language:** Java 17

**Security:** Spring Security 6

**Database:** Spring Data JPA / Hibernate

**Production DB:** PostgreSQL (designed for NeonDB)

**Local DB:** MySQL

**Authentication:** JSON Web Token (JJWT)

**Build Tool:** Maven

**Deployment:** Docker

## API Endpoints

**Authentication**
 
**Method---------------Endpoint-------------Protection----------------Description**

  POST-------------/api/auth/login------------Public-----------Authenticates a user and returns a JWT.


**Internships**


  **Method---------------Endpoint-------------Protection----------------Description**
  
   GET-------------/api/internships-----------Public----------Retrieves a list of all internships, sorted newest first.
   
   POST------------/api/internships-----------Admin-----------Creates a new internship posting.
   
   PUT-------------/api/internships/{id}------Admin-----------Updates an existing internship.
   
  DELETE----------/api/internships/{id}-------Admin------------Deletes an internship.
  

**Admin Management**


  **Method---------------Endpoint---------------Protection----------------Description**
  
  GET---------------/api/management/users---------Admin----------Retrieves a list of all users.

  POST--------------/api/management/users---------Admin----------Creates a new user (can be ADMIN or USER).

  DELETE------------/api/management/users/{id}----Admin----------Deletes a user.

  GET---------------/api/management/messages------Admin----------Retrieves all contact messages.

  DELETE------------/api/management/messages/{id}--Admin---------Deletes a contact message.



**Public Services**

 **Method---------------Endpoint---------------Protection----------------Description**

  POST-----------------/api/contact	-------------Public------------Submits a message from the contact form.

  GET-----------------/api/visits----------------Public-------------Gets the current visitor count.

  POST----------------/api/visits----------------Public-------------Increments the visitor count.

  GET-----------------/health--------------------Public--------------A simple health check endpoint.



## Local Setup

To run this project on your local machine, follow these steps:

**1) Prerequisites:**

       a) Java 17 JDK 

       b) Maven

       c) A local MySQL server

**2) Clone the repository:** git clone <your-repository-url>

**3) Create a database:** In your MySQL client, create a new database for the project.

**4) SQL**

       CREATE DATABASE your_app_db;
       
       Configure local properties:
       
       Open the src/main/resources/application.properties file and update the datasource properties with your local MySQL credentials.

**5) Run the application:**

       You can run the main method in the SqlAdminAuthApplication.java class from your IDE,or use the Maven wrapper in your terminal:
       
       ./mvnw spring-boot:run
       
       The application will start on http://localhost:8080.

**6) Configuration for Deployment**
       The application is configured to use a separate profile for production.

**7) application.properties:** 
       Used for local development with MySQL.

**8)application-prod.properties:**
       Used for production deployment.It is configured for PostgreSQL and expects the following environment variables to be set on the hosting platform (e.g., Render).

## Required Environment Variables for Production

**SPRING_PROFILES_ACTIVE:** Must be set to prod.

 **DB_URL:** The full JDBC connection string for your production PostgreSQL database (e.g., from Neon).

 **DB_USERNAME:** Your production database username.

**DB_PASSWORD:** Your production database password.

**JWT_SECRET:** Your secure, long secret key for signing JWTs.
