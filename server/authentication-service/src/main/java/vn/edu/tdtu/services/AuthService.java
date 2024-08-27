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
import vn.edu.tdtu.models.User;
import vn.edu.tdtu.utils.JwtUtils;

import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final DaoAuthenticationProvider authenticationProvider;
    private final JwtUtils jwtUtils;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
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
        }catch (AuthenticationException e){
            log.error(e.getMessage());
        }

        ResDTO<?> responseDto = new ResDTO<>();
        responseDto.setCode(HttpServletResponse.SC_UNAUTHORIZED);
        responseDto.setMessage("Sai tên đăng nhập hoặc mật khẩu!");
        responseDto.setData(null);
        return responseDto;
    }

    public ResDTO<?> signUpUser(SignUpRequest request){
        ResDTO<SignUpResponse> response = new ResDTO<>();
        request.setPassword(passwordEncoder.encode(request.getPassword()));

        SignUpResponse data = userService.saveUser(request);

        response.setData(data);
        response.setMessage(data != null ? "Đăng ký tài khoản thành công" : "Email đã tồn tại!");
        response.setCode(data != null ? HttpServletResponse.SC_OK : HttpServletResponse.SC_BAD_REQUEST);

        return response;
    }
}
