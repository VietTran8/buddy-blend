import { http } from "@/config";
import { BaseResponse } from "@/types";
import { Violation } from "@/types/notification";

const basePath = "/violation"

export const getViolationByRefId = async (refId: string): Promise<BaseResponse<Violation>> => {
    const response: BaseResponse<Violation> = await http.get(`${basePath}`, {
        params: {
            ref: refId
        }
    });

    return response;
}

export const getViolationById = async (id: string): Promise<BaseResponse<Violation>> => {
    const response: BaseResponse<Violation> = await http.get(`${basePath}/${id}`);

    return response;
}