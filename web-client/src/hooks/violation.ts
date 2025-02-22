import { getViolationById, getViolationByRefId } from "@/services"
import { Post } from "@/types";
import { useQuery } from "@tanstack/react-query"
import { useQueryDetachedPost } from "./post";

type ViolationInfo = {
    content: string[];
    post?: Post;
}

export type UseGetViolationInfoType = {
    isLoading: boolean;
    data: ViolationInfo;
}

export const useQueryViolationByRef = (refId?: string) => {
    return useQuery({
        queryKey: ["violation-by-ref"],
        queryFn: () => getViolationByRefId(refId!),
        enabled: !!refId
    });
}

export const useQueryViolation = (id?: string) => {
    return useQuery({
        queryKey: ["violation"],
        queryFn: () => getViolationById(id!),
        enabled: !!id
    });
}

export const useGetViolationInfo = (violationId?: string): UseGetViolationInfoType => {
    const { data: violationResponse, isLoading: isViolationLoading } = useQueryViolation(violationId);
    const { data: postResponse, isLoading: isPostLoading } = useQueryDetachedPost(violationResponse?.data.refId);

    const { content } = violationResponse?.data || {
        content: ""
    };
    const post = postResponse?.data;

    return {
        isLoading: isViolationLoading || isPostLoading,
        data: {
            content: content?.split(";") || [],
            post
        }
    }
}