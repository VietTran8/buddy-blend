package vn.edu.tdtu.service.interfaces;

import vn.edu.tdtu.dto.ResDTO;

public interface SearchService {
    public ResDTO<?> search(String token, String key);
    public ResDTO<?> fetchResult(String token, String key);
    public ResDTO<?> getSearchHistory(String token);
    public ResDTO<?> deleteSearchHistory(String id);
    public ResDTO<?> deleteAllSearchHistory();
}
