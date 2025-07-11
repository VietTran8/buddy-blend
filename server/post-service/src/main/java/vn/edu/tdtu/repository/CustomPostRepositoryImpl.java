package vn.edu.tdtu.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import vn.edu.tdtu.model.Post;
import vn.tdtu.common.enums.post.EPrivacy;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Component
public class CustomPostRepositoryImpl implements CustomPostRepository {
    private final MongoTemplate mongoTemplate;

    @Override
    public Page<Post> findNewsFeed(String userId, List<String> friendIds, List<String> groupIds, LocalDateTime startTime, int page, int size) {
        Criteria detachCriteria = new Criteria().orOperator(
                Criteria.where("detached").is(false),
                Criteria.where("detached").is(null)
        );

        Criteria criteria = new Criteria().orOperator(
                Criteria.where("userId").in(friendIds).and("privacy").in(EPrivacy.PUBLIC, EPrivacy.ONLY_FRIENDS),
                Criteria.where("userId").is(userId),
                Criteria.where("groupId").in(groupIds)
        ).andOperator(detachCriteria);

        Query countQuery = Query.query(criteria);
        long totalElements = mongoTemplate.count(countQuery, Post.class);

        Query query = Query.query(criteria)
                .with(Sort.by(Sort.Direction.DESC, "createdAt"))
                .skip((long) (page - 1) * size)
                .limit(size);

        List<Post> posts = mongoTemplate.find(query, Post.class);

        return new PageImpl<>(posts, PageRequest.of(page - 1, size), totalElements);
    }
}
