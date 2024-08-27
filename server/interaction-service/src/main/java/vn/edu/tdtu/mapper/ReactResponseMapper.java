package vn.edu.tdtu.mapper;

import org.springframework.stereotype.Component;
import vn.edu.tdtu.dtos.response.ReactResponse;
import vn.edu.tdtu.models.CommentReactions;
import vn.edu.tdtu.models.Reactions;
import vn.edu.tdtu.models.User;
import vn.edu.tdtu.utils.DateUtils;

import java.util.List;

@Component
public class ReactResponseMapper {

    public ReactResponse mapToDto(String userId, Reactions reaction, List<User> users){
        ReactResponse response = new ReactResponse();

        response.setId(reaction.getId());
        response.setMine(userId.equals(reaction.getUserId()));
        response.setType(reaction.getType());
        response.setCreatedAt(DateUtils.localDateTimeToDate(reaction.getCreatedAt()));
        response.setUser(users.stream()
                .filter(user -> user.getId().equals(reaction.getUserId()))
                .findFirst()
                .orElse(null));

        return response;
    }

    public ReactResponse mapToCommentDto(CommentReactions commentReactions, User reactedUser){
        ReactResponse response = new ReactResponse();

        response.setId(commentReactions.getId());
        response.setMine(reactedUser.getId().equals(commentReactions.getUserId()));
        response.setType(commentReactions.getType());
        response.setCreatedAt(DateUtils.localDateTimeToDate(commentReactions.getCreatedAt()));
        response.setUser(reactedUser);

        return response;
    }
}
