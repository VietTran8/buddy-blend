import { Plus } from "lucide-react";
import { FC } from "react";
import { GroupItem, GroupItemSkeleton } from "../../components";
import { Link } from "react-router-dom";
import { useQueryMyGroup } from "../../hooks";
import { Empty } from "antd";

interface IProps { };

const GroupPage: FC<IProps> = ({ }) => {
    const { data: groupResponse, isLoading } = useQueryMyGroup();

    const groups = groupResponse?.data;

    return (
        <>
            <div className="pb-10">
                <div className="p-5 bg-white rounded-md flex items-center justify-between">
                    <h1 className="font-bold lg:text-lg text-base">Nhóm của bạn</h1>
                    <Link to="create">
                        <button className="flex gap-x-2 items-center btn-primary">
                            <Plus size={18} />
                            <span>Tạo nhóm</span>
                        </button>
                    </Link>
                </div>
                <div className="grid grid-cols-3 gap-3 mt-3">
                    {groups?.map((group, index) => (<GroupItem group={group} key={index} />))}
                    {isLoading && Array(10).fill(null).map((_, index) => <GroupItemSkeleton key={index} />)}
                </div>
                {groups?.length === 0 && <Empty description className="py-24">
                    <p className="font-semibold text-gray-300">Bạn chưa tham gia nhóm nào, hãy tạo nhóm nhé!</p>
                </Empty>}
            </div>
        </>
    );
}

export default GroupPage;