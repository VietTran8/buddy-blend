import { http } from "../config";
import { BaseResponse, DoCmtReactRequest, DoReactRequest, EReactionType, Reaction, TopReact } from "../types";

export const doReact = async (payload: DoReactRequest): Promise<BaseResponse<TopReact[]>> => {
    const response: BaseResponse<TopReact[]> = await http.post("/reacts", payload);

    return response;
}

export const doCmtReact = async (payload: DoCmtReactRequest): Promise<BaseResponse<TopReact[]>> => {
    const response: BaseResponse<TopReact[]> = await http.post("/reacts/cmt", payload);

    return response;
}


export const getReactByPost = async (postId: string): Promise<BaseResponse<Record<EReactionType, Reaction[]>>> => {
    const response: BaseResponse<Record<EReactionType, Reaction[]>> = await http.get("/reacts", {
        params: {
            postId
        }
    });

    return response;
}

export const getReactByCmt = async (cmtId: string): Promise<BaseResponse<Record<EReactionType, Reaction[]>>> => {
    const response: BaseResponse<Record<EReactionType, Reaction[]>> = await http.get("/reacts/cmt", {
        params: {
            cmtId
        }
    });

    return response;
}