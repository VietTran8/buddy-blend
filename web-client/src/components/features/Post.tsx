import { faEllipsis, faPeopleGroup } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { Avatar, Modal, Popover } from "antd";
import { CornerDownLeft, Dot, Link2, MessageCircle, ThumbsUp } from "lucide-react";
import { FC, useContext, useState } from "react";
import { Link } from "react-router-dom";
import ReactionPopoverContent from "./popovers/ReactionPopoverContent";
import PostGallery from "./PostGallery";
import PostActionPopoverContent from "./popovers/PostActionPopoverContent";
import PostCommentModalContent from "./modals/PostCommentModalContent";
import CommentCreator from "./CommentCreator";
import ShareModalContent from "./modals/ShareModalContent";
import ReactionsModalContent from "./modals/ReactionsModalContent";
import { EPostType, EReactionType, Post as PostData } from "../../types";
import { countCharacters, getPrivacyDesc, getReactionDesc, getReactionIcon, getReactionTextColor, getTimeDiff, randomUUID, removeExtraSpaces, renderTaggingTitle } from "../../utils";
import { useReaction } from "../../hooks";
import { AuthContext } from "../../context";
import { cn } from "@/lib/utils";
import { STORY_BACKGROUND } from "@/constants";

interface IProps {
    className?: string;
    post?: PostData;
    children?: boolean;
    isDetails?: boolean;
    insideGroup?: boolean;
    isPrivateGroupPost?: boolean;
    isFavouritePost?: boolean;
    collectionName?: string;
    pageIndex?: number;
    postIndex?: number;
    detached?: boolean;
};

