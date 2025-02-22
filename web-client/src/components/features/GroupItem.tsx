import { Divider, Modal } from "antd";
import { FC, useState } from "react";
import { Link } from "react-router-dom";
import { GroupPrivacy, GroupWithPending } from "../../types";
import GroupInviteModalContent from "./modals/GroupInviteModalContent";

interface IProps {
    group: GroupWithPending
};

const GroupItem: FC<IProps> = ({ group }) => {
    const [openInviteModal, setOpenInviteModal] = useState<boolean>(false);

    return (
        <>
            <div className="p-4 bg-white rounded-md">
                <div className="relative w-full">
                    <div className="relative w-full h-[100px]">
                        <img className="rounded-md w-full h-full object-cover" src={group.cover || "/images/community.jpg"} />
                        <div className="rounded-md w-full h-full absolute top-0 left-0 bg-gradient-to-t from-transparent via-transparent to-black opacity-95"></div>
                    </div>
                    <img className="w-14 h-14 object-cover rounded-md absolute left-[50%] -translate-x-[50%] -translate-y-[50%] shadow" src={group.avatar || "/images/community2.jpg"} />
                </div>
                <div className="mt-10 flex flex-col gap-y-3 items-center">
                    <Link to={`/group/${group.id}`}>
                        <h3 className="font-semibold text-base hover:text-[--primary-color] transition-all">{group.name}</h3>
                    </Link>
                    <span className="text-sm -mt-3 font-medium text-gray-400">{group.privacy === GroupPrivacy.PRIVACY_PUBLIC ? 'Nhóm công khai' : 'Nhóm riêng tư'}</span>
                    <div className="flex items-center gap-x-1">
                        <span className="text-sm font-semibold text-gray-400">{`${group.memberCount} thành viên`}</span>
                    </div>
                    <Divider className="my-3" />
                    <div className="flex gap-x-3 items-center justify-center">
                        <Link to={`/group/${group.id}`}>
                            <button className="btn-success-light">{group.pending ? 'Chờ phê duyệt...' : 'Đã tham gia'}</button>
                        </Link>
                        <button onClick={() => setOpenInviteModal(true)} className="btn-secondary">Mời bạn bè</button>
                    </div>
                </div>
            </div>
            <Modal
                title={<h1 className="md:text-lg mb-5 text-base text-center font-semibold">Mời bạn bè tham gia nhóm này</h1>}
                centered
                open={openInviteModal}
                onCancel={() => setOpenInviteModal(false)}
                width={950}
                destroyOnClose
                footer
            >
                <GroupInviteModalContent groupId={group?.id} />
            </Modal>
        </>
    );
};

export default GroupItem;