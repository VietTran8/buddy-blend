import { FC } from "react";
import { History, Users, Earth, Dot, Lock } from "lucide-react";
import { Avatar } from "antd";
import { Link, useOutletContext, useParams } from "react-router-dom";
import { GroupPrivacy, OutletGroupContextType } from "../../types";
import { useQueryAdminMembers } from "../../hooks";
import { MemberAboutSkeleton } from "@/components";

interface IProps { };

const GroupAbout: FC<IProps> = ({ }) => {
    const { id } = useParams();
    const { group } = useOutletContext<OutletGroupContextType>();
    const {
        data: adminMemberData,
        isLoading: isAdminMembersLoading,
    } = useQueryAdminMembers(group?.id);

    const firstAdminMembers = adminMemberData?.pages[0].data.data;

    return (
        <div className="flex flex-col gap-y-3">
            <div className="p-4 rounded-md bg-white">
                <h3 className="font-semibold text-base mt-2 text-gray-500">Giới thiệu về nhóm</h3>
                <p className="mt-1">{group?.description}</p>
                <div className="mt-4 flex flex-col gap-y-1">
                    <div className="flex items-center gap-x-2 font-semibold text-gray-500">
                        <History size={20} />
                        <span>{`Được tạo lúc: ${group?.createdAt}`}</span>
                    </div>
                    <div className="flex items-center gap-x-2 font-semibold text-gray-500">
                        <Users size={20} />
                        <span>{`${group?.memberCount} thành viên`}</span>
                    </div>
                    <div className="flex items-center gap-x-2 font-semibold text-gray-500">
                        {group?.privacy && group.privacy === GroupPrivacy.PRIVACY_PUBLIC ? <Earth size={20} /> : <Lock size={20} />}
                        <span>{group?.privacy && group.privacy === GroupPrivacy.PRIVACY_PUBLIC ? 'Nhóm công khai' : 'Nhóm riêng tư'}</span>
                    </div>
                </div>
            </div>
            <div className="p-4 rounded-md bg-white">
                <h3 className="font-semibold text-base text-gray-500 flex items-center">
                    <span className="text-base font-semibold">Thành viên</span>
                    <Dot size={20} className="text-gray-400" />
                    <span className="font-semibold text-base text-gray-400">{group?.memberCount}</span>
                </h3>
                <hr className="my-4" />
                {group && group?.memberCount > 2 && <div className="mb-3">
                    <div className="flex items-center mb-1">
                        {group.firstTenMembers.map((member, index) => (
                            <Avatar key={index} src={member.user.profilePicture || "/images/default-user.png"} />
                        ))}
                    </div>
                    <span className="font-medium">{`${group.firstTenMembers[0].user.lastName}, ${group.firstTenMembers[1].user.lastName} và ${group.memberCount - 2} người khác đã tham gia.`}</span>
                </div>}
                <div className="mb-3">
                    <div className="flex items-center mb-1">
                        {firstAdminMembers && firstAdminMembers.map((admin, index) => (
                            <Avatar key={index} src={admin.user.profilePicture || "/images/default-user.png"} />
                        ))}
                    </div>
                    <span className="font-medium">{
                        firstAdminMembers && firstAdminMembers?.length > 2 ? `${firstAdminMembers[0].user.lastName}, ${firstAdminMembers[1].user.lastName} và ${firstAdminMembers.length - 2} người khác là quản trị viên` :
                            `${firstAdminMembers?.map(member => member.user.lastName).join(', ')} là quản trị viên`
                    }</span>
                    {isAdminMembersLoading && <MemberAboutSkeleton />}
                </div>
                <Link to={`/group/${id}/members`}>
                    <button className="md:btn-secondary-lg btn-secondary w-full">Xem tất cả</button>
                </Link>
            </div>
        </div>
    );
};

export default GroupAbout;