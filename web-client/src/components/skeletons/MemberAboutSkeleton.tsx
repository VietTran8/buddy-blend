import { Avatar, Skeleton } from "antd";
import { FC } from "react";

interface IProps { };

const MemberAboutSkeleton: FC<IProps> = ({ }) => {
    return <div className="flex flex-col gap-y-2">
        <Avatar.Group>
            <Skeleton.Avatar active />
            <Skeleton.Avatar active />
            <Skeleton.Avatar active />
            <Skeleton.Avatar active />
            <Skeleton.Avatar active />
        </Avatar.Group>
        <Skeleton.Node active children className="!w-[200px] !h-[16px]" />
    </div>
};

export default MemberAboutSkeleton;