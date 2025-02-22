import { EFriendStatus } from "./enum";

export type BaseUser = {
    id: string;
    email: string;
    userFullName: string;
    profilePicture: string;
    online: boolean;
}

export type User = {
    firstName: string;
    middleName: string;
    lastName: string;
    createdAt: string; // định dạng "dd/MM/yyyy HH:mm:ss"
    userFullName: string;
    firstThreeFriends?: string[];
    friend: boolean;
    friendStatus: EFriendStatus;
    friendsCount: number;
    mutualFriends: MutualFriend[];
} & BaseUser;

export type UserDetails = {
    myAccount: boolean;
    gender: string;
    bio: string;
    phone: string;
    fromCity: string;
    coverPicture: string;
    cover?: string;
    notificationKey: string;
    otherFriends: MutualFriend[];
} & User

export type MutualFriend = {
    id: string;
    fullName: string;
    profileImage: string
}

export type Blocking = {
    id: string;
    blockedAt: string;
    blockedUser: User;
}