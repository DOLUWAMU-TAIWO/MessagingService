package dev.dolu.messaging.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(ApiKeyFilter.class);
    private static final String API_KEY = System.getenv("SERVICE_PASSWORD");

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        logger.info("Request [{} {}] from IP: {}",
                request.getMethod(), request.getRequestURI(), request.getRemoteAddr());

        if (request.getRequestURI().startsWith("/actuator")) {
            logger.info("Actuator endpoint accessed from IP: {}", request.getRemoteAddr());
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            logger.warn("Unauthorized request - Missing Authorization header from IP: {}", request.getRemoteAddr());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing or invalid Authorization header");
            return;
        }

        String providedApiKey = header.substring(7);
        if (!API_KEY.equals(providedApiKey)) {
            logger.warn("Unauthorized request - Invalid API Key from IP: {}", request.getRemoteAddr());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid API Key");
            return;
        }

        logger.info("Successful authentication via API Key from IP: {}", request.getRemoteAddr());

        var auth = new UsernamePasswordAuthenticationToken(
                "apiKeyUser", null, Collections.emptyList()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request, response);
    }
}