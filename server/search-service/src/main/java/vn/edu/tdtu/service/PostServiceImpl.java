package vn.edu.tdtu.service;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.constant.MessageCode;
import vn.edu.tdtu.dto.request.FindByIdsReq;
import vn.edu.tdtu.exception.BadRequestException;
import vn.edu.tdtu.model.es.SyncPost;
import vn.edu.tdtu.repository.EsPostRepository;
import vn.edu.tdtu.repository.httpclient.PostClient;
import vn.edu.tdtu.service.interfaces.PostService;
import vn.tdtu.common.dto.PostDTO;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostClient postClient;
    private final EsPostRepository postRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public void savePost(SyncPost post) {
        postRepository.save(post);
    }

    @Override
    public void updatePost(SyncPost post) {
        postRepository.findById(post.getId()).ifPresentOrElse(
                foundPost -> {
                    foundPost.setContent(post.getContent());
                    postRepository.save(post);
                }, () -> {
                    throw new BadRequestException(MessageCode.POST_NOT_FOUND_ID, post.getId());
                }
        );
    }

    @Override
    public void deletePost(SyncPost post) {
        postRepository.deleteById(post.getId());
    }

    @Override
    public List<PostDTO> findByContentContaining(String token, String key, String fuzziness) {
        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q.match(mq -> mq
                                .field("content")
                                .query(key)
                                .fuzziness(fuzziness)
                        )
                )
                .withSort(SortOptions.of(so -> so
                        .score(score -> score
                                .order(SortOrder.Desc)
                        )
                ))
                .build();

        SearchHits<SyncPost> searchHits = elasticsearchOperations.search(query, SyncPost.class);

        List<String> matchPostIds = searchHits.getSearchHits()
                .stream()
                .map(hit -> hit.getContent().getId())
                .collect(Collectors.toList());

        return postClient
                .findAll(token, new FindByIdsReq(matchPostIds))
                .getData();
    }
}
