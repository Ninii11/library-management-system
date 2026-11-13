# Library Management System

A RESTful Library Management System developed using Java, Spring Boot, Spring Data JPA, and PostgreSQL.

The application manages books, authors, categories, publishers, library members, and book loans.

## Technologies Used

- Java 21
- Spring Boot
- Spring Data JPA / Hibernate
- PostgreSQL
- Maven
- Jakarta Bean Validation
- Postman
- pgAdmin

## Project Architecture

The application follows a layered architecture:

- **Entity:** Defines database entities and relationships.
- **Repository:** Handles database operations using Spring Data JPA.
- **Service:** Implements business logic and transaction management.
- **Controller:** Exposes REST API endpoints.
- **DTO:** Separates API request and response data from database entities.
- **Exception:** Handles application errors and validation failures.

## Main Features

- CRUD operations for books, authors, categories, members, and publishers
- Book search and filtering
- Pagination and sorting
- Book cover image uploading
- Book borrowing and returning
- Prevention of duplicate active loans for the same book
- Loan history and overdue loan tracking
- Request validation and HTTP error handling
- PostgreSQL database relationships

## Database Entities

The application contains six main entities:

1. Book
2. Author
3. Category
4. Publisher
5. Member
6. Loan

Relationships include:

- One author can write multiple books.
- One publisher can publish multiple books.
- One book can belong to multiple categories.
- One member can have multiple loans.
- One book can have multiple loan records over time.

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| GET | /api/books | Retrieve books with pagination |
| GET | /api/books/{id} | Retrieve a book |
| POST | /api/books | Create a book |
| PUT | /api/books/{id} | Update a book |
| DELETE | /api/books/{id} | Delete a book |
| POST | /api/books/{id}/cover | Upload a book cover |
| GET | /api/authors | Retrieve authors |
| POST | /api/authors | Create an author |
| GET | /api/categories | Retrieve categories |
| POST | /api/categories | Create a category |
| GET | /api/members | Retrieve members |
| POST | /api/members | Register a member |
| GET | /api/publishers | Retrieve publishers |
| POST | /api/publishers | Create a publisher |
| GET | /api/loans | Retrieve loans |
| POST | /api/loans | Borrow a book |
| PUT | /api/loans/{id}/return | Return a book |
| GET | /api/loans/active | Retrieve active loans |
| GET | /api/loans/overdue | Retrieve overdue loans |
| GET | /api/loans/member/{memberId} | Retrieve member loan history |

## Running the Application

### Prerequisites

- Java 21
- PostgreSQL
- Maven or Maven Wrapper
- IntelliJ IDEA or another Java IDE

### Setup

1. Clone or download the project.
2. Create a PostgreSQL database.
3. Configure the database connection in `application.properties` or through environment variables.
4. Start the Spring Boot application.
5. Use Postman to test the REST API.

The default application URL is:

`http://localhost:8080`

Example:

`GET http://localhost:8080/api/books`

## Pagination and Sorting

Example request:

`GET /api/books?page=0&size=5&sort=title,asc`

This retrieves the first page with up to five books sorted alphabetically.

## Book Cover Upload

Use a multipart/form-data POST request:

`POST /api/books/{id}/cover`

Form field:

`file` — JPG or PNG image.

## Borrowing Books

Example request:

`POST /api/loans`

```json
{
  "memberId": 1,
  "bookId": 2,
  "days": 14
}
```

The system prevents borrowing a book that already has an active loan.

## Sample Data

The project includes an optional `sample-data.sql` script containing Georgian literature, George R. R. Martin novels, authors, categories, publishers, and demonstration members.

The script can be executed in pgAdmin after the database tables have been created.

## Testing

The application has been tested using Postman and pgAdmin.

Verified functionality includes:

- Book retrieval
- Pagination and sorting
- Input validation
- Book borrowing and returning
- Prevention of duplicate active loans
- Book cover uploads
- Database relationships

## Notes

This is a backend REST API project. A graphical frontend is not included.

## Author

Library Management System - Nini Mikadze 
