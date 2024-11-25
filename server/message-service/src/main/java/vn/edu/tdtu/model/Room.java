package vn.edu.tdtu.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Document
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Room {
    @Id
    private String id;
    private String userId1;
    private String userId2;
    private Date user1LastSeenTime;
    private Date user2LastSeenTime;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Date createdAt;
    @DBRef
    private ChatMessage latestMessage;
}
