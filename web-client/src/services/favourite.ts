import { http } from "../config";
import { BaseResponse, SaveUserFavouriteRequest, SaveUserFavouriteResponse, UserFavourite, UserFavouriteDetails } from "../types";

const baseRoute = "/users/favourite"

export const getUserFavorites = async (): Promise<BaseResponse<UserFavourite[]>> => {
    const response: BaseResponse<UserFavourite[]> = await http.get(`${baseRoute}`);

    return response;
}

export const handleUserFavouritePost = async (payload: SaveUserFavouriteRequest): Promise<BaseResponse<SaveUserFavouriteResponse>> => {
    const response: BaseResponse<SaveUserFavouriteResponse> = await http.post(`${baseRoute}`, payload);

    return response;
}

export const getUserFavouritePosts = async (id: string): Promise<BaseResponse<UserFavouriteDetails>> => {
    const response: BaseResponse<UserFavouriteDetails> = await http.get(`${baseRoute}/${id}`);

    return response;
}

export const deleteUserFavourite = async (id: string): Promise<BaseResponse<any>> => {
    const response: BaseResponse<any> = await http.post(`${baseRoute}/delete/${id}`);

    return response;
}