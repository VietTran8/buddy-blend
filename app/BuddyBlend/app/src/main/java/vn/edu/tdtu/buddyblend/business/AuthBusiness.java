package vn.edu.tdtu.buddyblend.business;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.edu.tdtu.buddyblend.callbacks.ActionCallback;
import vn.edu.tdtu.buddyblend.dto.ResDTO;
import vn.edu.tdtu.buddyblend.dto.request.LoginRequestDTO;
import vn.edu.tdtu.buddyblend.dto.request.SignUpRequestDTO;
import vn.edu.tdtu.buddyblend.dto.response.SignInResponseData;
import vn.edu.tdtu.buddyblend.dto.response.SignUpResponseData;
import vn.edu.tdtu.buddyblend.repository.APIClient;
import vn.edu.tdtu.buddyblend.repository.IAuthRepository;
import vn.edu.tdtu.buddyblend.utils.ErrorResponseUtils;

public class AuthBusiness {
    private final IAuthRepository authRepository;
    private static AuthBusiness instance;

    private AuthBusiness(){
        authRepository = APIClient.getClient().create(IAuthRepository.class);
    }

    public static AuthBusiness getInstance(){
        if(instance == null)
            instance = new AuthBusiness();
        return instance;
    }

    public void loginUser(String email, String password, ActionCallback<SignInResponseData> callback){
        LoginRequestDTO requestDTO = new LoginRequestDTO();
        requestDTO.setEmail(email);
        requestDTO.setPassword(password);

        Call<ResDTO<SignInResponseData>> call = authRepository.loginUser(requestDTO);

        call.enqueue(new Callback<ResDTO<SignInResponseData>>() {
            @Override
            public void onResponse(Call<ResDTO<SignInResponseData>> call, Response<ResDTO<SignInResponseData>> response) {
                if(response.isSuccessful()){
                    callback.onSuccess(response.body().getData());
                }else{
                    ErrorResponseUtils<ResDTO<SignInResponseData>, SignInResponseData> responseUtils = new ErrorResponseUtils<>();
                    responseUtils.doErrorCallback(response, callback);
                }
            }

            @Override
            public void onFailure(Call<ResDTO<SignInResponseData>> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    public void signUpUser(SignUpRequestDTO signUpRequestDTO, ActionCallback<SignUpResponseData> callback){
        Call<ResDTO<SignUpResponseData>> call = authRepository.signUpUser(signUpRequestDTO);

        call.enqueue(new Callback<ResDTO<SignUpResponseData>>() {
            @Override
            public void onResponse(Call<ResDTO<SignUpResponseData>> call, Response<ResDTO<SignUpResponseData>> response) {
                if(response.isSuccessful()){
                    callback.onSuccess(response.body().getData());
                }else{
                    ErrorResponseUtils<ResDTO<SignUpResponseData>, SignUpResponseData> errorResponseUtils = new ErrorResponseUtils<>();
                    errorResponseUtils.doErrorCallback(response, callback);
                }
            }

            @Override
            public void onFailure(Call<ResDTO<SignUpResponseData>> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }
}
