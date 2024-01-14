package com.knowledgebase.security.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.knowledgebase.dto.ServerResponseDto;
import com.knowledgebase.entities.Status;
import com.knowledgebase.entities.UserRole;
import com.knowledgebase.security.HttpMethod;
import com.knowledgebase.security.JWTTokenProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebFilter("/RoleBasedFilter")
public class RoleBasedFilter implements Filter {

    private Map<String, HttpMethod[]> userEndpoints;
    private Map<String, HttpMethod[]> librarianEndpoints;
    private List<String> excludedEndpoints;
    private ObjectMapper MAPPER;
    private JWTTokenProvider tokenProvider;
    private static final Logger logger = LogManager.getLogger(RoleBasedFilter.class);

    @Override
    public void init(FilterConfig filterConfig) {
        MAPPER = new ObjectMapper();
        tokenProvider = new JWTTokenProvider();

        excludedEndpoints = Arrays.asList("/login", "/register");
        userEndpoints = new HashMap<>();
        userEndpoints.put("/category", new HttpMethod[]{HttpMethod.GET});
        userEndpoints.put("/books", new HttpMethod[]{HttpMethod.GET});
        userEndpoints.put("/users", new HttpMethod[]{HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE});
        userEndpoints.put("/lending", new HttpMethod[]{HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT});

        librarianEndpoints = new HashMap<>();
        librarianEndpoints.put("/category", new HttpMethod[]{HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE});
        librarianEndpoints.put("/books", new HttpMethod[]{HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE});
        librarianEndpoints.put("/users", new HttpMethod[]{HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE});
        librarianEndpoints.put("/lending", new HttpMethod[]{HttpMethod.GET});
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        logger.debug("In Role based authentication filter...");

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        PrintWriter out = httpServletResponse.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        ServerResponseDto responseDto;

        String requestEndpoint = httpServletRequest.getServletPath();
        String requestMethod = httpServletRequest.getMethod();

        if (excludedEndpoints.contains(requestEndpoint)) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = httpServletRequest.getHeader("Authorization");
        String token = authHeader.substring(7);
        JSONObject tokenPayload = tokenProvider.decodeTokenPayload(token);
        String userRole = tokenPayload.getString("aud");
        Map<String, HttpMethod[]> allowedEndpoints = userRole.equals(UserRole.LIBRARIAN.name()) ?
                librarianEndpoints : userEndpoints;
        if (!isUserAuthorized(allowedEndpoints, requestEndpoint, requestMethod)) {
            responseDto = new ServerResponseDto(Status.FAILURE, "You do not have access to this endpoint");
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            out.println(MAPPER.writeValueAsString(responseDto));
            return;
        }
        chain.doFilter(request, response);
    }

    private boolean isUserAuthorized(Map<String, HttpMethod[]> allowedEndpoints, String requestEndpoint, String requestMethod) {
        logger.debug("allowedEndpoints: {}", allowedEndpoints);
        logger.debug("requestEndpoint: {}", requestEndpoint);
        logger.debug("requestMethod: {}", requestMethod);

        if (!allowedEndpoints.containsKey(requestEndpoint)) {
            return false;
        }

        HttpMethod[] allowedMethods = allowedEndpoints.get(requestEndpoint);
        return isMethodAllowed(allowedMethods, HttpMethod.valueOf(requestMethod));
    }

    private boolean isMethodAllowed(HttpMethod[] allowedMethods, HttpMethod method) {
        boolean isMethodAllowed = false;
        for (HttpMethod allowedMethod : allowedMethods) {
            if (allowedMethod.equals(method)) {
                isMethodAllowed = true;
                break;
            }
        }
        return isMethodAllowed;
    }

    @Override
    public void destroy() {
        // cleanup code (if any)
    }
}