import { useInfiniteQuery, useMutation, useQueryClient } from "@tanstack/react-query"
import { detachNotification, getUserNotifications, readNotification } from "../services";
import { PAGE_LIMIT } from "../constants";
import toast from "react-hot-toast";
import { AxiosError } from "axios";
import { getErrorRespMsg } from "../utils";

export const useQueryUserNotifications = () => {
    const { data, ...queryResult } = useInfiniteQuery({
        queryKey: ["user-notifications"],
        queryFn: ({ pageParam }) => getUserNotifications(pageParam, PAGE_LIMIT),
        getNextPageParam: (lastPage) => {
            const {page, totalPages} = lastPage.data

            return page < totalPages ? page + 1 : undefined;
        },
        initialPageParam: 1,
    });

    return {
        ...queryResult,
        data,
        totalElements: data?.pages[0].data.totalElements
    }
}

export const useDetachNotification = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: (id: string) => detachNotification(id),
        onSuccess: () => {
            toast.success("Đã gỡ thông báo");
            queryClient.invalidateQueries({
                queryKey: ["user-notifications"]
            })
        },
        onError: (error: AxiosError) => {
            toast.error(getErrorRespMsg(error));
        }
    });
}

export const useReadNotification = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: (id: string) => readNotification(id),
        onSuccess: () => {
            queryClient.invalidateQueries({
                queryKey: ["user-notifications"]
            })
        },
        onError: (error: AxiosError) => {
            toast.error(getErrorRespMsg(error));
        }
    });
}