package vn.edu.tdtu.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties
public class SaveUserReqDTO {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String middleName;
    private String gender;
    private String profilePicture;
    private String bio;
}
