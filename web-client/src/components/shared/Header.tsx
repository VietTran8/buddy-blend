import { FC, useContext, useState } from "react";
import { Link, useLocation } from "react-router-dom";
import { Search as SearchIcon } from "lucide-react";
import { mainMenuItems } from "../../constants";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faBell, faComment } from "@fortawesome/free-regular-svg-icons";
import { Avatar, Popover } from "antd";
import MessagesPopoverContent from "../features/popovers/MessagesPopoverContent";
import NotificationsPopoverContent from "../features/popovers/NotificationsPopoverContent";
import MePopoverContent from "../features/popovers/MePopoverContent";
import { AuthContext, ChatContext } from "../../context";
import { useQueryUserNotifications } from "../../hooks";
import Search from "./Search";

interface IHeaderProps {
    className?: string
};

const Header: FC<IHeaderProps> = ({ className }) => {
    const { pathname } = useLocation();

    const [openMessages, setOpenMessages] = useState(false);
    const [openNotis, setOpenNotis] = useState(false);
    const [openMe, setOpenMe] = useState(false);

    const { user } = useContext(AuthContext);
    const { rooms } = useContext(ChatContext);

    const { data, isLoading, totalElements, isFetchingNextPage, hasNextPage, fetchNextPage } = useQueryUserNotifications();

    const handleOpenMsgChange = (newOpen: boolean) => {
        setOpenMessages(newOpen);
    };

    const handleOpenNotiChange = (newOpen: boolean) => {
        setOpenNotis(newOpen);
    };

    const handleOpenMeChange = (newOpen: boolean) => {
        setOpenMe(newOpen);
    };

    const handleChatUserClick = () => {
        setOpenMessages(false);
    }

    return (
        <header className={`w-full z-50 h-[--header-height] flex items-center bg-white shadow ${className}`}>
            <div className="w-full md:px-10">
                <div className="flex items-center justify-between w-full">
                    <div className="flex gap-x-5 items-center">
                        <Link to='/' className="inline flex-grow-0 flex-shrink-0 ms-3 md:ms-0">
                            <img src="/logos/brand.png" className="w-14 h-14 object-cover rounded-lg" />
                        </Link>
                        <Search />
                        <Link to="/search" className="flex items-center justify-center w-12 h-12 rounded-full bg-gray-100 transition-all hover:bg-gray-200 -ms-3 md:hidden">
                            <SearchIcon className="text-gray-400" width={15} height={15} />
                        </Link>
                        <div className="flex-1 flex justify-between">
                        </div>
                    </div>
                    <div className="hidden items-center justify-center gap-x-5 lg:flex me-52">
                        {mainMenuItems.map((item, index) => (
                            <Link className={`text-gray-500 hover:text-[--primary-color] transition-all p-5 text-xl ${index === 0 && pathname === '/' && '!text-[--primary-color]'} ${index !== 0 && (pathname.includes(item.linkTo) || ((index === 1 && pathname.includes("/friends")) && '!text-[--primary-color]') || (index === 3 && pathname.includes("/group"))) && '!text-[--primary-color]'}`} key={index} to={item.linkTo}>
                                {item.icon}
                            </Link>
                        ))}
                    </div>
                    <div className="flex items-center justify-end gap-x-5 me-3 lg:me-0">
                        <Popover
                            content={<MessagesPopoverContent onChatUserClick={handleChatUserClick} />}
                            title={<h1 className="font-semibold text-lg">Tin nhắn</h1>}
                            trigger="click"
                            open={openMessages}
                            onOpenChange={handleOpenMsgChange}
                        >
                            <div className="relative w-12 h-12 flex justify-center items-center bg-gray-100 text-gray-400 text-xl rounded-md hover:bg-gray-200 cursor-pointer transition-all">
                                <div className="w-6 h-6 bg-red-500 flex items-center justify-center absolute -top-2 -left-2 rounded-full">
                                    <span className="text-sm text-white font-medium">{rooms.length}</span>
                                </div>
                                <FontAwesomeIcon icon={faComment} />
                            </div>
                        </Popover>

                        <Popover
                            content={<NotificationsPopoverContent
                                data={data}
                                fetchNextPage={fetchNextPage}
                                isFetchingNextPage={isFetchingNextPage}
                                hasNextPage={hasNextPage}
                                isLoading={isLoading}
                            />}
                            title="Thông báo"
                            trigger="click"
                            open={openNotis}
                            onOpenChange={handleOpenNotiChange}
                        >
                            <div className="relative w-12 h-12 flex justify-center items-center bg-gray-100 text-gray-400 text-xl rounded-md hover:bg-gray-200 cursor-pointer transition-all">
                                <div className="w-6 h-6 bg-red-500 flex items-center justify-center absolute -top-2 -left-2 rounded-full">
                                    <span className="text-sm text-white font-medium">{(totalElements && totalElements > 9 ? '9+' : totalElements) || 0}</span>
                                </div>
                                <FontAwesomeIcon icon={faBell} />
                            </div>
                        </Popover>

                        <Popover
                            content={<MePopoverContent />}
                            title="Bạn"
                            trigger="click"
                            open={openMe}
                            onOpenChange={handleOpenMeChange}
                        >
                            <Avatar className="cursor-pointer" shape="square" size="large" src={user?.profilePicture || "/images/default-user.png"} />
                        </Popover>
                    </div>
                </div>
            </div>
        </header>
    )
};

export default Header;