package vn.edu.tdtu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.tdtu.dto.request.ApprovePostRequest;
import vn.edu.tdtu.dto.request.ReportRequest;
import vn.edu.tdtu.service.intefaces.ReportService;
import vn.tdtu.common.viewmodel.ResponseVM;

@RestController
@RequestMapping("/api/v1/report")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @PostMapping()
    public ResponseEntity<?> report(@RequestBody ReportRequest requestBody) {
        ResponseVM<?> response = reportService.reportPost(requestBody);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/approve")
    public ResponseEntity<?> approvePost(@RequestBody ApprovePostRequest requestBody) {
        ResponseVM<?> response = reportService.approvePost(requestBody);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping()
    public ResponseEntity<?> reports(@RequestHeader("Authorization") String token,
                                     @RequestParam(name = "page", required = false, defaultValue = "1") int page,
                                     @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        ResponseVM<?> response = reportService.getAllReport(token, page, size);
        return ResponseEntity.status(response.getCode()).body(response);
    }
}
