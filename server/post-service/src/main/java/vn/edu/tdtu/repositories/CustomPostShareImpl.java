package vn.edu.tdtu.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import vn.edu.tdtu.models.PostShare;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CustomPostShareImpl implements CustomPostShareRepository{
    private final MongoTemplate mongoTemplate;
    @Override
    public List<PostShare> findSharedPostIdsByFriends(List<String> friendIds, String userId, LocalDateTime startTime) {
        Criteria friendsCriteria = Criteria.where("sharedUserId").in(friendIds)
                .and("sharedAt").lte(startTime);

        Criteria userCriteria = Criteria.where("sharedUserId").is(userId)
                .and("sharedAt").lte(startTime);

        Criteria combinedCriteria = new Criteria().orOperator(friendsCriteria, userCriteria);

        Query query = Query.query(combinedCriteria);
        return mongoTemplate.find(query, PostShare.class);
    }
}
