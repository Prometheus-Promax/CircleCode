package org.bteam.circlecode.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bteam.circlecode.utils.JwtUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.aspectj.weaver.tools.cache.SimpleCacheFactory.path;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/CodeCircle/auth/login",
            "/CodeCircle/auth/register"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestURI = httpRequest.getRequestURI();
        for (String publicPath : PUBLIC_PATHS) {
            if (pathMatcher.match(publicPath, requestURI)) {
                chain.doFilter(request, response);
                return;
            }
        }

        // 2. 获取 Token
        String token = null;
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            token =  bearerToken.substring(7);
        }

        // 3. 验证 Token
        if (token == null) {
            sendErrorResponse(httpResponse, 401, "Unauthorized: Token missing");
            return;
        }
        if (!jwtUtil.validateToken(token)) {
            sendErrorResponse(httpResponse, 401, "Token invalid");
            return;
        }

        // 4. 检查 Token 是否过期
        Date expiration = jwtUtil.getExpirationDateFromToken(token);
        if (expiration.before(new Date())) {
            sendErrorResponse(httpResponse, 401, "Token expired");
            return;
        }

        // 5. 将用户信息存入请求属性，方便后续使用
        String userId = jwtUtil.getUserIdFromToken(token);
        String username = jwtUtil.getUsernameFromToken(token);

        httpRequest.setAttribute("userId", userId);
        httpRequest.setAttribute("username", username);

        // 6. 继续过滤器链
        chain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message)
            throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(String.format(
                "{\"success\": false, \"code\": \"AUTH_ERROR\", \"message\": \"%s\"}",
                message
        ));
    }

}
