package vn.edu.tdtu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.request.ReportRequest;
import vn.edu.tdtu.service.intefaces.ReportService;

@RestController
@RequestMapping("/api/v1/report")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;
    @PostMapping()
    public ResponseEntity<?> report(@RequestBody ReportRequest requestBody){
        ResDTO<?> response = reportService.reportPost(requestBody);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping()
    public ResponseEntity<?> reports(@RequestHeader("Authorization") String token,
                                     @RequestParam(name = "page", required = false, defaultValue = "1") int page,
                                     @RequestParam(name = "size", required = false, defaultValue = "10") int size){
        ResDTO<?> response = reportService.getAllReport(token, page, size);
        return ResponseEntity.status(response.getCode()).body(response);
    }
}
