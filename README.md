# Personal Finance Tracker — Spring Boot Website

A responsive finance dashboard backed by Java Spring Boot. It uses `BigDecimal` for money, `java.time` for dates, NIO.2 (`Path`/`Files`) for CSV persistence, and CSV downloads for reports.

## Requirements
- JDK 17 or newer
- Maven 3.6+ (or use the Maven wrapper if you add one)
- VS Code with Extension Pack for Java (optional but helpful)

## Run
1. Extract the ZIP and open the project folder in VS Code.
2. Ensure Java 17+ and Maven are installed (`java -version`, `mvn -version`).
3. In the project root, run:

   ```bash
   mvn spring-boot:run
   ```

4. Open http://localhost:8080 in your browser.

## Features
- Responsive dashboard with monthly income, expenses, net cash flow and transaction count.
- Add income and expense records.
- Filter transactions by date range and category.
- Category-wise expense totals and percentages.
- Download transaction CSV and monthly summary CSV.
- Persists transactions in `data/transactions.csv`.

## API
- `GET /api/transactions?start=2026-10-01&end=2026-10-31&category=Food`
- `POST /api/transactions` with JSON such as:
  `{"type":"EXPENSE","category":"Food","amount":250.50,"remarks":"Lunch"}`
- `GET /api/summary?month=2026-10`
- `GET /api/export/transactions`
- `GET /api/export/summary?month=2026-10`

## Notes
This starter is intended for local coursework/demo use. It does not include authentication or multi-user isolation; do not expose it publicly with personal financial data without adding appropriate security.
=======
# Personal-Finance-Tracker
A Java-based Personal Finance Tracker web application built with Spring Boot, HTML, CSS, and JavaScript to manage income, track expenses, and view financial summaries.
>>>>>>> c0630c2d4e2aa9f6be6f4d65a4fc8a04c99f8e80
