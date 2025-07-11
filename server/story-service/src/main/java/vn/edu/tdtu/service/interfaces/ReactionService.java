package vn.edu.tdtu.service.interfaces;

import vn.edu.tdtu.dto.request.DoReactRequest;
import vn.edu.tdtu.dto.response.DoReactResponse;
import vn.tdtu.common.viewmodel.ResponseVM;

public interface ReactionService {
    ResponseVM<DoReactResponse> doReact(String accessToken, DoReactRequest payload);
}
