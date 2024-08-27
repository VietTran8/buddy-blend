package vn.edu.tdtu.exception;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import vn.edu.tdtu.dtos.ResDTO;

@RestControllerAdvice
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

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException ex){
        ResDTO<?> response = new ResDTO<>();

        response.setCode(HttpServletResponse.SC_BAD_REQUEST);
        response.setData(null);
        response.setMessage(ex.getMessage());

        return ResponseEntity.status(response.getCode()).body(response);
    }
}
