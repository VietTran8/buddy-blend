package vn.edu.tdtu.buddyblend.dto.response;

public class SignInResponseData {
    private String id;
    private String username;
    private String tokenType;
    private String token;
    private String userFullName;
    private String userAvatar;

    public SignInResponseData(String id, String username, String tokenType, String token, String userFullName, String userAvatar) {
        this.id = id;
        this.username = username;
        this.tokenType = tokenType;
        this.token = token;
        this.userFullName = userFullName;
        this.userAvatar = userAvatar;
    }

    public SignInResponseData() {
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "SignInResponse{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", tokenType='" + tokenType + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}