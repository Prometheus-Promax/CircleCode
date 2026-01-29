package org.bteam.circlecode.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@Component
@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request, 2048);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);
        LocalDateTime startTime = LocalDateTime.now();

        try {
            Map<String, String> paramMap = wrappedRequest.getParameterMap().entrySet().stream()
                    .collect(java.util.stream.Collectors.toMap(
                            Map.Entry::getKey,
                            e -> String.join(",", e.getValue())
                    ));
            log.info("Incoming Request: URI: {}, Method: {}, Parameters: {}",
                    request.getRequestURI(),
                    request.getMethod(),
                    paramMap
            );

            filterChain.doFilter(wrappedRequest, wrappedResponse);
        }catch (Exception e){
            log.error("Error during request processing: ", e);
            throw e;
        } finally {

            LocalDateTime endTime = LocalDateTime.now();
            long duration = java.time.Duration.between(startTime, endTime).toMillis();

            log.info("Outgoing Response: Status: {}, Duration: {} ms",
                    wrappedResponse.getStatus(),
                    duration
            );
            wrappedResponse.copyBodyToResponse();
        }

    }
}
