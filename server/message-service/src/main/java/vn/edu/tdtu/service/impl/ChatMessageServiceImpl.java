package vn.edu.tdtu.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dto.MessageResponse;
import vn.edu.tdtu.dto.PaginationResponse;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.mapper.RoomResponseMapper;
import vn.edu.tdtu.model.ChatMessage;
import vn.edu.tdtu.repository.ChatMessageRepository;
import vn.edu.tdtu.service.interfaces.ChatMessageService;
import vn.edu.tdtu.util.SecurityContextUtils;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;

    @Override
    public ChatMessage saveMessage(ChatMessage message) {
        return chatMessageRepository.save(message);
    }

    @Override
    public ResDTO<PaginationResponse<MessageResponse>> getRoomMessages(String roomId, Date anchorDate, int page, int size) {
        ResDTO<PaginationResponse<MessageResponse>> response = new ResDTO<>();
        Page<ChatMessage> messagePage = chatMessageRepository.findByRoomIdAndCreatedAtBeforeOrderByCreatedAtDesc(roomId, anchorDate, PageRequest.of(page - 1, size));

        String userId = SecurityContextUtils.getUserId();

        response.setData(new PaginationResponse<>(
                page,
                size,
                messagePage.getTotalPages(),
                messagePage.get().map(message -> RoomResponseMapper.mapMsgToMsgResponse(userId, message)).toList(),
                messagePage.getTotalElements()
        ));
        response.setCode(HttpServletResponse.SC_OK);
        response.setMessage("Messages fetched successfully!");

        return response;
    }
}
