package vn.edu.tdtu.mapper.response;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.edu.tdtu.dtos.request.FindByIdsReq;
import vn.edu.tdtu.dtos.request.FindByIdsReqDTO;
import vn.edu.tdtu.dtos.response.UserFavouriteDetailResp;
import vn.edu.tdtu.models.UserFavourite;
import vn.edu.tdtu.services.PostService;

@Component
@RequiredArgsConstructor
public class FavDetailResponseMapper {
    private final PostService postService;

    public UserFavouriteDetailResp mapToDto(String token, UserFavourite userFavourite) {
        UserFavouriteDetailResp resp = new UserFavouriteDetailResp();

        resp.setId(userFavourite.getId());
        resp.setName(userFavourite.getName());
        resp.setCreatedAt(userFavourite.getCreatedAt());
        resp.setPosts(
                postService.findByIds(token, new FindByIdsReq(userFavourite.getPostIds()))
        );

        return resp;
    }
}
