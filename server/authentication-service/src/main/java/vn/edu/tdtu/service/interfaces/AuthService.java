package vn.edu.tdtu.service.interfaces;

import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.request.*;
import vn.edu.tdtu.dto.response.LoginResponse;
import vn.edu.tdtu.dto.response.SignUpResponse;
import vn.edu.tdtu.enums.EUserRole;

import java.util.List;

public interface AuthService {
    public ResDTO<LoginResponse> loginUser(LoginRequest loginRequest);

    public ResDTO<LoginResponse> refreshToken(String refreshToken);

    public ResDTO<SignUpResponse> createAdminUser(SignUpRequest request);

    public ResDTO<?> revokeAdminUser(String email);

    public ResDTO<SignUpResponse> signUpUser(SignUpRequest request, List<EUserRole> userRoles);

    public ResDTO<?> createChangePasswordOTP(CreateChangePasswordRequest request);

    public ResDTO<?> changePassword(ChangePasswordRequest request);

    public ResDTO<?> createForgotPasswordOTP(CreateForgotPasswordRequest request);

    public ResDTO<?> validateOTP(ValidateOTPRequest request);

    public ResDTO<?> passwordChecking(PasswordCheckingRequest request);

    public ResDTO<?> confirmTokenChecking(ConfirmTokenCheckingRequest request);
}