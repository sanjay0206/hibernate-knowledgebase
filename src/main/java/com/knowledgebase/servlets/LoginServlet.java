package com.knowledgebase.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.knowledgebase.dao.LoginDao;
import com.knowledgebase.dto.LoginDto;
import com.knowledgebase.security.JWTTokenProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    static ObjectMapper MAPPER = new ObjectMapper();
    static String INTERNAL_SERVER_ERROR_MESSAGE = "Sorry, something went wrong.";
    static String UNAUTHORIZED_ERROR_MESSAGE = "Sorry, Invalid credentials!";
    private static final Logger logger = LogManager.getLogger(LoginServlet.class);

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out =  response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            String payload = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            logger.debug("Incoming payload: {}", payload);

            // validate the username and password
            LoginDto loginDto = MAPPER.readValue(payload, LoginDto.class);
            LoginDao loginDao = new LoginDao();
            if (loginDao.isValidateCredentials(loginDto)) {
                logger.debug("User {} credentials are valid!", loginDto.getUsername());

                //generate the JWT token
                JWTTokenProvider tokenProvider = new JWTTokenProvider();
                String token = tokenProvider.generateJWT(loginDto.getUsername());
                if (token == null) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.println(MAPPER.writeValueAsString(INTERNAL_SERVER_ERROR_MESSAGE));
                    return;
                }

                Map<String, String> bearerToken = new HashMap<>();
                bearerToken.put("bearerToken", token);
                out.println(MAPPER.writeValueAsString(bearerToken));

                //set the JWT token in the response header
                response.setHeader("Authorization", "Bearer " + token);
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                //return error if invalid credentials
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.println(MAPPER.writeValueAsString(UNAUTHORIZED_ERROR_MESSAGE));
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println(MAPPER.writeValueAsString(INTERNAL_SERVER_ERROR_MESSAGE));
        }
    }
}
