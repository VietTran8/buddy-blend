import { Skeleton } from "antd";
import { FC } from "react";

interface IProps { };

const NotificationSkeleton: FC<IProps> = ({ }) => {
    return (
        <div className="bg-white hover:bg-gray-100 w-full hover:text-[#333] min-h-[68px] flex items-center px-2 py-10 gap-x-4 rounded-md transition-all cursor-pointer">
            <div className={`relative`}>
                <Skeleton.Avatar size={55} active />
            </div>
            <div className="flex-1 flex flex-col min-w-0 !text-[#333]">
                <Skeleton.Node active children style={{
                    height: "16px",
                    width: "100%"
                }}/>
                <Skeleton.Node active children style={{
                    height: "16px",
                    width: "80%",
                    marginTop: 4
                }}/>
                <Skeleton.Node active children style={{
                    height: "10px",
                    width: "30%",
                    marginTop: 10
                }}/>
            </div>
        </div>
    );
};

export default NotificationSkeleton;