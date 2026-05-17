# AI Expense Tracker 💰

A modern, responsive, full-stack personal finance application designed to help users track expenses, manage budgets, and gain insights into their spending habits through beautiful visualizations.

---

## 🚀 Tech Stack

### Frontend (Client-Side)
* **HTML5**: Semantic structure for all pages (Dashboard, Expenses, Budget, Analytics, Profile).
* **Vanilla CSS3**: Custom design system featuring CSS variables, modern flexbox/grid layouts, responsive media queries, and sleek aesthetics (gradients, glassmorphism, hover effects). No external CSS frameworks were used, ensuring full control over the UI.
* **Vanilla JavaScript (ES6+)**: Handles all client-side logic, DOM manipulation, asynchronous API requests (`fetch`), and state management via `sessionStorage` and `localStorage`.
* **Chart.js**: Utilized for rendering dynamic, responsive data visualizations (Donut charts for category breakdowns, Bar charts for spending trends).

### Backend (Server-Side)
* **Java 17**: Core programming language.
* **Spring Boot 3.2.5**: The foundational framework for the backend application.
  * **Spring Web**: Builds the RESTful APIs serving the frontend.
  * **Spring Data JPA**: Simplifies database interactions, ORM mapping, and query execution.
  * **Spring Security**: Implements foundational security configurations.
* **Lombok**: Reduces boilerplate code (getters, setters, constructors, builders) in Java models and DTOs.
* **Maven**: Dependency management and build automation.

### Database
* **PostgreSQL**: Robust relational database used for storing users, expenses, budgets, and profiles.
* **Supabase**: Cloud database provider hosting the PostgreSQL instance. The application connects directly to the Supabase database using standard JDBC connections.

---

## ✨ Key Features

* **📊 Dashboard**: A high-level overview of total spending, remaining budget, recent transactions, and category breakdowns.
* **💳 Expense Management**: Add, view, and filter expenses by month, year, and category. Features dynamic UI updates and backend integration.
* **🎯 Budgeting**: Set and monitor monthly budgets. Progress bars visually indicate how much of the budget has been consumed.
* **📈 Analytics**: Deep-dive visualizations using Chart.js to understand spending patterns, accompanied by automated "AI Insights" analyzing the user's data.
* **👤 User Profiles**: Complete profile management including display name, bio, and a custom photo upload feature (images are converted to base64 and synced instantly across the application).
* **📱 Fully Responsive**: The UI automatically adapts from large desktop displays down to mobile screens, featuring a collapsible sidebar, off-canvas mobile hamburger menu, and fluid grid layouts.

---

## 📁 Project Structure

* `src/main/java/com/expensetracker/`: Contains all backend Java code (Controllers, Services, Repositories, Models, DTOs).
* `src/main/resources/static/`: Contains the entire frontend application (HTML pages, `style.css`, and `app.js`).
* `src/main/resources/application.properties`: Configuration file containing database connection details and Spring Boot settings.

---

## 🛠️ How to Run Locally

1. Ensure you have **Java 17+** and **Maven** installed.
2. Clone this repository.
3. Verify your Supabase PostgreSQL credentials in `src/main/resources/application.properties`.
4. Open a terminal in the project root and run:
   ```bash
   mvn spring-boot:run
   ```
5. Navigate to `http://localhost:8080` in your web browser.
