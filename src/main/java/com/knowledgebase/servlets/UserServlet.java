package com.knowledgebase.servlets;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.knowledgebase.dao.UserDao;
import com.knowledgebase.dto.UserDto;
import com.knowledgebase.entities.User;
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

@WebServlet(urlPatterns = {"/users", "/register"})
public class UserServlet extends HttpServlet {
    static final UserDao userDao = new UserDao();
    static ObjectMapper MAPPER = new ObjectMapper();
    static String INTERNAL_SERVER_ERROR_MESSAGE = "Sorry, something went wrong.";
    private static final Logger logger = LogManager.getLogger(UserServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            List<User> users = userDao.getAllUsers();
            logger.debug("Fetched users list: {}", users);
            if (users.isEmpty()) {
                out.println(MAPPER.writeValueAsString("No users are found."));
            } else {
                out.println(MAPPER.writeValueAsString(users));
            }
        } catch (Exception e) {
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
            
            UserDto userDto = MAPPER.readValue(payload, UserDto.class);
            logger.debug("User DTO: {}", userDto);

            serverResponse = userDao.saveUser(userDto);
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

            int userId = request.getParameter("userId").isEmpty() ? 0: Integer.parseInt(request.getParameter("userId"));
            UserDto userDto = MAPPER.readValue(payload, UserDto.class);
            logger.debug("User DTO: {}", userDto);

            serverResponse = userDao.updateUser(userId, userDto);
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
            int userId = request.getParameter("userId").isEmpty() ? 0: Integer.parseInt(request.getParameter("userId"));
            serverResponse = userDao.deleteUser(userId);
            out.println(MAPPER.writeValueAsString(serverResponse));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println(MAPPER.writeValueAsString(INTERNAL_SERVER_ERROR_MESSAGE));
        }
    }
}
