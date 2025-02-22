import { AuthContext, ChatContext } from "@/context";
import { useHandleFriendRequest } from "@/hooks";
import { EFriendStatus, User } from "@/types";
import { getFriendRequestBtnDesc } from "@/utils";
import { faComment } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { Avatar } from "antd";
import { FC, useContext } from "react";
import { Link, useNavigate } from "react-router-dom";
import HtmlText from "../shared/HtmlText";
import { useQueryClient } from "@tanstack/react-query";

interface IProps {
    user: User
};

const SearchPeopleItem: FC<IProps> = ({ user }) => {
    const queryClient = useQueryClient();

    const { user: authUser } = useContext(AuthContext);
    const { openChatDrawer } = useContext(ChatContext);

    const { mutate: handleFQ } = useHandleFriendRequest();

    const navigate = useNavigate();

    const handleButton = () => {
        if (user) {
            switch (user.friendStatus) {
                case EFriendStatus.SENT_TO_YOU:
                    navigate(`/user/${user.id}`);
                    break;

                case EFriendStatus.IS_FRIEND:
                    openChatDrawer(user.id);
                    queryClient.invalidateQueries({
                        queryKey: ["chat-rooms"]
                    });
                    break;

                default:
                    handleFQ({
                        toUserId: user.id
                    });
            }
        }
    }

    const { icon, text } = getFriendRequestBtnDesc(user.friendStatus)

    return (
        <div className="flex p-3 rounded-md bg-white transition-all gap-x-3 items-center w-full cursor-pointer hover:bg-gray-100">
            <Link className="self-start" to={`/user/${user.id}`}>
                <Avatar size={70} src={user.profilePicture || "/images/default-user.png"} />
            </Link>
            <div className="flex-1">
                <Link to={`/user/${user.id}`} className="font-semibold text-base -mb-1 block">
                    <HtmlText>
                        {user.userFullName}
                    </HtmlText>
                </Link>
                <span className="font-medium text-gray-500 text-sm">{ 
                    authUser?.id === user.id ? 'Bạn' : user.friend ? 'Bạn bè' : ''
                }</span>
                {authUser?.id !== user.id && <div className="flex items-center gap-x-1 mt-2">
                    <Avatar.Group size={"small"}>
                        {user.mutualFriends?.slice(0, 5).map(friend => (
                            <Avatar src={friend.profileImage || "/images/default-user.png"} />
                        ))}
                    </Avatar.Group>
                    <p className="text-sm text-gray-500">{`${user.mutualFriends.length} bạn chung`}</p>
                </div>}
            </div>
            {user.id !== authUser?.id && (user.friend ? <button onClick={handleButton} className="btn-secondary flex gap-x-2 items-center">
                <FontAwesomeIcon icon={faComment} />
                <span className="text-sm">Nhắn tin</span>
            </button> : <button onClick={handleButton} className="btn-primary flex gap-x-2 items-center">
                <FontAwesomeIcon icon={icon} />
                <span className="text-sm">{text}</span>
            </button>)}
        </div>
    );
};

export default SearchPeopleItem;