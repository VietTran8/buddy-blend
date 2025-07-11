package vn.tdtu.common.exception;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import vn.tdtu.common.utils.MessageCode;
import vn.tdtu.common.utils.MessageUtils;
import vn.tdtu.common.viewmodel.ResponseVM;

@RestControllerAdvice
@Slf4j
public class ApiExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> uncheckedExceptionHandler(Exception ex, WebRequest webRequest) {
        log.warn("[uncheckedExceptionHandler]", ex);

        String message;
        int status;

        if (ex instanceof NullPointerException) {
            message = MessageCode.Exception.EXCEPTION_NULL_POINTER;
            status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        } else if (ex instanceof IllegalArgumentException) {
            message = MessageCode.Exception.EXCEPTION_ILLEGAL_ARGUMENT;
            status = HttpServletResponse.SC_BAD_REQUEST;
        } else if (ex instanceof IndexOutOfBoundsException) {
            message = MessageCode.Exception.EXCEPTION_OUT_OF_BOUNDS;
            status = HttpServletResponse.SC_BAD_REQUEST;
        } else if (ex instanceof IllegalStateException) {
            message = MessageCode.Exception.EXCEPTION_ILLEGAL_STATE;
            status = HttpServletResponse.SC_CONFLICT;
        } else {
            message = MessageUtils.getMessage(MessageCode.Exception.EXCEPTION_UNKNOWN, ex.getMessage());
            status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        }

        return ResponseEntity.status(status).body(new ResponseVM<>(
                message,
                null,
                status
        ));
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<?> jwtExceptionHandler(JwtException ex) {
        log.warn("[jwtExceptionHandler]", ex);

        ResponseVM<?> response = new ResponseVM<>(
                ex.getMessage(),
                null,
                HttpServletResponse.SC_UNAUTHORIZED
        );

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<?> missingHeaderRequestHandler(MissingRequestHeaderException ex) {
        log.warn("[missingHeaderRequestHandler]", ex);

        ResponseVM<?> response = new ResponseVM<>();
        response.setCode(HttpServletResponse.SC_UNAUTHORIZED);
        response.setData(null);
        response.setMessage(MessageCode.Authentication.AUTH_UNAUTHORIZED);

        return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> otherExceptionHandler(Exception ex, WebRequest webRequest) {
        log.warn("[otherExceptionHandler]", ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseVM<Object>(
                        ex.getMessage(),
                        null,
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR
                ));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<?> unauthorizedExceptionHandler(UnauthorizedException ex, WebRequest webRequest) {
        log.warn("[unauthorizedExceptionHandler]", ex);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ResponseVM<Object>(
                        ex.getMessage(),
                        null,
                        HttpServletResponse.SC_UNAUTHORIZED
                ));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> badRequestExceptionHandler(BadRequestException ex, WebRequest webRequest) {
        log.warn("[unauthorizedExceptionHandler]", ex);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseVM<Object>(
                        ex.getMessage(),
                        null,
                        HttpServletResponse.SC_BAD_REQUEST
                ));
    }
}