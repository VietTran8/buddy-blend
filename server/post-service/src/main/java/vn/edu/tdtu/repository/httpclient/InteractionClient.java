package vn.edu.tdtu.repository.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import vn.tdtu.common.config.openfeign.FeignConfig;
import vn.tdtu.common.dto.CommentDTO;
import vn.tdtu.common.dto.ReactionDTO;
import vn.tdtu.common.enums.interaction.EReactionType;
import vn.tdtu.common.utils.Constants;
import vn.tdtu.common.viewmodel.ResponseVM;

import java.util.List;
import java.util.Map;

@FeignClient(name = "${service.interaction-service.name}", configuration = FeignConfig.class, path = Constants.API_PREFIX + Constants.API_SUB_PREFIX_INTERACTION)
public interface InteractionClient {

    @GetMapping("/comments")
    ResponseVM<List<CommentDTO>> findCommentsByPostId(@RequestParam("postId") String postId);

    @GetMapping("/reacts")
    ResponseVM<Map<EReactionType, List<ReactionDTO>>> findReactionsByPostId(@RequestParam("postId") String postId);

    @GetMapping("/comments/count/post/{id}")
    ResponseVM<Long> countCommentByPostId(@PathVariable("id") String id);
}
