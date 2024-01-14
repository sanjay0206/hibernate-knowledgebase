package com.knowledgebase.dao;


import com.knowledgebase.dto.BookDto;
import com.knowledgebase.dto.ServerResponseDto;
import com.knowledgebase.entities.Book;
import com.knowledgebase.entities.Category;
import com.knowledgebase.entities.Status;
import com.knowledgebase.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

public class BookDao {

    public List<BookDto> getAllBooks() {
        List<BookDto> books = new ArrayList<>();
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Query<BookDto> query = session.createQuery
                    ("SELECT new com.knowledgebase.dto.BookDto(b.bookId, b.title, b.author, b.isbn, b.quantity) FROM Book b",
                            BookDto.class);

            books = query.getResultList();
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null)
                transaction.rollback();
        }
        return books;
    }

    public Object saveBook(long categoryId, BookDto bookDto) {
        Transaction transaction = null;
        if (bookDto.getTitle() == null || bookDto.getTitle().trim().isEmpty()) {
            return new ServerResponseDto(Status.FAILURE, "Book title is mandatory");
        }
        if (bookDto.getAuthor() == null || bookDto.getAuthor().trim().isEmpty()) {
            return new ServerResponseDto(Status.FAILURE, "Book author is mandatory");
        }
        if (bookDto.getIsbn() == null || bookDto.getIsbn().trim().isEmpty()) {
            return new ServerResponseDto(Status.FAILURE, "Book ISBN is mandatory");
        }
        if (bookDto.getAvailableStock() <= 0) {
            return new ServerResponseDto(Status.FAILURE, "Book available stock must be greater than 0");
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Category category = session.get(Category.class, categoryId);
            if (category == null) {
                return new ServerResponseDto(Status.FAILURE, "Category with ID " + categoryId + " not found.");
            }

            Book book = new Book();
            book.setTitle(bookDto.getTitle());
            book.setAuthor(bookDto.getAuthor());
            book.setIsbn(bookDto.getIsbn());
            book.setAvailableStock(bookDto.getAvailableStock());
            book.setCategory(category);

            transaction = session.beginTransaction();
            session.save(book);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null)
                transaction.rollback();
        }
        return new ServerResponseDto(Status.SUCCESS, "Book record is saved.");
    }

    @Transactional
    public Object updateBook(long bookId, long categoryId, BookDto updatedBook) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Book book = session.get(Book.class, bookId);
            if (book == null) {
                return new ServerResponseDto(Status.FAILURE, "Book with ID " + bookId + " not found.");
            }
            Category category = session.get(Category.class, categoryId);
            if (category == null) {
                return new ServerResponseDto(Status.FAILURE, "Category with ID " + categoryId + " not found.");
            }

            book.setCategory(category);
            if (updatedBook.getTitle() != null && !updatedBook.getTitle().isEmpty()) {
                book.setTitle(updatedBook.getTitle());
            }
            if (updatedBook.getAuthor() != null && !updatedBook.getAuthor().isEmpty()) {
                book.setAuthor(updatedBook.getAuthor());
            }
            if (updatedBook.getIsbn() != null && !updatedBook.getIsbn().isEmpty()) {
                book.setIsbn(updatedBook.getIsbn());
            }
            if (updatedBook.getAvailableStock() > 0) {
                book.setAvailableStock(updatedBook.getAvailableStock());
            }

            transaction = session.beginTransaction();
            session.update(book);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null)
                transaction.rollback();
        }
        return new ServerResponseDto(Status.SUCCESS, "Book with ID " + bookId + " is updated.");
    }

    public Object deleteBook(long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Book book = session.get(Book.class, id);
            if (book == null) {
                return new ServerResponseDto(Status.FAILURE, "Book with ID " + id + " not found.");
            }

            transaction = session.beginTransaction();
            session.delete(book);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null)
                transaction.rollback();
        }
        return new ServerResponseDto(Status.SUCCESS, "Book with ID " + id + " is deleted.");
    }

}
