import { http } from "../config";
import { BaseResponse, PostCommentRequest, Comment } from "../types";
import { uploadFile } from "./upload";

export const postComment = async (payload: PostCommentRequest): Promise<BaseResponse<Comment>> => {
    const uploadImagesResponse = await uploadFile("img", payload.imageUrls as File[]);

    const newPayload: PostCommentRequest = {
        ...payload,
        imageUrls: uploadImagesResponse?.data.map(resp => resp.url) as unknown as string[]
    }

    const response: BaseResponse<Comment> = await http.post("/comments", newPayload);

    return response;
}

export const getCommentByPost = async (postId: string): Promise<BaseResponse<Comment[]>> => {
    const response: BaseResponse<Comment[]> = await http.get("/comments", {
        params: {
            postId
        }
    });

    return response;
}