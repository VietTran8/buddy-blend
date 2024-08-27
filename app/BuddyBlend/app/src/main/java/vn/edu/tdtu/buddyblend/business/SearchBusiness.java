package vn.edu.tdtu.buddyblend.business;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.edu.tdtu.buddyblend.callbacks.ActionCallback;
import vn.edu.tdtu.buddyblend.dto.ResDTO;
import vn.edu.tdtu.buddyblend.dto.response.SearchHistory;
import vn.edu.tdtu.buddyblend.dto.response.SearchResponse;
import vn.edu.tdtu.buddyblend.repository.APIClient;
import vn.edu.tdtu.buddyblend.repository.ISearchRepository;
import vn.edu.tdtu.buddyblend.utils.ErrorResponseUtils;

public class SearchBusiness {
    private final ISearchRepository searchRepository;
    private final String TAG = "SearchBusiness";
    private static SearchBusiness instance;
    private SearchBusiness() {
        searchRepository = APIClient.getClient().create(ISearchRepository.class);
    }

    public static SearchBusiness getInstance(){
        if(instance == null)
            instance = new SearchBusiness();
        return instance;
    }

    public void fetchResult(String token, String key, ActionCallback<SearchResponse> actionCallback){
        Call<ResDTO<SearchResponse>> call = searchRepository.fetchResult(token, key);

        call.enqueue(new Callback<ResDTO<SearchResponse>>() {
            @Override
            public void onResponse(Call<ResDTO<SearchResponse>> call, Response<ResDTO<SearchResponse>> response) {
                if(response.isSuccessful()) {
                    actionCallback.onSuccess(response.body().getData());
                }else{
                    ErrorResponseUtils<ResDTO<SearchResponse>, SearchResponse> errorResponseUtils = new ErrorResponseUtils<>();
                    actionCallback.onFailure(errorResponseUtils.getErrorResponseMessage(response));
                }
            }

            @Override
            public void onFailure(Call<ResDTO<SearchResponse>> call, Throwable t) {
                actionCallback.onFailure(t.getMessage());
            }
        });
    }

    public void doSearch(String token, String key, ActionCallback<SearchResponse> actionCallback){
        Call<ResDTO<SearchResponse>> call = searchRepository.doSearch(token, key);

        call.enqueue(new Callback<ResDTO<SearchResponse>>() {
            @Override
            public void onResponse(Call<ResDTO<SearchResponse>> call, Response<ResDTO<SearchResponse>> response) {
                if(response.isSuccessful()) {
                    actionCallback.onSuccess(response.body().getData());
                }else{
                    ErrorResponseUtils<ResDTO<SearchResponse>, SearchResponse> errorResponseUtils = new ErrorResponseUtils<>();
                    actionCallback.onFailure(errorResponseUtils.getErrorResponseMessage(response));
                }
            }

            @Override
            public void onFailure(Call<ResDTO<SearchResponse>> call, Throwable t) {
                actionCallback.onFailure(t.getMessage());
            }
        });
    }

    public void getHistory(String token, ActionCallback<List<SearchHistory>> actionCallback){
        Call<ResDTO<List<SearchHistory>>> call = searchRepository.getSearchHistory(token);

        call.enqueue(new Callback<ResDTO<List<SearchHistory>>>() {
            @Override
            public void onResponse(Call<ResDTO<List<SearchHistory>>> call, Response<ResDTO<List<SearchHistory>>> response) {
                if(response.isSuccessful()) {
                    actionCallback.onSuccess(response.body().getData());
                }else{
                    ErrorResponseUtils<ResDTO<List<SearchHistory>>, List<SearchHistory>> errorResponseUtils = new ErrorResponseUtils<>();
                    actionCallback.onFailure(errorResponseUtils.getErrorResponseMessage(response));
                }
            }

            @Override
            public void onFailure(Call<ResDTO<List<SearchHistory>>> call, Throwable t) {
                actionCallback.onFailure(t.getMessage());
            }
        });
    }

    public void deleteHistory(String token, String historyId, ActionCallback<Object> actionCallback){
        Call<ResDTO<Object>> call = searchRepository.deleteSearchHistory(token, historyId);

        call.enqueue(new Callback<ResDTO<Object>>() {
            @Override
            public void onResponse(Call<ResDTO<Object>> call, Response<ResDTO<Object>> response) {
                if(response.isSuccessful()) {
                    actionCallback.onSuccess();
                }else{
                    ErrorResponseUtils<ResDTO<Object>, List<Object>> errorResponseUtils = new ErrorResponseUtils<>();
                    actionCallback.onFailure(errorResponseUtils.getErrorResponseMessage(response));
                }
            }

            @Override
            public void onFailure(Call<ResDTO<Object>> call, Throwable t) {
                actionCallback.onFailure(t.getMessage());
            }
        });
    }

    public void clearHistory(String token, ActionCallback<Object> actionCallback){
        Call<ResDTO<Object>> call = searchRepository.clearSearchHistory(token);

        call.enqueue(new Callback<ResDTO<Object>>() {
            @Override
            public void onResponse(Call<ResDTO<Object>> call, Response<ResDTO<Object>> response) {
                if(response.isSuccessful()) {
                    actionCallback.onSuccess();
                }else{
                    ErrorResponseUtils<ResDTO<Object>, List<Object>> errorResponseUtils = new ErrorResponseUtils<>();
                    actionCallback.onFailure(errorResponseUtils.getErrorResponseMessage(response));
                }
            }

            @Override
            public void onFailure(Call<ResDTO<Object>> call, Throwable t) {
                actionCallback.onFailure(t.getMessage());
            }
        });
    }
}
