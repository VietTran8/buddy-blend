import { Avatar, Skeleton } from "antd";
import { FC } from "react";

interface IProps { };

const GroupLayoutSkeleton: FC<IProps> = ({ }) => {
    return <>
        <div className="rounded-md w-full bg-white p-5">
            <Skeleton.Node
                children
                className="!w-full !h-[260px] mb-4"
                active
            >
            </Skeleton.Node>
            <div className="flex md:flex-row flex-col items-center gap-x-3 md:mt-0 -mt-14">
                <Skeleton.Node active children className="!w-[80px] !h-[80px] flex-shrink-0">
                </Skeleton.Node>
                <div className="flex-1">
                    <Skeleton.Input active className="!w-[50%] !h-[18px] md:self-start md:mt-0 mt-3"></Skeleton.Input>
                    <div className="flex items-center mt-1">
                        <Skeleton.Node children active className="md:!w-[30%] !w-[100px] md:mx-0 mx-auto md:mt-0 mt-3 !h-[14px]"></Skeleton.Node>
                    </div>
                </div>
                <div className="flex items-center gap-x-3 md:mt-0 mt-10">
                    <Skeleton.Button active />
                    <Skeleton.Button active />
                </div>
            </div>
            <div className="mt-5 flex md:flex-row flex-col items-center gap-x-3">
                <Avatar.Group size={32}>
                    {Array(5).fill(null).map((_, index) => <Skeleton.Avatar key={index} active />)}
                </Avatar.Group>
                <Skeleton.Input active className="!w-[30%] !h-[18px] md:ms-0 ms-3 md:mt-0 mt-2"></Skeleton.Input>
            </div>
        </div>
        <main className="mt-3">
            <div className="grid grid-cols-12 gap-x-3 pb-10">
                <section className="lg:col-span-8 col-span-full flex flex-col gap-y-3">
                </section>
                <section className="col-span-4 h-fit lg:block hidden bg-white p-4 rounded-md sticky top-24">
                    <Skeleton.Input active className="!w-[50%] !h-[18px] md:self-start md:mt-0 mt-3"></Skeleton.Input>
                    <div className="flex-col flex gap-y-3 mt-3">
                        <Skeleton.Input active className="!w-full !h-[14px] md:self-start md:mt-0 mt-3"></Skeleton.Input>
                        <Skeleton.Input active className="!w-full !h-[14px] md:self-start md:mt-0 mt-3"></Skeleton.Input>
                        <Skeleton.Input active className="!w-[87%] !h-[14px] md:self-start md:mt-0 mt-3"></Skeleton.Input>
                    </div>
                </section>
            </div>
        </main>
    </>
};

export default GroupLayoutSkeleton;