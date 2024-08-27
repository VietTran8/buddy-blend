package vn.edu.tdtu.exception;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import vn.edu.tdtu.dtos.ResDTO;

@RestControllerAdvice
@Slf4j
public class MyExceptionHandler {
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<?> missingHeaderRequestHandler(MissingRequestHeaderException ex){
        ResDTO<?> response = new ResDTO<>();
        response.setCode(HttpServletResponse.SC_UNAUTHORIZED);
        response.setData(null);
        response.setMessage("Unauthorized: " + ex.getMessage());

        return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
                .body(response);
    }

    @ExceptionHandler({RuntimeException.class, IllegalArgumentException.class})
    public ResponseEntity<?> runtimeExceptionHandler(Exception exception){
        ResDTO<?> response = new ResDTO<>();
        response.setCode(HttpServletResponse.SC_BAD_REQUEST);
        response.setData(null);
        response.setMessage(exception.getMessage());

        log.error(exception.getMessage());
        exception.printStackTrace();

        return ResponseEntity.status(HttpServletResponse.SC_BAD_REQUEST)
                .body(response);
    }
}
