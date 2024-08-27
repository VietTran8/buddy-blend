package vn.edu.tdtu.buddyblend.repository;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import vn.edu.tdtu.buddyblend.dto.ResDTO;
import vn.edu.tdtu.buddyblend.dto.request.LoginRequestDTO;
import vn.edu.tdtu.buddyblend.dto.request.SignUpRequestDTO;
import vn.edu.tdtu.buddyblend.dto.response.SignInResponseData;
import vn.edu.tdtu.buddyblend.dto.response.SignUpResponseData;

public interface IAuthRepository {
    @POST("auth/login")
    Call<ResDTO<SignInResponseData>> loginUser(@Body LoginRequestDTO payload);
    @POST("auth/sign-up")
    Call<ResDTO<SignUpResponseData>> signUpUser(@Body SignUpRequestDTO payload);
}
