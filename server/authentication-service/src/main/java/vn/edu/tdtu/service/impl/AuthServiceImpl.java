package vn.edu.tdtu.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.tdtu.dto.keycloak.KeycloakTokenResponse;
import vn.edu.tdtu.dto.request.*;
import vn.edu.tdtu.dto.response.AuthTokenResponse;
import vn.edu.tdtu.dto.response.LoginResponse;
import vn.edu.tdtu.dto.response.PasswordCheckingResponse;
import vn.edu.tdtu.dto.response.SignUpResponse;
import vn.edu.tdtu.message.SendOTPMailMessage;
import vn.edu.tdtu.model.AuthInfo;
import vn.edu.tdtu.model.data.User;
import vn.edu.tdtu.publisher.KafkaEventPublisher;
import vn.edu.tdtu.repository.AuthInfoRepository;
import vn.edu.tdtu.service.interfaces.AuthService;
import vn.edu.tdtu.service.interfaces.RedisService;
import vn.edu.tdtu.service.interfaces.UserService;
import vn.edu.tdtu.service.keycloak.interfaces.KeycloakService;
import vn.edu.tdtu.util.CookieUtils;
import vn.edu.tdtu.util.OTPUtils;
import vn.tdtu.common.enums.user.EUserRole;
import vn.tdtu.common.exception.BadRequestException;
import vn.tdtu.common.exception.UnauthorizedException;
import vn.tdtu.common.utils.Constants;
import vn.tdtu.common.utils.JwtUtils;
import vn.tdtu.common.utils.MessageCode;
import vn.tdtu.common.viewmodel.ResponseVM;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserService userService;
    private final RedisService<String> redisService;
    private final KafkaEventPublisher publisher;
    private final AuthInfoRepository authInfoRepository;
    private final KeycloakService keycloakService;
    private final JwtUtils jwtUtils;

    private static UserRepresentation getUserRepresentation(SignUpRequest request, Map<String, List<String>> userAttributes) {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEmail(request.getEmail());
        userRepresentation.setUsername(request.getEmail());
        userRepresentation.setFirstName(request.getFirstName());
        userRepresentation.setLastName(request.getLastName());
        userRepresentation.setEnabled(true);
        userRepresentation.setEmailVerified(true);
        userRepresentation.setAttributes(userAttributes);
        return userRepresentation;
    }

    @Override
    public ResponseVM<LoginResponse> loginUser(LoginRequest loginRequest, HttpServletResponse response) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        KeycloakTokenResponse keycloakTokenResponse = keycloakService.loginUser(email, password);

        User foundUser = userService.getUserInfo(email);

        String socketAccessToken = jwtUtils.generateJwtToken(foundUser.getId());

        ResponseVM<LoginResponse> responseDto = new ResponseVM<>();
        responseDto.setCode(HttpServletResponse.SC_OK);
        responseDto.setMessage(MessageCode.Authentication.AUTH_LOGIN_SUCCESS);
        responseDto.setData(
                LoginResponse.builder()
                        .id(foundUser.getId())
                        .email(foundUser.getEmail())
                        .username(foundUser.getEmail())
                        .token(AuthTokenResponse.from(keycloakTokenResponse, socketAccessToken))
                        .userAvatar(foundUser.getUserAvatar())
                        .userFullName(foundUser.getUserFullName())
                        .build()
        );

        String refreshToken = responseDto.getData().getToken().refreshToken();
        Long refreshExpiresIn = responseDto.getData().getToken().refreshExpiresIn();

        CookieUtils.setCookie(
                response,
                Constants.Cookie.REFRESH_TOKEN_COOKIE_NAME,
                refreshToken,
                refreshExpiresIn,
                "/"
        );

        return responseDto;
    }

    @Override
    public ResponseVM<?> logoutUser(HttpServletRequest request, HttpServletResponse response) {
        Optional<String> refreshToken = CookieUtils.getCookieValue(request, Constants.Cookie.REFRESH_TOKEN_COOKIE_NAME);

        if (refreshToken.isEmpty())
            throw new UnauthorizedException(MessageCode.Authentication.AUTH_REFRESH_TOKEN_NOT_FOUND);

        boolean isLoggedOut = keycloakService.logoutUser(refreshToken.get());

        if(isLoggedOut)
            CookieUtils.deleteCookie(
                    response,
                    Constants.Cookie.REFRESH_TOKEN_COOKIE_NAME,
                    "/"
            );

        return ResponseVM.noContent();
    }

    @Override
    public ResponseVM<LoginResponse> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        Optional<String> refreshToken = CookieUtils.getCookieValue(request, Constants.Cookie.REFRESH_TOKEN_COOKIE_NAME);

        if (refreshToken.isEmpty())
            throw new UnauthorizedException(MessageCode.Authentication.AUTH_REFRESH_TOKEN_NOT_FOUND);

        KeycloakTokenResponse keycloakTokenResponse = keycloakService.refreshToken(refreshToken.get());

        ResponseVM<LoginResponse> responseDto = new ResponseVM<>();
        responseDto.setCode(HttpServletResponse.SC_OK);
        responseDto.setMessage(MessageCode.Authentication.AUTH_TOKEN_REFRESHED);
        responseDto.setData(
                LoginResponse.builder()
                        .token(AuthTokenResponse.from(keycloakTokenResponse))
                        .build()
        );

        CookieUtils.setCookie(
                response,
                Constants.Cookie.REFRESH_TOKEN_COOKIE_NAME,
                responseDto.getData().getToken().refreshToken(),
                responseDto.getData().getToken().refreshExpiresIn(),
                "/"
        );

        return responseDto;
    }

    @Override
    @Transactional
    public ResponseVM<SignUpResponse> createAdminUser(SignUpRequest request) {
        AuthInfo foundAuthInfo = authInfoRepository.findByEmail(request.getEmail()).orElse(null);

        if (foundAuthInfo == null)
            return signUpUser(request, List.of(EUserRole.ROLE_ADMIN, EUserRole.ROLE_USER));

        UserRepresentation keycloakUser = keycloakService.findUserByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException(MessageCode.Authentication.KEYCLOAK_EXISTS_AUTH_INFO_BUT_NOT_KC_ACCOUNT));

        keycloakService.assignRealmRole(keycloakUser.getId(), Collections.singletonList(EUserRole.ROLE_ADMIN));

        foundAuthInfo.setRole(EUserRole.ROLE_ADMIN);
        authInfoRepository.save(foundAuthInfo);

        SignUpResponse responseData = new SignUpResponse();
        responseData.setEmail(request.getEmail());
        responseData.setId(foundAuthInfo.getId());

        ResponseVM<SignUpResponse> response = new ResponseVM<>();
        response.setData(responseData);
        response.setCode(HttpServletResponse.SC_CREATED);
        response.setMessage(MessageCode.Authentication.AUTH_ADMIN_ROLE_ASSIGNED);

        return response;
    }

    @Override
    @Transactional
    public ResponseVM<?> revokeAdminUser(String email) {
        AuthInfo foundAuthInfo = authInfoRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException(MessageCode.User.USER_NOT_FOUND_EMAIL, email));

        UserRepresentation keycloakUser = keycloakService.findUserByEmail(email)
                .orElseThrow(() -> new BadRequestException(MessageCode.Authentication.KEYCLOAK_EXISTS_AUTH_INFO_BUT_NOT_KC_ACCOUNT));

        keycloakService.removeRealmRole(keycloakUser.getId(), Collections.singletonList(EUserRole.ROLE_ADMIN));

        foundAuthInfo.setRole(EUserRole.ROLE_USER);
        authInfoRepository.save(foundAuthInfo);

        SignUpResponse responseData = new SignUpResponse();
        responseData.setEmail(email);
        responseData.setId(foundAuthInfo.getId());

        ResponseVM<SignUpResponse> response = new ResponseVM<>();
        response.setData(responseData);
        response.setCode(HttpServletResponse.SC_OK);
        response.setMessage(MessageCode.Authentication.AUTH_ADMIN_ROLE_REVOKED);

        return response;
    }

    @Override
    @Transactional
    public ResponseVM<SignUpResponse> signUpUser(SignUpRequest request, List<EUserRole> userRoles) {
        if (authInfoRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException(MessageCode.Authentication.AUTH_EMAIL_EXISTS);
        }

        String userId = UUID.randomUUID().toString();
        request.setId(userId);

        createKeycloakUser(request, userRoles);

        SignUpResponse data = userService.saveUser(request);

        AuthInfo authInfo = new AuthInfo();
        authInfo.setActive(true);
        authInfo.setRole(userRoles != null ?
                userRoles.stream().findFirst().orElse(EUserRole.ROLE_USER) :
                EUserRole.ROLE_USER);
        authInfo.setUserId(data.getId());
        authInfo.setEmail(request.getEmail());

        authInfoRepository.save(authInfo);

        ResponseVM<SignUpResponse> response = new ResponseVM<>();
        response.setData(data);
        response.setMessage(MessageCode.Authentication.AUTH_REGISTERED);
        response.setCode(HttpServletResponse.SC_CREATED);

        return response;
    }

    private void createKeycloakUser(SignUpRequest request, List<EUserRole> userRoles) {
        Map<String, List<String>> userAttributes = new HashMap<>();
        userAttributes.put(Constants.KeyCloakUserAttribute.USER_ID, Collections.singletonList(request.getId()));

        UserRepresentation userRepresentation = getUserRepresentation(request, userAttributes);

        String createdKeycloakUserId = keycloakService.createUser(userRepresentation);

        keycloakService.resetPassword(createdKeycloakUserId, request.getPassword());
        keycloakService.assignRealmRole(createdKeycloakUserId, userRoles != null ? userRoles : Collections.singletonList(EUserRole.ROLE_USER));
    }

    @Override
    public ResponseVM<?> createChangePasswordOTP(CreateChangePasswordRequest request) {
        if (!keycloakService.passwordChecking(request.getEmail(), request.getOldPassword())) {
            throw new BadRequestException(MessageCode.Authentication.AUTH_INCORRECT_OLD_PASSWORD);
        }

        String otp = OTPUtils.generateOTP(6);

        publisher.publishSendOtpMailMsg(new SendOTPMailMessage(
                request.getEmail(),
                otp
        ));

        redisService.set(Constants.RedisKey.combineKey(Constants.RedisKey.OTP_KEY, request.getEmail()), otp, 300);

        return new ResponseVM<>(
                MessageCode.Authentication.AUTH_OTP_SENT,
                null,
                HttpServletResponse.SC_CREATED
        );
    }

    @Override
    public ResponseVM<?> changePassword(ChangePasswordRequest request) {
        String otp = redisService.get(Constants.RedisKey.combineKey(Constants.RedisKey.OTP_KEY, request.getEmail()));

        if (otp == null || !otp.equals(request.getOtp()))
            throw new BadRequestException(MessageCode.Authentication.AUTH_OTP_INCORRECT);

        UserRepresentation foundUser = keycloakService.findUserByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException(MessageCode.User.USER_NOT_FOUND));

        keycloakService.resetPassword(foundUser.getId(), request.getNewPassword());

        redisService.delete(Constants.RedisKey.combineKey(Constants.RedisKey.OTP_KEY, request.getEmail()));

        return new ResponseVM<>(
                MessageCode.Authentication.AUTH_PASSWORD_CHANGED,
                null,
                HttpServletResponse.SC_OK
        );
    }

    @Override
    public ResponseVM<?> createForgotPasswordOTP(CreateForgotPasswordRequest request) {
        if (!authInfoRepository.existsByEmail(request.getEmail()))
            throw new BadRequestException(MessageCode.User.USER_NOT_FOUND);

        String otp = OTPUtils.generateOTP(6);

        publisher.publishSendOtpMailMsg(new SendOTPMailMessage(
                request.getEmail(),
                otp
        ));

        redisService.set(Constants.RedisKey.combineKey(Constants.RedisKey.OTP_KEY, request.getEmail()), otp, 300);

        return new ResponseVM<>(
                MessageCode.Authentication.AUTH_OTP_SENT,
                null,
                HttpServletResponse.SC_CREATED
        );
    }

    @Override
    public ResponseVM<?> validateOTP(ValidateOTPRequest request) {
        if (!authInfoRepository.existsByEmail(request.getEmail()))
            throw new BadRequestException(MessageCode.User.USER_NOT_FOUND);

        String otp = redisService.get(Constants.RedisKey.combineKey(Constants.RedisKey.OTP_KEY, request.getEmail()));

        if (otp == null || !otp.equals(request.getOtp()))
            throw new BadRequestException(MessageCode.Authentication.AUTH_OTP_INCORRECT);

        redisService.extendsTtl(Constants.RedisKey.combineKey(Constants.RedisKey.OTP_KEY, request.getEmail()), 3600);

        return new ResponseVM<>(
                MessageCode.Authentication.AUTH_OTP_CORRECT,
                null,
                HttpServletResponse.SC_OK
        );
    }

    @Override
    public ResponseVM<?> passwordChecking(PasswordCheckingRequest request) {
        authInfoRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException(MessageCode.User.USER_NOT_FOUND_EMAIL, request.getEmail()));

        boolean result = keycloakService.passwordChecking(request.getEmail(), request.getPassword());

        String token = null;

        if (result) {
            token = OTPUtils.generateOTP(20);

            redisService.set(
                    Constants.RedisKey.combineKey(Constants.RedisKey.PASSWORD_CHECKING_TOKEN_KEY, request.getEmail()),
                    token,
                    600
            );
        }

        return new ResponseVM<>(
                result ? MessageCode.Authentication.AUTH_MATCH : MessageCode.Authentication.AUTH_NONE_MATCH,
                result ? new PasswordCheckingResponse(token) : null,
                result ? HttpServletResponse.SC_OK : HttpServletResponse.SC_BAD_REQUEST
        );
    }

    @Override
    public ResponseVM<?> confirmTokenChecking(ConfirmTokenCheckingRequest request) {
        String storedToken = redisService.get(Constants.RedisKey.combineKey(
                Constants.RedisKey.PASSWORD_CHECKING_TOKEN_KEY,
                request.getEmail()
        ));

        boolean result = request.getToken() != null && request.getToken().equals(storedToken);

        return new ResponseVM<>(
                result ? MessageCode.Authentication.AUTH_MATCH : MessageCode.Authentication.AUTH_NONE_MATCH,
                null,
                result ? HttpServletResponse.SC_OK : HttpServletResponse.SC_BAD_REQUEST
        );
    }
}