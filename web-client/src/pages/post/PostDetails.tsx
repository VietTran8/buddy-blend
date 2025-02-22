import { FC } from "react";
import { Post, PostCommentModalContent, SharedPost, NotFound, PostSkeleton } from "../../components";
import { useQueryPost } from "../../hooks";
import { useParams } from "react-router-dom";
import { EPostType } from "../../types";

interface IProps {
};

const PostDetailsPage: FC<IProps> = ({ }) => {
    const { id } = useParams();

    const { data: postResponse, isLoading, isError } = useQueryPost(id);

    const fetchedPost = postResponse?.data;

    return (
        <div className="container">
            {fetchedPost && <>
                {fetchedPost.type === EPostType.SHARE ? <SharedPost className="max-w-[650px] mx-auto mt-3" post={fetchedPost} /> : <Post className="mt-3 mx-auto max-w-[650px]" post={fetchedPost} />}
                <div className="bg-white rounded mt-3 h-fit px-3 max-w-[650px] mb-10 mx-auto">
                    <PostCommentModalContent isModal={false} post={fetchedPost!} />
                </div>
            </>}
            {isLoading && <PostSkeleton className="max-w-[650px] mx-auto mt-3" />}
            {!isLoading && isError && <NotFound />}
        </div>
    );
};

export default PostDetailsPage;