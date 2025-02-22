import { Skeleton } from "antd";
import { FC } from "react";

interface IProps {
    className?: string;
};

const PostSkeleton: FC<IProps> = ({ className }) => {
    return <div className={`w-full rounded-md bg-white p-4 ${className}`}>
        <div className="flex gap-x-2 w-full items-center">
            <Skeleton.Avatar shape="circle" active size={50} />
            <div className="flex-1">
                <Skeleton.Button active block style={{ width: "150px", height: "14px" }} size="small" />
                <Skeleton.Input active block style={{ width: "100px", height: "14px" }} size="small" />
            </div>
        </div>
        <div className="my-32"></div>
        <div className="mt-3 flex justify-around gap-y-1 w-full">
            <Skeleton.Button active block className="px-4" style={{ height: "14px" }} size="small" />
            <Skeleton.Button active block className="px-4" style={{ height: "14px" }} size="small" />
            <Skeleton.Button active block className="px-4" style={{ height: "14px" }} size="small" />
        </div>
    </div>
};

export default PostSkeleton;