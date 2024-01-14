package com.knowledgebase.security.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.knowledgebase.dto.ServerResponseDto;
import com.knowledgebase.entities.Status;
import com.knowledgebase.security.JWTTokenProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebFilter("/JWTFilter")
public class JWTFilter implements Filter {
    private static JWTTokenProvider tokenProvider;
    private static ObjectMapper MAPPER = new ObjectMapper();
    static String INTERNAL_SERVER_ERROR_MESSAGE = "Sorry, something went wrong.";
    private static final Logger logger = LogManager.getLogger(JWTFilter.class);

    public void init(FilterConfig config) {
        tokenProvider = new JWTTokenProvider();
        MAPPER = new ObjectMapper();
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException {
        logger.debug("Inside JWT filter...");

        HttpServletRequest httpServletReq = (HttpServletRequest) req;
        HttpServletResponse httpServletRes = (HttpServletResponse) resp;
        PrintWriter out = httpServletRes.getWriter();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        ServerResponseDto responseDto;
        try {
            String requestEndpoint = httpServletReq.getServletPath();
            logger.debug("requestEndpoint: {}", requestEndpoint);
            if (!requestEndpoint.equals("/register")) {
                String authHeader = httpServletReq.getHeader("Authorization");
                logger.debug("authHeader: {}", authHeader);

                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    responseDto = new ServerResponseDto(Status.FAILURE, "Missing or invalid Authorization header");
                    httpServletRes.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    out.println(MAPPER.writeValueAsString(responseDto));
                    return;
                }

                String token = authHeader.substring(7);
                if (!tokenProvider.isValidToken(token)) {
                    responseDto = new ServerResponseDto(Status.FAILURE, "Could not verify JWT token integrity!");
                    httpServletRes.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    out.println(MAPPER.writeValueAsString(responseDto));
                    return;
                }
            }
            chain.doFilter(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            httpServletRes.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println(MAPPER.writeValueAsString(INTERNAL_SERVER_ERROR_MESSAGE));
        }
    }

    public void destroy() {
    }
}