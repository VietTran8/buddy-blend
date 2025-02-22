import { Divider, Skeleton } from "antd";
import { FC } from "react";
import { profileNavItems } from "../../constants";

interface IProps { };

const ProfileLayoutSkeleton: FC<IProps> = ({ }) => {
    return <div className="container">
        <div className="bg-white rounded-lg p-5 w-full">
            <Skeleton.Node children active className="!w-full !h-[270px]" />
            <div className="flex items-center mt-3 gap-x-3">
                <Skeleton.Avatar size={115} active />
                <div className="flex-1 md:self-center self-start">
                    <Skeleton.Input active className="!w-[50%] !h-[20px]"></Skeleton.Input>
                    <div className="mt-1">
                        <Skeleton.Input active className="!w-[14px] !h-[16px]"></Skeleton.Input>
                    </div>
                </div>
                <div className="md:block hidden self-start">
                    <Skeleton.Button size="large" active />
                </div>
                <Skeleton.Button size="large" active className="self-start"></Skeleton.Button>
            </div>
            <p className="mt-8" />
            <div className="flex gap-x-3 items-center mt-4">
                {profileNavItems.map((_, index) => (
                    <div key={index} className="flex gap-x-3 items-center">
                        <Skeleton.Node children active className="!w-[70px] !h-[16px]"></Skeleton.Node>
                        <Divider type="vertical" />
                    </div>
                ))}
            </div>
        </div>
    </div>
};

export default ProfileLayoutSkeleton;