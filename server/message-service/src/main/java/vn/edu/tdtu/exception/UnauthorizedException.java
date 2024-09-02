package vn.edu.tdtu.exception;


import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UnauthorizedException extends RuntimeException{
    private String message;

    @Override
    public String getMessage() {
        return message;
    }
}
