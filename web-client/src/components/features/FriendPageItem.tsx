import { faUserCheck } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { Avatar, Image } from "antd";
import { FC } from "react";
import { Link } from "react-router-dom";
import { FriendRequestResponse, User } from "../../types";
import { useHandleFRAcceptation, useHandleFriendRequest } from "../../hooks";
import { getFriendRequestBtnDesc } from "@/utils";

interface IProps {
    type?: "request" | "suggest";
    request?: FriendRequestResponse,
    user?: User
};

const FriendPageItem: FC<IProps> = ({ request, type = "request", user }) => {
    const { mutate: handleAcceptation, isPending: isHandlingAcceptation } = useHandleFRAcceptation();
    const { mutate: handleFQ, isPending: isHandlingFQ } = useHandleFriendRequest();

    const bindUser = type === "request" ? request?.fromUser : user;

    const handleFQAcceptation = (friendReqId: string, isAccept: boolean) => {
        handleAcceptation({
            friendReqId,
            isAccept
        });
    }

    const handleFriendRequest = (toUserId: string) => {
        handleFQ({
            toUserId
        })
    }

    const { text, icon } = getFriendRequestBtnDesc(bindUser?.friendStatus);

    return <div className="rounded-md bg-white p-5 w-full">
        <div className="w-full h-[185px] rounded-md overflow-hidden">
            <Image preview={{
                mask: <></>
            }} width={"100%"} height={185} src={bindUser?.profilePicture || "/images/default-user.png"} className="object-cover rounded-md" />
        </div>
        <Link to={`/user/${bindUser?.id}`}>
            <h2 className="text-center text-base font-semibold mt-3 hover:text-[--primary-color] transition-all">{bindUser?.userFullName}</h2>
        </Link>
        <div className="flex gap-x-1 items-center justify-center mt-2">
            <Avatar.Group size="small">
                {bindUser?.firstThreeFriends?.map((avatar, index) => (
                    <Avatar key={index} src={avatar || "/images/default-user.png"} />
                ))}
            </Avatar.Group>
            <span className="text-sm text-gray-500 font-medium">{bindUser?.mutualFriends.length} bạn chung</span>
        </div>
        <div className="flex gap-x-3 justify-center mt-4">
            <button
                disabled={type === "request" ? isHandlingAcceptation : isHandlingFQ}
                onClick={
                    () => type === "request" ?
                        handleFQAcceptation(request?.id || "", true) :
                        handleFriendRequest(bindUser?.id || "")
                }
                className="btn-primary">{type === "request" ?
                    <span>
                        <FontAwesomeIcon className="text-sm" icon={faUserCheck} />
                        <span className="ms-2 text-sm">Đồng ý</span>
                    </span> : <span>
                        <FontAwesomeIcon className="text-sm" icon={icon} />
                        <span className="ms-2 text-sm">{text}</span>
                    </span>
                }</button>
            <button disabled={type === "request" && isHandlingAcceptation} onClick={() => type === "request" && handleFQAcceptation(request?.id || "", false)} className="btn-danger">{type === "request" ? 'Từ chối' : 'Xóa'}</button>
        </div>
    </div>
};

export default FriendPageItem;