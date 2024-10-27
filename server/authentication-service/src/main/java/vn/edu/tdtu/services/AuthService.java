package vn.edu.tdtu.services;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dtos.ResDTO;
import vn.edu.tdtu.dtos.request.LoginRequest;
import vn.edu.tdtu.dtos.request.SignUpRequest;
import vn.edu.tdtu.dtos.response.LoginResponse;
import vn.edu.tdtu.dtos.response.SignUpResponse;
import vn.edu.tdtu.enums.EUserRole;
import vn.edu.tdtu.exception.BadRequestException;
import vn.edu.tdtu.exception.UnauthorizedException;
import vn.edu.tdtu.models.AuthInfo;
import vn.edu.tdtu.models.User;
import vn.edu.tdtu.repository.AuthInfoRepository;
import vn.edu.tdtu.utils.JwtUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final DaoAuthenticationProvider authenticationProvider;
    private final JwtUtils jwtUtils;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthInfoRepository authInfoRepository;
    public ResDTO<?> loginUser(LoginRequest loginRequest){
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        try{
            log.info("Password: " + password);
            log.info("Username: " + email);

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
}
