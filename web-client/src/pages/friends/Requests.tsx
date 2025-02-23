import { FC } from "react";
import { FriendPageItem, FriendPageItemSkeleton } from "../../components";
import { useQueryFriendRequests } from "../../hooks";
import { Empty } from "antd";

interface IProps { };

const RequestPage: FC<IProps> = ({ }) => {
    const { data: friendRequestsResponse, isLoading } = useQueryFriendRequests();

    const friendRequests = friendRequestsResponse?.data;

    return (
        <div className="grid grid-cols-12 gap-3 w-full pb-10">
            {friendRequests?.map((request, index) => (
                <div key={index} className="md:col-span-4 col-span-6">
                    <FriendPageItem key={index} request={request} />
                </div>
            ))}
            {friendRequests?.length === 0 && <Empty className="col-span-full mt-20" description={false}>
                <p className="text-base font-semibold text-gray-400">Bạn chưa có lời mời kết bạn nào</p>
            </Empty>}
            {isLoading && Array(10).fill(null).map((_, index) => (
                <div key={index} className="md:col-span-4 col-span-6">
                    <FriendPageItemSkeleton />
                </div>))
            }
        </div>
    )
};

export default RequestPage;