package vn.edu.tdtu.service;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dto.request.FindByUserIdsReq;
import vn.edu.tdtu.exception.BadRequestException;
import vn.edu.tdtu.model.data.User;
import vn.edu.tdtu.model.es.SyncUser;
import vn.edu.tdtu.repository.EsUserRepository;
import vn.edu.tdtu.repository.httpclient.UserClient;
import vn.edu.tdtu.service.interfaces.UserService;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final EsUserRepository userRepository;
    private final UserClient userClient;
    private final ElasticsearchOperations elasticsearchOperations;

    public void saveUser(SyncUser user) {
        userRepository.save(user);
    }

    public void updateUser(SyncUser user) {
        userRepository.findById(user.getId()).ifPresentOrElse(
                foundUser -> {
                    foundUser.setEmail(user.getEmail());
                    foundUser.setFullName(user.getFullName());
                    
                    userRepository.save(foundUser);
                }, () -> {
                    throw new BadRequestException("User not found with id: " + user.getId());
                }
        );
    }

    public void deleteUser(SyncUser user) {
        userRepository.deleteById(user.getId());
    }
    
    public List<User> searchUserFullName(String accessToken, String name, String fuzziness) {
        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q
                        .match(mq -> mq
                                .field("fullName")
                                .query(name)
                                .fuzziness(fuzziness)
                        )
                )
                .withSort(SortOptions.of(so -> so
                        .score(score -> score
                                .order(SortOrder.Desc)
                        )
                ))
                .build();

        SearchHits<SyncUser> searchHits = elasticsearchOperations.search(query, SyncUser.class);

        List<String> matchedUserIds = searchHits.getSearchHits().stream()
                .map(hit -> hit.getContent().getId())
                .toList();

        return userClient
                .findByIds(accessToken, new FindByUserIdsReq(matchedUserIds))
                .getData();
    }
}
