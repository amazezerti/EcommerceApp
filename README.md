# E-Commerce Application
# Overview
This is a Spring Boot e-commerce application with Thymeleaf frontend and PostgreSQL database (ecommerce_db), deployed on Render as a global web app using JDK 24. It supports customer and admin functionalities with role-based authentication, meeting the following user stories:

Customer: Browse products, view details, add to cart, view cart, checkout, register/login, view order history.
Admin: Add/manage products, view/update order status, manage users, view sales analytics.

# Public URL

URL: [Insert Render URL, e.g., https://ecommerce-app.onrender.com]
Note: Hosted on Render’s free tier; may take up to 50 seconds to spin up after inactivity.

# Features
# Backend (Spring Boot)

Technologies: Spring Boot 3.x, Spring Data JPA, Spring Security, Maven, JDK 24.
Roles: USER (customers), ADMIN (administrators).
APIs:
Products: CRUD operations (/api/products) for listing, adding, updating, deleting products.
Orders: Place orders (/api/orders), view history (/api/orders/history), update status (/api/orders/{id}/status).
Users: Register (/api/auth/signup), login (/api/auth/login), manage users (/api/users).


Entities: User (username, email, password, role, profile picture), Product (name, price, stock, category, image), Order (user, order items, total, status).
Security: Spring Security with role-based access (e.g., /admin/* for ADMIN only).
Error Handling: Custom exceptions, JSON error responses (e.g., 404 for invalid product ID).

# Frontend (Thymeleaf)

Technologies: Thymeleaf, Spring MVC, Bootstrap 5.
Templates:
Landing Page: Welcome message, login prompt, drawer with “About Us” and dark/light mode settings.
Login/Signup: Forms with username, email, password, role selection (signup), and signup link (login).
Customer Dashboard: Sidebar (profile, browse products, cart, orders, settings, logout), navbar, footer; features product browsing, cart management, checkout, order history.
Admin Dashboard: Sidebar (profile, user management, product management, order management, settings, logout), navbar, footer; includes product CRUD, order status updates, user CRUD, and dynamic sales chart (monthly/yearly).


# UI Components:
Bootstrap-styled product grids, cart tables, and forms.
Sidebar with profile picture upload (gallery/camera), dark/light mode toggle, password change.
Dynamic sales chart (Chart.js) for admin, updating with new orders.


Validation: Server-side input validation (e.g., email format, password match) with error messages.
Static Resources: CSS (/css/styles.css), JavaScript (/js/scripts.js) for interactivity (e.g., mode toggle, chart updates).

# Additional Requirements

Error Handling: User-friendly messages (e.g., “Product not found”, “Invalid login credentials”).
Deployment: Hosted on Render with PostgreSQL (ecommerce_db).
Source Code: Public GitHub repository: [Insert GitHub URL, e.g., https://github.com/your-username/ecommerce-app].
Documentation: This README and code comments.

# Project Setup

Backend: Maven project with Spring Boot 3.x, PostgreSQL driver, Spring Security, and JPA.
Database: PostgreSQL (ecommerce_db), initialized via spring.jpa.hibernate.ddl-auto=update.
Frontend: Thymeleaf templates in src/main/resources/templates/, static resources in src/main/resources/static/.

Local Deployment Instructions
Prerequisites

Docker Desktop (Windows 11 Pro: WSL 2 enabled).
Maven (in system PATH).
Git.
Local PostgreSQL (optional for non-Docker): ecommerce_db, user postgres, password 1234.

Steps

Clone Repository:git clone [Your-GitHub-URL]
cd ecommerce-app


Build JAR:mvn clean package


Run Docker Compose:docker-compose up --build


Access at http://localhost:8080.
PostgreSQL: localhost:5432, database ecommerce_db, user postgres, password password (Docker).
For local PostgreSQL (no Docker): Use password 1234.


Stop:docker-compose down



# Cloud Deployment (Render)

Web Service: Dockerized Spring Boot app, port 8080, JDK 24.
Database: Render PostgreSQL (ecommerce_db), auto-initialized.
Environment Variables:
SPRING_DATASOURCE_URL: Render-provided PostgreSQL URL.
SPRING_DATASOURCE_USERNAME: postgres.
SPRING_DATASOURCE_PASSWORD: Render-provided.
SPRING_JPA_HIBERNATE_DDL_AUTO: update.


URL: [Insert Render URL].

# Testing Endpoints

Landing Page: [Your-URL]/
Login: [Your-URL]/login (signup link available).
Signup: [Your-URL]/signup (select role: USER or ADMIN).
Customer Dashboard: [Your-URL]/customer/dashboard (post-login).
Admin Dashboard: [Your-URL]/admin/dashboard (post-login, ADMIN only).
APIs (use Postman):
GET /api/products: List products.
POST /api/orders: Place order.
GET /api/orders/history: Order history (USER).
PUT /api/orders/{id}/status: Update order status (ADMIN).



Notes

Database: Empty initially; add products via admin dashboard.
Render Free Tier: App spins down after inactivity (50s wakeup); PostgreSQL expires after 90 days.
Credentials: Signup to create users/admins; no default credentials.
Contact: [Your-Email] for issues.
GitHub: [Your-GitHub-URL].







# E-Commerce Application

A simple e-commerce application built with Spring Boot and Thymeleaf.

## Setup Instructions
1. Clone the repository: `git clone https://github.com/yourusername/ecommerce.git`
2. Configure PostgreSQL database in `application.properties`.
3. Run the application: `mvn spring-boot:run`
4. Access the app at `http://localhost:8080`.

## Features
- Admin and Customer dashboards.
- Product management, shopping cart, and order processing.
- User authentication with Spring Security.