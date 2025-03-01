import { EFriendReqStatus, EJoinGroupStatus } from "./enum";
import { Group } from "./group";
import { Post } from "./post";
import { Story } from "./story";
import { User } from "./user";

export type BaseResponse<D> = {
    message: string;
    data: D;
    code: number;
}

export type PaginationResponse<D> = {
    limit: number;
    totalPages: number;
    page: number;
    data: D[];
    totalElements?: number;
}

export type IdResponse = StoryIdResponse;

// Auth
export type SignInResponse = {
    id: string;
    username: string;
    tokenType: string;
    email: string;
    token: string;
    userFullName: string;
    userAvatar: string;
}

export type SignUpResponse = {
    id: string;
    email: string;
}

export type PasswordCheckingResponse = {
    token: string;
}

// Friend
export type HandleFriendReqResponse = {
    requestId: string;
    status: EFriendReqStatus;
}

export type FriendRequestResponse = {
    id: string;
    status: EFriendReqStatus;
    createdAt: string; 
    updatedAt: string;
    fromUser: User;
};

// Group
export type GroupIdResponse = {
    groupId: string;
}

export type JoinGroupResponse = {
    groupId: string;
    status: EJoinGroupStatus
}

export type PromoteToAdminResponse = {
    groupId: string;
    admin: boolean;
}

//Story
export type StoryIdResponse = {
    id: string;
}

export type LatestStoryResponse = {
    user: User;
    latestStory: Story;
    seenAll: boolean;
}

//Favorite
export type SaveUserFavouriteResponse = {
    savedId: string;
}

//Post
export type HandleSavePostResponse = {
    savedId: string;
}

//Search
export type SearchResponse = {
    users: User[];
    posts: Post[];
    groups: Group[];
}