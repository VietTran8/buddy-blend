import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { DoCmtReactRequest, DoReactRequest } from "../types";
import { doCmtReact, doReact, getReactByCmt, getReactByPost } from "../services";

export const useDoReact = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: (payload: DoReactRequest) => doReact(payload),
        onMutate: (payload) => {
            return payload.postId
        },
        onSuccess: (_, __, postId) => {
            queryClient.invalidateQueries({
                queryKey: ["post-reaction", postId]
            });
        }
    });
}

export const useDoCmtReact = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: ({ payload }: { payload: DoCmtReactRequest, postId: string }) => doCmtReact(payload),
        onMutate: (variables) => ({
            postId: variables.postId,
            cmtId: variables.payload.cmtId
        }),
        onSuccess: (_, __, context) => {
            queryClient.invalidateQueries({
                queryKey: ["get-post-comments", context.postId]
            });
            queryClient.invalidateQueries({
                queryKey: ["cmt-reaction", context.cmtId]
            });
        },
    });
}

export const useQueryPostReactions = (postId?: string) => {
    return useQuery({
        queryKey: ["post-reaction", postId],
        queryFn: () => getReactByPost(postId || ""),
        enabled: !!postId
    })
}

export const useQueryCmtReactions = (cmtId?: string) => {
    return useQuery({
        queryKey: ["cmt-reaction", cmtId],
        queryFn: () => getReactByCmt(cmtId || ""),
        enabled: !!cmtId
    })
}