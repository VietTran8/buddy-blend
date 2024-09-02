package vn.tdtu.edu.exception;


import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BadRequestException extends RuntimeException {
    private String message;

    @Override
    public String getMessage() {
        return message;
    }
}
