package vn.edu.tdtu.services;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.tdtu.dtos.ResDTO;
import vn.edu.tdtu.dtos.response.SearchResponse;
import vn.edu.tdtu.model.SearchHistory;
import vn.edu.tdtu.model.User;
import vn.edu.tdtu.repository.SearchHistoryRepository;
import vn.edu.tdtu.utils.JwtUtils;
import vn.edu.tdtu.utils.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final UserService userService;
    private final PostService postService;
    private final SearchHistoryRepository repository;
    private final JwtUtils jwtUtils;

    @CacheEvict(cacheNames = "search-history", allEntries = true)
    @Cacheable(key = "T(java.util.Objects).hash(#p1)", value = "search-result", unless = "#result.data.users.isEmpty() and #result.data.posts.isEmpty()")
    public ResDTO<?> search(String token, String key){
        if(token != null){
            SearchHistory searchHistory = new SearchHistory();
            searchHistory.setQuery(key);
            searchHistory.setUserId(jwtUtils.getUserIdFromJwtToken(token));
            searchHistory.setCreatedAt(new Date());

            repository.save(searchHistory);
        }
        key = StringUtils.toSlug(key);

        SearchResponse data = new SearchResponse();
        data.setUsers(userService.findByNameContaining(token, key));
        data.setPosts(postService.findByContentContaining(token, key));

        ResDTO<SearchResponse> response = new ResDTO<>();
        response.setCode(HttpServletResponse.SC_OK);
        response.setData(data);
        response.setMessage("success");

        return response;
    }

    @Cacheable(key = "T(java.util.Objects).hash(#p1)", value = "fetch-result", unless = "#result.data.users.isEmpty() and #result.data.posts.isEmpty()")
    public ResDTO<?> fetchResult(String token, String key){
        key = StringUtils.toSlug(key);

        SearchResponse data = new SearchResponse();
        data.setUsers(userService.findByNameContaining(token, key));
        data.setPosts(postService.findByContentContaining(token, key));

        ResDTO<SearchResponse> response = new ResDTO<>();
        response.setCode(HttpServletResponse.SC_OK);
        response.setData(data);
        response.setMessage("success");

        return response;
    }

    @Cacheable(key = "T(java.util.Objects).hash(#p0)", value = "search-history", unless = "#result.data.isEmpty() and #result.data.isEmpty()")
    public ResDTO<?> getSearchHistory(String token){
        ResDTO<List<SearchHistory>> response = new ResDTO<>();
        response.setMessage("success");
        response.setData(new ArrayList<>());
        response.setCode(HttpServletResponse.SC_OK);

        List<SearchHistory> searchHistories = repository.findByUserId(jwtUtils.getUserIdFromJwtToken(token))
                .stream().sorted(Comparator.comparingLong(s -> ((SearchHistory) s).getCreatedAt().getTime()).reversed())
                .toList();

        if(token != null){
            response.setData(searchHistories);
        }
        return response;
    }

    @Transactional
    @CacheEvict(cacheNames = "search-history", allEntries = true)
    public ResDTO<?> deleteSearchHistory(String token, String id){
        ResDTO<Object>  response = new ResDTO<>();
        repository.deleteByUserIdAndId(jwtUtils.getUserIdFromJwtToken(token), id);
        response.setMessage("success");
        response.setCode(200);
        return response;
    }

    @Transactional
    @CacheEvict(cacheNames = "search-history", allEntries = true)
    public ResDTO<?> deleteAllSearchHistory(String token){
        ResDTO<Object>  response = new ResDTO<>();
        repository.deleteByUserId(jwtUtils.getUserIdFromJwtToken(token));
        response.setMessage("success");
        response.setCode(200);
        return response;
    }
}
