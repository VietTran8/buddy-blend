package vn.edu.tdtu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.tdtu.dto.ModerateResponseDto;
import vn.edu.tdtu.message.ModerateMessage;
import vn.edu.tdtu.service.interfaces.ModerationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/moderation")
public class TestController {
    private final ModerationService moderationService;

    @PostMapping("/test")
    public ModerateResponseDto test(@RequestBody ModerateMessage message) {
        return moderationService.moderate(message);
    }
}
