import { Skeleton } from "antd";
import { FC } from "react";

interface IProps {
    rightButton?: boolean,
    avatarShape?: "square" | "circle",
    avatarSize?: 'large' | 'small' | 'default' | number
};

const BasicUserItemSkeleton: FC<IProps> = ({ rightButton, avatarShape = "circle", avatarSize = "large" }) => {
    return (
        <div className="flex items-center rounded-md gap-x-2 p-2 mt-2 bg-white hover:bg-gray-100 transition-all cursor-pointer">
            <Skeleton.Avatar shape={avatarShape} size={avatarSize} active />
            <Skeleton.Node active children style={{
                width: 130,
                height: 13
            }} />
            {rightButton && <Skeleton.Button active className="ms-auto"/>}
        </div>
    );
};

export default BasicUserItemSkeleton;