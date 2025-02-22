import { EJoinGroupStatus, GroupPrivacy } from "./enum";
import { User } from "./user";

export type Member = {
    id: string;
    joinedAt: string;
    user: User;
    admin: boolean;
    pending: boolean;
};

export type BaseGroup = {
    id: string;
    name: string;
    privacy: GroupPrivacy;
    description: string;
    avatar?: string;
    cover?: string;
    createdAt: string;
}

export type GroupWithPending = {
    pending: boolean;
    memberCount: number;
} & BaseGroup

export type Group = {
    firstTenMembers: Member[];
    pendingMemberCount?: number;
    memberCount: number;
    admin: boolean;
    currentMemberId?: string;
    joinStatus: EJoinGroupStatus;
    joined: boolean;
} & BaseGroup;