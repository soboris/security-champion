package com.secchamp.chal.config;

import com.secchamp.chal.util.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.util.AntPathMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtTokenUtil jwtTokenUtil;
    private final String[] whitelistedEndpoints;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public JwtAuthenticationFilter(JwtTokenUtil jwtTokenUtil, String[] whitelistedEndpoints) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.whitelistedEndpoints = whitelistedEndpoints;
        logger.info("Whitelisted endpoints: {}", String.join(", ", whitelistedEndpoints));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        logger.debug("Processing request for path: {}", path);

        // Skip JWT validation for static resources and whitelisted endpoints
        if (isAllowedResources(path) || isWhitelisted(path)) {
            logger.debug("Path {} is static resource or whitelisted, skipping JWT validation", path);
            chain.doFilter(request, response);
            return;
        }

        logger.debug("Path {} requires JWT validation", path);

        final String authorizationHeader = request.getHeader("Authorization");
        String jwt = null;
        Claims claims = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);  // Extract the token
            try {
                claims = jwtTokenUtil.extractAllClaims(jwt);  // Extract claims from the token
            } catch (Exception e) {
                logger.error("Error extracting claims from JWT", e);
            }
        }

        // Validate the JWT token and check the login flag
        if (jwt != null && claims != null && jwtTokenUtil.isLoggedIn(jwt)) {
            String username = claims.getSubject();
            Integer role = claims.get("role", Integer.class);  // Extract role as Integer

            logger.debug("Valid JWT found for user: {}", username);

            UsernamePasswordAuthenticationToken authenticationToken = 
                new UsernamePasswordAuthenticationToken(
                    new User(username, "", Collections.emptyList()), 
                    null, 
                    Collections.singleton(() -> role.toString())
                );
            
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            
            chain.doFilter(request, response);
        } else {
            logger.debug("Invalid or missing JWT");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private boolean isWhitelisted(String path) {
        for (String pattern : whitelistedEndpoints) {
            if (pathMatcher.match(pattern, path)) {
                logger.debug("Path {} matched whitelist pattern {}", path, pattern);
                return true;
            }
        }
        logger.debug("Path {} did not match any whitelist patterns", path);
        return false;
    }

    private boolean isAllowedResources(String path) {
        return path.startsWith("/pages/") || path.startsWith("/images/") 
            || path.startsWith("/css/") || path.startsWith("/js/")
            || path.startsWith("/ws/") || path.startsWith("/files/")
            || path.startsWith("/uploads/");
    }
}