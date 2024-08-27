package vn.edu.tdtu.buddyblend.dto.response;

public class RegistrationIdResDTO {
    private String registrationId;

    public RegistrationIdResDTO(String registrationId) {
        this.registrationId = registrationId;
    }

    public RegistrationIdResDTO() {
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }
}
