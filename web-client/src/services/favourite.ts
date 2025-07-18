import { http } from "../config";
import { BaseResponse, SaveUserFavouriteRequest, SaveUserFavouriteResponse, UserFavourite, UserFavouriteDetails } from "../types";

const basePath = "/users/favourite"

export const getUserFavorites = async (): Promise<BaseResponse<UserFavourite[]>> => {
    const response: BaseResponse<UserFavourite[]> = await http.get(`${basePath}`);

    return response;
}

export const handleUserFavouritePost = async (payload: SaveUserFavouriteRequest): Promise<BaseResponse<SaveUserFavouriteResponse>> => {
    const response: BaseResponse<SaveUserFavouriteResponse> = await http.post(`${basePath}`, payload);

    return response;
}

export const getUserFavouritePosts = async (id: string): Promise<BaseResponse<UserFavouriteDetails>> => {
    const response: BaseResponse<UserFavouriteDetails> = await http.get(`${basePath}/${id}`);

    return response;
}

export const deleteUserFavourite = async (id: string): Promise<BaseResponse<any>> => {
    const response: BaseResponse<any> = await http.post(`${basePath}/delete/${id}`);

    return response;
}