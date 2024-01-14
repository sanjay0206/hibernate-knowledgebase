package com.knowledgebase.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.knowledgebase.dao.CategoryDao;
import com.knowledgebase.dto.CategoryDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/category")
public class CategoryServlet extends HttpServlet {
    static final CategoryDao categoryDao = new CategoryDao();
    static ObjectMapper MAPPER = new ObjectMapper();
    static String INTERNAL_SERVER_ERROR_MESSAGE = "Sorry, something went wrong.";
    private static final Logger logger = LogManager.getLogger(CategoryServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            List<CategoryDto> categories = categoryDao.getAllCategories();
            logger.debug("Fetched categories list: {}", categories);
            if (categories.isEmpty()) {
                out.println(MAPPER.writeValueAsString("No categories are found."));
            } else {
                out.println(MAPPER.writeValueAsString(categories));
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println(MAPPER.writeValueAsString(INTERNAL_SERVER_ERROR_MESSAGE));
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Object serverResponse = "";
        try {
            String payload = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            logger.debug("Incoming payload: {}", payload);

            CategoryDto categoryDto = MAPPER.readValue(payload, CategoryDto.class);
            logger.debug("Category DTO: {}", categoryDto);
            serverResponse = categoryDao.saveCategory(categoryDto);
            out.println(MAPPER.writeValueAsString(serverResponse));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println(MAPPER.writeValueAsString(INTERNAL_SERVER_ERROR_MESSAGE));
        }
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Object serverResponse = "";
        try {
            String payload = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            logger.debug("Incoming payload: {}", payload);

            int categoryId = request.getParameter("categoryId").isEmpty() ? 0: Integer.parseInt(request.getParameter("categoryId"));
            CategoryDto categoryDto = MAPPER.readValue(payload, CategoryDto.class);
            logger.debug("Category DTO: {}", categoryDto);
            serverResponse = categoryDao.updateCategory(categoryId, categoryDto);
            out.println(MAPPER.writeValueAsString(serverResponse));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println(MAPPER.writeValueAsString(INTERNAL_SERVER_ERROR_MESSAGE));
        }
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Object serverResponse = "";
        try {
            int categoryId = request.getParameter("categoryId").isEmpty() ? 0: Integer.parseInt(request.getParameter("categoryId"));
            serverResponse = categoryDao.deleteCategory(categoryId);
            out.println(MAPPER.writeValueAsString(serverResponse));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println(MAPPER.writeValueAsString(INTERNAL_SERVER_ERROR_MESSAGE));
        }
    }
}
