package vn.edu.tdtu.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.constant.MessageCode;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.request.SaveUserFavouriteDTO;
import vn.edu.tdtu.dto.response.UserFavouriteDetailResp;
import vn.edu.tdtu.dto.response.UserFavouriteResponse;
import vn.edu.tdtu.exception.BadRequestException;
import vn.edu.tdtu.mapper.response.FavDetailResponseMapper;
import vn.edu.tdtu.model.User;
import vn.edu.tdtu.model.UserFavourite;
import vn.edu.tdtu.repository.UserFavouriteRepository;
import vn.edu.tdtu.service.interfaces.UserFavouriteService;
import vn.edu.tdtu.service.interfaces.UserService;
import vn.edu.tdtu.util.SecurityContextUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class UserFavouriteServiceImpl implements UserFavouriteService {
    private final UserFavouriteRepository userFavouriteRepository;
    private final UserService userService;
    private final FavDetailResponseMapper favDetailResponseMapper;

    @Override
    public ResDTO<?> getUserFavById(String token, String favId) {
        String userId = SecurityContextUtils.getUserId();
        ResDTO<UserFavouriteDetailResp> response = new ResDTO<>();

        User foundUser = userService.findById(userId);

        if (foundUser == null) {
            throw new BadRequestException(MessageCode.USER_NOT_FOUND_ID, userId);
        }

        UserFavourite fav = userFavouriteRepository.findByIdAndUser(favId, foundUser).orElseThrow(
                () -> new BadRequestException(MessageCode.USER_FAVOURITE_NOT_FOUND)
        );

        response.setData(favDetailResponseMapper.mapToDto(token, fav));
        response.setCode(200);
        response.setMessage(MessageCode.USER_FAVOURITE_FETCHED);

        return response;
    }

    @Override
    public ResDTO<?> getUserFavourites() {
        String userId = SecurityContextUtils.getUserId();
        ResDTO<List<UserFavouriteResponse>> response = new ResDTO<>();

        User foundUser = userService.findById(userId);
        if (foundUser != null) {
            response.setData(userFavouriteRepository.findByUser(foundUser)
                    .stream().map(fav -> {
                        UserFavouriteResponse userFavouriteResponse = new UserFavouriteResponse();
                        userFavouriteResponse.setId(fav.getId());
                        userFavouriteResponse.setName(fav.getName());
                        userFavouriteResponse.setCreatedAt(fav.getCreatedAt());
                        userFavouriteResponse.setPostCount(fav.getPostIds().size());

                        return userFavouriteResponse;
                    }).toList());
            response.setCode(200);
            response.setMessage(MessageCode.USER_FAVOURITE_FETCHED);

            return response;
        }

        throw new BadRequestException(MessageCode.USER_NOT_FOUND_ID, userId);
    }

    @Override
    public ResDTO<?> saveUserFavorite(SaveUserFavouriteDTO request) {
        AtomicReference<String> message = new AtomicReference<>();
        AtomicReference<String> savedId = new AtomicReference<>();
        User foundUser = userService.findById(SecurityContextUtils.getUserId());

        userFavouriteRepository.findByNameAndUser(request.getName(), foundUser)
                .ifPresentOrElse(
                        (f) -> {
                            if (f.getPostIds() != null) {
                                if (!f.getPostIds().contains(request.getPostId())) {
                                    f.getPostIds().add(request.getPostId());
                                    message.set("Đã thêm vào danh sách yêu thích: " + request.getName());
                                } else {
                                    f.getPostIds().remove(request.getPostId());
                                    message.set("Đã xóa khỏi danh sách yêu thích: " + request.getName());
                                }
                            } else {
                                List<String> postIds = new ArrayList<>();
                                postIds.add(request.getPostId());
                                f.setPostIds(postIds);
                                message.set("Đã thêm vào danh sách yêu thích: " + request.getName());
                            }

                            f.setUpdatedAt(LocalDateTime.now());
                            savedId.set(f.getId());
                            userFavouriteRepository.save(f);
                        }, () -> {
                            UserFavourite userFavourite = new UserFavourite();
                            userFavourite.setName(request.getName());
                            userFavourite.setUser(foundUser);
                            userFavourite.setCreatedAt(LocalDateTime.now());

                            List<String> postIds = new ArrayList<>();
                            postIds.add(request.getPostId());

                            userFavourite.setPostIds(postIds);
                            userFavourite.setUpdatedAt(LocalDateTime.now());

                            userFavouriteRepository.save(userFavourite);
                            savedId.set(userFavourite.getId());
                            message.set("Đã thêm vào danh sách yêu thích: " + request.getName());
                        }
                );

        Map<String, String> data = new HashMap<>();
        data.put("savedId", savedId.get());

        ResDTO<Map<String, String>> response = new ResDTO<>();
        response.setMessage(message.get());
        response.setCode(200);
        response.setData(data);

        return response;
    }

    @Override
    public ResDTO<?> deleteUserFavourite(String id) {
        userFavouriteRepository.deleteById(id);

        ResDTO<?> response = new ResDTO<>();
        response.setData(null);
        response.setCode(200);
        response.setMessage(MessageCode.USER_FAVOURITE_DELETED);

        return response;
    }

}
