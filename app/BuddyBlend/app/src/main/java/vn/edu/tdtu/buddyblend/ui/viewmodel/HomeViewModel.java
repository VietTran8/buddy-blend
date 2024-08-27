package vn.edu.tdtu.buddyblend.ui.viewmodel;

import android.content.SharedPreferences;
import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.time.LocalDateTime;
import java.util.List;

import vn.edu.tdtu.buddyblend.business.InteractionBusiness;
import vn.edu.tdtu.buddyblend.business.PostBusiness;
import vn.edu.tdtu.buddyblend.business.UserBusiness;
import vn.edu.tdtu.buddyblend.callbacks.ActionCallback;
import vn.edu.tdtu.buddyblend.dto.request.DoReactReqDTO;
import vn.edu.tdtu.buddyblend.dto.request.FetchNewsFeedReqDTO;
import vn.edu.tdtu.buddyblend.dto.request.SharePostReqDTO;
import vn.edu.tdtu.buddyblend.enums.EPrivacy;
import vn.edu.tdtu.buddyblend.enums.EReactionType;
import vn.edu.tdtu.buddyblend.models.PostResponse;
import vn.edu.tdtu.buddyblend.models.MinimizedUser;
import vn.edu.tdtu.buddyblend.models.TopReacts;
import vn.edu.tdtu.buddyblend.models.User;
import vn.edu.tdtu.buddyblend.utils.DateUtils;
import vn.edu.tdtu.buddyblend.utils.SharedPreferenceKeys;

public class HomeViewModel extends ViewModel {
    private PostBusiness postBusiness;
    private UserBusiness userBusiness;
    private InteractionBusiness interactionBusiness;
    private int totalPages;
    private boolean isLoading;
    private int currentPage;
    private MutableLiveData<List<PostResponse>> posts;
    private SharedPreferences sharedPreferences;
    private LiveData<User> currentUser;
    private OnCurrentUserFetchedFail onCurrentUserFetched;
    private LocalDateTime anchorTime;
    private EPrivacy currentPrivacy;
    private View btnPrivacy;

    public HomeViewModel(SharedPreferences sharedPreferences, OnCurrentUserFetchedFail onCurrentUserFetchedFail) {
        totalPages = 1;
        currentPage = 1;
        anchorTime = LocalDateTime.now();
        postBusiness = PostBusiness.getInstance();
        userBusiness = UserBusiness.getInstance();
        interactionBusiness = InteractionBusiness.getInstance();
        this.sharedPreferences = sharedPreferences;
        currentPrivacy = EPrivacy.PUBLIC;

        posts = fetchNewsFeed();
    }
    public boolean isLoading() {
        return isLoading;
    }
    public void setLoading(boolean loading) {
        isLoading = loading;
    }
    public interface OnCurrentUserFetchedFail{
        void onFailure(int statusCode, String message);
    }

    public View getBtnPrivacy() {
        return btnPrivacy;
    }

    public void setBtnPrivacy(View btnPrivacy) {
        this.btnPrivacy = btnPrivacy;
    }

    public EPrivacy getCurrentPrivacy() {
        return currentPrivacy;
    }

    public void setCurrentPrivacy(EPrivacy currentPrivacy) {
        this.currentPrivacy = currentPrivacy;
    }

    public void sharePost(String token, SharePostReqDTO reqDTO, ActionCallback<PostResponse> callback){
        postBusiness.sharePost(token, reqDTO, callback);
    }

    public LiveData<List<PostResponse>> getPosts() {
        return posts;
    }
    public int getCurrentPage() {
        return currentPage;
    }
    public int getTotalPages() {
        return totalPages;
    }
    public void refresh(SwipeRefreshLayout layout){
        anchorTime = LocalDateTime.now();
        fetchNewsFeed().observeForever(p -> {
            posts.setValue(p);
            if(layout != null) layout.setRefreshing(false);
        });
    }
    public void refresh(LocalDateTime dateTime){
        fetchNewsFeed(dateTime).observeForever(p -> {
            posts.setValue(p);
        });
    }
    private MutableLiveData<List<PostResponse>>  fetchNewsFeed(){
        currentPage = 1;
        FetchNewsFeedReqDTO reqDTO = new FetchNewsFeedReqDTO();
        reqDTO.setPage(currentPage);
        reqDTO.setSize(10);
        reqDTO.setStartTime(DateUtils.formatLocalDateTime(anchorTime));

        MutableLiveData<List<PostResponse>> posts = new MutableLiveData<>();

        postBusiness.fetchNewsFeed(sharedPreferences.getString(SharedPreferenceKeys.USER_ACCESS_TOKEN, ""), reqDTO).observeForever(response -> {
            posts.setValue(response.getPosts());
            totalPages = response.getTotalPages();
        });

        return posts;
    }
    private MutableLiveData<List<PostResponse>> fetchNewsFeed(LocalDateTime dateTime){
        currentPage = 1;
        FetchNewsFeedReqDTO reqDTO = new FetchNewsFeedReqDTO();
        reqDTO.setPage(currentPage);
        reqDTO.setSize(20);
        reqDTO.setStartTime(DateUtils.formatLocalDateTime(dateTime));

        MutableLiveData<List<PostResponse>> posts = new MutableLiveData<>();

        postBusiness.fetchNewsFeed(sharedPreferences.getString(SharedPreferenceKeys.USER_ACCESS_TOKEN, ""), reqDTO).observeForever(response -> {
            posts.setValue(response.getPosts());
            totalPages = response.getTotalPages();
        });

        return posts;
    }
    public LiveData<User> getCurrentUser(){
        return currentUser;
    }
    public void fetchUser(OnCurrentUserFetchedFail currentUserFetchedFail){
        this.onCurrentUserFetched = currentUserFetchedFail;

        currentUser = userBusiness.getUser(sharedPreferences.getString(SharedPreferenceKeys.USER_ACCESS_TOKEN, ""), new ActionCallback<MinimizedUser>() {
            @Override
            public void onFailure(Integer status, String message) {
                onCurrentUserFetched.onFailure(status, message);
            }
        });
    }
    public void getNextPage(OnPostsFetched onPostsFetched){
        isLoading = true;
        FetchNewsFeedReqDTO reqDTO = new FetchNewsFeedReqDTO();
        reqDTO.setPage(currentPage + 1);
        reqDTO.setSize(10);
        reqDTO.setStartTime(DateUtils.formatLocalDateTime(anchorTime));

        postBusiness.fetchNewsFeed(sharedPreferences.getString(SharedPreferenceKeys.USER_ACCESS_TOKEN, ""), reqDTO).observeForever(response -> {
            totalPages = response.getTotalPages();
            currentPage++;
            onPostsFetched.onFetched(response.getPosts(), currentPage, totalPages);
        });
    }
    public interface OnPostsFetched{
        void onFetched(List<PostResponse> posts, int currentPage, int totalPages);
    }
    public void doReact(String postId, EReactionType reactionType, ActionCallback<List<TopReacts>> actionCallback){
        DoReactReqDTO requestBody = new DoReactReqDTO(postId, reactionType);

        interactionBusiness.doReact(sharedPreferences.getString(SharedPreferenceKeys.USER_ACCESS_TOKEN, ""),
                requestBody,
                actionCallback
        );
    }

    public void deletePost(String token, String postId, ActionCallback<Object> actionCallback){
        postBusiness.deletePost(token, postId, actionCallback);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
