package vn.edu.tdtu.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.enums.EReactionType;
import vn.edu.tdtu.model.data.Comment;
import vn.edu.tdtu.model.data.Reacts;
import vn.edu.tdtu.repository.httpclient.InteractionClient;
import vn.edu.tdtu.service.intefaces.InteractionService;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class InteractionServiceImpl implements InteractionService {
    private final InteractionClient interactionClient;

    @Override
    public List<Comment> findCommentsByPostId(String token ,String postId){
        ResDTO<List<Comment>> response = interactionClient.findCommentsByPostId(token, postId);
        log.info(response.toString());

        return response.getData();
    }

    @Override
    public long countCommentByPostId(String token ,String postId){
        ResDTO<Long> response = interactionClient.countCommentByPostId(token, postId);
        log.info(response.toString());

        return response.getData();
    }

    @Override
    public Map<EReactionType, List<Reacts>> findReactionsByPostId(String token, String postId){
        ResDTO<Map<EReactionType, List<Reacts>>> response = interactionClient.findReactionsByPostId(token, postId);
        log.info(response.toString());

        return response.getData();
    }
}   
