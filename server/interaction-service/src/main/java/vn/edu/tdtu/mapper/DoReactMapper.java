package vn.edu.tdtu.mapper;

import org.springframework.stereotype.Component;
import vn.edu.tdtu.dtos.requests.DoReactRequest;
import vn.edu.tdtu.models.Reactions;

import java.time.LocalDateTime;

@Component
public class DoReactMapper {
    public Reactions mapToObject(String userId, DoReactRequest request){
        Reactions reaction = new Reactions();

        reaction.setType(request.getType());
        reaction.setCreatedAt(LocalDateTime.now());
        reaction.setPostId(request.getPostId());
        reaction.setUserId(userId);

        return reaction;
    }
}