import { clearHistory, deleteHistory, fetchResult, getSearchHistory, search } from "@/services";
import { BaseResponse, SearchHistory } from "@/types";
import { getErrorRespMsg } from "@/utils";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { AxiosError } from "axios";
import toast from "react-hot-toast";

export const useQuerySearchHistory = () => {
    return useQuery({
        queryKey: ["search-history"],
        queryFn: () => getSearchHistory()
    });
}

export const useSearchResult = (key: string | null) => {
    return useQuery({
        queryKey: ["search-result", key],
        queryFn: () => {
            if (!key) return Promise.resolve(null);
            return search(key);
        },
        enabled: !!key
    });
};


export const useFetchSearchResult = (key?: string) => {
    return useQuery({
        queryKey: ["fetch-search-result", key],
        queryFn: () => {
            if (!key) return Promise.resolve(null);
            return fetchResult(key);
        },
        enabled: !!key
    });
};


export const useDeleteHistory = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: (id: string) => deleteHistory(id),
        onMutate: (id: string) => {
            const oldData = queryClient.getQueryData(["search-history"]);

            queryClient.setQueryData<any>(["search-history"], (oldData: BaseResponse<SearchHistory[]>) => ({
                ...oldData,
                data: oldData.data.filter(history => history.id !== id)
            }))

            return { oldData };
        },
        onSuccess: () => {
            toast.success("Xóa lịch sử thành công!");
        },
        onError: (error: AxiosError, _, context) => {
            if(context) {
                queryClient.setQueryData(["search-history"], context.oldData);
            }

            toast.error(getErrorRespMsg(error));
        }
    })
}

export const useClearHistory = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: () => clearHistory(),
        onMutate: () => {
            const oldData = queryClient.getQueryData(["search-history"]);

            queryClient.setQueryData<any>(["search-history"], (oldData: BaseResponse<SearchHistory[]>) => ({
                ...oldData,
                data: []
            }))

            return { oldData };
        },
        onSuccess: () => {
            toast.success("Xóa tất cả lịch sử thành công!");
        },
        onError: (error: AxiosError, _, context) => {
            if(context) {
                queryClient.setQueryData(["search-history"], context.oldData);
            }

            toast.error(getErrorRespMsg(error));
        }
    })
}