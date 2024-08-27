package vn.edu.tdtu.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dtos.ResDTO;
import vn.edu.tdtu.dtos.requests.DoCommentReactRequest;
import vn.edu.tdtu.dtos.response.ReactResponse;
import vn.edu.tdtu.mapper.CommentReactionMapper;
import vn.edu.tdtu.mapper.ReactResponseMapper;
import vn.edu.tdtu.models.CommentReactions;
import vn.edu.tdtu.repositories.CommentReactionRepository;
import vn.edu.tdtu.utils.JwtUtils;

import java.time.LocalDateTime;

@Service
public record CommentReactionService (
        CommentReactionRepository commentReactionRepository,
        JwtUtils jwtUtils,
        CommentReactionMapper commentReactionMapper,
        UserService userService,
        ReactResponseMapper reactResponseMapper
) {
    public ResDTO<?> doReact(String token, DoCommentReactRequest request) {
        ResDTO<ReactResponse> responseData = new ResDTO<>();
        responseData.setCode(200);
        responseData.setData(null);
        responseData.setMessage("success");

        String userId = jwtUtils.getUserIdFromJwtToken(token);
        commentReactionRepository.findByUserIdAndCmtId(userId, request.getCmtId()).ifPresentOrElse(
                (reaction) -> {
                    if(request.getType().equals(reaction.getType())) {
                        commentReactionRepository.delete(reaction);
                        responseData.setMessage("reaction canceled");
                    }else{
                        reaction.setType(request.getType());
                        reaction.setCreatedAt(LocalDateTime.now());
                        responseData.setMessage("reacted updated");
                        responseData.setData(
                                reactResponseMapper.mapToCommentDto(reaction, userService.findById(token, userId))
                        );
                        commentReactionRepository.save(reaction);
                    }
                }, () -> {
                    CommentReactions commentReactions = commentReactionMapper.mapToObject(userId, request);
                    commentReactionRepository.save(commentReactions);
                }
        );

        return responseData;
    }

}
