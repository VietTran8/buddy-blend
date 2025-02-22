import { EReactionType } from "./enum";
import { User } from "./user";

export type TopReact = {
    type: EReactionType;
    count: number;
};

export type Reaction = {
    id: string;
    type: EReactionType;
    createdAt: string;
    user: User;
    mine: boolean;
}