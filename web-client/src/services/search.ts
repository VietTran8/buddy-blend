import { http } from "@/config";
import { BaseResponse, SearchHistory, SearchResponse } from "@/types";

const basePath = "/search";

export const getSearchHistory = async (): Promise<BaseResponse<SearchHistory[]>> => {
    const response: BaseResponse<SearchHistory[]> = await http.get(`${basePath}/history`);

    return response;
}

export const search = async (key: string): Promise<BaseResponse<SearchResponse>> => {
    const response: BaseResponse<SearchResponse> = await http.get(`${basePath}`, {
        params: {
            key
        }
    });

    return response;
}

export const fetchResult = async (key: string): Promise<BaseResponse<SearchResponse>> => {
    const response: BaseResponse<SearchResponse> = await http.get(`${basePath}/fetch`, {
        params: {
            key
        }
    });

    return response;
}

export const deleteHistory = async (id: string): Promise<BaseResponse<any>> => {
    const response: BaseResponse<any> = await http.post(`${basePath}/history/delete/${id}`);

    return response;
}

export const clearHistory = async (): Promise<BaseResponse<any>> => {
    const response: BaseResponse<any> = await http.post(`${basePath}/history/clear`);

    return response;
}