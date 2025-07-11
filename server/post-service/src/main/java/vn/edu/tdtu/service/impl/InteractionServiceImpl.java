package vn.edu.tdtu.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.repository.httpclient.InteractionClient;
import vn.edu.tdtu.service.intefaces.InteractionService;
import vn.tdtu.common.dto.CommentDTO;
import vn.tdtu.common.dto.ReactionDTO;
import vn.tdtu.common.enums.interaction.EReactionType;
import vn.tdtu.common.viewmodel.ResponseVM;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class InteractionServiceImpl implements InteractionService {
    private final InteractionClient interactionClient;

    @Override
    public List<CommentDTO> findCommentsByPostId(String token, String postId) {
        ResponseVM<List<CommentDTO>> response = interactionClient.findCommentsByPostId(token, postId);
        log.info(response.toString());

        return response.getData();
    }

    @Override
    public long countCommentByPostId(String token, String postId) {
        ResponseVM<Long> response = interactionClient.countCommentByPostId(token, postId);
        log.info(response.toString());

        return response.getData();
    }

    @Override
    public Map<EReactionType, List<ReactionDTO>> findReactionsByPostId(String token, String postId) {
        ResponseVM<Map<EReactionType, List<ReactionDTO>>> response = interactionClient.findReactionsByPostId(token, postId);
        log.info(response.toString());

        return response.getData();
    }
}   
