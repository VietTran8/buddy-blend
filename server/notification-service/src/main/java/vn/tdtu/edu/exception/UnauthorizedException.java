package vn.tdtu.edu.exception;

import vn.tdtu.edu.util.MessageUtils;

public class UnauthorizedException extends RuntimeException {
    private final String errorCode;
    private Object[] vars;

    public UnauthorizedException(String errorCode) {
        this.errorCode = errorCode;
    }

    public UnauthorizedException(String errorCode, Object... vars) {
        this.errorCode = errorCode;
        this.vars = vars;
    }

    @Override
    public String getMessage() {
        return MessageUtils.getMessage(errorCode, vars);
    }
}
