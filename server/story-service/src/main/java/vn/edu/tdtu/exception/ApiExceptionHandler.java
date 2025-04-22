package vn.edu.tdtu.exception;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import vn.edu.tdtu.dto.ResDTO;

@RestControllerAdvice
@Slf4j
public class ApiExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException ex) {

        ex.printStackTrace();

        ResDTO<?> response = new ResDTO<>(ex.getMessage(), null, HttpServletResponse.SC_BAD_REQUEST);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<?> handleJwtException(JwtException ex) {
        ex.printStackTrace();

        ResDTO<?> response = new ResDTO<>(ex.getMessage(), null, HttpServletResponse.SC_UNAUTHORIZED);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<?> missingHeaderRequestHandler(MissingRequestHeaderException ex) {
        ResDTO<?> response = new ResDTO<>();
        response.setCode(HttpServletResponse.SC_UNAUTHORIZED);
        response.setData(null);
        response.setMessage("Unauthorized: " + ex.getMessage());

        return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> otherExceptionHandler(Exception ex, WebRequest webRequest) {
        log.warn(ex.getMessage());

        ex.printStackTrace();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResDTO<Object>(
                        ex.getMessage(),
                        null,
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR
                ));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<?> unauthorizedExceptionHandler(UnauthorizedException ex, WebRequest webRequest) {
        log.warn(ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ResDTO<Object>(
                        ex.getMessage(),
                        null,
                        HttpServletResponse.SC_UNAUTHORIZED
                ));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> badRequestExceptionHandler(BadRequestException ex, WebRequest webRequest) {
        log.warn(ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResDTO<Object>(
                        ex.getMessage(),
                        null,
                        HttpServletResponse.SC_BAD_REQUEST
                ));
    }
}
