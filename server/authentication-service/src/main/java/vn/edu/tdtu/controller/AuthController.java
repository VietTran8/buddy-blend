package vn.edu.tdtu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.request.*;
import vn.edu.tdtu.service.interfaces.AuthService;

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

    @PostMapping("/create-change-pass")
    public ResponseEntity<?> createChangePasswordOtp(@RequestBody CreateChangePasswordRequest changePasswordRequest){
        ResDTO<?> response = authService.createChangePasswordOTP(changePasswordRequest);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/create-forgot-pass")
    public ResponseEntity<?> createForgotPasswordOtp(@RequestBody CreateForgotPasswordRequest changePasswordRequest){
        ResDTO<?> response = authService.createForgotPasswordOTP(changePasswordRequest);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/change-pass")
    public ResponseEntity<?> changePasswordOtp(@RequestBody ChangePasswordRequest changePasswordRequest){
        ResDTO<?> response = authService.changePassword(changePasswordRequest);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/validate-otp")
    public ResponseEntity<?> validateOtp(@RequestBody ValidateOTPRequest validateOTPRequest){
        ResDTO<?> response = authService.validateOTP(validateOTPRequest);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/password-checking")
    public ResponseEntity<?> passwordChecking(@RequestBody PasswordCheckingRequest request) {
        ResDTO<?> response = authService.passwordChecking(request);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/confirm-token-checking")
    public ResponseEntity<?> confirmTokenChecking(@RequestBody ConfirmTokenCheckingRequest request){
        ResDTO<?> response = authService.confirmTokenChecking(request);
        return ResponseEntity.status(response.getCode()).body(response);
    }
}