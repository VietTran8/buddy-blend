package vn.tdtu.edu.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@Builder
public class Message {
    private String id;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Date createdAt;
    private String content;
    private String medias;
    private String fromUserId;
    private String toUserId;
}