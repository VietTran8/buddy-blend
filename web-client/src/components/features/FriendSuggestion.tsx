import { useHandleFriendRequest } from "@/hooks";
import { User } from "@/types";
import { getFriendRequestBtnDesc } from "@/utils";
import { faUserMinus } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { Avatar } from "antd";
import { FC } from "react";
import { Link } from "react-router-dom";

interface IProps {
    user: User
};

const FriendSuggestion: FC<IProps> = ({ user }) => {
    const { mutate: handleFriendRequest } = useHandleFriendRequest();

    const handleSentFriendRequest = (userId: string) => {
        handleFriendRequest({
            toUserId: userId
        })
    }

    const { text, icon } = getFriendRequestBtnDesc(user.friendStatus);

    return (
        <div className="mt-5">
            <div className="flex gap-x-2 items-center">
                <Link to={`/user/${user.id}`}>
                    <Avatar shape="square" size="large" src={user.profilePicture || "/images/default-user.png"} />
                </Link>
                <div className="">
                    <Link to={`/user/${user.id}`} className="font-semibold text-base">{user.userFullName}</Link>
                    <div className="flex gap-x-2 items-center mt-1">
                        <Avatar.Group>
                            {user.mutualFriends?.map((f, index) => (
                                <Avatar size='small' src={f.profileImage || "/images/default-user.png"} key={index} />
                            ))}
                        </Avatar.Group>
                        <span className="text-sm text-gray-500">{user.mutualFriends.length} bạn chung</span>
                    </div>
                </div>
            </div>
            <div className="flex gap-x-2 mt-3">
                <button className="btn-secondary w-full mt-2">
                    <FontAwesomeIcon icon={faUserMinus} />
                    <span className="text-sm ms-2 font-semibold">Ẩn lúc này</span>
                </button>
                <button onClick={() => handleSentFriendRequest(user.id)} className="btn-primary w-full mt-2">
                    <FontAwesomeIcon icon={icon} />
                    <span className="text-sm ms-2 font-semibold">
                        {text}
                    </span>
                </button>
            </div>
        </div>
    )
};

export default FriendSuggestion;