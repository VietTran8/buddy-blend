package vn.edu.tdtu.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.tdtu.constant.MessageCode;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.requests.DoCommentReactRequest;
import vn.edu.tdtu.dto.response.ReactResponse;
import vn.edu.tdtu.dto.response.TopReacts;
import vn.edu.tdtu.enums.EReactionType;
import vn.edu.tdtu.mapper.CommentReactionMapper;
import vn.edu.tdtu.mapper.ReactResponseMapper;
import vn.edu.tdtu.model.CommentReactions;
import vn.edu.tdtu.model.data.User;
import vn.edu.tdtu.repository.CommentReactionRepository;
import vn.edu.tdtu.service.interfaces.CommentReactionService;
import vn.edu.tdtu.service.interfaces.UserService;
import vn.edu.tdtu.util.SecurityContextUtils;

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
        ResDTO<List<TopReacts>> responseData = new ResDTO<>();
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
    public ResDTO<Map<EReactionType, List<ReactResponse>>> getReactsByCmtId(String token, String cmtId) {
        ResDTO<Map<EReactionType, List<ReactResponse>>> response = new ResDTO<>();
        String userId = SecurityContextUtils.getUserId();

        List<CommentReactions> reactions = commentReactionRepository.findReactionsByCmtIdOrderByCreatedAtDesc(cmtId);
        List<String> userIds = reactions.stream().map(CommentReactions::getUserId).toList();
        List<User> users = userService.findByIds(token, userIds);

        Map<EReactionType, List<ReactResponse>> reactResponses = reactions
                .stream()
                .map(r -> reactResponseMapper.mapToCommentDto(userId, r, users))
                .collect(Collectors.groupingBy(ReactResponse::getType));

        response.setCode(200);
        response.setData(reactResponses);
        response.setMessage(MessageCode.REACTION_FETCHED);

        return response;
    }

    private List<TopReacts> getTopCmtReacts(List<CommentReactions> commentReactions) {
        return commentReactions.stream()
                .collect(Collectors.groupingBy(CommentReactions::getType, Collectors.counting()))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<EReactionType, Long>comparingByValue().reversed())
                .map(entry -> new TopReacts(entry.getKey(), entry.getValue().intValue()))
                .collect(Collectors.toList());
    }
}
