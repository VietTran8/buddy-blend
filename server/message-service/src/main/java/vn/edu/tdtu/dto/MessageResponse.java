package vn.edu.tdtu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import vn.edu.tdtu.model.ChatMessage;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponse extends ChatMessage {
    private boolean sentByYou;
}
