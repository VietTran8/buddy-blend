import { http } from "../config";
import { BaseGroup, Group, BaseResponse, CreateGroupRequest, GroupIdResponse, Member, ModerateMemberRequest, UpdateGroupRequest, GroupWithPending, PaginationResponse, JoinGroupResponse, HandleLeaveOrPendingRequest, InviteUsersRequest, PromoteToAdminRequest, PromoteToAdminResponse } from "../types";

export const createGroup = async (payload: CreateGroupRequest): Promise<BaseResponse<GroupIdResponse>> => {
    const response: BaseResponse<GroupIdResponse> = await http.post("/groups", payload);

    return response;
}

export const getGroupById = async (id: string): Promise<BaseResponse<Group>> => {
    const response: BaseResponse<Group> = await http.get(`/groups/${id}`);

    return response;
}

export const getMinGroupById = async (id: string): Promise<BaseResponse<BaseGroup>> => {
    const response: BaseResponse<BaseGroup> = await http.get(`/groups/min/${id}`);

    return response;
}

export const getMyGroups = async (): Promise<BaseResponse<GroupWithPending[]>> => {
    const response: BaseResponse<GroupWithPending[]> = await http.get(`/groups`);

    return response;
}

export const getPendingMembers = async (groupId: string): Promise<BaseResponse<Member[]>> => {
    const response: BaseResponse<Member[]> = await http.get(`/groups/${groupId}/members/pending`);

    return response;
}

export const getAdminMembers = async (groupId: string, page?: number): Promise<BaseResponse<PaginationResponse<Member>>> => {
    const response: BaseResponse<PaginationResponse<Member>> = await http.get(`/groups/${groupId}/members/admin`, {
        params: {
            page
        }
    });

    return response;
}

export const getNewMembers = async (groupId: string, page?: number): Promise<BaseResponse<PaginationResponse<Member>>> => {
    const response: BaseResponse<PaginationResponse<Member>> = await http.get(`/groups/${groupId}/members/new`, {
        params: {
            page
        }
    });

    return response;
}

export const getFriendMembers = async (groupId: string, page?: number): Promise<BaseResponse<PaginationResponse<Member>>> => {
    const response: BaseResponse<PaginationResponse<Member>> = await http.get(`/groups/${groupId}/members/friend`, {
        params: {
            page
        }
    });

    return response;
}

export const getAllMembers = async (groupId: string, page?: number): Promise<BaseResponse<PaginationResponse<Member>>> => {
    const response: BaseResponse<PaginationResponse<Member>> = await http.get(`/groups/${groupId}/members`, {
        params: {
            page
        }
    });

    return response;
}

export const joinGroup = async (groupId: string): Promise<BaseResponse<JoinGroupResponse>> => {
    const response: BaseResponse<JoinGroupResponse> = await http.post(`/groups/join/${groupId}`);

    return response;
}

export const moderateMember = async (payload: ModerateMemberRequest): Promise<BaseResponse<Member>> => {
    const response: BaseResponse<Member> = await http.post(`/groups/member/moderate`, payload);

    return response;
}

export const cancelOrLeaveGroup = async (payload: HandleLeaveOrPendingRequest, type: "cancel-pending" | "leave"): Promise<BaseResponse<GroupIdResponse>> => {
    const response: BaseResponse<GroupIdResponse> = await http.post(`/groups/member/${type}`, payload);

    return response;
}

export const deleteMember = async (groupId: string, memberId: string): Promise<BaseResponse<any>> => {
    const response: BaseResponse<any> = await http.delete(`/groups/${groupId}/member/${memberId}`);

    return response;
}

export const updateGroup = async (groupId: string, payload: UpdateGroupRequest): Promise<BaseResponse<GroupIdResponse>> => {
    const response: BaseResponse<GroupIdResponse> = await http.put(`/groups/${groupId}`, payload);

    return response;
}

export const deleteGroup = async (groupId: string): Promise<BaseResponse<GroupIdResponse>> => {
    const response: BaseResponse<GroupIdResponse> = await http.delete(`/groups/${groupId}`);

    return response;
}

export const inviteUsers = async (payload: InviteUsersRequest): Promise<BaseResponse<void>> => {
    const response: BaseResponse<void> = await http.post(`/groups/member/invite`, payload);

    return response;
}

export const handlePromoteAdmin = async (payload: PromoteToAdminRequest): Promise<BaseResponse<PromoteToAdminResponse>> => {
    const response: BaseResponse<PromoteToAdminResponse> = await http.post(`/groups/member/promoteToAdmin`, payload);

    return response;
}