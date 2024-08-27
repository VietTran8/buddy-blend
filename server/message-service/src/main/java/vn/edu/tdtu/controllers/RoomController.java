package vn.edu.tdtu.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.service.RoomService;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;
    @GetMapping()
    public ResponseEntity<?> getRooms(@RequestHeader("Authorization") String token){
        ResDTO<?> response = roomService.findRoomsByToken(token);
        return ResponseEntity.status(response.getCode()).body(response);
    }
}
