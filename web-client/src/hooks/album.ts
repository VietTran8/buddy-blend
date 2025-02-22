import { getAlbum } from "@/services";
import { useInfiniteQuery, useQuery } from "@tanstack/react-query"

export const useQueryAlbum = (ownerId?: string) => {
    return useInfiniteQuery({
        queryKey: ["album"],
        queryFn: ({ pageParam }) => getAlbum(ownerId!, pageParam, 12),
        getNextPageParam: (data) => {
            const { page, totalPages } = data;

            return page + 1 <= totalPages ? page + 1 : undefined
        },
        initialPageParam: 1,
        enabled: !!ownerId
    });
}

export const useQueryTopMedia = (ownerId?: string) => {
    return useQuery({
        queryKey: ["top-medias"],
        queryFn: () => getAlbum(ownerId!, 1, 10),
        enabled: !!ownerId
    });
}