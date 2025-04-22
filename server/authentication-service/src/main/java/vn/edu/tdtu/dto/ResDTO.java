package vn.edu.tdtu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.tdtu.util.MessageUtils;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResDTO<D> {
    private int code;
    private String message;
    private D data;

    public String getMessage() {
        return MessageUtils.getMessage(this.message);
    }

    public void setMessage(String message, Object... vars) {
        this.message = MessageUtils.getMessage(message, vars);
    }
}
