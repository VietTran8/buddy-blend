import { FC, useContext, useState } from "react";
import { Link } from "react-router-dom";
import { EReactionType, Post } from "../../types";
import { Avatar, Modal, Popover } from "antd";
import { getPrivacyDesc, getReactionDesc, getReactionIcon, getReactionTextColor, getTimeDiff, randomUUID } from "../../utils";
import { CornerDownLeft, Dot, MessageCircle, ThumbsUp } from "lucide-react";
import { Post as PostComponent, PostActionPopoverContent, ReactionPopoverContent, ReactionsModalContent, ShareModalContent, PostCommentModalContent, CommentCreator } from "..";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faEllipsis, faLock } from "@fortawesome/free-solid-svg-icons";
import { AuthContext } from "../../context";
import { useReaction } from "../../hooks";

interface IProps {
    className?: string;
    post?: Post;
    isDetails?: boolean,
    pageIndex?: number,
    postIndex?: number,
    detached?: boolean
};

const SharedPost: FC<IProps> = ({ className, post, isDetails = false, pageIndex, postIndex, detached = false }) => {
    const [openAction, setOpenAction] = useState(false);

    const [openCommentModal, setOpenCommentModal] = useState(false);
    const [openShareModal, setOpenShareModal] = useState(false);
    const [openReactionsModal, setOpenReactionsModal] = useState(false);
    const [openReactionPopover, setOpenReactionPopover] = useState(false);

    const { user } = useContext(AuthContext);

    const [currentNoComments, setCurrentNoComments] = useState<number>(post?.noComments || 0);

    const {
        currentReaction,
        currentTopReacts,
        handleOnReactionChange
    } = useReaction({
        setOpenReactionPopover,
        post,
        postIndex,
        pageIndex
    });

    const handleOpenActionChange = (newOpen: boolean) => {
        setOpenAction(newOpen);
    };

    const handleOpenCommentModal = () => {
        setOpenCommentModal(true);
    }

    const handleOpenShareModal = () => {
        setOpenShareModal(true);
    }

    const handleOpenReactionsModal = () => {
        setOpenReactionsModal(true);
    }

    const handleOnNewComment = () => {
        setCurrentNoComments(currentNoComments + 1);
    }

    const sharedPost = post;
    const { icon } = getPrivacyDesc(post?.privacy, " !text-gray-400 text-sm");

    return (
        <>
            <div className={`w-full border-zinc-100 border-2 rounded-md bg-white px-4 pt-4 pb-1 ${className}`}>
                <div className="flex gap-x-3 items-start">
                    <Link to={`/user/${sharedPost?.user.id}`}>
                        <Avatar className="flex-0" size={45} src={sharedPost?.user.profilePicture || "/images/default-user.png"} />
                    </Link>
                    <div className="flex-1">
                        <div>
                            <h1 className="font-semibold text-md inline">
                                <Link to={`/user/${sharedPost?.user.id}`}>
                                    <span className="text-base">{sharedPost?.user.userFullName}</span>
                                </Link>
                            </h1>
                            <span className="text-gray-500 text-base ms-1.5">{`đã chia sẻ một bài viết.`}</span>
                        </div>
                        <div className="flex items-center">
                            <p className="text-sm text-gray-500">{getTimeDiff(sharedPost?.createdAt)}</p>
                            <Dot className="text-gray-500" />
                            {icon}
                        </div>
                    </div>
                    {!post?.illegal && <Popover
                        content={<PostActionPopoverContent post={post} />}
                        title="Tùy chọn"
                        trigger="click"
                        open={openAction}
                        onOpenChange={handleOpenActionChange}
                    >
                        <FontAwesomeIcon className="cursor-pointer" icon={faEllipsis} />
                    </Popover>}
                </div>
                {sharedPost?.content && <p className="pb-1 pt-2 text-base text-justify">{sharedPost?.content}</p>}
                <div className="rounded border my-3">
                    {post?.illegal || !post?.sharedPost ? <div className="py-4 px-6 w-full rounded-md flex gap-x-6 items-center">
                        <FontAwesomeIcon icon={faLock} className="text-[30px] text-gray-500 flex-shrink-0" />
                        <div className="flex-1">
                            <p className="font-semibold text-gray-500">Nội dung này không thể hiển thị ở thời điểm hiện tại</p>
                            <span className="lg:text-sm text-xs text-gray-400">Lí do có thể bài viết này đang có quyền riêng tư không phù hợp với bạn hoặc ở trong một nhóm riêng tư nào đó.</span>
                        </div>
                    </div> : <PostComponent post={post?.sharedPost} children />}
                </div>
                {!detached && <>
                    <div className=" flex items-center">
                        <div className={`ms-1 flex-1 flex items-center text-gray-500 ${currentTopReacts.topReacts?.length === 0 && 'hidden'}`}>
                            <div onClick={handleOpenReactionsModal} className="flex cursor-pointer">
                                {currentTopReacts.topReacts?.map((react, index) => (
                                    <img key={randomUUID()} src={getReactionIcon(react.type)} className={`w-7 h-7 ${index !== 0 && '-ms-2'}`} />
                                ))}
                            </div>
                            <span onClick={handleOpenReactionsModal} className="cursor-pointer ms-1 select-none">{currentTopReacts.noReactions}</span>
                        </div>
                        <div className="flex text-gray-500 gap-x-3">
                            <span onClick={() => !isDetails && handleOpenCommentModal()} className={`cursor-pointer select-none ${currentNoComments === 0 && 'hidden'}`}>{currentNoComments} bình luận</span>
                            <span className={`select-none ${post?.noShared === 0 && 'hidden'}`}>{post?.noShared} chia sẻ</span>
                        </div>
                    </div>
                    <hr className="mt-3 mb-1" />
                    <div className="flex items-center justify-between">
                        <Popover open={openReactionPopover} trigger={"hover"} onOpenChange={(open) => setOpenReactionPopover(open)} content={<ReactionPopoverContent onReactionChange={handleOnReactionChange} />}>
                            <div onClick={() => handleOnReactionChange(currentReaction || EReactionType.LIKE)} className="flex justify-center items-center rounded cursor-pointer transition-all flex-1 gap-x-2 hover:bg-gray-100 p-3">
                                {currentReaction ? <img src={getReactionIcon(currentReaction)} alt="icon" className="w-6 h-6" /> : <ThumbsUp className="text-gray-600 md:text-base text-sm" size={20} />}
                                <span className={`${currentReaction ? getReactionTextColor(currentReaction) : 'text-gray-600'} font-medium select-none`}>{currentReaction ? getReactionDesc(currentReaction) : 'Thích'}</span>
                            </div>
                        </Popover>
                        <div onClick={() => !isDetails && handleOpenCommentModal()} className="flex justify-center items-center rounded cursor-pointer transition-all flex-1 gap-x-2 hover:bg-gray-100 p-3">
                            <MessageCircle className="text-gray-600" size={20} />
                            <span className="text-gray-600 font-medium select-none">Bình luận</span>
                        </div>
                        <div onClick={handleOpenShareModal} className="flex justify-center items-center rounded cursor-pointer transition-all flex-1 gap-x-2 hover:bg-gray-100 p-3">
                            <CornerDownLeft className="text-gray-600 md:text-base text-sm" size={20} />
                            <span className="text-gray-600 font-medium select-none">Chia sẻ</span>
                        </div>
                    </div>
                </>}
            </div>
            <Modal
                title={<h1 className="md:text-lg mb-5 text-base text-center font-semibold">Bình luận về bài viết của {sharedPost?.user.userFullName}</h1>}
                centered
                destroyOnClose
                open={openCommentModal}
                onCancel={() => setOpenCommentModal(false)}
                width={850}
                footer={<CommentCreator onCommented={handleOnNewComment} post={post!} placeholder={`Bình luận dưới tên ${user?.userFullName}...`} />}
            >
                <PostCommentModalContent onNewComment={handleOnNewComment} post={post!} />
            </Modal>
            <Modal
                title={<h1 className="md:text-lg mb-5 text-base text-center font-semibold">Chia sẻ</h1>}
                centered
                open={openShareModal}
                onCancel={() => setOpenShareModal(false)}
                width={650}
                footer
            >
                <ShareModalContent post={post} />
            </Modal>
            <Modal
                title={<h1 className="md:text-lg mb-5 text-base text-center font-semibold">Lượt thả cảm xúc</h1>}
                centered
                open={openReactionsModal}
                onCancel={() => setOpenReactionsModal(false)}
                width={650}
                footer
            >
                <ReactionsModalContent post={post} />
            </Modal>
        </>
    )
};

export default SharedPost;