import { Skeleton } from "antd";
import { FC } from "react";

interface IProps { };

const GroupItemSkeleton: FC<IProps> = ({ }) => {
    return (
        <div className="p-4 bg-white rounded-md">
            <div className="relative w-full">
                <div className="relative w-full h-[100px]">
                    <Skeleton.Node children active className="!w-full !h-full" />
                </div>
            </div>
            <div className="mt-10 flex flex-col gap-y-3 items-center">
                <div className="w-full flex justify-center items-center flex-col gap-y-2">
                    <Skeleton.Node active children className="!w-[80%] !h-[18px] !flex justify-center" />
                    <Skeleton.Node active children className="!w-[60%] !h-[13px] !flex justify-center" />
                    <Skeleton.Node active children className="!w-[50%] !h-[13px] !flex justify-center mt-1" />
                </div>
                <div className="flex items-center gap-x-1">
                    <Skeleton.Node active children className="!w-[60%] !h-[13px]" />
                </div>
                <div className="flex gap-x-3 mt-6 items-center justify-center">
                    <Skeleton.Button active />
                    <Skeleton.Button active />
                </div>
            </div>
        </div>
    )
};

export default GroupItemSkeleton;