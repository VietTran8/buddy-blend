package vn.edu.tdtu.buddyblend.ui.viewmodel;

import androidx.lifecycle.ViewModel;

import java.util.List;

import vn.edu.tdtu.buddyblend.business.SearchBusiness;
import vn.edu.tdtu.buddyblend.callbacks.ActionCallback;
import vn.edu.tdtu.buddyblend.dto.response.SearchHistory;
import vn.edu.tdtu.buddyblend.dto.response.SearchResponse;

public class SearchViewModel extends ViewModel {
    private SearchBusiness searchBusiness;

    public SearchViewModel () {
        searchBusiness = SearchBusiness.getInstance();
    }

    public void doSearch(String token, String key, ActionCallback<SearchResponse> actionCallback){
        searchBusiness.fetchResult(token, key, actionCallback);
    }

    public void getSearchHistory(String token, ActionCallback<List<SearchHistory>> actionCallback){
        searchBusiness.getHistory(token, actionCallback);
    }

    public void deleteSearchHistory(String token, String historyId, ActionCallback<Object> actionCallback){
        searchBusiness.deleteHistory(token, historyId, actionCallback);
    }

    public void clearSearchHistory(String token, ActionCallback<Object> actionCallback){
        searchBusiness.clearHistory(token, actionCallback);
    }
}
