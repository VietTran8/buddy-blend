import { ENotificationType } from "./enum";
import { User } from "./user";

export type InteractNotification = {
    id: string;
    content: string;
    title: string;
    refId: string;
    type: ENotificationType;
    createAt: string;
    fromUser: User;
    hasRead: boolean;
    toUserId: string;
};

export type MessageNotification = {
    id: string;
    createdAt: string;
    avatar: string;
    content: string;
    imageUrls: string[];
    fromUserId: string;
    toUserId: string;
    title: string;
}


export type NotificationSocketMessage = {
    userFullName: string;
    avatarUrl: string;
    content: string;
    refId: string;
    title: string;
    fromUserId: string;
    toUserIds: string[];
    type: ENotificationType;
    createAt: string;
}

export type Violation = {
    id: string;
    refId: string;
    content: string;
}