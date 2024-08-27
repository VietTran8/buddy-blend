package vn.edu.tdtu.mapper.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.edu.tdtu.dtos.response.FriendRequestResponse;
import vn.edu.tdtu.models.FriendRequest;
import vn.edu.tdtu.services.UserService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FriendRequestResponseMapper {
    private final MinimizedUserMapper minimizedUserMapper;

    public FriendRequestResponse mapToDTO(String token, FriendRequest friendRequest){
        FriendRequestResponse response = new FriendRequestResponse();
        response.setId(friendRequest.getId());
        response.setStatus(friendRequest.getStatus());
        response.setCreatedAt(friendRequest.getCreatedAt());
        response.setUpdatedAt(friendRequest.getUpdatedAt());
        response.setFromUser(
                minimizedUserMapper.mapToDTO(token, friendRequest.getFromUser())
        );

        return response;
    }
}
