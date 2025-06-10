package vn.edu.tdtu.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.tdtu.constant.KeycloakUserAttribute;
import vn.edu.tdtu.constant.MessageCode;
import vn.edu.tdtu.constant.RedisKey;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.keycloak.KeycloakTokenResponse;
import vn.edu.tdtu.dto.request.*;
import vn.edu.tdtu.dto.response.AuthTokenResponse;
import vn.edu.tdtu.dto.response.LoginResponse;
import vn.edu.tdtu.dto.response.PasswordCheckingResponse;
import vn.edu.tdtu.dto.response.SignUpResponse;
import vn.edu.tdtu.enums.EUserRole;
import vn.edu.tdtu.exception.BadRequestException;
import vn.edu.tdtu.exception.UnauthorizedException;
import vn.edu.tdtu.message.SendOTPMailMessage;
import vn.edu.tdtu.model.AuthInfo;
import vn.edu.tdtu.model.data.User;
import vn.edu.tdtu.publisher.KafkaEventPublisher;
import vn.edu.tdtu.repository.AuthInfoRepository;
import vn.edu.tdtu.service.interfaces.AuthService;
import vn.edu.tdtu.service.interfaces.RedisService;
import vn.edu.tdtu.service.interfaces.UserService;
import vn.edu.tdtu.service.keycloak.interfaces.KeycloakService;
import vn.edu.tdtu.util.JwtUtils;
import vn.edu.tdtu.util.OTPUtils;

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

    @Override
    public ResDTO<LoginResponse> loginUser(LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        KeycloakTokenResponse keycloakTokenResponse = keycloakService.loginUser(email, password);

        User foundUser = userService.getUserInfo(email);

        String socketAccessToken = jwtUtils.generateJwtToken(foundUser.getId());

        ResDTO<LoginResponse> responseDto = new ResDTO<>();
        responseDto.setCode(HttpServletResponse.SC_OK);
        responseDto.setMessage(MessageCode.AUTH_LOGIN_SUCCESS);
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

        return responseDto;
    }

    @Override
    public ResDTO<LoginResponse> refreshToken(String refreshToken) {
        if (refreshToken == null)
            throw new UnauthorizedException(MessageCode.AUTH_REFRESH_TOKEN_NOT_FOUND);

        KeycloakTokenResponse keycloakTokenResponse = keycloakService.refreshToken(refreshToken);

        ResDTO<LoginResponse> responseDto = new ResDTO<>();
        responseDto.setCode(HttpServletResponse.SC_OK);
        responseDto.setMessage(MessageCode.AUTH_TOKEN_REFRESHED);
        responseDto.setData(
                LoginResponse.builder()
                        .token(AuthTokenResponse.from(keycloakTokenResponse))
                        .build()
        );

        return responseDto;
    }

    @Override
    @Transactional
    public ResDTO<SignUpResponse> createAdminUser(SignUpRequest request) {
        AuthInfo foundAuthInfo = authInfoRepository.findByEmail(request.getEmail()).orElse(null);

        if (foundAuthInfo == null)
            return signUpUser(request, List.of(EUserRole.ROLE_ADMIN, EUserRole.ROLE_USER));

        UserRepresentation keycloakUser = keycloakService.findUserByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException(MessageCode.KEYCLOAK_EXISTS_AUTH_INFO_BUT_NOT_KC_ACCOUNT));

        keycloakService.assignRealmRole(keycloakUser.getId(), Collections.singletonList(EUserRole.ROLE_ADMIN));

        foundAuthInfo.setRole(EUserRole.ROLE_ADMIN);
        authInfoRepository.save(foundAuthInfo);

        SignUpResponse responseData = new SignUpResponse();
        responseData.setEmail(request.getEmail());
        responseData.setId(foundAuthInfo.getId());

        ResDTO<SignUpResponse> response = new ResDTO<>();
        response.setData(responseData);
        response.setCode(HttpServletResponse.SC_CREATED);
        response.setMessage(MessageCode.AUTH_ADMIN_ROLE_ASSIGNED);

        return response;
    }

    @Override
    @Transactional
    public ResDTO<?> revokeAdminUser(String email) {
        AuthInfo foundAuthInfo = authInfoRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException(MessageCode.USER_NOT_FOUND_EMAIL, email));

        UserRepresentation keycloakUser = keycloakService.findUserByEmail(email)
                .orElseThrow(() -> new BadRequestException(MessageCode.KEYCLOAK_EXISTS_AUTH_INFO_BUT_NOT_KC_ACCOUNT));

        keycloakService.removeRealmRole(keycloakUser.getId(), Collections.singletonList(EUserRole.ROLE_ADMIN));

        foundAuthInfo.setRole(EUserRole.ROLE_USER);
        authInfoRepository.save(foundAuthInfo);

        SignUpResponse responseData = new SignUpResponse();
        responseData.setEmail(email);
        responseData.setId(foundAuthInfo.getId());

        ResDTO<SignUpResponse> response = new ResDTO<>();
        response.setData(responseData);
        response.setCode(HttpServletResponse.SC_OK);
        response.setMessage(MessageCode.AUTH_ADMIN_ROLE_REVOKED);

        return response;
    }

    @Override
    @Transactional
    public ResDTO<SignUpResponse> signUpUser(SignUpRequest request, List<EUserRole> userRoles) {
        if (authInfoRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException(MessageCode.AUTH_EMAIL_EXISTS);
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

        ResDTO<SignUpResponse> response = new ResDTO<>();
        response.setData(data);
        response.setMessage(MessageCode.AUTH_REGISTERED);
        response.setCode(HttpServletResponse.SC_CREATED);

        return response;
    }

    private void createKeycloakUser(SignUpRequest request, List<EUserRole> userRoles) {
        Map<String, List<String>> userAttributes = new HashMap<>();
        userAttributes.put(KeycloakUserAttribute.USER_ID, Collections.singletonList(request.getId()));

        UserRepresentation userRepresentation = getUserRepresentation(request, userAttributes);

        String createdKeycloakUserId = keycloakService.createUser(userRepresentation);

        keycloakService.resetPassword(createdKeycloakUserId, request.getPassword());
        keycloakService.assignRealmRole(createdKeycloakUserId, userRoles != null ? userRoles : Collections.singletonList(EUserRole.ROLE_USER));
    }

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
    public ResDTO<?> createChangePasswordOTP(CreateChangePasswordRequest request) {
        if (!keycloakService.passwordChecking(request.getEmail(), request.getOldPassword())) {
            throw new BadRequestException(MessageCode.AUTH_INCORRECT_OLD_PASSWORD);
        }

        String otp = OTPUtils.generateOTP(6);

        publisher.publishSendOtpMailMsg(new SendOTPMailMessage(
                request.getEmail(),
                otp
        ));

        redisService.set(RedisKey.combineKey(RedisKey.OTP_KEY, request.getEmail()), otp, 300);

        return new ResDTO<>(
                HttpServletResponse.SC_CREATED,
                MessageCode.AUTH_OTP_SENT,
                null
        );
    }

    @Override
    public ResDTO<?> changePassword(ChangePasswordRequest request) {
        String otp = redisService.get(RedisKey.combineKey(RedisKey.OTP_KEY, request.getEmail()));

        if (otp == null || !otp.equals(request.getOtp()))
            throw new BadRequestException(MessageCode.AUTH_OTP_INCORRECT);

        UserRepresentation foundUser = keycloakService.findUserByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException(MessageCode.USER_NOT_FOUND));

        keycloakService.resetPassword(foundUser.getId(), request.getNewPassword());

        redisService.delete(RedisKey.combineKey(RedisKey.OTP_KEY, request.getEmail()));

        return new ResDTO<>(
                HttpServletResponse.SC_OK,
                MessageCode.AUTH_PASSWORD_CHANGED,
                null
        );
    }

    @Override
    public ResDTO<?> createForgotPasswordOTP(CreateForgotPasswordRequest request) {
        if (!authInfoRepository.existsByEmail(request.getEmail()))
            throw new BadRequestException(MessageCode.USER_NOT_FOUND);

        String otp = OTPUtils.generateOTP(6);

        publisher.publishSendOtpMailMsg(new SendOTPMailMessage(
                request.getEmail(),
                otp
        ));

        redisService.set(RedisKey.combineKey(RedisKey.OTP_KEY, request.getEmail()), otp, 300);

        return new ResDTO<>(
                HttpServletResponse.SC_CREATED,
                MessageCode.AUTH_OTP_SENT,
                null
        );
    }

    @Override
    public ResDTO<?> validateOTP(ValidateOTPRequest request) {
        if (!authInfoRepository.existsByEmail(request.getEmail()))
            throw new BadRequestException(MessageCode.USER_NOT_FOUND);

        String otp = redisService.get(RedisKey.combineKey(RedisKey.OTP_KEY, request.getEmail()));

        if (otp == null || !otp.equals(request.getOtp()))
            throw new BadRequestException(MessageCode.AUTH_OTP_INCORRECT);

        redisService.extendsTtl(RedisKey.combineKey(RedisKey.OTP_KEY, request.getEmail()), 3600);

        return new ResDTO<>(
                HttpServletResponse.SC_OK,
                MessageCode.AUTH_OTP_CORRECT,
                null
        );
    }

    @Override
    public ResDTO<?> passwordChecking(PasswordCheckingRequest request) {
        authInfoRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException(MessageCode.USER_NOT_FOUND_EMAIL, request.getEmail()));

        boolean result = keycloakService.passwordChecking(request.getEmail(), request.getPassword());

        String token = null;

        if (result) {
            token = OTPUtils.generateOTP(20);

            redisService.set(
                    RedisKey.combineKey(RedisKey.PASSWORD_CHECKING_TOKEN_KEY, request.getEmail()),
                    token,
                    600
            );
        }

        return new ResDTO<>(
                result ? HttpServletResponse.SC_OK : HttpServletResponse.SC_BAD_REQUEST,
                result ? MessageCode.AUTH_MATCH : MessageCode.AUTH_NONE_MATCH,
                result ? new PasswordCheckingResponse(token) : null
        );
    }

    @Override
    public ResDTO<?> confirmTokenChecking(ConfirmTokenCheckingRequest request) {
        String storedToken = redisService.get(RedisKey.combineKey(
                RedisKey.PASSWORD_CHECKING_TOKEN_KEY,
                request.getEmail()
        ));

        boolean result = request.getToken() != null && request.getToken().equals(storedToken);

        return new ResDTO<>(
                result ? HttpServletResponse.SC_OK : HttpServletResponse.SC_BAD_REQUEST,
                result ? MessageCode.AUTH_MATCH : MessageCode.AUTH_NONE_MATCH,
                null
        );
    }
}