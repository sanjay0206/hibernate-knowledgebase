package com.knowledgebase.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.knowledgebase.dao.LendingDao;
import com.knowledgebase.dto.LendingRequestDto;
import com.knowledgebase.dto.LendingResponseDto;
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

@WebServlet("/lending")
public class LendingServlet extends HttpServlet {
    static final LendingDao lendingDao = new LendingDao();
    static String INTERNAL_SERVER_ERROR_MESSAGE = "Sorry, something went wrong.";
    static ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    private static final Logger logger = LogManager.getLogger(LendingServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            List<LendingResponseDto> lendings = lendingDao.getAllLendings();
            logger.debug("Fetched lendings list: {}", lendings);
            if (lendings.isEmpty()) {
                out.println(MAPPER.writeValueAsString("No lendings are found."));
            } else {
                out.println(MAPPER.writeValueAsString(lendings));
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

            LendingRequestDto lendingDto = MAPPER.readValue(payload, LendingRequestDto.class);
            logger.debug("Lending DTO: {}", lendingDto);
            int bookId = request.getParameter("bookId").isEmpty() ? 0: Integer.parseInt(request.getParameter("bookId"));
            int userId = request.getParameter("userId").isEmpty() ? 0: Integer.parseInt(request.getParameter("userId"));

            serverResponse = lendingDao.saveLending(bookId, userId, lendingDto);
            out.println(MAPPER.writeValueAsString(serverResponse));
        } catch (Exception e) {
            e.printStackTrace();
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

            LendingRequestDto lendingDto = MAPPER.readValue(payload, LendingRequestDto.class);
            logger.debug("Lending DTO: {}", lendingDto);

            int lendingId = request.getParameter("lendingId").isEmpty() ? 0: Integer.parseInt(request.getParameter("lendingId"));
            int bookId = request.getParameter("bookId").isEmpty() ? 0: Integer.parseInt(request.getParameter("bookId"));
            int userId = request.getParameter("userId").isEmpty() ? 0: Integer.parseInt(request.getParameter("userId"));

            serverResponse = lendingDao.updateLending(lendingId, bookId, userId, lendingDto);
            out.println(MAPPER.writeValueAsString(serverResponse));
        } catch (Exception e) {
            e.printStackTrace();
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
            int lendingId = request.getParameter("lendingId").isEmpty() ? 0: Integer.parseInt(request.getParameter("lendingId"));
            serverResponse = lendingDao.deleteLending(lendingId);
            out.println(MAPPER.writeValueAsString(serverResponse));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println(MAPPER.writeValueAsString(INTERNAL_SERVER_ERROR_MESSAGE));
        }
    }
}

