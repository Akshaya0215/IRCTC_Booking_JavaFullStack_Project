# 🚆 IRCTC Booking System — Java Full Stack Project

A full-featured **Railway Ticket Booking Web Application** inspired by IRCTC, built using **Java, JSP, J2EE, JDBC, and MySQL**. Includes OTP authentication, real-time train tracking, Stripe payment integration, AI chatbot, and PDF ticket generation.

---

## 🔥 Features

- 🔐 **OTP-Based Authentication** — Secure user registration and login with OTP verification
- 🔍 **Train Search** — Search trains by source, destination, and date
- 💺 **Seat Selection** — Interactive seat selection with real-time availability
- 💳 **Stripe Payment** — Online payment integration (test mode)
- 🎫 **PDF Ticket Generation** — Auto-generated ticket with booking details sent via email
- 🤖 **ChatGPT Chatbot** — AI-powered chatbot with database context for user queries
- 📍 **Live Train Tracking** — Real-time train location via third-party API
- 📧 **Email Notifications** — Booking confirmation sent to user email

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|------------|
| **Frontend** | HTML, CSS, JavaScript, JSP |
| **Backend** | Java, J2EE, Servlets |
| **Database** | MySQL, JDBC |
| **Payment** | Stripe API (Test Mode) |
| **AI Chatbot** | ChatGPT API |
| **Live Tracking** | Third-party Train API |
| **PDF Generation** | iText / Apache PDFBox |
| **Email** | JavaMail API |

---

## 📁 Project Structure

```
IRCTC-Clone/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/irctc/
│       │       ├── model/          # Entity classes (User, Train, Booking, etc.)
│       │       ├── servlet/        # All Servlet controllers
│       │       └── util/           # Utility classes (Email, PDF, etc.)
│       ├── resources/
│       │   └── config.properties   # DB and API config
│       └── webapp/
│           ├── WEB-INF/
│           │   └── web.xml
│           └── *.jsp               # All JSP pages
├── pom.xml
└── README.md
```

---

## ⚙️ How to Run Locally

### Prerequisites
- Java JDK 8+
- Apache Tomcat 9+
- MySQL
- Maven

