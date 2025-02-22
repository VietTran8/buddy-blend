import { faCheck, faComment, faXmark } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { Avatar } from "antd";
import { FC, useContext } from "react";
import { EFriendStatus, EMemberAcceptation, Member, User } from "../../types";
import { AuthContext, ChatContext } from "../../context";
import { getFriendRequestBtnDesc } from "@/utils";
import { Link, useNavigate } from "react-router-dom";
import { useHandleFriendRequest } from "@/hooks";

interface IProps {
    className?: string;
    isModerate?: boolean;
    member?: Member;
    isModerating?: boolean;
    onModerate?: (memberId: string, accept: EMemberAcceptation) => void;
    groupId?: string
};

const GroupMemberItem: FC<IProps> = ({ groupId, className, isModerate = false, member, onModerate, isModerating = false }) => {
    const { user: authUser } = useContext(AuthContext);
    const { openChatDrawer } = useContext(ChatContext);

    const navigate = useNavigate();

    const user: User | undefined = member?.user;

    const { mutate: handleFQ } = useHandleFriendRequest(groupId);

    const { icon, text } = getFriendRequestBtnDesc(user?.friendStatus);

    const handleButton = () => {
        if(user) {
            switch(user.friendStatus) {
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

    return (
        <div className={`p-3 rounded-md hover:bg-gray-50 transition-all flex items-center gap-x-3 ${className}`}>
            <Link to={`/user/${user?.id}`}>
                <Avatar className="flex-shrink-0" size={60} src={user?.profilePicture || "/images/default-user.png"} />
            </Link>
            <div className="flex-1 flex flex-col">
                <Link to={`/user/${user?.id}`}>
                    <h3 className="font-semibold">{user?.userFullName}</h3>
                </Link>
                <span className="text-sm mt-1 font-medium text-gray-400">{`${user?.friendsCount} bạn bè`}</span>
            </div>
            {isModerate ? <div className="flex items-center gap-x-3">
                <button disabled={isModerating} onClick={() => onModerate && onModerate(member?.id || "", EMemberAcceptation.REJECT)} className="btn-danger flex gap-x-2 items-center">
                    <FontAwesomeIcon icon={faXmark} />
                    <span className="text-sm">Từ chối</span>
                </button>
                <button disabled={isModerating} onClick={() => onModerate && onModerate(member?.id || "", EMemberAcceptation.AGREE)} className="btn-primary flex gap-x-2 items-center">
                    <FontAwesomeIcon icon={faCheck} />
                    <span className="text-sm">Phê duyệt</span>
                </button>
            </div> : (authUser?.id !== user?.id && <button onClick={handleButton} className={`${user?.friend ? 'btn-secondary' : 'btn-primary'} flex gap-x-2 items-center`}>
                {user?.friend ? <>
                    <FontAwesomeIcon icon={faComment} />
                    <span className="text-sm">Nhắn tin</span>
                </> : <>
                    <FontAwesomeIcon icon={icon} />
                    <span className="text-sm">{text}</span>
                </>}
            </button>)}
        </div>
    )
};

export default GroupMemberItem;