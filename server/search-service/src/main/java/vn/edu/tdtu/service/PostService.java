package vn.edu.tdtu.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.common.unit.Fuzziness;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dto.request.FindByIdsReq;
import vn.edu.tdtu.exception.BadRequestException;
import vn.edu.tdtu.model.Post;
import vn.edu.tdtu.model.es.SyncPost;
import vn.edu.tdtu.repository.EsPostRepository;
import vn.edu.tdtu.repository.httpclient.PostClient;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {
    private final PostClient postClient;
    private final EsPostRepository postRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    public void savePost(SyncPost post) {
        postRepository.save(post);
    }

    public void updatePost(SyncPost post) {
        postRepository.findById(post.getId()).ifPresentOrElse(
                foundPost -> {
                    foundPost.setContent(post.getContent());
                    postRepository.save(post);
                }, () -> {
                    throw new BadRequestException("Post not found with id: " + post.getId());
                }
        );
    }
    
    public void deletePost(SyncPost post) {
        postRepository.deleteById(post.getId());
    }
    
    public List<Post> findByContentContaining(String token, String key) {
        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q.match(mq -> mq
                        .field("content")
                        .query(key)
                        .fuzziness(Fuzziness.ONE.asString())
                    )
                )
                .build();

        SearchHits<SyncPost> searchHits = elasticsearchOperations.search(query, SyncPost.class);

        List<String> matchPostIds = searchHits.getSearchHits()
                .stream()
                .map(hit -> hit.getContent().getId())
                .collect(Collectors.toList());;

        return postClient
                .findAll(token, new FindByIdsReq(matchPostIds))
                .getData();
    }
}
