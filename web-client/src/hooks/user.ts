import { useMutation, useQuery, useQueryClient, UseQueryResult } from "@tanstack/react-query"
import { banUser, getBanningUsers, getUserById, getUserProfile, getUserProfileById, updateUserInfo, renameUser, updateUserPic } from "../services"
import { BaseResponse, UpdateUserInfoRequest, RenameUserRequest, User } from "../types";
import toast from "react-hot-toast";
import { AxiosError } from "axios";
import { getErrorRespMsg } from "../utils";

export const useGetUserById = (id?: string): UseQueryResult<BaseResponse<User>, Error> => {
    return useQuery({
        queryKey: ["user-by-id", id],
        queryFn: () => getUserById(id || ""),
        enabled: !!id,
    });
}

export const useGetUserProfile = (token?: string) => {
    return useQuery({
        queryKey: ["user-profile", token],
        queryFn: () => getUserProfile(),
        enabled: !!token
    });
}

export const useGetUserProfileById = (userId?: string) => {
    return useQuery({
        queryKey: ["user-profile", userId],
        queryFn: () => getUserProfileById(userId!),
        enabled: !!userId
    });
}

export const useUpdateUserInfo = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: (payload: UpdateUserInfoRequest) => updateUserInfo(payload),
        onSuccess: (data) => {
            queryClient.invalidateQueries({
                queryKey: ["user-profile"]
            });
            
            toast.success(data.message);
        },
        onError: (error: AxiosError) => {
            toast.error(getErrorRespMsg(error));
        }
    });
}

export const useUpdateUserProfilePic = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: (file: File) => updateUserPic(file, "profile"),
        onMutate: () => {
            const toastId = toast.loading("Đang tải ảnh lên...", {
                position: "bottom-left"
            });

            return { toastId }
        },
        onSuccess: (data, _, { toastId }) => {
            queryClient.invalidateQueries({
                queryKey: ["user-profile"]
            });
            toast.success(data.message, { id: toastId });
        },
        onError: (error: AxiosError, _, context) => {
            toast.error(getErrorRespMsg(error), { id: context?.toastId });
        }
    });
}


export const useUpdateUserCoverPic = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: (file: File) => updateUserPic(file, "cover"),
        onMutate: () => {
            const toastId = toast.loading("Đang tải ảnh lên...", {
                position: "bottom-left"
            });

            return { toastId }
        },
        onSuccess: (data, _, { toastId }) => {
            queryClient.invalidateQueries({
                queryKey: ["user-profile"]
            });
            toast.success(data.message, { id: toastId });
        },
        onError: (error: AxiosError, _, context) => {
            toast.error(getErrorRespMsg(error), { id: context?.toastId });
        }
    });
}

export const useHandleBanUser = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: (userId: string) => banUser(userId),
        onMutate: (userId) => ({ userId }),
        onSuccess: (_, __, { userId }) => {
            queryClient.invalidateQueries({
                queryKey: ["user-profile", userId]
            });

            queryClient.invalidateQueries({
                queryKey: ["banning-users"]
            });
        },
        onError: (data: AxiosError) => {
            toast.error(getErrorRespMsg(data));
        }
    });
}

export const useQueryBanningUsers = () => {
    return useQuery({
        queryKey: ["banning-users"],
        queryFn: () => getBanningUsers()
    });
}

export const useRenameUser = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: (payload: RenameUserRequest) => renameUser(payload),
        onSuccess: () => {
            queryClient.invalidateQueries({
                queryKey: ["user-profile"]
            });

            toast.success("Đã thay đổi tên người dùng!");
        },
        onError: (error: AxiosError) => {
            toast.success(getErrorRespMsg(error));
        }
    });
}