const Post: FC<IProps> = ({
    className,
    post,
    collectionName,
    isFavouritePost = false,
    children = false,
    isDetails = false,
    insideGroup = false,
    isPrivateGroupPost = false,
    pageIndex,
    postIndex,
    detached = false,
}) => {
    const [openAction, setOpenAction] = useState(false);
    const [openCommentModal, setOpenCommentModal] = useState(false);
    const [openShareModal, setOpenShareModal] = useState(false);
    const [openReactionsModal, setOpenReactionsModal] = useState(false);
    const [openReactionPopover, setOpenReactionPopover] = useState(false);

    const { user } = useContext(AuthContext);

    const [currentNoComments, setCurrentNoComments] = useState<number>(post?.noComments || 0);
    const { currentReaction, currentTopReacts, handleOnReactionChange } = useReaction({
        setOpenReactionPopover,
        post,
        pageIndex,
        postIndex
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

    const { icon } = getPrivacyDesc(post?.privacy, "text-gray-400 text-sm");

    return (
        <>
            <div className={`w-full ${!children && 'border-2 border-zinc-100'} rounded-lg bg-white pt-4 pb-1 ${className}`}>
                {(post?.type === EPostType.GROUP || post?.groupInfo) && !insideGroup ? <div>
                    <div className="flex gap-x-2  px-4 ">
                        <div className="relative h-fit w-fit mb-2">
                            <img src={post.groupInfo.avatar || "/images/community2.jpg"} alt="group avatar" className="w-10 h-10 object-cover rounded" />
                            <img src={post.user?.profilePicture || "/images/default-user.png"} alt="user avatar" className="rounded-full w-8 h-8 object-cover absolute -bottom-2 -right-2 border-white border-2" />
                        </div>
                        <div className="flex-1">
                            <Link to={`/group/${post.groupInfo.id}`}>
                                <h1 className="text-base font-semibold">{post.groupInfo.name}</h1>
                            </Link>
                            <div className="ms-2 flex items-center">
                                <Link to={`/user/${post.user?.id}`}>
                                    <h2 className="text-sm font-medium text-gray-400">{post.user?.userFullName}</h2>
                                </Link>
                                <Dot className="text-gray-500" />
                                <div className="flex items-center">
                                    <p className="text-sm text-gray-500">{getTimeDiff(post?.createdAt)}</p>
                                    <Dot className="text-gray-500" />
                                    {<FontAwesomeIcon icon={faPeopleGroup} className="text-gray-400 text-sm" />}
                                </div>
                            </div>
                        </div>
                        {(!children) && <Popover
                            content={<PostActionPopoverContent isFavouritePost={isFavouritePost} collectionName={collectionName} post={post} />}
                            title="Tùy chọn"
                            trigger="click"
                            open={openAction}
                            onOpenChange={handleOpenActionChange}
                        >
                            <FontAwesomeIcon className="cursor-pointer" icon={faEllipsis} />
                        </Popover>}
                    </div>
                </div> : <div className="flex gap-x-3 items-start px-4 ">
                    <Link to={`/user/${post?.user.id}`}>
                        <Avatar className="flex-0" size={45} src={post?.user?.profilePicture || "/images/default-user.png"} />
                    </Link>
                    <div className="flex-1">
                        <h2 className="font-semibold text-md">
                            <span>
                                <Link to={`/user/${post?.user?.id}`}>
                                    <span className="text-base">{post?.user?.userFullName}</span>
                                </Link>
                                {post && post?.taggedUsers.length > 0 && <span>
                                    <span className="font-normal"> cùng với </span>
                                    {renderTaggingTitle(post?.taggedUsers)}
                                </span>}
                            </span>
                        </h2>
                        <div className="flex items-center">
                            <p className="text-sm text-gray-500">{getTimeDiff(post?.createdAt)}</p>
                            <Dot className="text-gray-500" />
                            {icon}
                        </div>
                    </div>
                    {(!children && !detached) && <Popover
                        content={<PostActionPopoverContent isFavouritePost={isFavouritePost} collectionName={collectionName} post={post} />}
                        title="Tùy chọn"
                        trigger="click"
                        open={openAction}
                        onOpenChange={handleOpenActionChange}
                    >
                        <FontAwesomeIcon className="cursor-pointer" icon={faEllipsis} />
                    </Popover>}
                </div>}
                {post && (post.background && [...post?.medias].length === 0) ?
                    <div className={cn(STORY_BACKGROUND[post.background], "w-full min-h-[300px] p-5 mb-3 mt-2 flex items-center justify-center md:font-bold font-semibold text-gray-50")}>
                        <p className={cn(countCharacters(post.content) > 100 ? "md:text-lg text-md" : "md:text-2xl text-lg", "text-center whitespace-pre-line")}>{post.content}</p>
                    </div> :
                    post?.content &&
                    <p
                        className={`px-4 ${post?.medias.length === 0 ? 'pb-5 pt-2' : 'pb-1 pt-2'} text-base text-justify whitespace-pre-line`}>
                        {post?.content}
                    </p>
                }
                {(post?.medias && post.medias.length > 0) && <div className="w-full h-fit relative">
                    <PostGallery postId={post.id} medias={post.medias} className="my-3" />
                </div>}
                {(!children && !detached) && <>
                    <div className="px-4 flex items-center">
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
                    <div className="px-4">
                        <hr className="mt-3 mb-1" />
                    </div>
                    <div className="flex items-center px-4 justify-between">
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
                        {isPrivateGroupPost ?
                            <div className="flex justify-center items-center rounded cursor-pointer transition-all flex-1 gap-x-2 hover:bg-gray-100 p-3">
                                <Link2 className="text-gray-600 md:text-base text-sm" size={20} />
                                <span className="text-gray-600 font-medium select-none">Sao chép</span>
                            </div>
                            : <div onClick={handleOpenShareModal} className="flex justify-center items-center rounded cursor-pointer transition-all flex-1 gap-x-2 hover:bg-gray-100 p-3">
                                <CornerDownLeft className="text-gray-600 md:text-base text-sm" size={20} />
                                <span className="text-gray-600 font-medium select-none">Chia sẻ</span>
                            </div>
                        }
                    </div>
                </>}
            </div >
            <Modal
                title={<h1 className="md:text-lg mb-5 text-base text-center font-semibold">Bình luận về bài viết của {post?.user.userFullName}</h1>}
                centered
                destroyOnClose
                open={openCommentModal}
                onCancel={() => setOpenCommentModal(false)}
                width={850}
                footer={<CommentCreator onCommented={handleOnNewComment} post={post!} placeholder={`Bình luận dưới tên ${removeExtraSpaces(user?.userFullName)}...`} />}
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
    );
};

export default Post;