package vn.edu.tdtu.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateInfoReqDTO {
    private String gender;
    private String phone;
    private String fromCity;
}
