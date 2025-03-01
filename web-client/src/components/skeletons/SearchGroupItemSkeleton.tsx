import { Skeleton } from "antd";
import { FC } from "react";

interface IProps { };

const SearchGroupItemSkeleton: FC<IProps> = ({ }) => {
    return <div className="rounded-md bg-white p-3 flex items-center gap-x-2">
        <Skeleton.Avatar active size={50} shape="square" />
        <div className="flex-1">
            <Skeleton.Node active children className="!w-[80%] !h-[16px]"/>
            <Skeleton.Node active children className="!w-[30%] !h-[14px]"/>
        </div>
       <Skeleton.Button active/>
    </div>
};

export default SearchGroupItemSkeleton;