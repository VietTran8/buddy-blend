package vn.edu.tdtu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.request.CreateBannedWordReq;
import vn.edu.tdtu.service.intefaces.BannedWordService;

@RestController
@RequestMapping("/api/v1/banned-word")
@RequiredArgsConstructor
public class BannedWordController {
    private final BannedWordService bannedWordService;

    @PostMapping
    public ResponseEntity<?> createBannedWord(@RequestBody CreateBannedWordReq requestBody){
        ResDTO<?> response = bannedWordService.saveBannedWord(requestBody);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/remove")
    public ResponseEntity<?> removeBannedWord(@RequestBody CreateBannedWordReq requestBody){
        ResDTO<?> response = bannedWordService.removeBannedWord(requestBody);

        return ResponseEntity.status(response.getCode()).body(response);
    }
}
