package vn.edu.tdtu.exception;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import vn.edu.tdtu.dtos.ResDTO;

@ControllerAdvice
@Slf4j
public class ApiExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> otherExceptionHandler(Exception ex, WebRequest webRequest) {
        log.warn(ex.getMessage());

        ex.printStackTrace();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResDTO<Object>(
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        ex.getMessage(),
                        null
                ));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<?> unauthorizedExceptionHandler(UnauthorizedException ex, WebRequest webRequest) {
        log.warn(ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ResDTO<Object>(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        ex.getMessage(),
                        null
                ));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> badRequestExceptionHandler(BadRequestException ex, WebRequest webRequest) {
        log.warn(ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResDTO<Object>(
                        HttpServletResponse.SC_BAD_REQUEST,
                        ex.getMessage(),
                        null
                ));
    }
}
