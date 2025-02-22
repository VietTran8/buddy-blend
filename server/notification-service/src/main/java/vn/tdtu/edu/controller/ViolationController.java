package vn.tdtu.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.tdtu.edu.dto.ResDTO;
import vn.tdtu.edu.model.Violation;
import vn.tdtu.edu.service.interfaces.ViolationService;

@RestController
@RequestMapping("/api/v1/violation")
@RequiredArgsConstructor
public class ViolationController {
    private final ViolationService violationService;

    @GetMapping("/ref")
    public ResponseEntity<ResDTO<Violation>> findByRefId(@RequestParam("ref") String refId){
        ResDTO<Violation> response = violationService.findByRefId(refId);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResDTO<Violation>> findById(@PathVariable("id") String id){
        ResDTO<Violation> response = violationService.findById(id);

        return ResponseEntity.status(response.getCode()).body(response);
    }
}
