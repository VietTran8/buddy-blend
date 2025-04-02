import { faCancel, faComment, faEllipsisVertical } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { Avatar, Popover } from "antd";
import { Dialog, DialogClose, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from "../ui/dialog";
import { FC, useContext, useState } from "react";
import { EFriendStatus, Member } from "../../types";
import { AuthContext, ChatContext } from "../../context";
import { getFriendRequestBtnDesc } from "@/utils";
import { useNavigate } from "react-router-dom";
import { useHandleFriendRequest, useHandlePromoteAdmin } from "@/hooks";

interface IProps {
    member?: Member;
    groupId?: string;
    admin: boolean;
};

const GroupAdminItem: FC<IProps> = ({ member, groupId, admin }) => {
    const { user } = useContext(AuthContext);
    const { openChatDrawer } = useContext(ChatContext);
    const navigate = useNavigate();

    const { icon, text } = getFriendRequestBtnDesc(member?.user.friendStatus);

    const [openPromoteConfirm, setOpenPromoteConfirm] = useState<boolean>(false);

    const { mutate: handleFQ } = useHandleFriendRequest(groupId);
    const { mutate: handlePromoteAdmin, isPending: isHandlingPromote } = useHandlePromoteAdmin();

    const memberUser = member?.user;

    const handleButton = () => {
        if (memberUser) {
            switch (memberUser.friendStatus) {
                case EFriendStatus.SENT_TO_YOU:
                    navigate(`/user/${memberUser.id}`);
                    break;

                case EFriendStatus.IS_FRIEND:
                    openChatDrawer(memberUser.id);
                    break;

                default:
                    handleFQ({
                        toUserId: memberUser.id
                    });
            }
        }
    }

    const handlePromoteAdminClick = () => {
        handlePromoteAdmin({
            groupId: groupId || "",
            memberId: member?.id || ""
        });
    }

    return (
        <>
            <div className="p-3 rounded-md hover:bg-gray-50 transition-all flex items-center gap-x-3">
                <Avatar className="flex-shrink-0" size={60} src={member?.user.profilePicture || "/images/default-user.png"} />
                <div className="flex-1 flex flex-col">
                    <h3 className="font-semibold">{member?.user.userFullName}</h3>
                    <span className="px-2 py-1 rounded-md w-fit bg-blue-100 text-blue-500 font-medium text-sm">Quản trị viên</span>
                    <span className="text-sm mt-1 font-medium text-gray-400">{`${member?.user.friendsCount} bạn bè`}</span>
                </div>
                {user?.id !== member?.user.id && (member?.user.friend ? <button onClick={handleButton} className="btn-secondary flex gap-x-2 items-center">
                    <FontAwesomeIcon icon={faComment} />
                    <span className="text-sm">Nhắn tin</span>
                </button> : <button onClick={handleButton} className="btn-primary flex gap-x-2 items-center">
                    <FontAwesomeIcon icon={icon} />
                    <span className="text-sm">{text}</span>
                </button>)}
                {admin && <Popover
                    trigger={"click"}
                    content={<ul>
                        <li onClick={() => setOpenPromoteConfirm(true)} className="px-3 py-2 text-sm items-center font-medium text-gray-600 rounded hover:bg-gray-100 flex cursor-pointer transition-all gap-x-2"><FontAwesomeIcon icon={faCancel} className="text-gray-500 text-sm" />
                            {user?.id === memberUser?.id ? 'Từ chức Quản trị viên' : 'Thu hồi quyền Quản trị viên'}
                        </li>
                    </ul>}
                >
                    <div className="btn-secondary">
                        <FontAwesomeIcon icon={faEllipsisVertical} />
                    </div>
                </Popover>}
            </div>
            <Dialog open={openPromoteConfirm} onOpenChange={setOpenPromoteConfirm}>
                <DialogContent>
                    <DialogHeader>
                        <DialogTitle className="font-semibold">
                            {`Bạn có chắc là muốn ${user?.id === memberUser?.id ? 'từ chức quản trị viên' : `thu hồi quyền Quản trị viên`}?`}
                        </DialogTitle>
                        <DialogDescription className="py-3">{`${user?.id === memberUser?.id ? 'Bạn sẽ bị thu hồi quyền Quản trị và không được phép thao tác lên các chức năng dành cho Quản trị viên trong nhóm này nữa' :
                            `${member?.user.userFullName} sẽ bị thu hồi quyền Quản trị và không được phép thao tác lên các chức năng dành cho Quản trị viên trong nhóm này nữa`
                        }`}
                        </DialogDescription>
                    </DialogHeader>
                    <DialogFooter>
                        <DialogClose>
                            <button disabled={isHandlingPromote} type="button" className="btn-secondary">Hủy</button>
                        </DialogClose>
                        <button disabled={isHandlingPromote} onClick={handlePromoteAdminClick} type="button" className="btn-primary">Đồng ý</button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>
        </>
    )
};

export default GroupAdminItem;