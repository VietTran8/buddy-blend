import { Dot } from "lucide-react";
import { FC } from "react";
import { BasicUserItemSkeleton, GroupAdminItem, GroupMemberItem } from "../../components";
import { Link, useOutletContext } from "react-router-dom";
import { GroupPrivacy, OutletGroupContextType } from "../../types";
import { useQueryAdminMembers, useQueryAllMembers, useQueryFriendMembers, useQueryNewMembers } from "../../hooks";

interface IProps { };

const GroupMembers: FC<IProps> = ({ }) => {
    const { group } = useOutletContext<OutletGroupContextType>();
    const {
        data: newMemberData,
        isLoading: isNewMembersLoading,
        isFetchingNextPage: isFetchingNextNewMembersPage,
        fetchNextPage: fetchNextNewMembersPage,
        hasNextPage: hasNewMembersPage,
    } = useQueryNewMembers(group?.id);
    const {
        data: allMemberData,
        isLoading: isAllMembersLoading,
        isFetchingNextPage: isFetchingNextAllMembersPage,
        fetchNextPage: fetchNextAllMembersPage,
        hasNextPage: hasAllMembersPage,
    } = useQueryAllMembers(group?.id);
    const {
        data: adminMemberData,
        isLoading: isAdminMembersLoading,
        isFetchingNextPage: isFetchingNextAdminMembersPage,
        fetchNextPage: fetchNextAdminMembersPage,
        hasNextPage: hasAdminMembersPage,
    } = useQueryAdminMembers(group?.id);
    const {
        data: friendMemberData,
        isLoading: isFriendMembersLoading,
        isFetchingNextPage: isFetchingNextFriendMembersPage,
        fetchNextPage: fetchNextFriendMembersPage,
        hasNextPage: hasFriendMembersPage,
    } = useQueryFriendMembers(group?.id);

    const newMembersPages = newMemberData?.pages;
    const adminMembersPages = adminMemberData?.pages;
    const friendMembersPages = friendMemberData?.pages;
    const allMembersPages = allMemberData?.pages;

    return (
        <>
            {(group?.privacy === GroupPrivacy.PRIVACY_PRIVATE && group.admin) && <div className="rounded-md flex bg-white w-full p-5">
                <h1 className="flex-1 font-semibold text-base flex gap-x-2 items-center">
                    <span>Thành viên chờ phê duyệt</span>
                    <span className="w-6 h-6 rounded-full flex items-center justify-center bg-red-500">
                        <span className="text-sm font-semibold text-white">{group.pendingMemberCount}</span>
                    </span>
                </h1>
                <Link to={`/group/${group.id}/moderate`}>
                    <button className="btn-primary">Phê duyệt</button>
                </Link>
            </div>}
            <div className="rounded-md w-full bg-white p-5">
                <h1 className="flex items-center">
                    <span className="font-semibold text-base">Thành viên</span>
                    <Dot size={20} className="text-gray-400" />
                    <span className="text-base font-semibold text-gray-500">{group?.memberCount}</span>
                </h1>
                <span className="text-gray-400">Người mới tham gia nhóm này sẽ hiển thị tại đây</span>
                {newMembersPages?.map((page, index) => <div key={index} className="flex flex-col gap-y-3">
                    {page.data.data.map((member, index) => <GroupMemberItem admin={!!group?.admin} groupId={group?.id} key={index} member={member} className="mt-3" />)}
                    {isNewMembersLoading && Array(5).fill(null).map((_, index) => <BasicUserItemSkeleton avatarSize={60} key={index} />)}
                    {isFetchingNextNewMembersPage && Array(5).fill(null).map((_, index) => <BasicUserItemSkeleton avatarSize={60} key={index} />)}
                </div>)}
                {hasNewMembersPage && <button onClick={() => fetchNextNewMembersPage()} className="w-full mt-3 btn-secondary">Xem thêm</button>}
                <hr className="my-5" />
                <h1 className="flex items-center">
                    <span className="font-semibold text-base">Quản trị viên</span>
                    <Dot size={20} className="text-gray-400" />
                    <span className="text-base font-semibold text-gray-500">{adminMembersPages && adminMembersPages[0].data.totalElements}</span>
                </h1>
                <div className="mt-2">
                    {adminMembersPages?.map((page, index) => <div key={index} className="flex flex-col gap-y-1">
                        {page.data.data.map((member, index) => <GroupAdminItem admin={!!group?.admin} groupId={group?.id} key={index} member={member} />)}
                        {isAdminMembersLoading && Array(5).fill(null).map((_, index) => <BasicUserItemSkeleton avatarSize={60} key={index} />)}
                        {isFetchingNextAdminMembersPage && Array(5).fill(null).map((_, index) => <BasicUserItemSkeleton avatarSize={60} key={index} />)}
                    </div>)}
                    {hasAdminMembersPage && <button onClick={() => fetchNextAdminMembersPage()} className="w-full mt-3 btn-secondary">Xem thêm</button>}
                </div>
                <hr className="my-5" />
                {friendMembersPages && friendMembersPages[0].data.totalElements !== 0 && <>
                    <h1 className="flex items-center">
                        <span className="font-semibold text-base">Bạn bè</span>
                        <Dot size={20} className="text-gray-400" />
                        <span className="text-base font-semibold text-gray-500">{friendMembersPages && friendMembersPages[0].data.totalElements}</span>
                    </h1>
                    <div className="mt-2">
                        {friendMembersPages?.map((page, index) => <div key={index} className="flex flex-col gap-y-1">
                            {page.data.data.map((member, index) => <GroupMemberItem admin={!!group?.admin} groupId={group?.id} key={index} member={member} />)}
                            {isFriendMembersLoading && Array(5).fill(null).map((_, index) => <BasicUserItemSkeleton avatarSize={60} key={index} />)}
                            {isFetchingNextFriendMembersPage && Array(5).fill(null).map((_, index) => <BasicUserItemSkeleton avatarSize={60} key={index} />)}
                        </div>)}
                        {hasFriendMembersPage && <button onClick={() => fetchNextFriendMembersPage()} className="w-full mt-3 btn-secondary">Xem thêm</button>}
                    </div>
                    <hr className="my-5" />
                </>}
                {group && (group?.privacy !== GroupPrivacy.PRIVACY_PRIVATE || group.joined) && <>
                    <h1 className="flex items-center">
                        <span className="font-semibold text-base">Tất cả</span>
                        <Dot size={20} className="text-gray-400" />
                        <span className="text-base font-semibold text-gray-500">{group.memberCount}</span>
                    </h1>
                    <div className="mt-2">
                        {allMembersPages?.map((page, index) => <div key={index} className="flex flex-col gap-y-1">
                            {page.data.data.map((member, index) => <GroupMemberItem admin={!!group.admin} groupId={group?.id} key={index} member={member} />)}
                            {isAllMembersLoading && Array(5).fill(null).map((_, index) => <BasicUserItemSkeleton avatarSize={60} key={index} />)}
                            {isFetchingNextAllMembersPage && Array(5).fill(null).map((_, index) => <BasicUserItemSkeleton avatarSize={60} key={index} />)}
                        </div>)}
                        {hasAllMembersPage && <button onClick={() => fetchNextAllMembersPage()} className="w-full mt-3 btn-secondary">Xem thêm</button>}
                    </div>
                </>}
            </div>
        </>
    );
};

export default GroupMembers;