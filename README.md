# 🛒 E-Commerce Platform

A full-featured E-Commerce REST API built using **Spring Boot**, **Spring Security**, **JWT Authentication**, and **MySQL**. It includes comprehensive unit testing with **JUnit/Mockito**, is fully containerized with **Docker**, and deployed live on **Render** and **Railway** using **Aiven Cloud MySQL**.

📄 **Swagger API Documentation/Railway:** [https://dnexa.up.railway.app/swagger-ui.html](https://dnexa.up.railway.app/swagger-ui.html)

🌐 **Live Base API/Render:** [https://dnexa.onrender.com](https://dnexa.onrender.com)  
📄 **Swagger API Documentation /Render:** [https://dnexa.onrender.com/swagger-ui.html](https://dnexa.onrender.com/swagger-ui.html)


---

## 🚀 Features

### 🔐 Authentication & Authorization
* User Registration & Login
* JWT-Based Authentication
* Role-Based Access Control (Admin / User)
* OTP Verification via Email

### 📦 Product Management
* Add, Update, & Delete Products
* Get Product Details
* Product Stock & Category Management

### 🛒 Cart & Order Management
* Cart Operations (Add, Update, Remove, View)
* Place & Cancel Orders
* Order Status Tracking & History
* Admin Order Management

### 🔄 Return & Refund System
* Return Requests & Approval/Rejection
* Refund Processing & Partial Returns

### 🛠️ Additional Features
* Address Management & Email Notifications
* Global Exception Handling & DTO Mapping
* Pagination, Sorting, & Input Validation

---

## 🛠️ Tech Stack & Cloud Services

* **Language & Framework:** Java 17, Spring Boot 
* **Security:** Spring Security, JWT
* **Database & ORM:** MySQL (Cloud-hosted on **Aiven**), Spring Data JPA, Hibernate
* **Containerization & Hosting:** Docker, Docker Hub, Render
* **Testing & Tools:** JUnit 5, Mockito, Lombok, Maven, Swagger/OpenAPI

---

## 🏗️ System Architecture
```

[ Client / Postman / React Frontend ]
                  │
                  ▼ (HTTPS Requests)
          ┌──────────────┐
          │  Render Cloud│ ──> (Pulls Container Image from Docker Hub)
          └──────┬───────┘
                 │
                 ▼
     ┌────────────────────────┐
     │  Spring Boot App Container │
     │  (JWT Filter & Security)   │
     └───────────┬────────────┘
                 │
                 ▼ (SSL Connection / HikariCP Pool)
     ┌────────────────────────┐
     │  Aiven MySQL Database  │ (Hosted in Cloud) 
     └────────────────────────┘

```

## 📂 Project Structure

``` text
src
├── controller
├── service
├── repository
├── entity
├── dto
├── exception
├── security
├── config
└── util



```
## 👤 Author & Copyright

**Project Created By:** Sanchita Koley  
**Education:** B.Tech CSE  
**Tech Stack:** Java | Spring Boot | MySQL | Cloud Deployment  
**LinkedIn:** [Sanchita Koley Profile](https://www.linkedin.com/in/sanchita-koley-a43860366/)

Copyright © 2026 Sanchita Koley. All rights reserved.  
This project is licensed under the [MIT License](LICENSE) - see the [LICENSE](LICENSE) file for details.
