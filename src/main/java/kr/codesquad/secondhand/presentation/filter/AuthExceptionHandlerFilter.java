package kr.codesquad.secondhand.presentation.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import kr.codesquad.secondhand.exception.BadRequestException;
import kr.codesquad.secondhand.exception.ErrorResponse;
import kr.codesquad.secondhand.exception.SecondHandException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class AuthExceptionHandlerFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (SecondHandException e) {
            setErrorResponse(e, response);
        }
    }

    private void setErrorResponse(SecondHandException e, HttpServletResponse response) throws IOException {
        int status = 500;
        if (e instanceof BadRequestException) {
            status = 400;
        }
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        ErrorResponse errorResponse = new ErrorResponse(status, e.getErrorCode().getMessage());
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
