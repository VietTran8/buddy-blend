package vn.edu.tdtu.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RenameReqDTO {
//    private String userId;
    private String firstName;
    private String middleName;
    private String lastName;
}