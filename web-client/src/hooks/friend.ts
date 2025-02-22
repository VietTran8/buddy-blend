import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { HandleFRAcceptationRequest, HandleFriendRequest } from "../types"
import { getFriendList, getFriendRequestIdByFromUserId, getFriendRequests, getFriendSuggestions, getGroupFriendSuggestion, handleFriendRequest, handleFriendRequestAcceptation } from "../services"
import toast from "react-hot-toast"
import { AxiosError } from "axios"
import { getErrorRespMsg } from "../utils"

/**
@param groupId: group id to invalidate members
@param postId: post id to invalidate
@param cmtId: cmt id to invalidate

@description handle friend request
*/
export const useHandleFriendRequest = (groupId?: string, postId?: string, cmtId?: string) => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: (payload: HandleFriendRequest) => handleFriendRequest(payload),
        onMutate: (variables) => variables.toUserId,
        onSuccess: async (data, _, toUserId) => {
            if (groupId)
                queryClient.invalidateQueries({
                    queryKey: ["members", groupId]
                });

            if (postId){
                queryClient.invalidateQueries({
                    queryKey: ["post-reaction", postId]
                });
            }

            if (cmtId) {
                queryClient.invalidateQueries({
                    queryKey: ["cmt-reaction", cmtId]
                });
            }
                
            queryClient.invalidateQueries({
                queryKey: ["user-profile", toUserId]
            });

            queryClient.invalidateQueries({
                queryKey: ["friend-suggestions"]
            });

            toast.success(data.message);
        },
        onError: (error: AxiosError) => {
            toast.error(getErrorRespMsg(error));
        }
    });
}

export const useQueryFriendRequests = () => {
    return useQuery({
        queryKey: ["friend-req"],
        queryFn: () => getFriendRequests(),
    });
}

export const useHandleFRAcceptation = (fromUserId?: string) => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: (payload: HandleFRAcceptationRequest) => handleFriendRequestAcceptation(payload),
        onSuccess: async (data) => {
            queryClient.invalidateQueries({
                queryKey: ["friend-req"]
            });

            queryClient.invalidateQueries({
                queryKey: ["friend-suggestions"]
            });

            fromUserId && queryClient.invalidateQueries({
                queryKey: ["user-profile", fromUserId]
            })

            toast.success(data.message);
        },
        onError: (error: AxiosError) => {
            toast.error(getErrorRespMsg(error));
        }
    });
}



export const useQueryFriendRequestId = (enabled: boolean, fromUserId?: string) => {
    return useQuery({
        queryKey: ["friend-req-id", fromUserId],
        queryFn: () => getFriendRequestIdByFromUserId(fromUserId!),
        enabled
    });
}

export const useQueryFriendList = (userId?: string) => {
    return useQuery({
        queryKey: ["friend-list", userId],
        queryFn: () => getFriendList(userId)
    });
}

export const useQueryGroupFriendSuggesstions = (groupId?: string) => {
    return useQuery({
        queryKey: ["group-friend-suggestions", groupId],
        queryFn: () => getGroupFriendSuggestion(groupId),
        enabled: !!groupId
    });
}

export const useQueryFriendSuggestions = () => {
    return useQuery({
        queryKey: ["friend-suggestions"],
        queryFn: () => getFriendSuggestions(),
    });
}