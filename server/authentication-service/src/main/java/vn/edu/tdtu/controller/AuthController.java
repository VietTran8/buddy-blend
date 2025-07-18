package vn.edu.tdtu.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.tdtu.dto.request.*;
import vn.edu.tdtu.dto.response.LoginResponse;
import vn.edu.tdtu.service.interfaces.AuthService;
import vn.tdtu.common.utils.Constants;
import vn.tdtu.common.viewmodel.ResponseVM;

@RestController
@RequestMapping(Constants.API_PREFIX + Constants.API_SUB_PREFIX_AUTH)
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        ResponseVM<LoginResponse> responseBody = authService.loginUser(loginRequest, response);
        return ResponseEntity.status(responseBody.getCode()).body(responseBody);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUpUser(@RequestBody SignUpRequest signUpRequest) {
        ResponseVM<?> response = authService.signUpUser(signUpRequest, null);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(HttpServletRequest request, HttpServletResponse response) {
        authService.logoutUser(request, response);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        ResponseVM<LoginResponse> responseBody = authService.refreshToken(request, response);
        return ResponseEntity.status(responseBody.getCode()).body(responseBody);
    }

    @PostMapping("/create-change-pass")
    public ResponseEntity<?> createChangePasswordOtp(@RequestBody CreateChangePasswordRequest changePasswordRequest) {
        ResponseVM<?> response = authService.createChangePasswordOTP(changePasswordRequest);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/create-forgot-pass")
    public ResponseEntity<?> createForgotPasswordOtp(@RequestBody CreateForgotPasswordRequest changePasswordRequest) {
        ResponseVM<?> response = authService.createForgotPasswordOTP(changePasswordRequest);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/change-pass")
    public ResponseEntity<?> changePasswordOtp(@RequestBody ChangePasswordRequest changePasswordRequest) {
        ResponseVM<?> response = authService.changePassword(changePasswordRequest);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/validate-otp")
    public ResponseEntity<?> validateOtp(@RequestBody ValidateOTPRequest validateOTPRequest) {
        ResponseVM<?> response = authService.validateOTP(validateOTPRequest);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/password-checking")
    public ResponseEntity<?> passwordChecking(@RequestBody PasswordCheckingRequest request) {
        ResponseVM<?> response = authService.passwordChecking(request);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/confirm-token-checking")
    public ResponseEntity<?> confirmTokenChecking(@RequestBody ConfirmTokenCheckingRequest request) {
        ResponseVM<?> response = authService.confirmTokenChecking(request);
        return ResponseEntity.status(response.getCode()).body(response);
    }
}