import { http } from "../config";
import { BaseResponse, FriendRequestResponse, HandleFRAcceptationRequest, HandleFriendReqResponse, HandleFriendRequest, User } from "../types";

const basePath = "/users"

export const handleFriendRequest = async (payload: HandleFriendRequest): Promise<BaseResponse<HandleFriendReqResponse>> => {
    const response: BaseResponse<HandleFriendReqResponse> = await http.post(`${basePath}/friend-req`, payload);

    return response;
}

export const getFriendRequests = async (): Promise<BaseResponse<FriendRequestResponse[]>> => {
    const response: BaseResponse<FriendRequestResponse[]> = await http.get(`${basePath}/friend-reqs`);

    return response;
}

export const handleFriendRequestAcceptation = async (payload: HandleFRAcceptationRequest): Promise<BaseResponse<HandleFriendReqResponse>> => {
    const response: BaseResponse<HandleFriendReqResponse> = await http.post(`${basePath}/friend-req/acceptation`, payload);

    return response;
}

export const getFriendList = async (id?: string): Promise<BaseResponse<User[]>> => {
    const response: BaseResponse<User[]> = await http.get(`${basePath}/friends`, {
        params: {
            id
        }
    });

    return response;
}

export const getFriendRequestIdByFromUserId = async (fromUserId: string): Promise<BaseResponse<string>> => {
    const response: BaseResponse<string> = await http.get(`${basePath}/friend-req/${fromUserId}`);

    return response;
}

export const getGroupFriendSuggestion = async (groupId?: string): Promise<BaseResponse<User[]>> => {
    const response: BaseResponse<User[]> = await http.get(`${basePath}/suggestions/group/${groupId}`);

    return response;
}

export const getFriendSuggestions = async (): Promise<BaseResponse<User[]>> => {
    const response: BaseResponse<User[]> = await http.get(`${basePath}/friends/suggestions`);

    return response;
}