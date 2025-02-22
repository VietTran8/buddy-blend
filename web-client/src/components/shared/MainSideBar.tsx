import { Avatar, Skeleton } from "antd";
import { FC, useContext } from "react";
import { Link, useLocation } from "react-router-dom";
import { mainSideBarItems } from "../../constants";
import { AuthContext } from "../../context";
import { useQueryMyGroup } from "../../hooks";

interface IProps {
    className?: string
};

const MainSideBar: FC<IProps> = ({ className }) => {
    const { pathname } = useLocation();
    const { user } = useContext(AuthContext);
    const { data: groupsResponse, isLoading: isLoadingGroups } = useQueryMyGroup();

    const groups = groupsResponse?.data;

    return (
        <aside className={`p-5 rounded-md h-screen-except-header overflow-auto custom-scroll bg-white ${className}`}>
            <Link to={`/user/${user?.id}`}>
                <div className="flex gap-x-3 hover:bg-gray-100 transition-all rounded-md p-2 cursor-pointer">
                    <div className="relative">
                        <Avatar size="large" src={user?.profilePicture || "/images/default-user.png"} />
                        <span className="absolute -bottom-0.5 -right-0.5 w-4 border-2 border-white h-4 rounded-full bg-green-500"></span>
                    </div>
                    <div>
                        <h1 className="font-bold line-clamp-1">{user?.userFullName}</h1>
                        <p className="text-sm -mt-1/2 text-gray-500">{`@${user?.email.split("@")[0]}`}</p>
                    </div>
                </div>
            </Link>
            <hr className="my-5" />
            {mainSideBarItems.map((item, index) => (
                <Link key={index} to={item.linkTo}>
                    <div className={`flex items-center rounded-md gap-x-3 p-2 hover:bg-gray-100 transition-all cursor-pointer ${index === 0 && pathname === '/' && 'bg-gray-100'} ${index !== 0 && (pathname.includes(item.linkTo) || (index === 2 && pathname.includes("/group"))) && 'bg-gray-100'}`}>
                        <div className="text-lg text-gray-400 w-5">
                            {item.icon}
                        </div>
                        <p className="font-semibold">{item.name}</p>
                    </div>
                </Link>
            ))}
            <hr className="my-5" />
            <h1 className="text-lg font-bold mb-5">Lối tắt của bạn</h1>
            {isLoadingGroups && <div className="flex flex-col gap-y-2">
                {Array(3).fill(null).map((_, index) =>
                    <div key={index} className="flex items-center rounded-md gap-x-2 p-2 mt-2 hover:bg-gray-100 transition-all cursor-pointer">
                        <Skeleton.Avatar key={index} size="large" active shape="square" />
                        <div className="flex-1 mt-3">
                            <Skeleton.Button active block style={{ width: "150px", height: "14px" }} size="small" />
                            <Skeleton.Input active block style={{ width: "100px", height: "14px" }} size="small" />
                        </div>
                    </div>
                )}
            </div>}
            {groups?.map((item, index) => (
                <Link key={index} to={`/group/${item.id}`}>
                    <div className="flex items-center rounded-md gap-x-2 p-2 mt-2 hover:bg-gray-100 transition-all cursor-pointer">
                        <Avatar shape="square" size={50} src={item.avatar || "/images/community2.jpg"} />
                        <div>
                            <p className="font-semibold">{item.name}</p>
                            <span className="text-sm text-gray-500">{item.pending ? `Đang chờ phê duyệt` : `${item.memberCount} thành viên`}</span>
                        </div>
                    </div>
                </Link>
            ))}
        </aside>
    )
};

export default MainSideBar;