import { http } from "@/config";
import { BaseResponse, SearchHistory, SearchResponse } from "@/types";

const baseUrl = "/search";

export const getSearchHistory = async (): Promise<BaseResponse<SearchHistory[]>> => {
    const response: BaseResponse<SearchHistory[]> = await http.get(`${baseUrl}/history`);

    return response;
}

export const search = async (key: string): Promise<BaseResponse<SearchResponse>> => {
    const response: BaseResponse<SearchResponse> = await http.get(`${baseUrl}`, {
        params: {
            key
        }
    });

    return response;
}

export const fetchResult = async (key: string): Promise<BaseResponse<SearchResponse>> => {
    const response: BaseResponse<SearchResponse> = await http.get(`${baseUrl}/fetch`, {
        params: {
            key
        }
    });

    return response;
}

export const deleteHistory = async (id: string): Promise<BaseResponse<any>> => {
    const response: BaseResponse<any> = await http.post(`${baseUrl}/history/delete/${id}`);

    return response;
}

export const clearHistory = async (): Promise<BaseResponse<any>> => {
    const response: BaseResponse<any> = await http.post(`${baseUrl}/history/clear`);

    return response;
}