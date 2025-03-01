import { SearchGroupItem, SearchGroupItemSkeleton } from "@/components";
import { SearchOutletContextType } from "@/layouts/SearchLayout";
import { Empty } from "antd";
import { FC } from "react";
import { useOutletContext } from "react-router-dom";

interface IProps { };

const Group: FC<IProps> = ({ }) => {
    const { searchResults, isLoading } = useOutletContext<SearchOutletContextType>();

    return <div className="">
        <h1 className="lg:text-lg text-base font-semibold md:mb-4 my-2 p-5 bg-white rounded-md">Nhóm</h1>
        <div className="">
            {searchResults.groups.map((group) => (
                <SearchGroupItem group={group} key={group.id} />
            ))}
            {!isLoading && searchResults.users.length === 0 && <Empty description>
                <span className="font-semibold text-gray-400">Không tìm thấy nhóm nào phù hợp...</span>
            </Empty>}
            {isLoading && Array(5).fill(null).map((_, index) => (
                <SearchGroupItemSkeleton key={index} />
            ))}
        </div>
    </div>
};

export default Group;