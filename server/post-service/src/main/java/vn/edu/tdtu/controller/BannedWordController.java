package vn.edu.tdtu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.tdtu.dto.request.CreateBannedWordReq;
import vn.edu.tdtu.service.intefaces.BannedWordService;
import vn.tdtu.common.utils.Constants;
import vn.tdtu.common.viewmodel.ResponseVM;

@RestController
@RequestMapping(Constants.API_PREFIX + Constants.API_SUB_PREFIX_POST + "/banned-word")
@RequiredArgsConstructor
public class BannedWordController {
    private final BannedWordService bannedWordService;

    @PostMapping
    public ResponseEntity<?> createBannedWord(@RequestBody CreateBannedWordReq requestBody) {
        ResponseVM<?> response = bannedWordService.saveBannedWord(requestBody);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/remove")
    public ResponseEntity<?> removeBannedWord(@RequestBody CreateBannedWordReq requestBody) {
        ResponseVM<?> response = bannedWordService.removeBannedWord(requestBody);

        return ResponseEntity.status(response.getCode()).body(response);
    }
}
