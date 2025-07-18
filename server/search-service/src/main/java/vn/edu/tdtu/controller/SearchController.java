package vn.edu.tdtu.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.tdtu.service.interfaces.SearchService;
import vn.tdtu.common.utils.Constants;
import vn.tdtu.common.utils.SecurityContextUtils;
import vn.tdtu.common.viewmodel.ResponseVM;

@RestController
@RequestMapping(Constants.API_PREFIX + Constants.API_SUB_PREFIX_SEARCH)
@Slf4j
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;

    @GetMapping()
    public ResponseEntity<?> search(@RequestParam("key") String key) {
        String authUserId = SecurityContextUtils.getUserId();
        ResponseVM<?> response = searchService.search(authUserId, key);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/fetch")
    public ResponseEntity<?> fetch(@RequestParam("key") String key) {
        String authUserId = SecurityContextUtils.getUserId();
        ResponseVM<?> response = searchService.fetchResult(authUserId, key);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/history")
    public ResponseEntity<?> getSearchHistory() {
        String userId = SecurityContextUtils.getUserId();
        ResponseVM<?> response = searchService.getSearchHistory(userId);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/history/delete/{id}")
    public ResponseEntity<?> deleteSearchHistory(@PathVariable("id") String historyId) {
        ResponseVM<?> response = searchService.deleteSearchHistory(historyId);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/history/clear")
    public ResponseEntity<?> clearSearchHistory() {
        ResponseVM<?> response = searchService.deleteAllSearchHistory();

        return ResponseEntity.status(response.getCode()).body(response);
    }
}
