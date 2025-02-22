import { faEllipsis } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { Avatar } from "antd";
import { FC, useContext } from "react";
import { ChatContext, ChatContextType } from "../../../context";

interface IMessagesPopoverContentProps {
    className?: string,
    onChatUserClick: () => void
};

const MessagesPopoverContent: FC<IMessagesPopoverContentProps> = ({ className, onChatUserClick }) => {
    const { openChatDrawer, rooms } = useContext<ChatContextType>(ChatContext);

    return (
        <div className={`${className} w-[320px] max-h-[70vh] overflow-y-auto no-scrollbar`}>
            {rooms.map((room, index) => (
                <div onClick={() => {
                    openChatDrawer(room.opponentUserId);
                    onChatUserClick();
                }} key={index} className="w-full flex items-center p-2 gap-x-3 rounded-md transition-all hover:bg-gray-100 cursor-pointer">
                    <div className="relative">
                        <Avatar size="large" src={room.roomImage || "/images/default-user.png"} />
                        {room.online && <span className="absolute bottom-0 right-0 w-3 border-2 border-white h-3 rounded-full bg-green-500"></span>}
                    </div>
                    <div className="flex-1 min-w-0">
                        <h1 className="font-semibold text-base line-clamp-1">{room.roomName}</h1>
                        <span className="block text-sm truncate text-gray-400">{room.latestMessage ? (room.lastSentByYou ? `Bạn: ${room.latestMessage.content}` : room.latestMessage.content) :
                                    `Các bạn vừa được kết nối...`}</span>
                    </div>
                    <span>
                        <FontAwesomeIcon icon={faEllipsis} className="text-lg text-gray-400" />
                    </span>
                </div>
            ))}
        </div>
    );    
};

export default MessagesPopoverContent;