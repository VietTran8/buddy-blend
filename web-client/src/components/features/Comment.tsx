import { faEllipsis } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { Avatar, Modal, Popover, Image } from "antd";
import { FC, useState } from "react";
import ReactionPopoverContent from "./popovers/ReactionPopoverContent";
import CommentCreator from "./CommentCreator";
import { Link } from "react-router-dom";
import ReactionsModalContent from "./modals/ReactionsModalContent";
import { Comment as CommentType, DoCmtReactRequest, EReactionType, Post, TopReact } from "../../types";
import { getReactionDesc, getReactionIcon, getReactionTextColor, getTimeDiff, randomUUID, removeExtraSpaces } from "../../utils";
import toast from "react-hot-toast";
import { useDoCmtReact } from "../../hooks";

interface IProps {
    className?: string;
    comment: CommentType;
    post: Post;
    children?: boolean;
    onChildOpenReplyInput?: (replyComment: CommentType) => void;
    onCommentReplied?: () => void;
};

const Comment: FC<IProps> = ({ className, comment, post, children = false, onChildOpenReplyInput, onCommentReplied }) => {
    const [openReplies, setOpenReplies] = useState<boolean>(false);
    const [openReplyInput, setOpenReplyInput] = useState(false);
    const [currentReplyCmt, setCurrentReplyCmt] = useState<CommentType>();
    const [openReactionsModal, setOpenReactionsModal] = useState(false);
    const [openReactionPopover, setOpenReactionPopover] = useState(false);

    const [currentTopReacts, setCurrentTopReacts] = useState<{
        topReacts?: TopReact[],
        noReactions?: number;
    }>({ topReacts: comment?.topReacts, noReactions: comment?.noReactions });
    const [currentReaction, setCurrentReaction] = useState<EReactionType | undefined>(comment.reacted);

    const { mutate: doReact } = useDoCmtReact();

    const handleOpenReplies = () => {
        setOpenReplies(true);
    }

    const toggleReplyInput = (replyCmt: CommentType) => {
        if (!children) {
            setOpenReplyInput(!openReplyInput);
            setCurrentReplyCmt(replyCmt);
        } else {
            onChildOpenReplyInput && onChildOpenReplyInput(replyCmt);
        }
    }

    const handleOpenReactionsModal = () => {
        setOpenReactionsModal(true);
    }

    const handleChildOpenReplyInput = (replyComment: CommentType) => {
        setOpenReplyInput(true);
        setCurrentReplyCmt(replyComment);
    }

    const handleOnReactionChange = (type: EReactionType) => {
        setCurrentReaction(type === currentReaction ? undefined : type);

        const doReactPayload: DoCmtReactRequest = {
            cmtId: comment?.id || "",
            type
        }

        doReact({
            payload: doReactPayload,
            postId: post.id
        }, {
            onSuccess: (data) => {
                toast.success(data.message);

                setCurrentTopReacts(prev => {
                    const currentNoReaction = prev.noReactions || 0

                    if (!currentReaction)
                        return {
                            noReactions: currentNoReaction + 1,
                            topReacts: data.data
                        }

                    if (type === currentReaction) {
                        return {
                            noReactions: currentNoReaction - 1,
                            topReacts: data.data
                        }
                    }

                    return {
                        ...prev,
                        topReacts: data.data
                    }
                });
            },
            onError: () => {
                setCurrentReaction(comment.reacted);
                toast.error("Lỗi khi bày tỏ cảm xúc, vui lòng thử lại!");
            }
        })

        setOpenReactionPopover(false);
    }

    return (
        <>
            <div className={`flex gap-x-3 ${className} ${children && 'mb-3'}`}>
                <Link to={`/user/${comment.user.id}`}>
                    <Avatar size="large" className="flex-shrink-0" src={comment.user.profilePicture || "/images/default-user.png"} />
                </Link>
                <div className="w-full">
                    <div className={`py-2 px-4 w-fit bg-gray-100 rounded-lg ${children && 'relative'}`}>
                        <div className="mb-1 flex items-center">
                            <div className="flex-1">
                                <Link to={`/user/${comment.user.id}`} className="inline-block">
                                    <h4 className="font-semibold">{comment.user.userFullName}</h4>
                                </Link>
                            </div>
                            {comment.mine && <FontAwesomeIcon icon={faEllipsis} className="cursor-pointer text-gray-500 ms-4" />}
                        </div>
                        <p>{comment.content}</p>
                    </div>
                    {comment.imageUrls && <div className="flex gap-2 mt-2 w-[80%] overflow-hidden">
                        {comment.imageUrls.map((url) => (
                            <div key={randomUUID()} className="flex-shrink-0 rounded-md overflow-hidden w-[350px] h-[200px]">
                                <Image preview={{
                                    mask: <div></div>
                                }} src={url} width={350} height={200} alt="comment image" className="object-cover" />
                            </div>
                        ))}
                    </div>}
                    <div className="flex flex-wrap items-center gap-x-1 mt-2 max-w-full">
                        <p className="text-gray-400 me-1">{getTimeDiff(comment.createdAt)}</p>
                        <Popover trigger={"hover"} onOpenChange={setOpenReactionPopover} open={openReactionPopover} content={<ReactionPopoverContent onReactionChange={handleOnReactionChange} />}>
                            <p onClick={() => handleOnReactionChange(currentReaction || EReactionType.LIKE)} className={`px-2 cursor-pointer ${currentReaction ? getReactionTextColor(currentReaction) : 'text-gray-600'} font-medium hover:text-[--primary-color] transition-all select-none`}>{getReactionDesc(currentReaction || EReactionType.LIKE)}</p>
                        </Popover>
                        <p onClick={() => toggleReplyInput(comment)} className="px-2 cursor-pointer text-gray-600 font-medium hover:text-[--primary-color] transition-all select-none">{openReplyInput && comment.id === currentReplyCmt?.id ? 'Hủy' : 'Phản hồi'}</p>
                        {!openReplies && comment.children.length > 0 && <p onClick={handleOpenReplies} className="px-2 font-medium text-gray-600 cursor-pointer hover:text-[--primary-color] transition-all select-none">{`Xem thêm ${comment.children.length} phản hồi`}</p>}
                        {currentTopReacts.noReactions != undefined && currentTopReacts.noReactions > 0 && <div className="ms-10 flex justify-end">
                            <div onClick={handleOpenReactionsModal} className="rounded-full px-2 bg-gray-50 flex items-center cursor-pointer">
                                <span className="text-gray-600 font-medium">{currentTopReacts.noReactions}</span>
                                <div className="flex">
                                    {currentTopReacts.topReacts?.map((react, index) => (
                                        <img key={randomUUID()} src={getReactionIcon(react.type)} className={`w-7 h-7 ${index !== 0 && '-ms-2'}`} />
                                    ))}
                                </div>
                            </div>
                        </div>}
                    </div>
                    {!children && openReplies && <div className="mt-3">
                        {comment.children.map((child) => (
                            <Comment key={randomUUID()} comment={child} post={post} children onChildOpenReplyInput={handleChildOpenReplyInput} />
                        ))}
                    </div>}
                    {openReplyInput && <>
                        <CommentCreator onCommented={onCommentReplied} post={post} replyCmt={currentReplyCmt} parentId={comment.id} placeholder={`Phản hồi bình luận của ${removeExtraSpaces(currentReplyCmt?.user.userFullName)}...`} className="my-3" />
                    </>}
                </div>
            </div>
            <Modal
                title={<h1 className="md:text-lg mb-5 text-base text-center font-semibold">Lượt thả cảm xúc</h1>}
                centered
                open={openReactionsModal}
                onCancel={() => setOpenReactionsModal(false)}
                width={650}
                footer
            >
                <ReactionsModalContent comment={comment} />
            </Modal>
        </>
    );
};

export default Comment;