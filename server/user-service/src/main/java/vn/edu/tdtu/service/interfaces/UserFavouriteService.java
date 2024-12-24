package vn.edu.tdtu.service.interfaces;

import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.request.SaveUserFavouriteDTO;

public interface UserFavouriteService {
    public ResDTO<?> getUserFavById(String token, String favId);
    public ResDTO<?> getUserFavourites();
    public ResDTO<?> saveUserFavorite(SaveUserFavouriteDTO request);
    public ResDTO<?> deleteUserFavourite(String id);
}
