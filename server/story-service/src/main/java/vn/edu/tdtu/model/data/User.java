package vn.edu.tdtu.model.data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private String id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String profilePicture;
    private String userFullName;
    private boolean isFriend;
    private boolean online;
}

