# Library Hibernate API Documentation

This document provides information on the API endpoints for the Library Hibernate system. The API allows interaction with categories, books, users, and lending functionalities.

**Note:** The following endpoints are accessible to both users and librarians.

## Users

- **Login**
    - **Endpoint:** `POST /knowledgebase/login`
    - **Description:** Authenticates a user and generates an access token.
    - **Role:** PUBLIC

- **Register**
    - **Endpoint:** `POST /knowledgebase/register`
    - **Description:** Registers a new user in the knowledge base.
    - **Role:** PUBLIC

- **Get User**
    - **Endpoint:** `GET /knowledgebase/users`
    - **Description:** Retrieves information about all users from the knowledge base.
    - **Headers:**
        - `Authorization: Bearer <access_token>`
    - **Role:** USER, LIBRARIAN

- **Update User**
    - **Endpoint:** `PUT /knowledgebase/users?userId=<userId>`
    - **Description:** Updates the details of a specific user in the knowledge base.
    - **Headers:**
        - `Authorization: Bearer <access_token>`
    - **Role:** USER, LIBRARIAN

- **Delete User**
    - **Endpoint:** `DELETE /knowledgebase/users?userId=<userId>`
    - **Description:** Deletes a specific user from the knowledge base.
    - **Headers:**
        - `Authorization: Bearer <access_token>`
    - **Role:** USER, LIBRARIAN

## Categories

- **Add Category**
    - **Endpoint:** `POST /knowledgebase/category`
    - **Description:** Adds a new category to the knowledge base.
    - **Headers:**
        - `Authorization: Bearer <access_token>`
    - **Role:** LIBRARIAN

- **Get Categories**
    - **Endpoint:** `GET /knowledgebase/category`
    - **Description:** Retrieves a list of all categories from the knowledge base.
    - **Headers:**
        - `Authorization: Bearer <access_token>`
    - **Role:** USER, LIBRARIAN

- **Update Category**
    - **Endpoint:** `PUT /knowledgebase/category?categoryId=<categoryId>`
    - **Description:** Updates the details of a specific category in the knowledge base.
    - **Headers:**
        - `Authorization: Bearer <access_token>`
    - **Role:** LIBRARIAN

- **Delete Category**
    - **Endpoint:** `DELETE /knowledgebase/category?categoryId=<categoryId>`
    - **Description:** Deletes a specific category from the knowledge base.
    - **Headers:**
        - `Authorization: Bearer <access_token>`
    - **Role:** LIBRARIAN

## Books

- **Add Book**
    - **Endpoint:** `POST /knowledgebase/books?categoryId=<categoryId>`
    - **Description:** Adds a new book to the knowledge base under a specified category.
    - **Headers:**
        - `Authorization: Bearer <access_token>`
    - **Role:** LIBRARIAN

- **Get All Books**
    - **Endpoint:** `GET /knowledgebase/books?categoryId=<categoryId>`
    - **Description:** Retrieves a list of all books from the knowledge base.
    - **Headers:**
        - `Authorization: Bearer <access_token>`
    - **Role:** USER, LIBRARIAN

- **Get Books By Category**
    - **Endpoint:** `GET /knowledgebase/books?categoryId=<categoryId>`
    - **Description:** Retrieves a list of books based on the specified category.
    - **Headers:**
        - `Authorization: Bearer <access_token>`
    - **Role:** USER, LIBRARIAN
    -
- **Update Book**
    - **Endpoint:** `PUT /knowledgebase/books?bookId=<bookId>&categoryId=<categoryId>`
    - **Description:** Updates the details of a specific book in the knowledge base.
    - **Headers:**
        - `Authorization: Bearer <access_token>`
    - **Role:** LIBRARIAN

- **Delete Book**
    - **Endpoint:** `DELETE /knowledgebase/books?bookId=<bookId>`
    - **Description:** Deletes a specific book from the knowledge base.
    - **Headers:**
        - `Authorization: Bearer <access_token>`
    - **Role:** LIBRARIAN
    
## Lending

- **Add Lending**
    - **Endpoint:** `POST /knowledgebase/lending?bookId=<bookId>&userId=<userId>`
    - **Description:** Initiates a lending transaction for a book to a user.
    - **Headers:**
        - `Authorization: Bearer <access_token>`
    - **Role:** USER

- **Get Lendings**
    - **Endpoint:** `GET /knowledgebase/lending`
    - **Description:** Retrieves a list of all lending transactions from the knowledge base.
    - **Headers:**
        - `Authorization: Bearer <access_token>`
    - **Role:** USER, LIBRARIAN

- **Update Lending**
    - **Endpoint:** `PUT /knowledgebase/lending?lendingId=<lendingId>&bookId=<bookId>&userId=<userId>`
    - **Description:** Updates the details of a specific lending transaction in the knowledge base.
    - **Headers:**
        - `Authorization: Bearer <access_token>`
    - **Role:** USER
    -
- **Delete Lending**
    - **Endpoint:** `DELETE /knowledgebase/lending?lendingId=<lendingId>`
    - **Description:** Ends a specific lending transaction and updates the status in the knowledge base.
    - **Headers:**
        - `Authorization: Bearer <access_token>`
    - **Role:** USER
