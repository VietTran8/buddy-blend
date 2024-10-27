package vn.edu.tdtu.dto.response;

import vn.edu.tdtu.dto.request.DoReactRequest;

public class DoReactResponse extends DoReactRequest {
    public DoReactResponse(DoReactRequest payload) {
        super(payload.getStoryId(), payload.getType());
    }
}
