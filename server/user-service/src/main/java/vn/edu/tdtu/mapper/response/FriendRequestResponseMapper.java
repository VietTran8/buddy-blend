package vn.edu.tdtu.mapper.response;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.edu.tdtu.dto.response.FriendRequestResponse;
import vn.edu.tdtu.model.FriendRequest;

@Component
@RequiredArgsConstructor
public class FriendRequestResponseMapper {
    private final MinimizedUserMapper minimizedUserMapper;

    public FriendRequestResponse mapToDTO(FriendRequest friendRequest){
        FriendRequestResponse response = new FriendRequestResponse();

        response.setId(friendRequest.getId());
        response.setStatus(friendRequest.getStatus());
        response.setCreatedAt(friendRequest.getCreatedAt());
        response.setUpdatedAt(friendRequest.getUpdatedAt());
        response.setFromUser(
                minimizedUserMapper.mapToDTO(friendRequest.getFromUser())
        );

        return response;
    }
}
