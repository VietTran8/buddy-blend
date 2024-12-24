package vn.edu.tdtu.service.interfaces;

import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.requests.DoCommentReactRequest;
import vn.edu.tdtu.dto.response.ReactResponse;
import vn.edu.tdtu.enums.EReactionType;

import java.util.List;
import java.util.Map;

public interface CommentReactionService {
    public ResDTO<?> doReact(DoCommentReactRequest request);
    public ResDTO<Map<EReactionType, List<ReactResponse>>> getReactsByCmtId(String token, String cmtId);
}
