package github.com._silva.upload_cloudflare_r2.cdn_r2.infrastructure;

import java.io.IOException;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {
    @Value("${api.key}")
    private String API_KEY;

    @Override
    protected void doFilterInternal(@SuppressWarnings("null") HttpServletRequest request, @SuppressWarnings("null") HttpServletResponse response, @SuppressWarnings("null") FilterChain filterChain) throws ServletException, IOException {
        String auth = request.getHeader("Authorization");

        if (isValidApiKey(auth)) {
            UsernamePasswordAuthenticationToken authResult = new UsernamePasswordAuthenticationToken(
                "api-user", null, Collections.emptyList()
            );
            SecurityContextHolder.getContext().setAuthentication(authResult);
        } else {}

        filterChain.doFilter(request, response);
    }

    private boolean isValidApiKey(String authHeader) {
        return authHeader != null &&
            authHeader.startsWith("Bearer ") &&
            authHeader.equals("Bearer " + API_KEY);
    }
}
