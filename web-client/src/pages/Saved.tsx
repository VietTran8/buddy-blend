import { FC } from "react";
import { SavedItem } from "../components";
import { useQuerySavedPosts } from "../hooks";
import { Empty, Skeleton } from "antd";
import { STORY_BACKGROUND } from "../constants";

interface IProps { };

const Saved: FC<IProps> = ({ }) => {
    const { data: response, isLoading } = useQuerySavedPosts();

    const posts = response?.data;

    return (
        <div className="pb-10">
            <div className="p-5 bg-white rounded-md">
                <h1 className="font-bold lg:text-lg text-base">Đã lưu</h1>
            </div>
            <div className="mt-4 flex flex-col gap-y-3">
                {posts?.map((post, index) => (<SavedItem thumbBackground={Object.values(STORY_BACKGROUND)[index]} key={post.id} post={post} />))}
                {!isLoading && posts && posts.length === 0 && <div className="py-40">
                    <Empty description>
                        <span className="text-gray-400 font-semibold">Chưa có bài viết nào được lưu...</span>
                    </Empty></div>}
                {isLoading && Array(10).fill(null).map((_, index) => (
                    <div key={index} className="p-3 bg-white rounded-md flex gap-x-4 w-full">
                        <Skeleton.Node children className="!w-[120px] !h-[120px]" active />
                        <div className="flex flex-col gap-y-3 w-full">
                            <div className="w-full">
                                <Skeleton.Node children className="!h-[16px] !w-full" active />
                                <Skeleton.Node children className="!h-[16px] !w-[80%]" active />
                            </div>
                            <div className="flex items-center gap-x-3">
                                <Skeleton.Avatar active />
                                <Skeleton.Node children className="!h-[14px] !w-[80px]" active />
                            </div>
                            <div className="flex gap-x-3 items-center">
                                <Skeleton.Button active />
                                <Skeleton.Button active />
                            </div>
                        </div>
                    </div>
                ))}

            </div>
        </div>
    );
};

export default Saved;