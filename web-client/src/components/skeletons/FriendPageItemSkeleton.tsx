import { Avatar, Skeleton } from "antd";
import { FC } from "react";

interface IProps { };

const FriendPageItemSkeleton: FC<IProps> = ({ }) => {
    return (
        <div className="rounded-md bg-white p-5 w-full">
            <div className="w-full h-[185px]">
                <Skeleton.Node children active className="!w-full !h-[185px]"/>
            </div>
            <Skeleton.Node children active className="mt-3 !w-full !h-[16px] mb-3"/>
            <div className="flex gap-x-1 items-center justify-center mt-2">
                <Avatar.Group size="small">
                    {Array(3).fill(null).map((_, index) => (
                        <Skeleton.Avatar key={index} active />
                    ))}
                </Avatar.Group>
                <Skeleton.Node children active className="" style={{
                    width: 60,
                    height: 13
                }} />
            </div>
            <div className="flex gap-x-3 justify-center mt-4">
                <Skeleton.Button active />
                <Skeleton.Button active />
            </div>
        </div>
    );
};

export default FriendPageItemSkeleton;