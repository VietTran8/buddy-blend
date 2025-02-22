import { Avatar, Modal } from "antd";
import { FC, useContext, useState } from "react";
import Input from "../shared/Input";
import { Smile } from "lucide-react";
import CreatePostModalContent from "./modals/CreatePostModalContent";
import { AuthContext } from "../../context";
import { removeExtraSpaces } from "@/utils";

interface IProps {
    className?: string,
    disabled?: boolean,
    isGroupPost?: boolean,
    groupId?: string,
};

const PostCreator: FC<IProps> = ({ className, disabled = false, isGroupPost = false, groupId }) => {
    const { user } = useContext(AuthContext);
    const [openPostModal, setOpenPostModal] = useState<boolean>(false);

    const handleOpenPostModal = () => {
        !disabled && setOpenPostModal(true);
    }

    const handleOnPostCreated = () => setOpenPostModal(false)

    return <>
        <div className={`rounded-md border-2 border-zinc-100 bg-white w-full flex gap-x-4 px-5 pt-5 pb-5 md:pb-2 ${className}`}>
            <Avatar size="large" src={user?.profilePicture || "/images/default-user.png"} className="flex-shrink-0" />
            <div className="w-full">
                <Input onClick={handleOpenPostModal} onMouseDown={(e) => e.preventDefault()} placeholder={`${removeExtraSpaces(user?.userFullName)}, bạn đang nghĩ gì?`} endDrawable={<Smile size={20} className="text-gray-500 w-full" />} />
                <div className="justify-between mt-2 hidden md:flex">
                    <div onClick={handleOpenPostModal} className={`flex gap-x-2 flex-1 items-center p-3 rounded-md ${disabled ? 'cursor-default' : 'cursor-pointer hover:bg-gray-100'} transition-all`}>
                        <img src="/icons/tag.png" className="w-7" />
                        <span className="text-gray-600 font-semibold">Gắn thẻ</span>
                    </div>
                    <div onClick={handleOpenPostModal} className={`flex gap-x-2 flex-1 justify-center items-center p-3 rounded-md ${disabled ? 'cursor-default' : 'cursor-pointer hover:bg-gray-100'} transition-all`}>
                        <img src="/icons/image.png" className="w-7" />
                        <span className="text-gray-600 font-semibold">Ảnh/Video</span>
                    </div>
                    <div onClick={handleOpenPostModal} className={`flex gap-x-2 flex-1 justify-end items-center p-3 rounded-md ${disabled ? 'cursor-default' : 'cursor-pointer hover:bg-gray-100'} transition-all`}>
                        <img src="/icons/smile.png" className="w-7" />
                        <span className="text-gray-600 font-semibold">Cảm xúc</span>
                    </div>
                </div>
            </div>
        </div>
        <Modal
            title={<h1 className="md:text-lg mb-5 text-base text-center font-semibold">Tạo bài viết</h1>}
            width={650}
            centered
            open={openPostModal}
            onCancel={() => setOpenPostModal(false)}
            footer
        >
            <CreatePostModalContent isGroupPost={isGroupPost} groupId={groupId} onCreated={handleOnPostCreated} />
        </Modal>
    </>
};

export default PostCreator;