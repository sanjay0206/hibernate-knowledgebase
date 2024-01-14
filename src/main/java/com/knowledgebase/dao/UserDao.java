package com.knowledgebase.dao;

import com.knowledgebase.dto.ServerResponseDto;
import com.knowledgebase.dto.UserDto;
import com.knowledgebase.entities.Status;
import com.knowledgebase.entities.User;
import com.knowledgebase.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

public class UserDao {

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            users = session.createNativeQuery("SELECT * FROM users", User.class).getResultList();
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null)
                transaction.rollback();
        }
        return users;
    }

    public Object saveUser(UserDto userDto) {
        Transaction transaction = null;
        if (userDto.getUsername() == null || userDto.getUsername().trim().isEmpty()) {
            return new ServerResponseDto(Status.FAILURE, "Username is mandatory");
        }
        if (userDto.getPassword() == null || userDto.getPassword().trim().isEmpty()) {
            return new ServerResponseDto(Status.FAILURE,  "Password is mandatory");
        }
        if (userDto.getEmail() == null || userDto.getEmail().trim().isEmpty()) {
            return new ServerResponseDto(Status.FAILURE, "Email is mandatory");
        }
        if (userDto.getPhoneNumber() == null || userDto.getPhoneNumber().trim().isEmpty()) {
            return new ServerResponseDto(Status.FAILURE, "Phone number is mandatory");
        }
        if (userDto.getUserRole() == null || userDto.getUserRole().name().trim().isEmpty()) {
            return new ServerResponseDto(Status.FAILURE, "User role is mandatory");
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            User user = new User();
            user.setUsername(userDto.getUsername());
            user.setPassword(userDto.getPassword());
            user.setEmail(userDto.getEmail());
            user.setPhoneNumber(userDto.getPhoneNumber());
            user.setUserRole(userDto.getUserRole());

            transaction = session.beginTransaction();
            session.save(user);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null)
                transaction.rollback();
        }
        return new ServerResponseDto(Status.SUCCESS, "User record is saved.");
    }

    @Transactional
    public Object updateUser(long id, UserDto updatedUser) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            User user = session.get(User.class, id);
            if (user == null) {
                return new ServerResponseDto(Status.FAILURE, "User record with ID " + id + " not found.");
            }

            if (updatedUser.getUsername() != null && !updatedUser.getUsername().isEmpty()) {
                user.setUsername(updatedUser.getUsername());
            }
            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                user.setPassword(updatedUser.getPassword());
            }
            if (updatedUser.getEmail() != null && !updatedUser.getEmail().isEmpty()) {
                user.setEmail(updatedUser.getEmail());
            }
            if (updatedUser.getPhoneNumber() != null && !updatedUser.getPhoneNumber().isEmpty()) {
                user.setPhoneNumber(updatedUser.getPhoneNumber());
            }
            if (updatedUser.getUserRole() != null) {
                user.setUserRole(updatedUser.getUserRole());
            }

            transaction = session.beginTransaction();
            session.update(user);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null)
                transaction.rollback();
        }
        return new ServerResponseDto(Status.SUCCESS, "User record with ID " + id + " is updated");
    }

    public Object deleteUser(long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            User user = session.get(User.class, id);
            if (user == null) {
                return new ServerResponseDto(Status.FAILURE, "User record with ID " + id + " not found.");
            }
            transaction = session.beginTransaction();
            session.delete(user);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null)
                transaction.rollback();
        }
        return new ServerResponseDto(Status.SUCCESS, "User record with ID " + id + " is deleted");
    }
}

