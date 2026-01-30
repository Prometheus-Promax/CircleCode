package org.bteam.circlecode.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bteam.circlecode.common.Response;
import org.bteam.circlecode.service.TokenService;
import org.bteam.circlecode.utils.JwtUtil;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final TokenService tokenService;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/CodeCircle/auth/login",
            "/CodeCircle/auth/register"
    );

    public JwtAuthenticationFilter(JwtUtil jwtUtil, TokenService tokenService) {
        this.jwtUtil = jwtUtil;
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain)
            throws ServletException, IOException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Check if the request path is public
        String requestURI = httpRequest.getRequestURI();
        for (String publicPath : PUBLIC_PATHS) {
            if (pathMatcher.match(publicPath, requestURI)) {
                chain.doFilter(request, response);
                return;
            }
        }

        // Access Token
        String token = null;
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            token =  bearerToken.substring(7);
        }

        // Validate Token
        if (token == null) {
            sendErrorResponse(httpResponse, HttpStatus.UNAUTHORIZED.value(), "Unauthorized: Token missing");
            return;
        }
        String userId = jwtUtil.getUserIdFromToken(token);
        String username = jwtUtil.getUsernameFromToken(token);
        if (!jwtUtil.validateToken(token) || !tokenService.isValid(token, userId)) {
            sendErrorResponse(httpResponse, HttpStatus.UNAUTHORIZED.value(), "Token invalid");
            return;
        }

        // Check Token Expiration
        Date expiration = jwtUtil.getExpirationDateFromToken(token);
        if (expiration.before(new Date())) {
            sendErrorResponse(httpResponse, HttpStatus.UNAUTHORIZED.value(), "Token expired");
            return;
        }

        // Store user information in request attribute
        httpRequest.setAttribute("userId", userId);
        httpRequest.setAttribute("username", username);

        // Filter Chain
        chain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message)
            throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        Response<Object> filterResponse = Response.builder()
                .code(response.getStatus())
                .message(message)
                .data(null)
                .build();
        String jsonResponse = new ObjectMapper().writeValueAsString(filterResponse);
        response.getWriter().write(jsonResponse);
    }

}
