package vn.edu.tdtu.exception;

import vn.edu.tdtu.utils.MessageUtils;

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
