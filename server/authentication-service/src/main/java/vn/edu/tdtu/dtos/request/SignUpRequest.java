package vn.edu.tdtu.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String middleName;
    private String gender;
    private String profilePicture;
    private String bio;
}