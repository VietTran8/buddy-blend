package vn.edu.tdtu.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.tdtu.constant.CommonConstant;
import vn.edu.tdtu.constant.MessageCode;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.request.*;
import vn.edu.tdtu.dto.response.LoginResponse;
import vn.edu.tdtu.exception.UnauthorizedException;
import vn.edu.tdtu.service.interfaces.AuthService;
import vn.edu.tdtu.util.CookieUtils;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        ResDTO<LoginResponse> responseBody = authService.loginUser(loginRequest);

        String refreshToken = responseBody.getData().getToken().refreshToken();
        Long refreshExpiresIn = responseBody.getData().getToken().refreshExpiresIn();

        CookieUtils.setCookie(
                response,
                CommonConstant.REFRESH_TOKEN_COOKIE_NAME,
                refreshToken,
                refreshExpiresIn,
                "/"
        );

        return ResponseEntity.status(responseBody.getCode()).body(responseBody);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUpUser(@RequestBody SignUpRequest signUpRequest) {
        ResDTO<?> response = authService.signUpUser(signUpRequest);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(HttpServletResponse response) {
        CookieUtils.setCookie(
                response,
                CommonConstant.REFRESH_TOKEN_COOKIE_NAME,
                "",
                0L,
                "/"
        );

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        jakarta.servlet.http.Cookie[] cookies = request.getCookies();

        if (cookies == null)
            throw new UnauthorizedException(MessageCode.AUTH_REFRESH_TOKEN_NOT_FOUND);

        String refreshToken = null;

        for (jakarta.servlet.http.Cookie cookie : cookies) {
            if (CommonConstant.REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                refreshToken = cookie.getValue();
                break;
            }
        }

        ResDTO<LoginResponse> responseBody = authService.refreshToken(refreshToken);

        CookieUtils.setCookie(
                response,
                CommonConstant.REFRESH_TOKEN_COOKIE_NAME,
                responseBody.getData().getToken().refreshToken(),
                responseBody.getData().getToken().refreshExpiresIn(),
                "/"
        );

        return ResponseEntity.status(responseBody.getCode()).body(responseBody);
    }

    @PostMapping("/create-change-pass")
    public ResponseEntity<?> createChangePasswordOtp(@RequestBody CreateChangePasswordRequest changePasswordRequest) {
        ResDTO<?> response = authService.createChangePasswordOTP(changePasswordRequest);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/create-forgot-pass")
    public ResponseEntity<?> createForgotPasswordOtp(@RequestBody CreateForgotPasswordRequest changePasswordRequest) {
        ResDTO<?> response = authService.createForgotPasswordOTP(changePasswordRequest);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/authenticated")
    public String authenticatedEndPoint() {
        return "authenticated";
    }

    @PostMapping("/change-pass")
    public ResponseEntity<?> changePasswordOtp(@RequestBody ChangePasswordRequest changePasswordRequest) {
        ResDTO<?> response = authService.changePassword(changePasswordRequest);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/validate-otp")
    public ResponseEntity<?> validateOtp(@RequestBody ValidateOTPRequest validateOTPRequest) {
        ResDTO<?> response = authService.validateOTP(validateOTPRequest);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/password-checking")
    public ResponseEntity<?> passwordChecking(@RequestBody PasswordCheckingRequest request) {
        ResDTO<?> response = authService.passwordChecking(request);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/confirm-token-checking")
    public ResponseEntity<?> confirmTokenChecking(@RequestBody ConfirmTokenCheckingRequest request) {
        ResDTO<?> response = authService.confirmTokenChecking(request);
        return ResponseEntity.status(response.getCode()).body(response);
    }
}