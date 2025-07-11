package vn.edu.tdtu.service.interfaces;

import vn.edu.tdtu.dto.MessageResponse;
import vn.edu.tdtu.model.ChatMessage;
import vn.tdtu.common.viewmodel.PaginationResponseVM;
import vn.tdtu.common.viewmodel.ResponseVM;

import java.util.Date;

public interface ChatMessageService {
    ChatMessage saveMessage(ChatMessage message);

    ResponseVM<PaginationResponseVM<MessageResponse>> getRoomMessages(String roomId, Date anchorDate, int page, int size);
}
