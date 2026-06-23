package in.rahul.learning.security.filter;

import in.rahul.learning.security.jwt.JWTTokenProvider;
import in.rahul.learning.util.AESUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.OK;

@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilterNew {
    //extends OncePerRequestFilter {

    private static final String HEADER_NAME = "X-Correlation-Id";
    private static final String MDC_KEY = "correlationId";

    private static final String OPTIONS_HTTP_METHOD = "OPTIONS";
    private static final String TOKEN_PREFIX = "Bearer ";

    private final JWTTokenProvider jwtTokenProvider;
    private final AESUtil aesUtil;

    //@Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {

            // ===============================
            // 1. CORRELATION ID
            // ===============================
            String correlationId = request.getHeader(HEADER_NAME);

            if (correlationId == null || correlationId.isEmpty()) {
                correlationId = UUID.randomUUID().toString();
            }

            MDC.put(MDC_KEY, correlationId);
            response.setHeader(HEADER_NAME, correlationId);

            // ===============================
            // 2. CORS PREFLIGHT
            // ===============================
            if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
                response.setStatus(OK.value());
                return;
            }

            // ===============================
            // 3. AUTH HEADER VALIDATION (VERY IMPORTANT)
            // ===============================
            String authorizationHeader = request.getHeader(AUTHORIZATION);

            if (authorizationHeader == null ||
                    !authorizationHeader.startsWith(TOKEN_PREFIX)) {

                filterChain.doFilter(request, response);
                return;
            }

            // ===============================
            // 4. SAFE TOKEN EXTRACTION
            // ===============================
            String encryptedToken = authorizationHeader.substring(TOKEN_PREFIX.length());

            if (encryptedToken.isBlank()) {
                filterChain.doFilter(request, response);
                return;
            }

            // ===============================
            // 5. DECRYPT TOKEN SAFELY
            // ===============================
            String token;
            try {
                token = aesUtil.decrypt(encryptedToken);
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
                filterChain.doFilter(request, response);
                return;
            }

            // ===============================
            // 6. JWT VALIDATION
            // ===============================
            String username = jwtTokenProvider.getSubject(token);

            if (jwtTokenProvider.isTokenValid(username, token)
                    && SecurityContextHolder.getContext().getAuthentication() == null) {

                List<GrantedAuthority> authorities =
                        jwtTokenProvider.getAuthorities(token);

                Authentication authentication =
                        jwtTokenProvider.getAuthentication(username, authorities, request);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            // ===============================
            // 7. CONTINUE FILTER CHAIN
            // ===============================
            filterChain.doFilter(request, response);

        } finally {
            MDC.clear();
            SecurityContextHolder.clearContext();
        }
    }
}
