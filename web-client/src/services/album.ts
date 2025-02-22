import { http } from "@/config";
import { Media, PaginationResponse } from "@/types";

const baseUrl = "/album";

export const getAlbum = async (ownerId: string, page: number, size: number): Promise<PaginationResponse<Media>> => {
    const response: PaginationResponse<Media> = await http.get(`${baseUrl}/${ownerId}`, {
        params: {
            page,
            size
        }
    });

    return response;
}