package vn.edu.tdtu.service.interfaces;

import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.request.*;
import vn.edu.tdtu.dto.response.LoginResponse;

public interface AuthService {
    public ResDTO<LoginResponse> loginUser(LoginRequest loginRequest);

    public ResDTO<LoginResponse> refreshToken(String refreshToken);

    public ResDTO<?> signUpUser(SignUpRequest request);

    public ResDTO<?> createChangePasswordOTP(CreateChangePasswordRequest request);

    public ResDTO<?> changePassword(ChangePasswordRequest request);

    public ResDTO<?> createForgotPasswordOTP(CreateForgotPasswordRequest request);

    public ResDTO<?> validateOTP(ValidateOTPRequest request);

    public ResDTO<?> passwordChecking(PasswordCheckingRequest request);

    public ResDTO<?> confirmTokenChecking(ConfirmTokenCheckingRequest request);
}