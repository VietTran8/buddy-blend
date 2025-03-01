import { Avatar, Skeleton } from "antd";
import { FC } from "react";

interface IProps { };

const SearchUserItemSkeleton: FC<IProps> = ({ }) => {
    return <div className="flex p-3 rounded-md bg-white transition-all gap-x-3 items-center w-full cursor-pointer hover:bg-gray-100">
        <Skeleton.Avatar className="self-start" size={70} active />
        <div className="flex-1">
            <Skeleton.Node active style={{ width: "250px", height: "18px" }} children/>
            <div className="flex items-center gap-x-1 mt-2">
                <Avatar.Group>
                    {Array(5).fill(null).map((_, index) => (
                        <Skeleton.Avatar active size={"small"} key={index} />
                    ))}
                </Avatar.Group>
                <Skeleton.Node active style={{ width: "100px", height: "13px" }} children />
            </div>
        </div>
        <Skeleton.Button active style={{ width: 100 }} />
    </div>
};

export default SearchUserItemSkeleton;