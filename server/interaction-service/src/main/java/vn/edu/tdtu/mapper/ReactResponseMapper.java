package vn.edu.tdtu.mapper;

import org.springframework.stereotype.Component;
import vn.edu.tdtu.dto.response.ReactResponse;
import vn.edu.tdtu.model.CommentReactions;
import vn.edu.tdtu.model.Reactions;
import vn.edu.tdtu.model.data.User;
import vn.edu.tdtu.util.DateUtils;

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

    public ReactResponse mapToCommentDto(String userId, CommentReactions reaction, List<User> users){
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
}
