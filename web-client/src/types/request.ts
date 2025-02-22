import { BackgroundKey } from "@/constants";
import { EMediaType, EMemberAcceptation, EPostType, EPrivacy, EReactionType, EStoryFont, EStoryType, Gender, GroupPrivacy, Media, User } from ".";

// Auth
export type SignInRequest = {
    email: string;
    password: string;
}

export type SignUpRequest = {
    firstName: string;
    lastName: string;
    middleName: string;
    gender: Gender;
    profilePicture?: string;
    bio?: string;
} & SignInRequest;

export type CreateChangePasswordRequest = {
    oldPassword: string;
} & CreateForgotPasswordRequest

export type CreateForgotPasswordRequest = {
    email: string;
}

export type ChangePasswordRequest = {
    newPassword: string;
} & ValidateOTPRequest

export type ValidateOTPRequest = {
    otp: string;
} & CreateForgotPasswordRequest

export type PasswordCheckingRequest = {
    email: string;
    password: string;
}

// User infomation
export type UpdateUserInfoRequest = {
    gender?: string;
    fromCity?: string;
    phone?: string;
    bio?: string;
}

export type RenameUserRequest = {
    firstName: string;
    lastName: string;
    middleName: string;
    token: string;
}

// Group
export type CreateGroupRequest = {
    name: string;
    description: string;
    privacy: GroupPrivacy
}

export type ModerateMemberRequest = {
    memberId: string;
    groupId: string;
    acceptOption: EMemberAcceptation;
    reason: string;
};

export type UpdateGroupRequest = {
    name?: string;
    description?: string;
    avatar?: string;
    cover?: string;
};

export type HandleLeaveOrPendingRequest = {
    groupId: string;
    memberId: string;
}

export type InviteUsersRequest = {
    groupId: string;
    userIds: string[];
}

// Post
type CreatePostTag = {
    taggedUserId: string;
}

export type CreatePostRequest = {
    content: string;
    medias: Media[];
    privacy: EPrivacy;
    active: boolean;
    background?: BackgroundKey;
    type: EPostType;
    groupId?: string;
    postTags: CreatePostTag[] | User[];
}

export type SharePostRequest = {
    postId: string;
    status: string;
    privacy: EPrivacy;
}

export type UpdatePostRequest = {
    id: string;
    content: string;
    privacy: EPrivacy;
    background?: BackgroundKey;
    taggingUsers: string[] | User[];
    medias: Media[]
}

// Reaction
export type DoReactRequest = {
    postId: string;
    type: EReactionType;
}

export type DoCmtReactRequest = {
    cmtId: string;
    type: EReactionType;
}

// Comment
export type PostCommentRequest = {
    content: string;
    parentId?: string;
    imageUrls?: string[] | File[];
    postId: string;
}

// Friend
export type HandleFriendRequest = {
    toUserId: string;
}

export type HandleFRAcceptationRequest = {
    friendReqId: string;
    isAccept: boolean;
}

//Story
export type CreateStoryRequest = {
    mediaUrl?: string;
    thumbnailUrl?: string;
    mediaType?: EMediaType;
    content?: string;
    background?: string;
    privacy: EPrivacy;
    storyType: EStoryType;
    font?: EStoryFont;
}

export type DoReactPayload = {
    storyId: string;
    type: EReactionType;
}

// Favourite
export type SaveUserFavouriteRequest = {
    name: string;
    postId: string;
}