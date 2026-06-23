package in.rahul.learning.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.rahul.learning.commons.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Component
public class JwtAuthenticationEntryPoint extends Http403ForbiddenEntryPoint {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void commence(@NonNull HttpServletRequest request,
                         HttpServletResponse response,
                         @NonNull AuthenticationException exception) throws IOException {

        BaseResponse httpResponse = BaseResponse.builder()
                .status(HttpServletResponse.SC_UNAUTHORIZED)
                .message("You need to login to access this resource")
                .data(null)
                .timestamp(Instant.now())
                .build();

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // IMPORTANT: use ONLY writer OR ONLY stream
        objectMapper.writeValue(response.getWriter(), httpResponse);
    }
}
