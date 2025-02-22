import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { BaseResponse, CreateStoryRequest, DoReactPayload, StoryIdResponse } from "../types"
import { countStoryView, createStory, doStoryReact, getStories, getStoryViewers, getUserStories } from "../services"
import toast from "react-hot-toast"
import { AxiosError } from "axios"
import { getErrorRespMsg } from "../utils"
import { useNavigate } from "react-router-dom"

export const useCreateStory = () => {
    const queryClient = useQueryClient();
    const navigate = useNavigate();

    return useMutation({
        mutationFn: (payload: CreateStoryRequest & { mediaFile: File | null }) => createStory(payload),
        onSuccess: (data: BaseResponse<StoryIdResponse>) => {
            toast.success(data.message);
            queryClient.invalidateQueries({
                queryKey: ["stories"]
            });

            navigate("/");
        },
        onError: (error: AxiosError) => {
            toast.error(getErrorRespMsg(error));
        }
    })
}

export const useDoStoryReact = () => {
    return useMutation({
        mutationFn: (payload: DoReactPayload) => doStoryReact(payload),
        onSuccess: (data: BaseResponse<DoReactPayload>) => {
            toast.success(data.message);
        },
        onError: (error: AxiosError) => {
            toast.error(getErrorRespMsg(error));
        }
    })
}

export const useCountStoryView = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: (storyId: string) => countStoryView(storyId),
        onSuccess: () => {
            queryClient.invalidateQueries({
                queryKey: ["stories"]
            })
        },
        onError: (error: AxiosError) => {
            toast.error(getErrorRespMsg(error));
        }
    })
}

export const useQueryStories = () => {
    return useQuery({
        queryKey: ["stories"],
        queryFn: () => getStories()
    })
}

export const useQueryUserStories = (userId?: string) => {
    return useQuery({
        queryKey: ["user-stories", userId],
        queryFn: () => getUserStories(userId!),
        enabled: !!userId
    })
}

export const useQueryStoryViewers = (storyId?: string) => {
    return useQuery({
        queryKey: ["story-viewers", storyId],
        queryFn: () => getStoryViewers(storyId!),
        enabled: !!storyId
    })
}