package vn.edu.tdtu.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.tdtu.dtos.ResDTO;
import vn.edu.tdtu.dtos.request.ReportRequest;
import vn.edu.tdtu.services.ReportService;

@RestController
@RequestMapping("/api/v1/report")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;
    @PostMapping()
    public ResponseEntity<?> report(@RequestHeader("Authorization") String token,
                                    @RequestBody ReportRequest requestBody){
        ResDTO<?> response = reportService.reportPost(token, requestBody);
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
