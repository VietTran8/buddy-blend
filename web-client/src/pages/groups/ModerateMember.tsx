import { FC, useEffect } from "react";
import { GroupMemberItem } from "../../components";
import { useNavigate, useOutletContext } from "react-router-dom";
import { EMemberAcceptation, OutletGroupContextType } from "../../types";
import { useModerateMember, useQueryPendingMembers } from "../../hooks";
import { Empty } from "antd";

interface IProps { };

const ModerateMemberPage: FC<IProps> = ({ }) => {
    const { group } = useOutletContext<OutletGroupContextType>();
    const navigate = useNavigate();

    const { data: pendingMemberResposne } = useQueryPendingMembers(group?.id);
    const { mutate: moderate } = useModerateMember();

    useEffect(() => {
        if (!group?.admin)
            navigate(`/group/${group?.id}/members`);

    }, [group]);

    const pendingMembers = pendingMemberResposne?.data;

    const handleOnModerate = (memberId: string, accept: EMemberAcceptation) => {
        moderate({
            acceptOption: accept,
            groupId: group?.id || "",
            memberId,
            reason: accept === EMemberAcceptation.AGREE ? "Đồng ý" : "Từ chối"
        });
    }

    return <div className="bg-white rounded-md p-5">
        <h1 className="font-semibold text-base flex gap-x-2 items-center">
            <span>Thành viên chờ phê duyệt</span>
            <span className="w-6 h-6 rounded-full flex items-center justify-center bg-red-500">
                <span className="text-sm font-semibold text-white">{pendingMembers?.length || 0}</span>
            </span>
        </h1>
        {pendingMembers && pendingMembers.length > 0 ? <div className="mt-5">
            {pendingMembers.map((member, index) => (
                <GroupMemberItem admin={false} key={index} member={member} isModerate onModerate={handleOnModerate}/>
            ))}
        </div> : <Empty className="my-10" description={false}>
                <p className="font-semibold text-gray-400">Không có thành viên nào.</p>
            </Empty>}
    </div>
};

export default ModerateMemberPage;