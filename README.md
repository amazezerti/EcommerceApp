# E-Commerce Application
# Project Overview
This Spring Boot e-commerce application, built with Thymeleaf and PostgreSQL (ecommerce_db), is successfully deployed on Heroku using JDK 24. It provides a robust platform for customers and administrators, featuring role-based authentication and a responsive UI. The application supports all required functionalities, including product browsing, cart management, order processing, and admin analytics, meeting the following user stories:

Customer: Browse products, view details, add to cart, checkout, register/login, view order history.
Admin: Manage products, update order status, manage users, view sales analytics.

# GitHub Repository
The source code is publicly available at: https://github.com/amazezerti/EcommerceApp
Features
Backend (Spring Boot)

# Technologies: Spring Boot 3.4.5, Spring Data JPA, Spring Security, Maven, JDK 24.
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
Landing Page: Welcome message, login prompt, drawer with “About Us” and dark/light mode toggle.
Login/Signup: Forms with username, email, password, role selection (signup), and signup link (login).
Customer Dashboard: Sidebar (profile, browse products, cart, orders, settings, logout), navbar, footer; supports product browsing, cart management, checkout, order history.
Admin Dashboard: Sidebar (profile, user management, product management, order management, settings, logout), navbar, footer; includes product CRUD, order status updates, user CRUD, and dynamic sales chart.


# UI Components:
Bootstrap-styled product grids, cart tables, and forms.
Sidebar with profile picture upload, dark/light mode toggle, password change.
Dynamic sales chart (Chart.js) for admin, updating with new orders.


Validation: Server-side input validation (e.g., email format, password match) with error messages.
Static Resources: CSS (/css/styles.css), JavaScript (/js/scripts.js) for interactivity.

#Database

PostgreSQL: Hosted on Heroku Postgres (Essential-0 tier), initialized with spring.jpa.hibernate.ddl-auto=update.
Schema: Tables for users, products, orders, and order items, automatically created.

# Project Setup

Backend: Maven project with Spring Boot 3.4.5, PostgreSQL driver, Spring Security, and JPA.
Frontend: Thymeleaf templates in src/main/resources/templates/, static resources in src/main/resources/static/.
Directory: C:\Users\amazezerti\ecommerce (Windows 11 Pro).
IDE: IntelliJ Community Edition.

# Local Deployment Instructions
Prerequisites

Maven (in system PATH).
Git.
Local PostgreSQL: ecommerce_db, user postgres, password 1234.
JDK 24.

Steps

Clone Repository:git clone https://github.com/amazezerti/EcommerceApp.git
cd EcommerceApp


Build JAR:mvn clean package


Run Locally:java -jar target/ecommerce-0.0.1-SNAPSHOT.jar


Access at http://localhost:8080.
Connects to local PostgreSQL (localhost:5432, ecommerce_db, postgres, 1234).



Heroku Deployment
Overview
The application will be deployed on Heroku with Heroku Postgres, ensuring global access for account creation, product browsing, and admin management. The deployment uses JDK 24 and Maven, with environment variables configured for seamless operation.
Setup Files

Procfile:web: java -Dserver.port=$PORT -jar target/ecommerce-0.0.1-SNAPSHOT.jar


system.properties:java.runtime.version=24


application.properties:spring.application.name=ecommerce
spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:postgresql://localhost:5432/ecommerce_db}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME:postgres}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD:1234}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.thymeleaf.cache=false
server.port=${PORT:8080}



#Deployment Steps

Create Heroku App:heroku create ecommerceapplication


Add Heroku Postgres:heroku addons:create heroku-postgresql:essential-0 -a ecommerceapplication


Set Environment Variables:heroku config:set SPRING_DATASOURCE_URL=[DATABASE_URL] -a ecommerceapplication
heroku config:set SPRING_DATASOURCE_USERNAME=[user] -a ecommerceapplication
heroku config:set SPRING_DATASOURCE_PASSWORD=[password] -a ecommerceapplication
heroku config:set SPRING_JPA_HIBERNATE_DDL_AUTO=update -a ecommerceapplication





Push to Heroku:git push heroku main


Verify Deployment:heroku logs --tail -a ecommerceapplication


Confirms Spring Boot startup (Started Application).



# Testing Endpoints

Landing Page: /
Login: /login (includes signup link).
Signup: /signup (select role: USER or ADMIN).
Customer Dashboard: /customer/dashboard (post-login).
Admin Dashboard: /admin/dashboard (post-login, ADMIN only).
APIs (use Postman):
GET /api/products: List products.
POST /api/orders: Place order.
GET /api/orders/history: Order history (USER).
PUT /api/orders/{id}/status: Update order status (ADMIN).



Important Notes
Database Initialization

Heroku Postgres is empty initially; add products via the admin dashboard.
Schema is auto-created with spring.jpa.hibernate.ddl-auto=update.

Heroku Essential-0 Tier

App may sleep after 30 minutes of inactivity but wakes quickly.
Requires minimal payment ($5/month) for database hosting.

Credentials

Create users/admins via signup; no default credentials.

Source Code

All code is available at: https://github.com/amazezerti/EcommerceApp

Contact

For issues, contact: amazezerti0103@gmail.com

Acknowledgments

Built with Spring Boot, Thymeleaf, and Heroku for a university capstone project.
Successfully deployed and tested for customer and admin functionalities.

