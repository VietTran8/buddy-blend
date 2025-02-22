import { Link, Trash } from "lucide-react";
import { FC } from "react";
import { Post } from "../../../types";
import { useHandleSavePost } from "../../../hooks";

interface IProps { 
    post: Post
};

const SavedActionPopoverContent: FC<IProps> = ({ post }) => {
    const { mutate: handle } = useHandleSavePost();

    const handleUnsavePost = () => {
        handle(post.id);
    }

    return <ul>
        <li onClick={handleUnsavePost} className="px-3 py-2 font-medium text-gray-600 rounded hover:bg-gray-100 flex cursor-pointer transition-all gap-x-2"><Trash className="text-gray-500 text-sm" /> Xóa khỏi danh sách đã lưu</li>
        <li className="px-3 py-2 font-medium text-gray-600 mt-1 rounded hover:bg-gray-100 flex cursor-pointer transition-all gap-x-2"><Link className="text-gray-500 text-sm" /> Sao chép địa chỉ liên kết</li>
    </ul>
};

export default SavedActionPopoverContent;