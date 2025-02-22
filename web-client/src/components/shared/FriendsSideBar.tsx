import { Avatar } from "antd";
import { FC, useContext } from "react";
import { Link, useLocation } from "react-router-dom";
import { friendSideBarItems } from "../../constants";
import ContactSection from "../features/ContactSection";
import { AuthContext } from "../../context";

interface IProps {
    className?: string
};

const FriendsSideBar: FC<IProps> = ({ className }) => {
    const { pathname } = useLocation();
    const { user } = useContext(AuthContext);

    return <aside className={`rounded-md h-fit max-h-[88vh] overflow-auto no-scrollbar bg-white ${className}`}>
        <div className="p-5">
            <Link to={`/user/${user?.id}`}>
                <div className="flex gap-x-3 hover:bg-gray-100 rounded-md p-2 cursor-pointer">
                    <div className="relative">
                        <Avatar size="large" src={user?.profilePicture || "/images/default-user.png"} />
                        <span className="absolute bottom-0 right-0 w-3 border-2 border-white h-3 rounded-full bg-green-500"></span>
                    </div>
                    <div>
                        <h1 className="font-bold line-clamp-1">{user?.userFullName}</h1>
                        <span className="text-sm text-gray-500">{`@${user?.email.split("@")[0]}`}</span>
                    </div>
                </div>
            </Link>
            <hr className="my-5" />
            {friendSideBarItems.map((item, index) => (
                <Link key={index} to={item.linkTo}>
                    <div className={`flex items-center rounded-md gap-x-3 p-2 hover:bg-gray-100 transition-all cursor-pointer ${pathname.includes(item.linkTo) && 'bg-gray-100'}`}>
                        <div className="text-lg text-gray-400 w-5">
                            {item.icon}
                        </div>
                        <p className="font-semibold">{item.name}</p>
                    </div>
                </Link>
            ))}
        </div>
        <ContactSection />
    </aside>
};

export default FriendsSideBar;