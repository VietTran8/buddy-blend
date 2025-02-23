import { FC, useState } from "react";
import Input from "../../shared/Input";
import { Plus, Search, X } from "lucide-react";
import { Avatar, Empty } from "antd";
import { User } from "../../../types";
import { useInvitesUsers, useQueryGroupFriendSuggesstions } from "../../../hooks";
import BasicUserItemSkeleton from "@/components/skeletons/BasicUserItemSkeleton";

interface IProps {
    groupId?: string
};

const GroupInviteModalContent: FC<IProps> = ({ groupId }) => {
    const { data: userResponse, isLoading } = useQueryGroupFriendSuggesstions(groupId);
    const { mutate: invites, isPending: isInviting } = useInvitesUsers();
    const [invitedUsers, setInvitedUsers] = useState<User[]>([]);

    const users: User[] | undefined = userResponse?.data;

    const handleAddInvitedUser = (user: User) => {
        if (invitedUsers.find(existingUser => existingUser.id === user.id))
            return;

        setInvitedUsers(prev => [...prev, user])
    }

    const handleRemoveInvitedUser = (index: number) => {
        setInvitedUsers(prev => {
            const newUsers = [...prev];

            newUsers.splice(index, 1);

            return [...newUsers]
        })
    }

    const handleInvites = () => {
        invites({
            groupId: groupId || "",
            userIds: invitedUsers.map(user => user.id)
        });
    }

    return (
        <div className="flex flex-col">
            <div className="grid grid-cols-12 h-[70vh]">
                <section className="md:col-span-7 col-span-full flex flex-col py-1 ps-1 pe-3 bg-white h-full overflow-hidden">
                    <Input placeholder="Tìm kiếm bạn bè..." startDrawable={<Search size={20} className="text-gray-500" />} />
                    <div className="flex flex-col gap-y-1 mt-4 overflow-y-auto custom-scroll-no-hover">
                        <h3 className="font-semibold text-base mb-3">Gợi ý</h3>
                        {users?.map((user) => {
                            if (invitedUsers.includes(user))
                                return;

                            return (
                                <div key={user.id} className="flex gap-x-3 cursor-pointer items-center p-2 rounded-md hover:bg-gray-100 transition-all">
                                    <Avatar className="flex-shrink-0" size={50} src={user.profilePicture || "/images/default-user.png"} />
                                    <h3 className="text-base font-semibold flex-1">{user.userFullName}</h3>
                                    <button onClick={() => handleAddInvitedUser(user)} className="btn-primary flex items-center">
                                        <Plus size={17} />
                                        <span className="text-sm">Mời</span>
                                    </button>
                                </div>
                            );
                        })}
                        {users && users.length === 0 && <Empty description={false}>
                            <p className="text-gray-400 font-semibold">Không có bạn bè nào phù hợp</p>
                        </Empty>}
                        {isLoading && Array(10).fill(null).map((_, index) => <BasicUserItemSkeleton rightButton key={index}/>)}
                    </div>
                </section>
                <section className="md:col-span-5 col-span-full bg-gray-50 md:h-full h-[300px] custom-scroll-no-hover overflow-y-auto md:m-0 m-1 rounded-md p-3">
                    <h3 className="uppercase font-semibold text-gray-400 mb-2">Đã chọn {invitedUsers.length} người bạn</h3>
                    {invitedUsers.map((user, index) => (
                        <div key={user.id}>
                            <div key={user.id} className="flex gap-x-3 cursor-pointer items-center p-2 rounded-md hover:bg-gray-100 transition-all">
                                <Avatar className="flex-shrink-0" size={50} src={user.profilePicture || "/images/default-user.png"} />
                                <h3 className="text-base font-semibold flex-1">{user.userFullName}</h3>
                                <button onClick={() => handleRemoveInvitedUser(index)} className="btn-secondary">
                                    <X size={17} />
                                </button>
                            </div>
                        </div>
                    ))}
                </section>
            </div>
            <div className="flex items-center gap-x-3 mt-5 justify-end bg-white">
                <button disabled={isInviting} onClick={handleInvites} className="btn-primary-lg">Gửi lời mời</button>
            </div>
        </div>
    );
};

export default GroupInviteModalContent;