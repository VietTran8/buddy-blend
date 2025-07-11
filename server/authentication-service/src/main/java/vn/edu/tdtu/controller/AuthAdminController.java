package vn.edu.tdtu.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.tdtu.dto.request.SignUpRequest;
import vn.edu.tdtu.service.interfaces.AuthService;
import vn.tdtu.common.viewmodel.ResponseVM;

@RestController
@RequestMapping("/api/v1/admin/auth")
@RequiredArgsConstructor
public class AuthAdminController {
    private final AuthService authService;

    @PostMapping("/create-admin")
    public ResponseEntity<?> createAdmin(@RequestBody SignUpRequest signUpRequest) {
        ResponseVM<?> response = authService.createAdminUser(signUpRequest);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/revoke-admin/{email}")
    public ResponseEntity<?> revokeAdmin(@PathVariable("email") String email) {
        ResponseVM<?> response = authService.revokeAdminUser(email);
        return ResponseEntity.status(response.getCode()).body(response);
    }
}
