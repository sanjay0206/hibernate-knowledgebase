package com.knowledgebase.dao;

import com.knowledgebase.dto.CategoryDto;
import com.knowledgebase.dto.ServerResponseDto;
import com.knowledgebase.entities.Category;
import com.knowledgebase.entities.Status;
import com.knowledgebase.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

public class CategoryDao {

    public List<CategoryDto> getAllCategories() {
        List<CategoryDto> categories = new ArrayList<>();
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Query<CategoryDto> query = session.createQuery
                    ("SELECT new com.knowledgebase.dto.CategoryDto(c.categoryId, c.name, c.description) FROM Category c",
                            CategoryDto.class);

            categories = query.getResultList();
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null)
                transaction.rollback();
        }
        return categories;
    }

    public Object saveCategory(CategoryDto categoryDto) {
        Transaction transaction = null;
        if (categoryDto.getName() == null || categoryDto.getName().trim().isEmpty()) {
            return new ServerResponseDto(Status.FAILURE,  "Category name is mandatory");
        }
        if (categoryDto.getDescription() == null || categoryDto.getDescription().trim().isEmpty()) {
            return new ServerResponseDto(Status.FAILURE,  "Category description is mandatory");
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Category category = new Category();

            category.setName(categoryDto.getName());
            category.setDescription(categoryDto.getDescription());
            session.save(category);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null)
                transaction.rollback();
        }
        return new ServerResponseDto(Status.SUCCESS, "Category record is saved.");
    }

    @Transactional
    public Object updateCategory(long id, CategoryDto categoryDto) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Category updatedCategory = session.get(Category.class, id);
            if (updatedCategory == null) {
                return new ServerResponseDto(Status.FAILURE, "Category with ID " + id + " not found.");
            }
            if (categoryDto.getName() != null && !categoryDto.getName().isEmpty()) {
                updatedCategory.setName(categoryDto.getName());
            }
            if (categoryDto.getDescription() != null && !categoryDto.getDescription().isEmpty()) {
                updatedCategory.setDescription(categoryDto.getDescription());
            }

            transaction = session.beginTransaction();
            session.update(updatedCategory);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null)
                transaction.rollback();
        }
        return new ServerResponseDto(Status.SUCCESS,  "Category with ID " + id + " is updated.");
    }

    public Object deleteCategory(long id) {
        String responseStr = "";
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Category category = session.get(Category.class, id);
            if (category == null) {
                return new ServerResponseDto(Status.FAILURE, "Category with ID " + id + " not found.");
            }

            transaction = session.beginTransaction();
            session.delete(category);
            transaction.commit();
            responseStr = "Category with ID " + id + " is deleted.";
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null)
                transaction.rollback();
        }
        return responseStr;
    }
}
