package com.knowledgebase.dao;

import com.knowledgebase.dto.LoginDto;
import com.knowledgebase.entities.User;
import com.knowledgebase.entities.UserRole;
import com.knowledgebase.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;

import java.util.List;

public class LoginDao {

    public boolean isValidateCredentials(LoginDto loginDto) {
        //validate the username and password against the database or any other method
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            NativeQuery<User> query = session.createNativeQuery(sql, User.class);
            query.setParameter(1, loginDto.getUsername());
            query.setParameter(2, loginDto.getPassword());
            List<User> result = query.list();
            transaction.commit();
            return !result.isEmpty();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null)
                transaction.rollback();
        }
        return false;
    }

    public UserRole fetchUserRoleByUsername(String username) {
        //validate the username and password against the database or any other method
        UserRole userRole = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String sql = "SELECT * FROM users WHERE username = :username";
            NativeQuery<User> query = session.createNativeQuery(sql, User.class);
            query.setParameter("username", username);
            userRole = query.uniqueResult().getUserRole();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userRole;
    }
}