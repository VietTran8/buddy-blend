package vn.edu.tdtu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.service.interfaces.ChatMessageService;
import vn.edu.tdtu.service.interfaces.RoomService;

import java.util.Date;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;
    private final ChatMessageService chatMessageService;

    @GetMapping()
    public ResponseEntity<?> getRooms() {
        ResDTO<?> response = roomService.findRoomsByUser();
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/{id}/messages")
    public ResponseEntity<?> getRoomMessages(
            @PathVariable("id") String id,
            @RequestParam(name = "page", required = false, defaultValue = "1") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @RequestParam(name = "anchorDate")
            @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss") Date anchorDate
    ) {
        ResDTO<?> response = chatMessageService.getRoomMessages(id, anchorDate, page, size);
        return ResponseEntity.status(response.getCode()).body(response);
    }
}
