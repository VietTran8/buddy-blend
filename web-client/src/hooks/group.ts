import { useInfiniteQuery, useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { CreateGroupRequest, HandleLeaveOrPendingRequest, InviteUsersRequest, ModerateMemberRequest, UpdateGroupRequest } from "../types"
import { cancelOrLeaveGroup, createGroup, deleteGroup, deleteMember, getAdminMembers, getAllMembers, getFriendMembers, getGroupById, getMinGroupById, getMyGroups, getNewMembers, getPendingMembers, inviteUsers, joinGroup, moderateMember, updateGroup } from "../services"
import toast from "react-hot-toast"
import { AxiosError } from "axios"
import { getErrorRespMsg } from "../utils"
import { useNavigate } from "react-router-dom"

export const useCreateGroup = () => {
    const navigate = useNavigate();

    return useMutation({
        mutationFn: (payload: CreateGroupRequest) => createGroup(payload),
        onSuccess: (data) => {
            navigate(`/group/${data.data.groupId}`)
            toast.success(data.message)
        },
        onError: (error: AxiosError) => {
            toast.error(getErrorRespMsg(error))
        }
    })
}

export const useQueryGroupById = (id?: string) => {
    return useQuery({
        queryKey: ["group", id],
        queryFn: () => getGroupById(id!),
        enabled: !!id
    });
}

export const useQueryMinGroupById = (id?: string) => {
    return useQuery({
        queryKey: ["group", id, "min-group"],
        queryFn: () => getMinGroupById(id!),
        enabled: !!id
    });
}

export const useQueryMyGroup = () => {
    return useQuery({
        queryKey: ["my-groups"],
        queryFn: () => getMyGroups(),
    });
}

export const useQueryPendingMembers = (groupId?: string) => {
    return useQuery({
        queryKey: ["members", groupId, "pending-members"],
        queryFn: () => getPendingMembers(groupId!),
        enabled: !!groupId
    });
}

export const useQueryNewMembers = (groupId?: string) => {
    return useInfiniteQuery({
        queryKey: ["members", groupId, "new-members"],
        queryFn: ({ pageParam }) => getNewMembers(groupId!, pageParam),
        enabled: !!groupId,
        initialPageParam: 1,
        getNextPageParam: (lastPage) => {
            const { page, totalPages } = lastPage.data;

            return page < totalPages ? page + 1 : undefined;
        }
    });
}

export const useQueryAllMembers = (groupId?: string) => {
    return useInfiniteQuery({
        queryKey: ["members", groupId, "all-members"],
        queryFn: ({ pageParam }) => getAllMembers(groupId!, pageParam),
        enabled: !!groupId,
        initialPageParam: 1,
        getNextPageParam: (lastPage) => {
            const { page, totalPages } = lastPage.data;

            return page < totalPages ? page + 1 : undefined;
        }
    });
}

export const useQueryAdminMembers = (groupId?: string) => {
    return useInfiniteQuery({
        queryKey: ["members", groupId, "admin-members"],
        queryFn: ({ pageParam }) => getAdminMembers(groupId!, pageParam),
        enabled: !!groupId,
        initialPageParam: 1,
        getNextPageParam: (lastPage) => {
            const { page, totalPages } = lastPage.data;

            return page < totalPages ? page + 1 : undefined;
        }
    });
}

export const useQueryFriendMembers = (groupId?: string) => {
    return useInfiniteQuery({
        queryKey: ["members", groupId, "friend-members"],
        queryFn: ({ pageParam }) => getFriendMembers(groupId!, pageParam),
        enabled: !!groupId,
        initialPageParam: 1,
        getNextPageParam: (lastPage) => {
            const { page, totalPages } = lastPage.data;

            return page < totalPages ? page + 1 : undefined;
        }
    });
}

export const useJoinGroup = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: (groupId: string) => joinGroup(groupId),
        onMutate: (groupId) => groupId,
        onSuccess: (data, _, groupId) => {
            queryClient.invalidateQueries({ queryKey: ["members", groupId] });
            queryClient.invalidateQueries({ queryKey: ["group", groupId] });
            queryClient.invalidateQueries({ queryKey: ["my-groups"] });
            
            toast.success(data.message);
        },
        onError: (error: AxiosError) => {
            toast.error(getErrorRespMsg(error));
        }
    });
}

export const useCancelOrLeaveGroup = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: ({ payload, type }: {payload: HandleLeaveOrPendingRequest, type: "cancel-pending" | "leave"}) => cancelOrLeaveGroup(payload, type),
        onMutate: ({ payload }) => payload.groupId,
        onSuccess: (data, _, groupId) => {
            queryClient.invalidateQueries({ queryKey: ["members", groupId] });
            queryClient.invalidateQueries({ queryKey: ["group", groupId] });
            queryClient.invalidateQueries({ queryKey: ["my-groups"] });
            
            toast.success(data.message);
        },
        onError: (error: AxiosError) => {
            toast.error(getErrorRespMsg(error));
        }
    });
}

export const useModerateMember = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: (payload: ModerateMemberRequest) => moderateMember(payload),
        onMutate: ({ groupId }) => groupId,
        onSuccess: async (data, _, groupId) => {
            queryClient.invalidateQueries({ queryKey: ["members", groupId] });
            queryClient.invalidateQueries({ queryKey: ["group", groupId] });
            queryClient.invalidateQueries({ queryKey: ["my-groups"] });

            toast.success(data.message);
        },
        onError: (error: AxiosError) => {
            toast.error(getErrorRespMsg(error));
        }
    });
}

export const useDeleteMember = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: ({ groupId, memberId }: { groupId: string, memberId: string }) => deleteMember(groupId, memberId),
        onMutate: ({ groupId }) =>  groupId,
        onSuccess: (data, _, groupId) => {
            queryClient.invalidateQueries({ queryKey: ["members", groupId] });
            queryClient.invalidateQueries({ queryKey: ["group", groupId] });
            queryClient.invalidateQueries({ queryKey: ["my-groups"] });

            toast.success(data.message);
        },
        onError: (error: AxiosError) => {
            toast.error(getErrorRespMsg(error));
        }
    });
}

export const useUpdateGroup = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: ({ groupId, payload }: { groupId: string, payload: UpdateGroupRequest }) => updateGroup(groupId, payload),
        onMutate: ({ groupId }) => groupId,
        onSuccess: (_, __, groupId) => {
            queryClient.invalidateQueries({ queryKey: ["group", groupId] });
            queryClient.invalidateQueries({ queryKey: ["my-groups"] });
        }
    });
}

export const useDeleteGroup = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: (groupId: string) => deleteGroup(groupId),
        onSuccess: (data) => {
            queryClient.invalidateQueries({ queryKey: ["my-groups"] });
            
            toast.success(data.message);
        },
        onError: (error: AxiosError) => {
            toast.error(getErrorRespMsg(error));
        }
    });
}

export const useInvitesUsers = () => {
    return useMutation({
        mutationFn: (payload: InviteUsersRequest) => inviteUsers(payload),
        onSuccess: () => {
            toast.success("Mời bạn bè thành công!");
        },
        onError: () => {
            toast.error("Lỗi xảy ra!");
        }
    });
}