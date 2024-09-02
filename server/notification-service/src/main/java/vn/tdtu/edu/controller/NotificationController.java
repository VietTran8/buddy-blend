package vn.tdtu.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.tdtu.edu.dtos.ResDTO;
import vn.tdtu.edu.service.InteractNotiService;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final InteractNotiService interactNotiService;

    @GetMapping()
    public ResponseEntity<?> getAllNotifications(@RequestHeader("Authorization") String token) {
        ResDTO<?> response = interactNotiService.findAllByToken(token);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/detach/{id}")
    public ResponseEntity<?> detachNotification(@RequestHeader("Authorization") String token,
                                                @PathVariable("id") String id) {
        ResDTO<?> response = interactNotiService.detachNotification(token, id);
        return ResponseEntity.status(response.getCode()).body(response);
    }
}
