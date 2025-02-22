import { FC } from "react";
import Comment from "../Comment";
import { useQueryPostComment } from "../../../hooks";
import { Post } from "../../../types";
import { Empty, Typography } from "antd";
import CommentCreator from "../CommentCreator";
import { removeExtraSpaces } from "@/utils";

interface IProps {
    post: Post,
    isModal?: boolean,
    onNewComment?: () => void
};

const PostCommentModalContent: FC<IProps> = ({ post, isModal = true, onNewComment }) => {
    const { data, isLoading } = useQueryPostComment(post.id);

    return (
        <div className={`${isModal ? 'h-[70vh]' : 'px-2'} py-4 w-full overflow-y-auto no-scrollbar flex flex-col gap-y-3`}>
            {!isModal && <>
                <CommentCreator onCommented={onNewComment} placeholder={`Bình luận về bài viết của ${removeExtraSpaces(post.user.userFullName)}...`} post={post} />
                <hr className="my-3"/>
                <h2 className="font-medium text-base text-gray-400">{`Bình luận về bài viết của ${removeExtraSpaces(post.user.userFullName)} (${data ? data.data.length : 0}):`}</h2>
            </>}
            {!data || data.data.length == 0 && <Empty className="my-auto" description={
                <Typography.Text className="text-gray-400">Chưa có bình luận nào...</Typography.Text>
            } />}
            {isLoading ? <p className="py-10 text-center">Đang tải bình luận...</p> : data?.data.map((comment) => (
                <Comment onCommentReplied={onNewComment} key={comment.id} comment={comment} post={post} />
            ))}
        </div>
    );
};

export default PostCommentModalContent;