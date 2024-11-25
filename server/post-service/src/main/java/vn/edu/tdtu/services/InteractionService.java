package vn.edu.tdtu.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dtos.ResDTO;
import vn.edu.tdtu.enums.EReactionType;
import vn.edu.tdtu.models.Comment;
import vn.edu.tdtu.models.Reacts;
import vn.edu.tdtu.repositories.httpclient.InteractionClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class InteractionService {
    private final InteractionClient interactionClient;

    public List<Comment> findCommentsByPostId(String token ,String postId){
        ResDTO<List<Comment>> response = interactionClient.findCommentsByPostId(token, postId);
        log.info(response.toString());

        return response.getData();
    }

    public long countCommentByPostId(String token ,String postId){
        ResDTO<Long> response = interactionClient.countCommentByPostId(token, postId);
        log.info(response.toString());

        return response.getData();
    }

    public Map<EReactionType, List<Reacts>> findReactionsByPostId(String token, String postId){
        ResDTO<Map<EReactionType, List<Reacts>>> response = interactionClient.findReactionsByPostId(token, postId);
        log.info(response.toString());

        return response.getData();
    }
}   
