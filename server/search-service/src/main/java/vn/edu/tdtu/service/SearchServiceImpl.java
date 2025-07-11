package vn.edu.tdtu.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.common.unit.Fuzziness;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.tdtu.dto.response.SearchHistory;
import vn.edu.tdtu.dto.response.SearchResponse;
import vn.edu.tdtu.repository.SearchHistoryRepository;
import vn.edu.tdtu.service.interfaces.GroupService;
import vn.edu.tdtu.service.interfaces.PostService;
import vn.edu.tdtu.service.interfaces.SearchService;
import vn.edu.tdtu.service.interfaces.UserService;
import vn.tdtu.common.utils.MessageCode;
import vn.tdtu.common.utils.SecurityContextUtils;
import vn.tdtu.common.viewmodel.ResponseVM;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    private final UserService userService;
    private final PostService postService;
    private final GroupService groupService;
    private final SearchHistoryRepository repository;

    @Override
    @CacheEvict(cacheNames = "search-history", allEntries = true)
    @Cacheable(key = "T(java.util.Objects).hash(#p0, #p1)", value = "search-result", unless = "#result.data.users.isEmpty() and #result.data.posts.isEmpty()")
    public ResponseVM<?> search(String token, String key) {
        if (token != null && !repository.existsByQuery(key)) {
            SearchHistory searchHistory = new SearchHistory();
            searchHistory.setQuery(key);
            searchHistory.setUserId(SecurityContextUtils.getUserId());
            searchHistory.setCreatedAt(new Date());

            repository.save(searchHistory);
        }

        SearchResponse data = new SearchResponse();

        data.setUsers(userService.searchUserFullName(token, key, null));
        data.setPosts(postService.findByContentContaining(token, key, null));
        data.setGroups(groupService.findByNameContaining(token, key, null));

        ResponseVM<SearchResponse> response = new ResponseVM<>();
        response.setCode(HttpServletResponse.SC_OK);
        response.setData(data);
        response.setMessage(MessageCode.Search.SEARCH_SEARCHED);

        return response;
    }

    @Override
    @Cacheable(key = "T(java.util.Objects).hash(#p0, #p1)", value = "fetch-result", unless = "#result.data.users.isEmpty() and #result.data.posts.isEmpty()")
    public ResponseVM<?> fetchResult(String token, String key) {
        SearchResponse data = new SearchResponse();

        data.setUsers(userService.searchUserFullName(token, key, Fuzziness.ONE.asString()));
        data.setPosts(postService.findByContentContaining(token, key, Fuzziness.ONE.asString()));
        data.setGroups(groupService.findByNameContaining(token, key, Fuzziness.ONE.asString()));

        ResponseVM<SearchResponse> response = new ResponseVM<>();
        response.setCode(HttpServletResponse.SC_OK);
        response.setData(data);
        response.setMessage(MessageCode.Search.SEARCH_RESULT_FETCHED);

        return response;
    }

    @Override
    @Cacheable(key = "T(java.util.Objects).hash(#p0)", value = "search-history", unless = "#result.data.isEmpty() and #result.data.isEmpty()")
    public ResponseVM<?> getSearchHistory(String token) {
        ResponseVM<List<SearchHistory>> response = new ResponseVM<>();
        response.setMessage(MessageCode.Search.SEARCH_HISTORY_FETCHED);
        response.setData(new ArrayList<>());
        response.setCode(HttpServletResponse.SC_OK);

        List<SearchHistory> searchHistories = repository.findByUserId(SecurityContextUtils.getUserId())
                .stream().sorted(Comparator.comparingLong(s -> ((SearchHistory) s).getCreatedAt().getTime()).reversed())
                .toList();

        if (token != null) {
            response.setData(searchHistories);
        }

        return response;
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "search-history", allEntries = true)
    public ResponseVM<?> deleteSearchHistory(String id) {
        ResponseVM<Object> response = new ResponseVM<>();

        repository.deleteByUserIdAndId(SecurityContextUtils.getUserId(), id);

        response.setMessage(MessageCode.Search.SEARCH_HISTORY_DELETED);
        response.setCode(200);

        return response;
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "search-history", allEntries = true)
    public ResponseVM<?> deleteAllSearchHistory() {
        ResponseVM<Object> response = new ResponseVM<>();

        repository.deleteByUserId(SecurityContextUtils.getUserId());

        response.setMessage(MessageCode.Search.SEARCH_HISTORY_DELETED);
        response.setCode(200);

        return response;
    }
}
