import { FC } from "react";
import Post from "./Post";
import { useQueryNewsFeed } from "../../hooks";
import SharedPost from "./SharedPost";
import { EPostType } from "../../types";
import { randomUUID } from "../../utils";
import PostSkeleton from "../skeletons/PostSkeleton";

interface IProps { }

const NewsFeed: FC<IProps> = () => {
    const { data, isLoading, isFetchingNextPage } = useQueryNewsFeed();

    return (
        <div className="mt-3 flex flex-col gap-y-3 pb-3">
            {data?.pages.map((page, pageIndex) => (
                <div key={randomUUID()} className="flex flex-col gap-y-3">
                    {page.data.data.map((post, postIndex) => (
                        <div key={randomUUID()} className="">
                            {post.type === EPostType.SHARE ? <SharedPost
                                postIndex={postIndex}
                                pageIndex={pageIndex}
                                post={post}
                            /> : <Post
                                postIndex={postIndex}
                                pageIndex={pageIndex}
                                post={post}
                            />}
                        </div>
                    ))}
                </div>
            ))}
            {isLoading && Array(5).fill(null).map((_, index) => <PostSkeleton key={index} />)}
            {isFetchingNextPage && <>
                <PostSkeleton />
                <PostSkeleton />
            </>}
        </div>
    );
};

export default NewsFeed;
