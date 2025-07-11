package vn.edu.tdtu.mapper;

import org.springframework.stereotype.Component;
import vn.edu.tdtu.model.CommentReactions;
import vn.edu.tdtu.model.Reactions;
import vn.tdtu.common.dto.ReactionDTO;
import vn.tdtu.common.dto.UserDTO;
import vn.tdtu.common.utils.DateUtils;

import java.util.List;

@Component
public class ReactResponseMapper {

    public ReactionDTO mapToDto(String userId, Reactions reaction, List<UserDTO> users) {
        ReactionDTO response = new ReactionDTO();

        response.setId(reaction.getId());
        response.setIsMine(userId.equals(reaction.getUserId()));
        response.setType(reaction.getType());
        response.setCreatedAt(DateUtils.localDateTimeToDate(reaction.getCreatedAt()));
        response.setUser(users.stream()
                .filter(user -> user.getId().equals(reaction.getUserId()))
                .findFirst()
                .orElse(null));

        return response;
    }

    public ReactionDTO mapToCommentDto(String userId, CommentReactions reaction, List<UserDTO> users) {
        ReactionDTO response = new ReactionDTO();

        response.setId(reaction.getId());
        response.setIsMine(userId.equals(reaction.getUserId()));
        response.setType(reaction.getType());
        response.setCreatedAt(DateUtils.localDateTimeToDate(reaction.getCreatedAt()));
        response.setUser(users.stream()
                .filter(user -> user.getId().equals(reaction.getUserId()))
                .findFirst()
                .orElse(null));

        return response;
    }
}
