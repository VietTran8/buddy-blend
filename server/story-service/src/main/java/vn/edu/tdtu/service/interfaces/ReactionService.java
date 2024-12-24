package vn.edu.tdtu.service.interfaces;

import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.request.DoReactRequest;
import vn.edu.tdtu.dto.response.DoReactResponse;

public interface ReactionService {
    public ResDTO<DoReactResponse> doReact(String accessToken, DoReactRequest payload);
}
