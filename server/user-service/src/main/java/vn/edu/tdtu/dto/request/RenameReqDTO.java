package vn.edu.tdtu.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RenameReqDTO {
    private String token;
    private String firstName;
    private String middleName;
    private String lastName;
}
