package vn.edu.tdtu.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.service.interfaces.SearchService;

@RestController
@RequestMapping("/api/v1/search")
@Slf4j
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;

    @GetMapping()
    public ResponseEntity<?> search(@RequestHeader(name = "Authorization", required = false) String token,
                                    @RequestParam("key") String key){
        ResDTO<?> response = searchService.search(token, key);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/fetch")
    public ResponseEntity<?> fetch(@RequestHeader(name = "Authorization", required = false) String token,
                                    @RequestParam("key") String key){
        ResDTO<?> response = searchService.fetchResult(token, key);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/history")
    public ResponseEntity<?> getSearchHistory(@RequestHeader(name = "Authorization") String token){
        ResDTO<?> response = searchService.getSearchHistory(token);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/history/delete/{id}")
    public ResponseEntity<?> deleteSearchHistory(@PathVariable("id") String historyId) {
        ResDTO<?> response = searchService.deleteSearchHistory(historyId);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/history/clear")
    public ResponseEntity<?> clearSearchHistory() {
        ResDTO<?> response = searchService.deleteAllSearchHistory();

        return ResponseEntity.status(response.getCode()).body(response);
    }
}
