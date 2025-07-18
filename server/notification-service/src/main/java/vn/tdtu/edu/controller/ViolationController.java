package vn.tdtu.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.tdtu.common.utils.Constants;
import vn.tdtu.common.viewmodel.ResponseVM;
import vn.tdtu.edu.model.Violation;
import vn.tdtu.edu.service.interfaces.ViolationService;

@RestController
@RequestMapping(Constants.API_PREFIX + Constants.API_SUB_PREFIX_NOTIFICATION + "/violation")
@RequiredArgsConstructor
public class ViolationController {
    private final ViolationService violationService;

    @GetMapping("/ref")
    public ResponseEntity<ResponseVM<Violation>> findByRefId(@RequestParam("ref") String refId) {
        ResponseVM<Violation> response = violationService.findByRefId(refId);

        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseVM<Violation>> findById(@PathVariable("id") String id) {
        ResponseVM<Violation> response = violationService.findById(id);

        return ResponseEntity.status(response.getCode()).body(response);
    }
}
