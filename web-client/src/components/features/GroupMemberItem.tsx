import { faCancel, faCheck, faComment, faEllipsisVertical, faEye, faShield, faTrash, faXmark } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { Avatar, Popover } from "antd";
import { FC, useContext, useState } from "react";
import { EFriendStatus, EMemberAcceptation, Member, User } from "../../types";
import { AuthContext, ChatContext } from "../../context";
import { getFriendRequestBtnDesc } from "@/utils";
import { Link, useNavigate } from "react-router-dom";
import { useDeleteMember, useHandleFriendRequest, useHandlePromoteAdmin } from "@/hooks";
import { Dialog, DialogTitle, DialogClose, DialogContent, DialogDescription, DialogFooter, DialogHeader } from "../ui/dialog";

interface IProps {
    className?: string;
    isModerate?: boolean;
    member?: Member;
    isModerating?: boolean;
    admin: boolean;
    onModerate?: (memberId: string, accept: EMemberAcceptation) => void;
    groupId?: string
};

const GroupMemberItem: FC<IProps> = ({ groupId, admin, className, isModerate = false, member, onModerate, isModerating = false }) => {
    const { user: authUser } = useContext(AuthContext);
    const { openChatDrawer } = useContext(ChatContext);

    const [openPromoteConfirm, setOpenPromoteConfirm] = useState<boolean>(false);
    const [openDeleteConfirm, setOpenDeleteConfirm] = useState<boolean>(false);
    const [openResignConfirm, setOpenResignConfirm] = useState<boolean>(false);

    const navigate = useNavigate();

    const user: User | undefined = member?.user;

    const { mutate: handleFQ } = useHandleFriendRequest(groupId);
    const { mutate: handlePromoteAdmin, isPending: isHandlingPromote } = useHandlePromoteAdmin();
    const { mutate: deleteMember, isPending: isDeleting } = useDeleteMember();

    const { icon, text } = getFriendRequestBtnDesc(user?.friendStatus);

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

    const handlePromoteAdminClick = () => {
        handlePromoteAdmin({
            groupId: groupId || "",
            memberId: member?.id || ""
        }, {
            onSuccess: () => {
                setOpenPromoteConfirm(false);
                setOpenResignConfirm(false);
            }
        });
    }

    const handleDeleteMemberClick = () => {
        deleteMember({
            groupId: groupId || "",
            memberId: member?.id || ""
        }, {
            onSuccess: () => {
                setOpenDeleteConfirm(false);
            }
        })
    }

    return (
        <>
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
                </div> : <>
                    {authUser?.id !== user?.id && <button onClick={handleButton} className={`${user?.friend ? 'btn-secondary' : 'btn-primary'} flex gap-x-2 items-center`}>
                        {user?.friend ? <>
                            <FontAwesomeIcon icon={faComment} />
                            <span className="text-sm">Nhắn tin</span>
                        </> : <>
                            <FontAwesomeIcon icon={icon} />
                            <span className="text-sm">{text}</span>
                        </>}
                    </button>}
                    {<Popover
                        trigger={"click"}
                        content={user?.id !== authUser?.id ? (admin ? <ul>
                            <li onClick={() => setOpenPromoteConfirm(true)} className="px-3 py-2 text-sm items-center font-medium text-gray-600 rounded hover:bg-gray-100 flex cursor-pointer transition-all gap-x-2"><FontAwesomeIcon icon={member?.admin ? faCancel : faShield} className="text-gray-500 text-sm" /> {member?.admin ? 'Thu hồi quyền Quản trị viên' : 'Bổ nhiệm làm Quản trị viên'}</li>
                            <li onClick={() => setOpenDeleteConfirm(true)} className="px-3 py-2 text-sm items-center font-medium text-gray-600 rounded hover:bg-gray-100 flex cursor-pointer transition-all gap-x-2"><FontAwesomeIcon icon={faTrash} className="text-gray-500 text-sm" /> Xóa khỏi nhóm</li>
                        </ul> : <ul>
                            <Link to={`/user/${user?.id}`}>
                                <li className="px-3 py-2 text-sm items-center font-medium text-gray-600 rounded hover:bg-gray-100 flex cursor-pointer transition-all gap-x-2"><FontAwesomeIcon icon={faEye} className="text-gray-500 text-sm" /> Xem trang cá nhân</li>
                            </Link>
                        </ul>) : admin ? (<ul>
                            <li onClick={() => setOpenResignConfirm(true)} className="px-3 py-2 text-sm items-center font-medium text-gray-600 rounded hover:bg-gray-100 flex cursor-pointer transition-all gap-x-2"><FontAwesomeIcon icon={faCancel} className="text-gray-500 text-sm" /> Từ chức Quản trị viên</li>
                        </ul>) : (<ul>
                            <li className="px-3 py-2 text-sm items-center font-medium text-gray-600 rounded hover:bg-gray-100 flex cursor-pointer transition-all gap-x-2"><FontAwesomeIcon icon={faEye} className="text-gray-500 text-sm" /> Xem trang cá nhân</li>
                        </ul>)}
                    >
                        <div className="btn-secondary">
                            <FontAwesomeIcon icon={faEllipsisVertical} />
                        </div>
                    </Popover>}
                </>}
            </div>
            <Dialog open={openResignConfirm} onOpenChange={setOpenResignConfirm}>
                <DialogContent>
                    <DialogHeader>
                        <DialogTitle className="leading-normal pe-5">{`Bạn có chắc là muốn từ chức?`}</DialogTitle>
                        <DialogDescription className="py-3">{`Bạn sẽ bị thu hồi quyền Quản trị và không được phép thao tác lên các chức năng dành cho Quản trị viên trong nhóm này nữa`}</DialogDescription>
                    </DialogHeader>
                    <DialogFooter>
                        <DialogClose>
                            <button disabled={isHandlingPromote} type="button" className="btn-secondary">Hủy</button>
                        </DialogClose>
                        <button disabled={isHandlingPromote} onClick={handlePromoteAdminClick} type="button" className="btn-primary">Đồng ý</button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>
            <Dialog open={openPromoteConfirm} onOpenChange={setOpenPromoteConfirm}>
                <DialogContent>
                    <DialogHeader>
                        <DialogTitle className="leading-normal pe-5">
                            {`Bạn có chắc là muốn ${member?.admin ? 'thu hồi quyền Quản trị viên của' : 'bổ nhiệm làm Quản trị viên cho'} ${member?.user.userFullName}?`}
                        </DialogTitle>
                        <DialogDescription className="py-3">{`${member?.user.userFullName} sẽ ${member?.admin ? 'bị thu hồi quyền Quản trị và không được phép thao tác lên các chức năng dành cho Quản trị viên trong nhóm này nữa' :
                            'được thăng chức làm Quản trị viên và được phép thao tác lên các chức năng dành cho Quản trị viên trong nhóm này'}`}
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
            <Dialog open={openDeleteConfirm} onOpenChange={setOpenDeleteConfirm}>
                <DialogContent>
                    <DialogHeader>
                        <DialogTitle className="leading-normal pe-5">
                            {`Bạn có chắc là muốn xóa ${member?.user.userFullName} khỏi nhóm?`}
                        </DialogTitle>
                        <DialogDescription className="py-3">
                            {`${member?.user.userFullName} sẽ bị xóa khỏi nhóm và không thể đăng bài hay xem bài viết (nếu là riêng tư) trong nhóm nữa`}
                        </DialogDescription>
                    </DialogHeader>
                    <DialogFooter>
                        <DialogClose>
                            <button disabled={isDeleting} type="button" className="btn-secondary">Hủy</button>
                        </DialogClose>
                        <button disabled={isDeleting} onClick={handleDeleteMemberClick} type="button" className="btn-danger">Xóa</button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>
        </>
    )
};

export default GroupMemberItem;