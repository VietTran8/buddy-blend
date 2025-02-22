import { EReactionType, TopReact, User } from ".";

export interface Comment {
    id: string;
    content: string;
    parentId?: string;
    imageUrls: string[];
    createdAt: string;
    updatedAt: string;
    user: User;
    children: Comment[];
    topReacts: TopReact[];
    reacted: EReactionType;
    noReactions: number;
    mine: boolean;
}