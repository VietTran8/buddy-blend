import { http } from "../config"
import { PAGE_LIMIT } from "../constants";
import { BaseResponse, CreatePostRequest, HandleSavePostResponse, PaginationResponse, Post, SharePostRequest, UpdatePostRequest } from "../types"

export const getNewsFeed = async (startTime: string, page?: number, size?: number): Promise<BaseResponse<PaginationResponse<Post>>> => {
    const response: BaseResponse<PaginationResponse<Post>> = await http.get("/posts/news-feed", {
        params: {
            "page": page || 1,
            "size": size || PAGE_LIMIT,
            startTime
        }
    });

    return response;
}

export const getUserPosts = async (userId: string, page?: number, size?: number): Promise<BaseResponse<PaginationResponse<Post>>> => {
    const response: BaseResponse<PaginationResponse<Post>> = await http.get("/posts", {
        params: {
            "page": page || 1,
            "size": size || PAGE_LIMIT,
            userId
        }
    });

    return response;
}

export const createPost = async (payload: CreatePostRequest): Promise<BaseResponse<Post>> => {
    const response: BaseResponse<Post> = await http.post("/posts", payload);

    return response;
}

export const sharePost = async (payload: SharePostRequest): Promise<BaseResponse<Post>> => {
    const response: BaseResponse<Post> = await http.post("/posts/share", payload);

    return response;
}

export const getPost = async (id: string): Promise<BaseResponse<Post>> => {
    const response: BaseResponse<Post> = await http.get(`/posts/${id}`);

    return response;
}

export const getGroupPosts = async (groupId: string, page: number): Promise<BaseResponse<PaginationResponse<Post>>> => {
    const response: BaseResponse<PaginationResponse<Post>> = await http.get(`/posts/group/${groupId}`, {
        params: {
            page,
            size: PAGE_LIMIT
        }
    });

    return response;
}

export const updatePost = async (payload: UpdatePostRequest) => {
    const response: BaseResponse<Post> = await http.post("/posts/update", payload);

    return response;
}

export const handleSavePost = async (postId: string): Promise<BaseResponse<HandleSavePostResponse>> => {
    const response: BaseResponse<HandleSavePostResponse> = await http.post(`/posts/save/${postId}`);

    return response;
}

export const getSavedPosts = async (): Promise<BaseResponse<Post[]>> => {
    const response: BaseResponse<Post[]> = await http.get("/posts/save/posts");

    return response;
}

export const deletePost = async (id: string): Promise<BaseResponse<any>> => {
    const response: BaseResponse<any> = await http.post(`/posts/delete/${id}`);

    return response;
}


export const getDetachedPost = async (id: string): Promise<BaseResponse<Post>> => {
    const response: BaseResponse<Post> = await http.get(`/posts/detached/${id}`);

    return response;
}