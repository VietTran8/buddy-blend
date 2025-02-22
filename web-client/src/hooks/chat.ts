import { useInfiniteQuery, useQuery } from "@tanstack/react-query"
import { getChatRooms, getRoomMessages } from "../services"

export const useQueryChatRooms = () => {
    return useQuery({
        queryKey: ["chat-rooms"],
        queryFn: () => getChatRooms(),
    });
}

export const useQueryRoomMessages = (anchorDate: Date, roomId?: string) => {
    return useInfiniteQuery({
        queryKey: ["room-message", roomId],
        queryFn: ({ pageParam }) => getRoomMessages(roomId!, anchorDate, pageParam),
        getNextPageParam: (lastPage) => {
            const { totalPages, page } = lastPage.data;

            return page < totalPages ? page + 1 : undefined;
        },
        initialPageParam: 1,
        enabled: !!roomId
    });
}