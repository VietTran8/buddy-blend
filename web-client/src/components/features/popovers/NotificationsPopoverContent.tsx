
import { Empty } from "antd";
import { FC } from "react";
import { FetchNextPageOptions, InfiniteData, InfiniteQueryObserverResult } from "@tanstack/react-query";
import { BaseResponse, PaginationResponse } from "../../../types";
import { InteractNotification } from "../../../types/notification";
import NotificationItem from "../NotificationItem";
import NotificationSkeleton from "@/components/skeletons/NotificationSkeleton";


interface IProps {
    className?: string,
    data: InfiniteData<BaseResponse<PaginationResponse<InteractNotification>>, unknown> | undefined,
    isLoading: boolean,
    isFetchingNextPage: boolean,
    hasNextPage: boolean,
    fetchNextPage: (options?: FetchNextPageOptions) => Promise<InfiniteQueryObserverResult<InfiniteData<BaseResponse<PaginationResponse<InteractNotification>>, unknown>, Error>>
};



const NotificationsPopoverContent: FC<IProps> = ({
    className,
    data,
    isLoading,
    isFetchingNextPage,
    hasNextPage,
    fetchNextPage
}) => {

    return (
        <div className={`${className} w-[450px] max-h-[70vh] overflow-y-auto no-scrollbar flex flex-col gap-1`}>
            {data?.pages.map((page, index) => (
                <div key={index} className="flex flex-col gap-1">
                    {page.data.data.map((item) => (
                        <NotificationItem key={item.id} notification={item} />
                    ))}
                </div>
            ))}
            {data?.pages.length === 1 && data.pages[0].data.data.length == 0 && <Empty className="my-10" description={false}>
                <p className="font-semibold text-gray-400">Hiện tại chưa có thông báo nào...</p>
            </Empty>}
            {isLoading && Array(5).fill(null).map((_, index) => <NotificationSkeleton key={index}/>)}
            {isFetchingNextPage && Array(5).fill(null).map((_, index) => <NotificationSkeleton key={index}/>)}
            {hasNextPage && <p onClick={() => fetchNextPage()} className="text-center my-2 text-[--primary-color] cursor-pointer">Xem thêm</p>}
        </div>
    )
};

export default NotificationsPopoverContent;