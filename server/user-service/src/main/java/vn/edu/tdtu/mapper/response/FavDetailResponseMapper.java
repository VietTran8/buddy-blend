package vn.edu.tdtu.mapper.response;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.edu.tdtu.dto.request.FindByIdsReq;
import vn.edu.tdtu.dto.response.UserFavouriteDetailResp;
import vn.edu.tdtu.model.UserFavourite;
import vn.edu.tdtu.service.interfaces.PostService;

@Component
@RequiredArgsConstructor
public class FavDetailResponseMapper {
    private final PostService postService;

    public UserFavouriteDetailResp mapToDto(UserFavourite userFavourite) {
        UserFavouriteDetailResp resp = new UserFavouriteDetailResp();

        resp.setId(userFavourite.getId());
        resp.setName(userFavourite.getName());
        resp.setCreatedAt(userFavourite.getCreatedAt());
        resp.setPosts(
                postService.findByIds(new FindByIdsReq(userFavourite.getPostIds()))
        );
        resp.setPostCount(resp.getPosts().size());

        return resp;
    }
}
