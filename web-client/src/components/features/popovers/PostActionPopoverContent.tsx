import { Bookmark, BookmarkX, Eye, Heart, HeartCrack, Info, Link, Pen, Trash } from "lucide-react";
import { Link as RouterLink } from "react-router-dom";
import { FC, useState } from "react";
import { Post } from "../../../types";
import { Modal } from "antd";
import UpdatePostModalContent from "../modals/UpdatePostModalContent";
import UserFavouritesContent from "../modals/UserFavouritesContent";
import { useDeletePost, useHandleFavouritePost, useHandleSavePost } from "../../../hooks";

interface IProps {
    post?: Post;
    isFavouritePost?: boolean;
    collectionName?: string;
};

const PostActionPopoverContent: FC<IProps> = ({ post, isFavouritePost = false, collectionName }) => {
    const [openUpdateModal, setOpenUpdateModal] = useState<boolean>();
    const [openFavouriteModal, setOpenFavouriteModal] = useState<boolean>();

    const { mutate: handle } = useHandleFavouritePost();
    const { mutate: savePost } = useHandleSavePost();
    const { mutate: deletePost } = useDeletePost();

    const handleUnFavourite = () => {
        if (post && collectionName && collectionName.length !== 0)
            handle({
                name: collectionName,
                postId: post.id
            });
    }

    const handleSavePost = () => {
        if (post)
            savePost(post.id);
    }

    const handleDeletePost = () => {
        if (post)
            deletePost(post.id);
    }

    const handleOnPostUpdated = () => setOpenUpdateModal(false);
    const handleOnFavouriteAdded = () => setOpenFavouriteModal(false);

    return <>
        <ul>
            <li onClick={handleSavePost} className="px-3 py-2 font-medium text-gray-600 rounded hover:bg-gray-100 flex cursor-pointer transition-all gap-x-2">
                {post?.saved ? <>
                    <BookmarkX className="text-gray-500 text-sm" /> Bỏ lưu bài viết
                </> : <>
                    <Bookmark className="text-gray-500 text-sm" /> Lưu bài viết
                </>}
            </li>
            {!isFavouritePost && <li onClick={() => setOpenFavouriteModal(true)} className="px-3 py-2 font-medium text-gray-600 rounded mt-1 hover:bg-gray-100 flex cursor-pointer transition-all gap-x-2"><Heart className="text-gray-500 text-sm" /> Thêm vào yêu thích</li>}
            {isFavouritePost && <li onClick={handleUnFavourite} className="px-3 py-2 font-medium text-gray-600 rounded mt-1 hover:bg-gray-100 flex cursor-pointer transition-all gap-x-2"><HeartCrack className="text-gray-500 text-sm" /> Xóa khỏi danh sách yêu thích</li>}
            {!post?.mine && <li className="px-3 py-2 font-medium text-gray-600 mt-1 rounded hover:bg-gray-100 flex cursor-pointer transition-all gap-x-2"><Info className="text-gray-500 text-sm" /> Báo cáo bài viết</li>}
            <RouterLink to={`/post/${post?.id}`} className="px-3 py-2 font-medium text-gray-600 mt-1 rounded hover:bg-gray-100 flex cursor-pointer transition-all gap-x-2">
                <span className="flex gap-x-2.5">
                    <Eye className="text-gray-500 text-sm" /> Xem chi tiết
                </span>
            </RouterLink>
            {post?.mine && <li onClick={() => setOpenUpdateModal(true)} className="px-3 py-2 font-medium text-gray-600 mt-1 rounded hover:bg-gray-100 flex cursor-pointer transition-all gap-x-2"><Pen className="text-gray-500 text-sm" /> Chỉnh sửa bài viết</li>}
            {post?.mine && <li onClick={handleDeletePost} className="px-3 py-2 font-medium text-gray-600 mt-1 rounded hover:bg-gray-100 flex cursor-pointer transition-all gap-x-2"><Trash className="text-gray-500 text-sm" /> Xóa bài viết</li>}
            <li className="px-3 py-2 font-medium text-gray-600 mt-1 rounded hover:bg-gray-100 flex cursor-pointer transition-all gap-x-2"><Link className="text-gray-500 text-sm" /> Sao chép địa chỉ liên kết</li>
        </ul>
        <Modal
            title={<h1 className="md:text-lg mb-5 text-base text-center font-semibold">Cập nhật bài viết</h1>}
            width={650}
            centered
            open={openUpdateModal}
            destroyOnClose
            onCancel={() => setOpenUpdateModal(false)}
            footer
        >
            <UpdatePostModalContent post={post!} onUpdated={handleOnPostUpdated}/>
        </Modal>
        <Modal
            title={<h1 className="md:text-lg mb-5 text-base text-center font-semibold">Thêm vào mục yêu thích</h1>}
            width={650}
            centered
            open={openFavouriteModal}
            destroyOnClose
            onCancel={() => setOpenFavouriteModal(false)}
            footer
        >
            <UserFavouritesContent postId={post?.id || ""} onAdded={handleOnFavouriteAdded} />
        </Modal>
    </>
};

export default PostActionPopoverContent;