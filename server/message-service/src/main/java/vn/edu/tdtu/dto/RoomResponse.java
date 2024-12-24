package vn.edu.tdtu.dto;

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
    @JsonIgnore()
    private Date createdAt;
}
