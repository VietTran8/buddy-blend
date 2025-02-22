import { http } from "../config";
import { BaseResponse, PaginationResponse } from "../types";
import { InteractNotification } from "../types/notification";

const baseUrl = '/notifications';

export const getUserNotifications: (page?: number, size?: number) => Promise<BaseResponse<PaginationResponse<InteractNotification>>> = async (page?: number, size?: number): Promise<BaseResponse<PaginationResponse<InteractNotification>>> => {
    const response: BaseResponse<PaginationResponse<InteractNotification>> = await http.get(baseUrl, {
        params: {
            page,
            size
        }
    });

    return response;
}

export const detachNotification: (id: string) => Promise<BaseResponse<any>> = async (id: string): Promise<BaseResponse<any>> => {
    const response: BaseResponse<any> = await http.post(`${baseUrl}/detach/${id}`);

    return response;
}

export const readNotification: (id: string) => Promise<BaseResponse<any>> = async (id: string): Promise<BaseResponse<any>> => {
    const response: BaseResponse<any> = await http.post(`${baseUrl}/read/${id}`);

    return response;
}