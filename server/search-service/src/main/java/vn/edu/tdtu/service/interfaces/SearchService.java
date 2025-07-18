package vn.edu.tdtu.service.interfaces;


import vn.tdtu.common.viewmodel.ResponseVM;

public interface SearchService {
    ResponseVM<?> search(String authUserId, String key);

    ResponseVM<?> fetchResult(String authUserId, String key);

    ResponseVM<?> getSearchHistory(String authUserId);

    ResponseVM<?> deleteSearchHistory(String id);

    ResponseVM<?> deleteAllSearchHistory();
}
