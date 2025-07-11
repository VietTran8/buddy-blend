package vn.tdtu.common.viewmodel;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.tdtu.common.utils.MessageUtils;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResponseVM<D> {
    private String message;
    private D data;
    private int code;

    public void setMessage(String message, Object... vars) {
        this.message = MessageUtils.getMessage(message, vars);
    }

    public String getMessage() {
        return MessageUtils.getMessage(this.message);
    }

    public ResponseVM<D> withMessage(String message) {
        this.message = message;
        return this;
    }

    public ResponseVM<D> withData(D message) {
        this.data = data;
        return this;
    }

    public ResponseVM<D> withCode(Integer code) {
        this.code = code;
        return this;
    }

    public static ResponseVM<?> noContent() {
        return new ResponseVM<>(
                null,
                null,
                HttpServletResponse.SC_NO_CONTENT
        );
    }
}
