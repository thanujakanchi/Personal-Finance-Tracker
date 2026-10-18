# Personal Finance Tracker 

A Java-based Personal Finance Tracker developed using Spring Boot to help users manage their personal income and expenses.

## About the Project

The Personal Finance Tracker is a web-based application that helps users record and manage their financial transactions. It is designed to provide an organized way to track income, monitor expenses, and understand personal spending.

## Features

- **Add Transactions:** Record financial transactions with relevant details.
- **View Transactions:** Display recorded financial transactions.
- **Income Tracking:** Keep track of money received.
- **Expense Tracking:** Monitor spending and expenses.
- **Financial Summary:** View income, expenses, and remaining balance.
- **Web Interface:** Interact with the application through a browser.

## Technologies Used

- Java 21
- Spring Boot
- Maven
- HTML5
- CSS3
- JavaScript

## How to Run

1. Install Java JDK 21.
2. Install Apache Maven.
3. Download or clone this repository.
4. Open a terminal in the project directory.
5. Run the following command:

   ```bash
   mvn spring-boot:run
   ```

6. Open your browser and visit:

   ```text
   http://localhost:8080
   ```

7. If port 8080 is already in use, start the application on port 8081:

   ```bash
   mvn spring-boot:run "-Dspring-boot.run.arguments=--server.port=8081"
   ```

   Then visit `http://localhost:8081`.

## Data Storage

The application manages financial information through its implemented storage mechanism. Transaction persistence depends on the database or storage configuration used in the project.

## Example Calculation

Suppose the recorded transactions are:

- Income: ₹5,000
- Food expenses: ₹1,200
- Travel expenses: ₹500

The financial summary will be:

- **Total Income:** ₹5,000
- **Total Expenses:** ₹1,700
- **Remaining Balance:** ₹3,300

## Learning Outcomes

Through this project, I practised:

- Java programming and object-oriented programming
- Spring Boot application development
- Maven dependency management
- Web application development
- HTML, CSS, and JavaScript integration
- Application execution and debugging

## Author

Created as a Java web development project to practise application development using Spring Boot.
