package vn.tdtu.edu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.tdtu.edu.util.MessageUtils;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResDTO<D> {
    private String message;
    private D data;
    private int code;

    public void setMessage(String message, Object... vars) {
        this.message = MessageUtils.getMessage(message, vars);
    }

    public String getMessage(){
        return MessageUtils.getMessage(this.message);
    }
}
