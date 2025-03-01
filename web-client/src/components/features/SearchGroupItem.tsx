import { cn } from "@/lib/utils";
import { EJoinGroupStatus, Group, GroupPrivacy } from "@/types";
import { Avatar } from "antd";
import { Dot } from "lucide-react";
import { FC } from "react";
import { Link } from "react-router-dom";

interface IProps {
    group: Group;
};

const SearchGroupItem:FC<IProps> = ({ group }) => {
    return <div className="rounded-md bg-white p-3 flex items-center gap-x-3 hover:bg-gray-100 transition-all">
        <Avatar src={group.avatar || "/images/community2.jpg"} size={50} shape="square"/>
        <div className="flex-1">
            <Link to={`/group/${group.id}`} className="font-semibold line-clamp-1">{group.name}</Link>
            <div className="flex items-center gap-x-0.5 text-gray-400">
                <span className="text-sm">{group.privacy === GroupPrivacy.PRIVACY_PRIVATE ? "Riêng tư" : "Công khai"}</span>
                <Dot size={10}/>
                <span className="text-sm">{`${group.memberCount} thành viên`}</span>
            </div>
        </div>
        <Link to={`/group/${group.id}`} className={cn(
            group.joinStatus === EJoinGroupStatus.SUCCESS ? 'btn-success' : (
                group.joinStatus === EJoinGroupStatus.PENDING ? 'btn-secondary' : 'btn-primary'
            )
        )}>{group.joinStatus === EJoinGroupStatus.SUCCESS ? 'Đã tham gia' : (
            group.joinStatus === EJoinGroupStatus.PENDING ? 'Đang chờ phê duyệt...' : 'Tham gia'
        )}</Link>
    </div>
};

export default SearchGroupItem;