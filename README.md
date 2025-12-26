# AnalyzingStockMarketData 📈

A full-stack financial dashboard built with **Spring Boot** and **Vaadin**. This application provides a modern interface for analyzing stock market trends, featuring dynamic charting, dark mode support, and data persistence with PostgreSQL.

## 🚀 Features

- **Dynamic Visualization:** Interactive stock price evolution charts powered by XChart.
- **Dark/Light Mode:** Seamless theme switching with persistent UI components.
- **Stock Analytics:** Real-time calculation of key metrics (High, Low, Current Price).
- **Data Persistence:** Robust storage using PostgreSQL and Spring Data JPA.
- **Responsive Design:** A clean, card-based dashboard layout built with Vaadin Lumo.
- **Containerized Database:** Dockerized PostgreSQL setup for easy deployment and environment consistency.

## 🛠️ Tech Stack

- **Backend:** Java 21, Spring Boot 3.x+, Spring Data JPA.
- **Frontend:** Vaadin 24 (Java-based web framework), HTML/CSS.
- **Database:** PostgreSQL via Docker.
- **Charts:** XChart library for Java.
- **Build Tool:** Maven.

## ⚙️ Setup and installation
1. Prerequisites:
   - Docker & Docker Compose
   - JDK 21
   - Maven
2. Clone the repository:
   ```bash
   git clone https://github.com/lucadani7/AnalyzingStockMarketData
   cd AnalyzingStockMarketData
   ```
3. Ensure Docker Desktop is running.
4. Run the database:
   ```bash
   docker-compose up -d
   ```
5. Build and run the application:
   ```bash
   mvn spring-boot:run
   ```
   The application will be available at `http://localhost:8080`.

## 📝 Note on Data Source
Currently, this project uses simulated/mock data for demonstration purposes. The architecture is designed to be easily integrated with external APIs (like Yahoo Finance or Alpha Vantage) by simply updating the StockService fetch logic.

## 📄 License
This project is licensed under the Apache-2.0 License.
