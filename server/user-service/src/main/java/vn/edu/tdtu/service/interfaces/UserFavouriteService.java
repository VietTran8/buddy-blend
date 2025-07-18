package vn.edu.tdtu.service.interfaces;

import vn.edu.tdtu.dto.request.SaveUserFavouriteDTO;
import vn.tdtu.common.viewmodel.ResponseVM;

public interface UserFavouriteService {
    ResponseVM<?> getUserFavById(String favId);

    ResponseVM<?> getUserFavourites();

    ResponseVM<?> saveUserFavorite(SaveUserFavouriteDTO request);

    ResponseVM<?> deleteUserFavourite(String id);
}
