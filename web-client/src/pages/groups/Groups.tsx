import { Plus } from "lucide-react";
import { FC } from "react";
import { GroupItem } from "../../components";
import { Link } from "react-router-dom";
import { useQueryMyGroup } from "../../hooks";

interface IProps { };

const GroupPage: FC<IProps> = ({ }) => {
    const { data: groupResponse } = useQueryMyGroup();

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
                </div>
            </div>
        </>
    );
}

export default GroupPage;