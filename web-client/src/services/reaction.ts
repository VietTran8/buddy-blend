import { http } from "../config";
import { BaseResponse, DoCmtReactRequest, DoReactRequest, EReactionType, Reaction, TopReact } from "../types";

const basePath = "/interactions/reacts";

export const doReact = async (payload: DoReactRequest): Promise<BaseResponse<TopReact[]>> => {
    const response: BaseResponse<TopReact[]> = await http.post(basePath, payload);

    return response;
}

export const doCmtReact = async (payload: DoCmtReactRequest): Promise<BaseResponse<TopReact[]>> => {
    const response: BaseResponse<TopReact[]> = await http.post(`${basePath}/cmt`, payload);

    return response;
}


export const getReactByPost = async (postId: string): Promise<BaseResponse<Record<EReactionType, Reaction[]>>> => {
    const response: BaseResponse<Record<EReactionType, Reaction[]>> = await http.get(basePath, {
        params: {
            postId
        }
    });

    return response;
}

export const getReactByCmt = async (cmtId: string): Promise<BaseResponse<Record<EReactionType, Reaction[]>>> => {
    const response: BaseResponse<Record<EReactionType, Reaction[]>> = await http.get(`${basePath}/cmt`, {
        params: {
            cmtId
        }
    });

    return response;
}