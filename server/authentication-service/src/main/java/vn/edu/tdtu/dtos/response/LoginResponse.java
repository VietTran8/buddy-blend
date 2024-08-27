package vn.edu.tdtu.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse {
    private String id;
    private String username;
    private String tokenType = "Bearer";
    private String token;
    private String userFullName;
    private String userAvatar;
}