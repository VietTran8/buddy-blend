import { Avatar } from "antd";
import { FC } from "react";
import { Link } from "react-router-dom";
import ContactSection from "../features/ContactSection";
import { useHandleFRAcceptation, useQueryFriendRequests, useQueryFriendSuggestions } from "../../hooks";
import FriendSuggestion from "../features/FriendSuggestion";

interface IProps {
    className?: string
};

const RightMainSideBar: FC<IProps> = ({ className }) => {
    const { data: friendRequestsResponse } = useQueryFriendRequests();
    const { data: friendSuggestionsResponse } = useQueryFriendSuggestions();

    const { mutate: handleAcceptation, isPending: isHandling } = useHandleFRAcceptation();

    const friendRequests = friendRequestsResponse?.data || [];
    const friendSuggestions = friendSuggestionsResponse?.data || [];

    const handleFQAcceptation = (friendReqId: string, isAccept: boolean) => {
        handleAcceptation({
            friendReqId,
            isAccept
        });
    }

    return (
        <aside className={`mt-3 pb-1 h-fit h-screen-except-header overflow-auto no-scrollbar ${className}`}>
            <div className="p-5 bg-white rounded-md">
                <h1 className="text-lg font-bold mb-5 flex gap-x-3 items-center">{(friendRequests && friendRequests.length > 0) ?
                    <Link to="/friends" className="flex items-center gap-x-3">
                        <p className="text-lg font-bold">Yêu cầu kết bạn</p>
                        <span className="bg-red-500 text-white font-semibold w-6 h-6 flex justify-center items-center rounded-full">{friendRequests.length}</span>
                    </Link>
                    : <p className="text-lg font-bold">Gợi ý kết bạn</p>
                }
                </h1>
                {(friendRequests.length > 0) ? friendRequests.map((request, index) => (
                    <div key={index} className="mt-5">
                        <div className="flex gap-x-2 items-center">
                            <Link to={`/user/${request.fromUser.id}`}>
                                <Avatar shape="square" size="large" src={request.fromUser.profilePicture || "/images/default-user.png"} />
                            </Link>
                            <div className="">
                                <Link to={`/user/${request.fromUser.id}`}>
                                    <h2 className="font-semibold text-base hover:text-[--primary-color] transition-all">{request.fromUser.userFullName}</h2>
                                </Link>
                                <div className="flex gap-x-2 items-center mt-1">
                                    <Avatar.Group>
                                        {request.fromUser.mutualFriends.length > 0 ? request.fromUser.mutualFriends?.slice(0, 3).map((user, index) => (
                                            <Avatar size='small' src={user.profileImage} key={index} />
                                        )) : request.fromUser.firstThreeFriends?.map((avatar, index) => (
                                            <Avatar size='small' src={avatar || "/images/default-user.png"} key={index} />
                                        ))}
                                    </Avatar.Group>
                                    <span className="text-sm text-gray-500">{
                                        request.fromUser.mutualFriends.length > 0 ?
                                            `${request.fromUser.mutualFriends.length} bạn chung` :
                                            `${request.fromUser.friendsCount} bạn bè`
                                    }</span>
                                </div>
                            </div>
                        </div>
                        <div className="flex gap-x-2 mt-3">
                            <button disabled={isHandling} onClick={() => handleFQAcceptation(request.id, true)} className="btn-primary w-full"><span className="text-sm font-semibold">Đồng ý</span></button>
                            <button disabled={isHandling} onClick={() => handleFQAcceptation(request.id, false)} className="btn-secondary w-full"><span className="text-sm font-semibold">Từ chối</span></button>
                        </div>
                    </div>
                )) : friendSuggestions.length > 0 && friendSuggestions.map((request) => (
                    <FriendSuggestion user={request} key={request.id} />
                ))}
            </div>
            <ContactSection className="mt-3" />
        </aside>
    )
};

export default RightMainSideBar;