package vn.edu.tdtu.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.tdtu.model.ChatMessage;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomResponse {
    private String id;
    private ChatMessage latestMessage;
    private String roomName;
    private String roomImage;
    private boolean lastSentByYou;
    private String opponentUserId;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
    private Date opponentLastSeenTime;
    private boolean online;
    @JsonIgnore()
    private Date createdAt;
}
