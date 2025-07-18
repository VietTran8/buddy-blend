package vn.edu.tdtu.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.tdtu.dto.requests.DoCommentReactRequest;
import vn.edu.tdtu.mapper.CommentReactionMapper;
import vn.edu.tdtu.mapper.ReactResponseMapper;
import vn.edu.tdtu.model.CommentReactions;
import vn.edu.tdtu.repository.CommentReactionRepository;
import vn.edu.tdtu.service.interfaces.CommentReactionService;
import vn.edu.tdtu.service.interfaces.UserService;
import vn.tdtu.common.dto.ReactionDTO;
import vn.tdtu.common.dto.UserDTO;
import vn.tdtu.common.enums.interaction.EReactionType;
import vn.tdtu.common.utils.MessageCode;
import vn.tdtu.common.utils.SecurityContextUtils;
import vn.tdtu.common.viewmodel.ResponseVM;

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
    public ResponseVM<?> doReact(DoCommentReactRequest request) {
        ResponseVM<List<ReactionDTO>> responseData = new ResponseVM<>();
        responseData.setCode(200);
        responseData.setData(null);

        String userId = SecurityContextUtils.getUserId();
        commentReactionRepository.findByUserIdAndCmtId(userId, request.getCmtId()).ifPresentOrElse(
                (reaction) -> {
                    if (request.getType().equals(reaction.getType())) {
                        commentReactionRepository.delete(reaction);
                        responseData.setMessage(MessageCode.Interaction.REACTION_UNCREATED);
                    } else {
                        reaction.setType(request.getType());
                        reaction.setCreatedAt(LocalDateTime.now());
                        responseData.setMessage(MessageCode.Interaction.REACTION_UPDATED);
                        commentReactionRepository.save(reaction);
                    }
                }, () -> {
                    CommentReactions commentReactions = commentReactionMapper.mapToObject(userId, request);
                    responseData.setMessage(MessageCode.Interaction.REACTION_CREATED);
                    commentReactionRepository.save(commentReactions);
                }
        );

        List<CommentReactions> commentReactions = commentReactionRepository.findByCmtId(request.getCmtId());
        responseData.setData(getTopCmtReacts(commentReactions));

        return responseData;
    }

    @Override
    public ResponseVM<Map<EReactionType, List<ReactionDTO>>> getReactsByCmtId(String cmtId) {
        ResponseVM<Map<EReactionType, List<ReactionDTO>>> response = new ResponseVM<>();
        String userId = SecurityContextUtils.getUserId();

        List<CommentReactions> reactions = commentReactionRepository.findReactionsByCmtIdOrderByCreatedAtDesc(cmtId);
        List<String> userIds = reactions.stream().map(CommentReactions::getUserId).toList();
        List<UserDTO> users = userService.findByIds(userIds);

        Map<EReactionType, List<ReactionDTO>> reactResponses = reactions
                .stream()
                .map(r -> reactResponseMapper.mapToCommentDto(userId, r, users))
                .collect(Collectors.groupingBy(ReactionDTO::getType));

        response.setCode(200);
        response.setData(reactResponses);
        response.setMessage(MessageCode.Interaction.REACTION_FETCHED);

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
