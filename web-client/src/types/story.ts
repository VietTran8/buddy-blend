import { EMediaType, EPrivacy, EReactionType, EStoryFont, EStoryType } from "./enum";
import { User } from "./user";

export type Story = {
    id: string;
    mediaUrl?: string;
    thumbnailUrl?: string;
    content?: string;
    font?: EStoryFont;
    mediaType?: EMediaType;
    storyType: EStoryType;
    background?: string;
    createdAt: string;
    privacy: EPrivacy;
    mine: boolean;
    storyCount: number;
    user: User;
    seen: boolean;
    viewCount?: number;
}

export type StoryViewer = {
    id: string;
    user: User;
    viewedAt: string;
    reactions: StoryReaction[];
}

export type StoryReaction = {
    id: string;
    type: EReactionType;
    createdAt: string;
}