package vn.edu.tdtu.buddyblend.dto;

public class ResDTO<D> {
    private String message;
    private D data;
    private int code;

    public ResDTO(String message, D data, int code) {
        this.message = message;
        this.data = data;
        this.code = code;
    }

    public ResDTO() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public D getData() {
        return data;
    }

    public void setData(D data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
