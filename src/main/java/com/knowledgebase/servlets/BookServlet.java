package com.knowledgebase.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.knowledgebase.dao.BookDao;
import com.knowledgebase.dto.BookDto;
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

@WebServlet("/books")
public class BookServlet extends HttpServlet {
    static final BookDao bookDao = new BookDao();
    static ObjectMapper MAPPER = new ObjectMapper();
    static String INTERNAL_SERVER_ERROR_MESSAGE = "Sorry, something went wrong.";
    private static final Logger logger = LogManager.getLogger(BookServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            List<BookDto> books = bookDao.getAllBooks();
            logger.debug("Fetched book list: {}", books);
            if (books.isEmpty()) {
                out.println(MAPPER.writeValueAsString("No books are found."));
            } else {
                out.println(MAPPER.writeValueAsString(books));
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println(MAPPER.writeValueAsString(INTERNAL_SERVER_ERROR_MESSAGE));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Object serverResponse = "";
        try {
            String payload = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            logger.debug("Incoming payload: {}", payload);

            int categoryId = request.getParameter("categoryId").isEmpty() ? 0: Integer.parseInt(request.getParameter("categoryId"));
            BookDto bookDto = MAPPER.readValue(payload, BookDto.class);
            logger.debug("Book DTO: {}", bookDto);

            serverResponse = bookDao.saveBook(categoryId, bookDto);
            out.println(MAPPER.writeValueAsString(serverResponse));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println(MAPPER.writeValueAsString(INTERNAL_SERVER_ERROR_MESSAGE));
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Object serverResponse = "";
        try {
            String payload = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            logger.debug("Incoming payload: {}", payload);

            int bookId = request.getParameter("bookId").isEmpty() ? 0: Integer.parseInt(request.getParameter("bookId"));
            int categoryId = request.getParameter("categoryId").isEmpty() ? 0: Integer.parseInt(request.getParameter("categoryId"));

            BookDto bookDto = MAPPER.readValue(payload, BookDto.class);
            logger.debug("Book DTO: {}", bookDto);
            serverResponse = bookDao.updateBook(bookId, categoryId, bookDto);
            out.println(MAPPER.writeValueAsString(serverResponse));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println(MAPPER.writeValueAsString(INTERNAL_SERVER_ERROR_MESSAGE));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Object serverResponse = "";
        try {
            int bookId = request.getParameter("bookId").isEmpty() ? 0: Integer.parseInt(request.getParameter("bookId"));
            serverResponse = bookDao.deleteBook(bookId);
            out.println(MAPPER.writeValueAsString(serverResponse));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println(MAPPER.writeValueAsString(INTERNAL_SERVER_ERROR_MESSAGE));
        }
    }
}
