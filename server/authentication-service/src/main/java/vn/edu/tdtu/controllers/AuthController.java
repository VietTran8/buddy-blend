package vn.edu.tdtu.controllers;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.tdtu.dtos.ResDTO;
import vn.edu.tdtu.dtos.request.LoginRequest;
import vn.edu.tdtu.dtos.request.SignUpRequest;
import vn.edu.tdtu.services.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest){
        ResDTO<?> response = authService.loginUser(loginRequest);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUpUser(@RequestBody SignUpRequest signUpRequest){
        ResDTO<?> response = authService.signUpUser(signUpRequest);
        return ResponseEntity.status(response.getCode()).body(response);
    }
}
