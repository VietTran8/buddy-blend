import { http } from "../config";
import { BaseResponse, CreateStoryRequest, DoReactPayload, LatestStoryResponse, Story, StoryIdResponse, StoryViewer } from "../types";
import { uploadFile } from "./upload";

const basePath = `/stories`;

export const createStory = async (payload: CreateStoryRequest & { mediaFile: File | null }): Promise<BaseResponse<StoryIdResponse>> => {
    const { mediaFile, ...actualPayload} = payload;
    
    if(mediaFile) {
        const uploadResponse = await uploadFile(`img`, [mediaFile]);

        actualPayload.mediaUrl = uploadResponse?.data[0].url;
        actualPayload.thumbnailUrl = uploadResponse?.data[0].thumbnailUrl;
    }
    
    const response: BaseResponse<StoryIdResponse> = await http.post(`${basePath}`, actualPayload);

    return response;
}

export const getStories = async (): Promise<BaseResponse<LatestStoryResponse[]>> => {
    const response: BaseResponse<LatestStoryResponse[]> = await http.get(`${basePath}`);

    return response;
}

export const getUserStories = async (userId: string): Promise<BaseResponse<Story[]>> => {
    const response: BaseResponse<Story[]> = await http.get(`${basePath}/by-id/${userId}`);

    return response;
}

export const doStoryReact = async (payload: DoReactPayload): Promise<BaseResponse<DoReactPayload>> => {
    const response: BaseResponse<DoReactPayload> = await http.post(`${basePath}/react`, payload);

    return response;
}

export const countStoryView = async (storyId: string): Promise<BaseResponse<StoryIdResponse>> => {
    const response: BaseResponse<StoryIdResponse> = await http.post(`${basePath}/views/count/${storyId}`);

    return response;
}

export const getStoryViewers = async (storyId: string): Promise<BaseResponse<StoryViewer[]>> => {
    const response: BaseResponse<StoryViewer[]> = await http.get(`${basePath}/views/${storyId}`);

    return response;
}