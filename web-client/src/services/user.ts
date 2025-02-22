import { http } from "../config";
import { Banning, BaseResponse, IdResponse, UpdateUserInfoRequest, RenameUserRequest, User, UserDetails } from "../types";

export const getUserById = async (id: string): Promise<BaseResponse<User>> => {
    const response: BaseResponse<User> = await http.get(`/users/${id}`);

    return response;
}

export const getUserProfile = async (): Promise<BaseResponse<UserDetails>> => {
    const response: BaseResponse<UserDetails> = await http.get("/users/profile");

    return response;
}

export const getUserProfileById = async (userId: string): Promise<BaseResponse<UserDetails>> => {
    const response: BaseResponse<UserDetails> = await http.get(`/users/profile`, {
        params: {
            id: userId
        }
    });

    return response;
}

export const updateUserInfo = async (payload: UpdateUserInfoRequest): Promise<BaseResponse<UserDetails>> => {
    const repsonse: BaseResponse<UserDetails> = await http.post("/users/info/update", payload);

    return repsonse;
}

export const updateUserPic = async (file: File, type: "profile" | "cover"): Promise<BaseResponse<User>> => {
    const formData = new FormData();
    formData.append("file", file);

    const response: BaseResponse<User> = await http.post(`/users/${type}/update`, formData);

    return response;
}

export const banUser = async (userId: string): Promise<BaseResponse<IdResponse>> => {
    const response: BaseResponse<IdResponse> = await http.post(`/users/ban/${userId}`);

    return response;
}

export const getBanningUsers = async (): Promise<BaseResponse<Banning[]>> => {
    const response: BaseResponse<Banning[]> = await http.get(`/users/ban`);

    return response;
}

export const renameUser = async (payload: RenameUserRequest): Promise<BaseResponse<any>> => {
    const response: BaseResponse<any> = await http.post("/users/name/update", payload);

    return response;
}