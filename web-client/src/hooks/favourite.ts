import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { deleteUserFavourite, getUserFavorites, getUserFavouritePosts, handleUserFavouritePost } from "../services"
import { SaveUserFavouriteRequest } from "../types"
import toast from "react-hot-toast"
import { AxiosError } from "axios"
import { getErrorRespMsg } from "../utils"

export const useQueryUserFavourites = () => {
    return useQuery({
        queryKey: ["user-favourite"],
        queryFn: () => getUserFavorites()
    })
}

export const useQueryUserFavouritePosts = (id?: string) => {
    return useQuery({
        queryKey: ["user-favourite-posts", id],
        queryFn: () => getUserFavouritePosts(id!),
        enabled: !!id
    })
}

export const useHandleFavouritePost = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: (payload: SaveUserFavouriteRequest) => handleUserFavouritePost(payload),
        onSuccess: (data) => {
            queryClient.invalidateQueries({ queryKey: ["user-favourite"] });
            queryClient.invalidateQueries({ queryKey: ["user-favourite-posts"] });
            toast.success(data.message);
        },
        onError: (error: AxiosError) => {
            toast.error(getErrorRespMsg(error));
        }
    });
}

export const useDeleteUserFavourite = () => {
    const queryClient = useQueryClient();
    
    return useMutation({
        mutationFn: (id: string) => deleteUserFavourite(id),
        onSuccess: (data) => {
            queryClient.invalidateQueries({
                queryKey: ["user-favourite"]
            })
            toast.success(data.message);
        },
        onError: (error: AxiosError) => {
            toast.error(getErrorRespMsg(error));
        }
    });
}