import { faEllipsis } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { Avatar } from "antd";
import { FC, useContext } from "react";
import { useQueryFriendList } from "../../hooks";
import { ChatContext } from "@/context";
import { useQueryClient } from "@tanstack/react-query";

interface IProps {
    className?: string;
};

const ContactSection: FC<IProps> = ({ className }) => {
    const { data: friendListResponse, isLoading } = useQueryFriendList();
    const { openChatDrawer } = useContext(ChatContext);
    const queryClient = useQueryClient();

    const handleUserClick = async (contactId?: string) => {
        queryClient.invalidateQueries({
            queryKey: ["chat-rooms"]
        });

        openChatDrawer(contactId)
    }

    const contacts = friendListResponse?.data;

    return (
        <div className={`p-5 bg-white rounded-md ${className}`}>
            <h1 className="text-lg font-bold mb-5">Liên hệ</h1>
            {contacts?.map((contact) => (
                <div key={contact.id} onClick={() => handleUserClick(contact.id)}>
                    <div className="flex items-center rounded-md gap-x-2 p-2 mt-2 hover:bg-gray-100 transition-all cursor-pointer">
                        <div className="relative">
                            <Avatar size="large" src={contact.profilePicture || "/images/default-user.png"} />
                            {contact.online && <span className="absolute -bottom-0.5 -right-0.5 w-4 border-2 border-white h-4 rounded-full bg-green-500"></span>}
                        </div>
                        <p className="font-semibold mb-1 flex-1">{contact.userFullName}</p>
                        {/* {contact.unreadMessages ?
                            <span className="bg-red-500 text-white font-semibold w-6 h-6 flex justify-center items-center rounded-full">{contact.unreadMessages}</span> :
                        } */}
                        <FontAwesomeIcon icon={faEllipsis} className="text-lg text-gray-400" />
                    </div>
                </div>
            ))}
            {isLoading && <p className="text-center my-10">Loading...</p>}
        </div>
    )
};

export default ContactSection;