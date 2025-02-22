import { Post } from "./post";

export type UserFavourite = {
    id: string;
    name: string;
    createdAt: string;
    postCount: number;
}

export type UserFavouriteDetails = {
    posts: Post[];
} & UserFavourite