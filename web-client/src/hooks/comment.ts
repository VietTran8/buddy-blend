import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { PostCommentRequest } from "../types"
import { getCommentByPost, postComment } from "../services"
import toast from "react-hot-toast";
import { AxiosError } from "axios";
import { getErrorRespMsg } from "../utils";

export const usePostComment = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: (payload: PostCommentRequest) => postComment(payload),
        onMutate: (payload) => {
            return { postId: payload.postId };
        },
        onSuccess: (_, __, { postId }) => {
            queryClient.invalidateQueries({
                predicate: (query) => {
                    const [key, postIdKey] = query.queryKey;

                    return key === "get-post-comments" || key === "post" && postIdKey === postId
                }
            });

            toast.success("Đã tải lên bình luận!");
        },
        onError: (error: AxiosError) => {
            toast.error(getErrorRespMsg(error));
        }
    });
}

export const useQueryPostComment = (postId: string) => {
    return useQuery({
        queryKey: ["get-post-comments", postId],
        queryFn: () => getCommentByPost(postId),
        enabled: !!postId
    });
}