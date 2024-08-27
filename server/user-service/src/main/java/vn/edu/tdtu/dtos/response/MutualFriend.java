package vn.edu.tdtu.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MutualFriend {
    private String id;
    private String fullName;
    private String profileImage;
}
