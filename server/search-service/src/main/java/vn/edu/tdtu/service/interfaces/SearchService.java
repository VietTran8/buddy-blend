package vn.edu.tdtu.service.interfaces;


import vn.tdtu.common.viewmodel.ResponseVM;

public interface SearchService {
    ResponseVM<?> search(String token, String key);

    ResponseVM<?> fetchResult(String token, String key);

    ResponseVM<?> getSearchHistory(String token);

    ResponseVM<?> deleteSearchHistory(String id);

    ResponseVM<?> deleteAllSearchHistory();
}
