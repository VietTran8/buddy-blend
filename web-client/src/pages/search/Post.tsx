import { FC } from "react";
import { Post as ResultPost } from "../../components";
import { useOutletContext } from "react-router-dom";
import { SearchOutletContextType } from "@/layouts/SearchLayout";
import { Empty } from "antd";

interface IProps { };

const Post: FC<IProps> = ({ }) => {
    const { searchResults, isLoading } = useOutletContext<SearchOutletContextType>();

    return (
        <div className="">
            <h1 className="lg:text-lg text-base font-semibold md:mb-4 my-2 p-5 rounded-md bg-white">Bài viết</h1>
            {searchResults.posts.map((post) => (
                <ResultPost post={post} className="md:mt-4 mt-2" key={post.id} />
            ))}
            {!isLoading && searchResults.posts.length === 0 && <div className="rounded-md p-5 bg-white">
                <Empty description>
                    <span className="font-semibold text-gray-400">Không tìm thấy bài viết nào phù hợp...</span>
                </Empty>
            </div>}
            {isLoading && <p className="text-center my-3">Loading...</p>}
        </div>
    );
};

export default Post;