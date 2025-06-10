package vn.tdtu.common.viewmodel;

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
}
