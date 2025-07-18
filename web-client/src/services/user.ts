import { http } from "../config";
import { Blocking, BaseResponse, IdResponse, UpdateUserInfoRequest, RenameUserRequest, User, UserDetails } from "../types";

const basePath = `/users`;

export const getUserById = async (id: string): Promise<BaseResponse<User>> => {
    const response: BaseResponse<User> = await http.get(`${basePath}/by-id/${id}`);

    return response;
}

export const getUserProfile = async (): Promise<BaseResponse<UserDetails>> => {
    const response: BaseResponse<UserDetails> = await http.get(`${basePath}/profile`);

    return response;
}

export const getUserProfileById = async (userId: string): Promise<BaseResponse<UserDetails>> => {
    const response: BaseResponse<UserDetails> = await http.get(`${basePath}/profile`, {
        params: {
            id: userId
        }
    });

    return response;
}

export const updateUserInfo = async (payload: UpdateUserInfoRequest): Promise<BaseResponse<UserDetails>> => {
    const repsonse: BaseResponse<UserDetails> = await http.post(`${basePath}/info/update`, payload);

    return repsonse;
}

export const updateUserPic = async (file: File, type: `profile` | `cover`): Promise<BaseResponse<User>> => {
    const formData = new FormData();
    formData.append(`file`, file);

    const response: BaseResponse<User> = await http.post(`${basePath}/${type}/update`, formData);

    return response;
}

export const blockUser = async (userId: string): Promise<BaseResponse<IdResponse>> => {
    const response: BaseResponse<IdResponse> = await http.post(`${basePath}/block/${userId}`);

    return response;
}

export const getBlockingUsers = async (): Promise<BaseResponse<Blocking[]>> => {
    const response: BaseResponse<Blocking[]> = await http.get(`${basePath}/block`);

    return response;
}

export const renameUser = async (payload: RenameUserRequest): Promise<BaseResponse<any>> => {
    const response: BaseResponse<any> = await http.post(`${basePath}/name/update`, payload);

    return response;
}