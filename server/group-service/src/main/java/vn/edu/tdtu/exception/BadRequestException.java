package vn.edu.tdtu.exception;

import vn.edu.tdtu.utils.MessageUtils;

public class BadRequestException extends RuntimeException {
    private final String errorCode;
    private Object[] vars;

    public BadRequestException(String errorCode) {
        this.errorCode = errorCode;
    }

    public BadRequestException(String errorCode, Object... vars) {
        this.errorCode = errorCode;
        this.vars = vars;
    }

    @Override
    public String getMessage() {
        return MessageUtils.getMessage(errorCode, vars);
    }
}
