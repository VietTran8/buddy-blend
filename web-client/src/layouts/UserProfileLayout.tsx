import { FC, useContext } from "react";
import { Header, ProfileLayoutSkeleton, UserProfileActionPopoverContent } from "../components";
import { Avatar, Button, Divider, Popconfirm, Popover } from "antd";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faComment, faList, faUserEdit } from "@fortawesome/free-solid-svg-icons";
import { Link, Outlet, useLocation, useParams } from "react-router-dom";
import { profileNavItems } from "../constants";
import { useGetUserProfileById, useHandleFRAcceptation, useHandleFriendRequest, useQueryFriendRequestId } from "../hooks";
import { EFriendStatus, UserDetails } from "../types";
import { getFriendRequestBtnDesc } from "../utils";
import { ChatContext, ChatContextType } from "../context";
import { useQueryClient } from "@tanstack/react-query";
import { NotFound } from "@/components";

interface IProps { };

const UserProfile: FC<IProps> = ({ }) => {
    const queryClient = useQueryClient();
    const { openChatDrawer } = useContext<ChatContextType>(ChatContext);
    const { pathname } = useLocation();
    const { id } = useParams();

    const { data: userDetailsReponse, isLoading } = useGetUserProfileById(id);

    const fetchedUser: UserDetails | undefined = userDetailsReponse?.data;

    const { data: friendRequestIdResponse } = useQueryFriendRequestId(!!fetchedUser && fetchedUser.friendStatus === EFriendStatus.SENT_TO_YOU, fetchedUser?.id);

    const friendRequestId = friendRequestIdResponse?.data || "";

    const { mutate: handle, isPending: isHandling } = useHandleFriendRequest();
    const { mutate: handleAcceptation, isPending: isHandlingAcceptation } = useHandleFRAcceptation(fetchedUser?.id);

    const { text, icon } = getFriendRequestBtnDesc(fetchedUser?.friendStatus);

    const handleFriendRequest = () => {
        handle({ toUserId: id || "" });
    }

    const handleOnMessageClick = async () => {
        queryClient.invalidateQueries({
            queryKey: ["chat-rooms"]
        });

        openChatDrawer(fetchedUser?.id);
    }

    const handleFQ = (isAccept: boolean) => {
        return new Promise((resolve, reject) => {
            handleAcceptation({
                friendReqId: friendRequestId,
                isAccept
            }, {
                onSuccess: () => {
                    resolve(null);
                },
                onError: () => {
                    reject();
                }
            })
        })
    }

    return (
        <>
            <Header className="sticky top-0 z-50 mb-3" />
            {isLoading ? <ProfileLayoutSkeleton /> : !fetchedUser ? <NotFound /> : <div className="container">
                <div className="bg-white rounded-lg p-5 w-full">
                    <img src={fetchedUser?.coverPicture || "https://pixner.net/circlehub/main/assets/images/profile-cover-img.png"} className="h-[270px] rounded-md w-full object-cover" />
                    <div className="flex items-center mt-3 gap-x-3">
                        <Avatar shape="square" size={115} src={fetchedUser?.profilePicture || "/images/default-user.png"} />
                        <div className="flex-1 md:self-center self-start">
                            <h1 className="text-xl font-bold text-neutral-800">{fetchedUser?.userFullName}</h1>
                            <div className="flex items-center gap-x-1 mt-1">
                                <Avatar.Group>
                                    {(fetchedUser?.mutualFriends || fetchedUser?.otherFriends) && [...fetchedUser?.mutualFriends, ...fetchedUser?.otherFriends].slice(0, 3).map((user, index) => (
                                        <Avatar key={index} size={20} src={user.profileImage || "/images/default-user.png"} />
                                    ))}
                                </Avatar.Group>
                                <span className="font-semibold text-gray-400">{`${fetchedUser?.friendsCount} bạn bè`}</span>
                            </div>
                        </div>
                        {!isLoading && fetchedUser?.myAccount ? <Link to="/settings" className="md:block hidden self-start">
                            <Button type="primary" size="large">
                                <span className="font-semibold">
                                    <FontAwesomeIcon icon={faUserEdit} />
                                    <span className="ms-2 text-sm">Chỉnh sửa</span>
                                </span>
                            </Button>
                        </Link> : fetchedUser.friendStatus === EFriendStatus.SENT_TO_YOU ? <Popconfirm
                            title={<p className="p-2 text-gray-500">Phản hồi lời mời kết bạn</p>}
                            okType="primary"
                            icon
                            cancelButtonProps={{
                                className: "!p-4 btn-danger",
                                loading: isHandlingAcceptation
                            }}
                            okButtonProps={{
                                className: "!p-4 btn-primary",
                                loading: isHandlingAcceptation
                            }}
                            onConfirm={() => handleFQ(true)}
                            onCancel={() => handleFQ(false)}
                            okText="Đồng ý"
                            cancelText="Từ chối"
                        >
                            <Button className="md:block hidden self-start" type="primary" size="large">
                                <span className="font-semibold">
                                    <FontAwesomeIcon icon={icon} />
                                    <span className="ms-2 text-sm">{text}</span>
                                </span>
                            </Button>
                        </Popconfirm> : <Button loading={isHandling} onClick={handleFriendRequest} className="md:block hidden self-start" type="primary" size="large">
                            <span className="font-semibold">
                                <FontAwesomeIcon icon={icon} />
                                <span className="ms-2 text-sm">{text}</span>
                            </span>
                        </Button>}
                        {!fetchedUser?.myAccount && <Button onClick={handleOnMessageClick} size="large" className="self-start">
                            <span className="font-semibold text-sm text-gray-600">
                                <FontAwesomeIcon icon={faComment} className="me-2" />
                                {'Nhắn tin'}
                            </span>
                        </Button>}
                        {!fetchedUser?.myAccount && <Popover
                            trigger={"click"}
                            content={<UserProfileActionPopoverContent userId={fetchedUser?.id || ""} />}
                        >
                            <Button size="large" className="self-start">
                                <span className="font-semibold">
                                    <FontAwesomeIcon icon={faList} />
                                </span>
                            </Button>
                        </Popover>}
                    </div>
                    <hr className="mt-8" />
                    <div className="flex gap-x-3 items-center mt-4">
                        {profileNavItems.map((item, index) => (
                            <div key={index} className="flex gap-x-3 items-center">
                                <Link to={item.linkTo}>
                                    <span className={`font-semibold transition-all ${((index === 0 && pathname.split("/").length === 3) || (index !== 0 && pathname.includes(item.linkTo))) && 'text-[--primary-color]'}`}>{item.name}</span>
                                </Link>
                                <Divider type="vertical" />
                            </div>
                        ))}
                    </div>
                </div>
                <Outlet context={{ user: fetchedUser, isFetching: isLoading }} />
            </div>}
        </>
    )
};

export default UserProfile;