package vn.edu.tdtu.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.tdtu.constant.MessageCode;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.requests.DoCommentReactRequest;
import vn.edu.tdtu.mapper.CommentReactionMapper;
import vn.edu.tdtu.mapper.ReactResponseMapper;
import vn.edu.tdtu.model.CommentReactions;
import vn.edu.tdtu.repository.CommentReactionRepository;
import vn.edu.tdtu.service.interfaces.CommentReactionService;
import vn.edu.tdtu.service.interfaces.UserService;
import vn.edu.tdtu.util.SecurityContextUtils;
import vn.tdtu.common.dto.ReactionDTO;
import vn.tdtu.common.dto.UserDTO;
import vn.tdtu.common.enums.interaction.EReactionType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public record CommentReactionServiceImpl(
        CommentReactionRepository commentReactionRepository,
        CommentReactionMapper commentReactionMapper,
        UserService userService,
        ReactResponseMapper reactResponseMapper
) implements CommentReactionService {

    @Override
    public ResDTO<?> doReact(DoCommentReactRequest request) {
        ResDTO<List<ReactionDTO>> responseData = new ResDTO<>();
        responseData.setCode(200);
        responseData.setData(null);

        String userId = SecurityContextUtils.getUserId();
        commentReactionRepository.findByUserIdAndCmtId(userId, request.getCmtId()).ifPresentOrElse(
                (reaction) -> {
                    if (request.getType().equals(reaction.getType())) {
                        commentReactionRepository.delete(reaction);
                        responseData.setMessage(MessageCode.REACTION_UNCREATED);
                    } else {
                        reaction.setType(request.getType());
                        reaction.setCreatedAt(LocalDateTime.now());
                        responseData.setMessage(MessageCode.REACTION_UPDATED);
                        commentReactionRepository.save(reaction);
                    }
                }, () -> {
                    CommentReactions commentReactions = commentReactionMapper.mapToObject(userId, request);
                    responseData.setMessage(MessageCode.REACTION_CREATED);
                    commentReactionRepository.save(commentReactions);
                }
        );

        List<CommentReactions> commentReactions = commentReactionRepository.findByCmtId(request.getCmtId());
        responseData.setData(getTopCmtReacts(commentReactions));

        return responseData;
    }

    @Override
    public ResDTO<Map<EReactionType, List<ReactionDTO>>> getReactsByCmtId(String token, String cmtId) {
        ResDTO<Map<EReactionType, List<ReactionDTO>>> response = new ResDTO<>();
        String userId = SecurityContextUtils.getUserId();

        List<CommentReactions> reactions = commentReactionRepository.findReactionsByCmtIdOrderByCreatedAtDesc(cmtId);
        List<String> userIds = reactions.stream().map(CommentReactions::getUserId).toList();
        List<UserDTO> users = userService.findByIds(token, userIds);

        Map<EReactionType, List<ReactionDTO>> reactResponses = reactions
                .stream()
                .map(r -> reactResponseMapper.mapToCommentDto(userId, r, users))
                .collect(Collectors.groupingBy(ReactionDTO::getType));

        response.setCode(200);
        response.setData(reactResponses);
        response.setMessage(MessageCode.REACTION_FETCHED);

        return response;
    }

    private List<ReactionDTO> getTopCmtReacts(List<CommentReactions> commentReactions) {
        return commentReactions.stream()
                .collect(Collectors.groupingBy(CommentReactions::getType, Collectors.counting()))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<EReactionType, Long>comparingByValue().reversed())
                .map(entry -> new ReactionDTO(entry.getKey(), entry.getValue().intValue()))
                .collect(Collectors.toList());
    }
}
