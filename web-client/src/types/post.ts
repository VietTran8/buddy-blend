import { BackgroundKey } from "@/constants";
import { EFileType, EPostType, EPrivacy, EReactionType, TopReact, User } from ".";

export type Post = {
    id: string;
    content: string;
    medias: Media[];
    createdAt: string; // format: dd/MM/yyyy HH:mm:ss
    updatedAt: string; // format: dd/MM/yyyy HH:mm:ss
    privacy: EPrivacy;
    type: EPostType;
    user: User;
    noShared: number;
    noComments: number;
    background: BackgroundKey;
    noReactions: number;
    topReacts: TopReact[];
    reacted?: EReactionType;
    sharedPost: Post;
    groupInfo: GroupInfo;
    taggedUsers: User[];
    mine: boolean;
    saved: boolean;
    illegal: boolean;
};

export type ShareInfo = {
    id: string;
    status: string;
    sharedAt: string; // format: dd/MM/yyyy HH:mm:ss
    sharedUser: User;
    privacy: EPrivacy;
};

export type GroupInfo = {
    id: string;
    name: string;
    avatar: string;
    private: boolean;
};

export type Media = {
    id?: string;
    url: string;
    type: EFileType;
    ownerId: string;
    thumbnail: string;
}