package vn.edu.tdtu.buddyblend.utils;

import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Callback;
import retrofit2.Response;
import vn.edu.tdtu.buddyblend.callbacks.ActionCallback;
import vn.edu.tdtu.buddyblend.dto.ResDTO;

public class ErrorResponseUtils<D, T> {
    public void doErrorCallback(Response<D> response, ActionCallback<T> callback){
        String errorBody = "";
        try {
            errorBody = response.errorBody().string();
            Gson gson = new Gson();
            ResDTO<Object> errorResponse = gson.fromJson(errorBody, ResDTO.class);
            callback.onFailure(errorResponse.getMessage() != null ? errorResponse.getMessage() : "Something went wrong");
        }catch (IOException e){
            callback.onError(e);
        }
    }

    public String getErrorResponseMessage(Response<D> response){
        String errorBody = "";
        try {
            errorBody = response.errorBody().string();
            Gson gson = new Gson();
            ResDTO<Object> errorResponse = gson.fromJson(errorBody, ResDTO.class);
            return errorResponse.getMessage();
        }catch (IOException e){
            return e.getMessage();
        }
    }
}
