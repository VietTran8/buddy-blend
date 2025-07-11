package vn.edu.tdtu.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.tdtu.service.interfaces.SearchService;
import vn.tdtu.common.viewmodel.ResponseVM;

@RestController
@RequestMapping("/api/v1/search")
@Slf4j
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;

    @GetMapping()
    public ResponseEntity<?> search(@RequestHeader(name = "Authorization", required = false) String token,
                                    @RequestParam("key") String key) {
        ResponseVM<?> response = searchService.search(token, key);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/fetch")
    public ResponseEntity<?> fetch(@RequestHeader(name = "Authorization", required = false) String token,
                                   @RequestParam("key") String key) {
        ResponseVM<?> response = searchService.fetchResult(token, key);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/history")
    public ResponseEntity<?> getSearchHistory(@RequestHeader(name = "Authorization") String token) {
        ResponseVM<?> response = searchService.getSearchHistory(token);

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
