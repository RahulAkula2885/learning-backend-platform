package in.rahul.learning.security.filter;

import in.rahul.learning.security.jwt.JWTTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.OK;

@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private static final String HEADER_NAME = "X-Correlation-Id";
    private static final String MDC_KEY = "correlationId";

    private static final String OPTIONS_HTTP_METHOD = "OPTIONS";
    private static final String TOKEN_PREFIX = "Bearer ";

    private final JWTTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        try {

            // ===============================
            // 1. CORRELATION ID HANDLING
            // ===============================

            String correlationId = request.getHeader(HEADER_NAME);

            // If not provided by client → generate new one
            if (correlationId == null || correlationId.isEmpty()) {
                correlationId = UUID.randomUUID().toString();
            }

            // Put into MDC → used in logs (%X{correlationId}):- MDC:- Mapped Diagnostic Context
            MDC.put(MDC_KEY, correlationId);

            // Send back in response (useful for debugging)
            response.setHeader(HEADER_NAME, correlationId);


            /*
               ===============================
              2. HANDLE CORS PREFLIGHT REQUEST
             ===============================
             */
            if (OPTIONS_HTTP_METHOD.equalsIgnoreCase(request.getMethod())) {
                response.setStatus(OK.value());
                return; // IMPORTANT: stop further processing
            }


            // ===============================
            // 3. JWT AUTHENTICATION
            // ===============================

            String authorizationHeader = request.getHeader(AUTHORIZATION);

            // If no token → continue without authentication (or block if required)
            if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {

                // Extract JWT token
                String token = authorizationHeader.substring(TOKEN_PREFIX.length());

                // Extract username from token
                String username = jwtTokenProvider.getSubject(token);

                // Put into MDC → used in logs ([%X{userId}])
                MDC.put("userId", username);

                // Validate token + ensure no existing authentication
                if (jwtTokenProvider.isTokenValid(username, token)
                        && SecurityContextHolder.getContext().getAuthentication() == null) {

                    // Get roles/authorities from token
                    List<GrantedAuthority> authorities =
                            jwtTokenProvider.getAuthorities(token);

                    // Create Authentication object
                    Authentication authentication =
                            jwtTokenProvider.getAuthentication(username, authorities, request);

                    // Set authentication in security context
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }


            // ===============================
            // 4. CONTINUE FILTER CHAIN
            // ===============================
            filterChain.doFilter(request, response);

        } finally {

            // ===============================
            // 5. CLEANUP (VERY IMPORTANT)
            // ===============================

            // Prevent thread-local memory leak in thread pools
            MDC.clear();

            // Clear security context after request
            SecurityContextHolder.clearContext();
        }
    }
}