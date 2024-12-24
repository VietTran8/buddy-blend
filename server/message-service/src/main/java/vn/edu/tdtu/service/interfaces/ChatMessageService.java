package vn.edu.tdtu.service.interfaces;

import vn.edu.tdtu.dto.MessageResponse;
import vn.edu.tdtu.dto.PaginationResponse;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.model.ChatMessage;

import java.util.Date;

public interface ChatMessageService {
    public ChatMessage saveMessage(ChatMessage message);
    public ResDTO<PaginationResponse<MessageResponse>> getRoomMessages(String roomId, Date anchorDate, int page, int size);
}
