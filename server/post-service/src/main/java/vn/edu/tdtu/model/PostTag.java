package vn.edu.tdtu.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.tdtu.common.dto.UserDTO;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostTag {
    private String id;
    private LocalDateTime createdAt;
    private UserDTO taggedUser;
}
