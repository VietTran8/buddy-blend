package vn.edu.tdtu.service.interfaces;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.edu.tdtu.dto.request.*;
import vn.edu.tdtu.dto.response.LoginResponse;
import vn.edu.tdtu.dto.response.SignUpResponse;
import vn.tdtu.common.enums.user.EUserRole;
import vn.tdtu.common.viewmodel.ResponseVM;

import java.util.List;

public interface AuthService {
    ResponseVM<LoginResponse> loginUser(LoginRequest loginRequest, HttpServletResponse response);

    ResponseVM<?> logoutUser(HttpServletRequest request, HttpServletResponse response);

    ResponseVM<LoginResponse> refreshToken(HttpServletRequest request, HttpServletResponse response);

    ResponseVM<SignUpResponse> createAdminUser(SignUpRequest request);

    ResponseVM<?> revokeAdminUser(String email);

    ResponseVM<SignUpResponse> signUpUser(SignUpRequest request, List<EUserRole> userRoles);

    ResponseVM<?> createChangePasswordOTP(CreateChangePasswordRequest request);

    ResponseVM<?> changePassword(ChangePasswordRequest request);

    ResponseVM<?> createForgotPasswordOTP(CreateForgotPasswordRequest request);

    ResponseVM<?> validateOTP(ValidateOTPRequest request);

    ResponseVM<?> passwordChecking(PasswordCheckingRequest request);

    ResponseVM<?> confirmTokenChecking(ConfirmTokenCheckingRequest request);
}