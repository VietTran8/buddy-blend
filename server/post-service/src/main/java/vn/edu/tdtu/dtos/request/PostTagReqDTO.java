package vn.edu.tdtu.dtos.request;

import jakarta.annotation.security.DenyAll;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.tdtu.models.User;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PostTagReqDTO {
    private String id;
    private LocalDateTime createdAt;
    private String taggedUserId;
}