package vn.tdtu.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.tdtu.common.utils.Constants;
import vn.tdtu.common.viewmodel.ResponseVM;
import vn.tdtu.edu.service.interfaces.NotificationService;

@RestController
@RequestMapping(Constants.API_PREFIX + Constants.API_SUB_PREFIX_NOTIFICATION)
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping()
    public ResponseEntity<?> getAllNotifications(@RequestParam(name = "page", required = false, defaultValue = "1") int page,
                                                 @RequestParam(name = "size", required = false, defaultValue = "10") int size
    ) {
        ResponseVM<?> response = notificationService.findAllUserNotifications(page, size);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/test-resource")
    public ResponseEntity<?> testGetResource() {
        ResponseVM<?> response = notificationService.testGetResource();
        return ResponseEntity.status(200).body(response);
    }

    @PostMapping("/detach/{id}")
    public ResponseEntity<?> detachNotification(@PathVariable("id") String id) {
        ResponseVM<?> response = notificationService.detachNotification(id);
        return ResponseEntity.status(response.getCode()).body(response);
    }


    @PostMapping("/read/{id}")
    public ResponseEntity<?> readNotification(@PathVariable("id") String id
    ) {
        ResponseVM<?> response = notificationService.readNotification(id);
        return ResponseEntity.status(response.getCode()).body(response);
    }
}
