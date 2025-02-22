import { Avatar, Modal, Popover } from "antd";
import { Ellipsis, Share } from "lucide-react";
import { FC, useState } from "react";
import SavedActionPopoverContent from "./popovers/SavedActionPopoverContent";
import ShareModalContent from "./modals/ShareModalContent";
import { Post } from "../../types";
import { Link } from "react-router-dom";

interface IProps {
    post: Post,
    thumbBackground?: string;
};

const SavedItem: FC<IProps> = ({ post, thumbBackground }) => {
    const [openAction, setOpenAction] = useState(false);
    const [openShareModal, setOpenShareModal] = useState(false);

    const handleOpenActionChange = (newOpen: boolean) => {
        setOpenAction(newOpen);
    };

    const handleOpenShareModal = () => {
        setOpenShareModal(true);
    }

    return (
        <>
            <div className="p-3 bg-white rounded-md flex gap-x-4">
                {post.medias.length > 0 ? <img className="object-cover rounded-md w-[120px] h-[120px]" src={post.medias[0].thumbnail} /> :
                    <div className={`rounded-md w-[120px] h-[120px] flex justify-center items-center px-4 ${thumbBackground}`}>
                        <span className="line-clamp-1 font-semibold lg:text-lg text-base select-none text-center text-white">{post.content}</span>
                    </div>
                }
                <div className="flex flex-col gap-y-3">
                    <h1 className="lg:text-lg text-base font-bold line-clamp-2">
                        {post.content}
                    </h1>
                    <div className="flex items-center gap-x-3">
                        <Avatar src={post.user.profilePicture || "/images/default-user.png"} />
                        <p className="text-gray-400 font-semibold">{`Lưu từ ${post.user.userFullName}`}</p>
                    </div>
                    <div className="flex gap-x-3 items-center">
                        <Link to={`/post/${post.id}`} className="btn-primary">Xem bài viết</Link>
                        <button onClick={handleOpenShareModal} className="btn-secondary flex items-center gap-x-2">
                            <Share size={18} />
                            <span className="text-sm">Chia sẻ</span>
                        </button>
                        <Popover
                            content={<SavedActionPopoverContent post={post} />}
                            title="Tùy chọn"
                            trigger="click"
                            open={openAction}
                            onOpenChange={handleOpenActionChange}
                        >
                            <button className="btn-secondary"><Ellipsis size={20} /></button>
                        </Popover>
                    </div>
                </div>
            </div>
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
        </>
    );
};

export default SavedItem;