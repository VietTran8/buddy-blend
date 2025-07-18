import { format } from "date-fns";
import { http } from "../config";
import { PAGE_LIMIT } from "../constants";
import { BaseResponse, ChatMessage, PaginationResponse, Room } from "../types";

const basePath = "/messages/rooms";

export const getChatRooms = async (): Promise<BaseResponse<Room[]>> => {
    const response: BaseResponse<Room[]> = await http.get(basePath);

    return response;
}

export const getRoomMessages = async (id: string, anchorDate: Date, page: number): Promise<BaseResponse<PaginationResponse<ChatMessage>>> => {
    const response: BaseResponse<PaginationResponse<ChatMessage>> = await http.get(`${basePath}/${id}/messages`, {
        params: {
            anchorDate: format(anchorDate, "dd/MM/yyyy HH:mm:ss"),
            page,
            size: PAGE_LIMIT
        }
    });

    return response;
}