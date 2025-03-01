import { FC } from "react";
import { Post, PostSkeleton, SearchGroupItem, SearchGroupItemSkeleton, SearchPeopleItem, SearchUserItemSkeleton } from "../../components";
import { useOutletContext } from "react-router-dom";
import { SearchOutletContextType } from "@/layouts/SearchLayout";
import { Empty } from "antd";

interface IProps { };

const Result: FC<IProps> = ({ }) => {
    const { searchResults, isLoading } = useOutletContext<SearchOutletContextType>();

    return (
        <>
            {!isLoading && searchResults.users.length > 0 && <div className="rounded-md bg-white p-5 mt-2">
                <h1 className="lg:text-lg text-base font-semibold mb-4">Mọi người</h1>
                {searchResults.users.map((user) => (
                    <SearchPeopleItem user={user} key={user.id} />
                ))}
            </div>}
            {!isLoading && searchResults.posts.length > 0 && <div className="rounded-md bg-white p-5 mt-4">
                <h1 className="lg:text-lg text-base font-semibold mb-4">Bài viết</h1>
                <div className="flex flex-col gap-y-3">
                    {searchResults.posts.map((post) => (
                        <Post key={post.id} post={post} />
                    ))}
                </div>
            </div>}
            {!isLoading && searchResults.groups.length > 0 && <div className="rounded-md bg-white p-5 mt-4">
                <h1 className="lg:text-lg text-base font-semibold mb-4">Nhóm</h1>
                <div className="flex flex-col gap-y-3">
                    {searchResults.groups.map((group) => (
                        <SearchGroupItem key={group.id} group={group} />
                    ))}
                </div>
            </div>}
            {!isLoading && (searchResults.posts.length + searchResults.users.length + searchResults.groups.length) === 0 && <div className="bg-white rounded-md p-5 mt-2">
                <Empty description>
                    <span className="font-semibold text-gray-400">Không tìm thấy kết quả phù hợp...</span>
                </Empty>
            </div>}
            {isLoading && <>
                <div className="rounded-md bg-white p-5 mt-2">
                    {Array(5).fill(null).map((_, index) => (
                        <SearchUserItemSkeleton key={index} />
                    ))}
                </div>
                <div className="rounded-md bg-white p-5 mt-2 flex flex-col gap-y-2">
                    {Array(5).fill(null).map((_, index) => (
                        <PostSkeleton key={index} />
                    ))}
                </div>
                <div className="rounded-md bg-white p-5 mt-2 flex flex-col gap-y-2">
                    {Array(5).fill(null).map((_, index) => (
                        <SearchGroupItemSkeleton key={index} />
                    ))}
                </div>
            </>}
        </>
    )
};

export default Result;