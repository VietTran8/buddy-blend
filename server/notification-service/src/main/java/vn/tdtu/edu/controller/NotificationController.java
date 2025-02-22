package vn.tdtu.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.tdtu.edu.dto.ResDTO;
import vn.tdtu.edu.service.interfaces.NotificationService;


//**
// some changes: added pagination on fetch user notifications
// **//

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping()
    public ResponseEntity<?> getAllNotifications(
            @RequestParam(name = "page", required = false, defaultValue = "1") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size
    ) {
        ResDTO<?> response = notificationService.findAllUserNotifications(page, size);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/detach/{id}")
    public ResponseEntity<?> detachNotification(@PathVariable("id") String id) {
        ResDTO<?> response = notificationService.detachNotification(id);
        return ResponseEntity.status(response.getCode()).body(response);
    }


    @PostMapping("/read/{id}")
    public ResponseEntity<?> readNotification(@PathVariable("id") String id) {
        ResDTO<?> response = notificationService.readNotification(id);
        return ResponseEntity.status(response.getCode()).body(response);
    }
}
