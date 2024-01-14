package com.knowledgebase.dao;


import com.knowledgebase.dto.LendingRequestDto;
import com.knowledgebase.dto.LendingResponseDto;
import com.knowledgebase.dto.ServerResponseDto;
import com.knowledgebase.entities.Book;
import com.knowledgebase.entities.Lending;
import com.knowledgebase.entities.Status;
import com.knowledgebase.entities.User;
import com.knowledgebase.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LendingDao {

    public List<LendingResponseDto> getAllLendings() {
        List<LendingResponseDto> lendings = new ArrayList<>();
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            /* Query<LendingDto> query = session.createQuery(
                    "SELECT new com.knowledgebase.dto.LendingDto(l.lendingId, l.dateOut, l.dueDate, l.dateReturned) FROM Lending l",
                    LendingDto.class
            );*/

            Query<LendingResponseDto> query = session.createQuery(
                    "SELECT new com.knowledgebase.dto.LendingResponseDto(l.lendingId, l.dateOut, l.dueDate, l.dateReturned, u.username, b.title) " +
                            "FROM Lending l " +
                            "JOIN l.user u " +
                            "JOIN l.book b",
                    LendingResponseDto.class
            );

            lendings = query.getResultList();
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null)
                transaction.rollback();
        }
        return lendings;
    }

    public Object saveLending(long bookId, long userId, LendingRequestDto lendingDto) {

        if (lendingDto.getDateReturned() != null) {
            return new ServerResponseDto(Status.FAILURE,"Providing return date is not applicable when lending a book.");
        }

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Lending lending = new Lending();
            User user = session.get(User.class, userId);
            if (user == null) {
                return new ServerResponseDto(Status.FAILURE, "User with ID " + userId + " not found.");
            }
            lending.setUser(user);
            Book book = session.get(Book.class, bookId);
            if (book == null) {
                return new ServerResponseDto(Status.FAILURE, "Book with ID " + bookId + " not found.");
            }
            // update the available stock in books table
            int availableStock = book.getAvailableStock() - 1;
            book.setAvailableStock(availableStock);
            lending.setBook(book);

            lending.setDateOut(lendingDto.getDateOut());
            // due date will be 7 days after date out
            lending.setDueDate(lendingDto.getDateOut().plusDays(7));
            lending.setDateReturned(lendingDto.getDateReturned());

            transaction = session.beginTransaction();
            session.save(lending);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null)
                transaction.rollback();
        }
        return new ServerResponseDto(Status.SUCCESS, "Lending record is saved.");
    }

    public LocalDate fetchDueDateFromDB(long lendingId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Lending lending = session.get(Lending.class, lendingId);
            if (lending != null) {
                return lending.getDueDate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public LocalDate fetchDateOutFromDB(long lendingId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Lending lending = session.get(Lending.class, lendingId);
            if (lending != null) {
                return lending.getDateOut();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Transactional
    public Object updateLending(long lendingId, long bookId, long userId, LendingRequestDto updatedLending) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Lending lending = session.get(Lending.class, lendingId);
            if (lending == null) {
                return new ServerResponseDto(Status.FAILURE, "Lending record with ID " + lendingId + " not found.");
            }
            Book book = session.get(Book.class, bookId);
            if (book == null) {
                return new ServerResponseDto(Status.FAILURE, "Book with ID " + bookId + " not found.");
            }
            User user = session.get(User.class, userId);
            if (user == null) {
                return new ServerResponseDto(Status.FAILURE, "User record with ID " + userId + " not found.");
            }
            lending.setBook(book);
            lending.setUser(user);

            if (updatedLending.getDateReturned() != null) {
                LocalDate returnDate = updatedLending.getDateReturned();
                LocalDate dateOut = fetchDateOutFromDB(lendingId);
                if (returnDate.isBefore(dateOut)) {
                    return new ServerResponseDto(Status.FAILURE,
                            "Invalid Return Date (" + returnDate + "). It cannot be before the Date Out (" + dateOut + ")");
                }

                LocalDate dueDate = fetchDueDateFromDB(lendingId);
                if (returnDate.isAfter(dueDate)) {
                    return new ServerResponseDto(Status.FAILURE,
                            "Late return on " + returnDate + ". Your due date was " + dueDate + ". Fine applies.");
                }
                lending.setDateReturned(updatedLending.getDateReturned());
            }

            transaction = session.beginTransaction();
            session.update(lending);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null)
                transaction.rollback();
        }
        return new ServerResponseDto(Status.SUCCESS, "Lending record with ID " + lendingId + " is deleted");
    }


    public Object deleteLending(long lendingId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Lending lending = session.get(Lending.class, lendingId);
            if (lending == null) {
                return new ServerResponseDto(Status.FAILURE, "Lending record with ID " + lendingId + " not found.");
            }

            transaction = session.beginTransaction();
            session.delete(lending);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null)
                transaction.rollback();
        }
        return new ServerResponseDto(Status.SUCCESS, "Lending record with ID " + lendingId + " is deleted");
    }
}
