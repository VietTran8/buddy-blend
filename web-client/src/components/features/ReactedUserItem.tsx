import { ChatContext } from "@/context";
import { useHandleFriendRequest } from "@/hooks";
import { EFriendStatus, Reaction } from "@/types";
import { getFriendRequestBtnDesc } from "@/utils";
import { faComment } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { Avatar } from "antd";
import { FC, useContext } from "react";
import { Link, useNavigate } from "react-router-dom";

interface IProps {
    reaction: Reaction;
    type: "haha" | "sad" | "like" | "hearth" | "wow" | "angry";
    postId?: string,
    cmtId?: string
};

const ReactedUserItem: FC<IProps> = ({ reaction, type, postId, cmtId }) => {
    const navigate = useNavigate();
    const { openChatDrawer } = useContext(ChatContext);
    const { mutate: handleFQ } = useHandleFriendRequest(undefined, postId, cmtId);

    const user = reaction.user;

    const { icon, text } = getFriendRequestBtnDesc(user.friendStatus);

    const handleButton = () => {
        if (user) {
            switch (user.friendStatus) {
                case EFriendStatus.SENT_TO_YOU:
                    navigate(`/user/${user.id}`);
                    break;

                case EFriendStatus.IS_FRIEND:
                    openChatDrawer(user.id);
                    break;

                default:
                    handleFQ({
                        toUserId: user.id
                    });
            }
        }
    }

    return (<>
        <Link to={`/user/${reaction.user.id}`} className="flex gap-x-3 transition-all cursor-pointer items-center rounded-md p-3 hover:bg-gray-50">
            <div className="relative">
                <Avatar size="large" src={reaction.user.profilePicture || "/images/default-user.png"} />
                <img src={`/icons/reactions/${type}.png`} className="w-5 h-5 absolute -bottom-1 -right-1" />
            </div>
            <h4 className="text-base font-semibold flex-1">{reaction.user.userFullName}</h4>
            {!reaction.mine && (user.friend ? <button onClick={handleButton} className="btn-secondary flex gap-x-2 items-center">
                <FontAwesomeIcon icon={faComment} />
                <span className="text-sm">Nhắn tin</span>
            </button> : <button onClick={handleButton} className="btn-primary flex gap-x-2 items-center">
                <FontAwesomeIcon icon={icon} />
                <span className="text-sm">{text}</span>
            </button>)}
        </Link>
    </>)
};

export default ReactedUserItem;