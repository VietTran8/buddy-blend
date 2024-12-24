package vn.edu.tdtu.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.constant.RedisKey;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.request.*;
import vn.edu.tdtu.dto.response.LoginResponse;
import vn.edu.tdtu.dto.response.SignUpResponse;
import vn.edu.tdtu.enums.EUserRole;
import vn.edu.tdtu.exception.BadRequestException;
import vn.edu.tdtu.exception.UnauthorizedException;
import vn.edu.tdtu.message.SendOTPMailMessage;
import vn.edu.tdtu.model.AuthInfo;
import vn.edu.tdtu.model.User;
import vn.edu.tdtu.publisher.KafkaEventPublisher;
import vn.edu.tdtu.repository.AuthInfoRepository;
import vn.edu.tdtu.service.interfaces.AuthService;
import vn.edu.tdtu.service.interfaces.RedisService;
import vn.edu.tdtu.service.interfaces.UserService;
import vn.edu.tdtu.util.JwtUtils;
import vn.edu.tdtu.util.OTPUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final DaoAuthenticationProvider authenticationProvider;
    private final JwtUtils jwtUtils;
    private final UserService userService;
    private final RedisService<String> redisService;
    private final KafkaEventPublisher publisher;
    private final PasswordEncoder passwordEncoder;
    private final AuthInfoRepository authInfoRepository;
    public ResDTO<?> loginUser(LoginRequest loginRequest){
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        try{
            Authentication authentication = authenticationProvider.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            if(authentication.isAuthenticated()){
                User foundUser = userService.getUserInfo(email);

                ResDTO<LoginResponse> responseDto = new ResDTO<>();
                responseDto.setCode(HttpServletResponse.SC_OK);
                responseDto.setMessage("User login successfully");
                responseDto.setData(
                        LoginResponse.builder()
                                .id(foundUser.getId())
                                .email(foundUser.getEmail())
                                .username(foundUser.getEmail())
                                .token(jwtUtils.generateJwtToken(foundUser.getId()))
                                .tokenType("Bearer")
                                .userAvatar(foundUser.getUserAvatar())
                                .userFullName(foundUser.getUserFullName())
                                .build()
                );
                return responseDto;
            }
        } catch (AuthenticationException e) {
            log.error(e.getMessage());
        }

        throw new UnauthorizedException("Sai tên đăng nhập hoặc mật khẩu!");
    }

    public ResDTO<?> signUpUser(SignUpRequest request){
        if(authInfoRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email này đã tồn tại!");
        }

        ResDTO<SignUpResponse> response = new ResDTO<>();

        SignUpResponse data = userService.saveUser(request);

        AuthInfo authInfo = new AuthInfo();
        authInfo.setActive(true);
        authInfo.setRole(EUserRole.ROLE_USER);
        authInfo.setUserId(data.getId());
        authInfo.setEmail(request.getEmail());
        authInfo.setHashedPassword(passwordEncoder.encode(request.getPassword()));

        authInfoRepository.save(authInfo);

        response.setData(data);
        response.setMessage("Đăng ký tài khoản thành công");
        response.setCode(HttpServletResponse.SC_CREATED);

        return response;
    }

    @Override
    public ResDTO<?> createChangePasswordOTP(CreateChangePasswordRequest request) {
        AuthInfo foundAuthInfo = authInfoRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Người dùng không tồn tại"));

        if(!passwordEncoder.matches(request.getOldPassword(), foundAuthInfo.getHashedPassword())) {
            throw new BadRequestException("Mật khẩu cũ không đúng");
        }

        String otp = OTPUtils.generateOTP(6);

        publisher.publishSendOtpMailMsg(new SendOTPMailMessage(
                request.getEmail(),
                otp
        ));

        redisService.set(RedisKey.combineKey(RedisKey.OTP_KEY, request.getEmail()), otp, 300);

        return new ResDTO<>(
                HttpServletResponse.SC_CREATED,
                "Otp was created and sent to the user mail",
                null
        );
    }

    @Override
    public ResDTO<?> changePassword(ChangePasswordRequest request) {
        String otp = redisService.get(RedisKey.combineKey(RedisKey.OTP_KEY, request.getEmail()));

        if(otp == null || !otp.equals(request.getOtp()))
            throw new BadRequestException("Mã OTP không hợp lệ");

        AuthInfo foundAuthInfo = authInfoRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Không tìm thấy người dùng"));

        foundAuthInfo.setHashedPassword(
                passwordEncoder.encode(request.getNewPassword())
        );

        authInfoRepository.save(foundAuthInfo);

        redisService.delete(RedisKey.combineKey(RedisKey.OTP_KEY, request.getEmail()));

        return new ResDTO<>(
                HttpServletResponse.SC_OK,
                "User password changed successfully",
                null
        );
    }

    @Override
    public ResDTO<?> createForgotPasswordOTP(CreateForgotPasswordRequest request) {
        if(!authInfoRepository.existsByEmail(request.getEmail()))
            throw new BadRequestException("Người dùng không tồn tại");

        String otp = OTPUtils.generateOTP(6);

        publisher.publishSendOtpMailMsg(new SendOTPMailMessage(
                request.getEmail(),
                otp
        ));

        redisService.set(RedisKey.combineKey(RedisKey.OTP_KEY, request.getEmail()), otp, 300);

        return new ResDTO<>(
                HttpServletResponse.SC_CREATED,
                "Otp was created and sent to the user mail",
                null
        );
    }

    @Override
    public ResDTO<?> validateOTP(ValidateOTPRequest request) {
        if(!authInfoRepository.existsByEmail(request.getEmail()))
                throw new BadRequestException("Không tìm thấy người dùng");

        String otp = redisService.get(RedisKey.combineKey(RedisKey.OTP_KEY, request.getEmail()));

        if(otp == null || !otp.equals(request.getOtp()))
            throw new BadRequestException("Mã OTP không hợp lệ");

        redisService.extendsTtl(RedisKey.combineKey(RedisKey.OTP_KEY, request.getEmail()), 3600);

        return new ResDTO<>(
                HttpServletResponse.SC_OK,
                "Otp is correct",
                null
        );
    }
}