package vn.edu.tdtu.buddyblend.dto.response;

public class SignUpResponseData {
    private String id;
    private String email;

    public SignUpResponseData(String id, String email) {
        this.id = id;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public SignUpResponseData() {
    }

    @Override
    public String toString() {
        return "SignUpResponseData{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
