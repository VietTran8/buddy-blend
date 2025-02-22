import { Avatar, Button } from "antd";
import { LogOut, Settings } from "lucide-react";
import { FC, useContext } from "react";
import { Link } from "react-router-dom";
import { useLogout } from "../../../hooks";
import { AuthContext } from "../../../context";

interface IProps { };

const MePopoverContent: FC<IProps> = ({ }) => {
    const logout = useLogout();
    const { user } = useContext(AuthContext);

    return (
        <div className="w-[230px]">
            <div className="flex items-start gap-x-3">
                <Avatar size="large" src={user?.profilePicture || "/images/default-user.png"} />
                <div className="">
                    <h1 className="font-bold -mb-1 line-clamp-1">{user?.userFullName}</h1>
                    <span className="md:text-sm text-xs font-semibold text-green-500">Đang hoạt động</span>
                </div>
            </div>
            <Link to={`/user/${user?.id}`}>
                <Button type="primary" className="w-full mt-3"><span className="text-sm font-semibold">Xem hồ sơ</span></Button>
            </Link>
            <Link to="/settings">
                <div className="flex items-center gap-x-2 mt-1 py-3 hover:text-blue-500 cursor-pointer transition-all">
                    <Settings width={20} height={20} />
                    <span className="font-semibold">Cài đặt tài khoản</span>
                </div>
            </Link>
            <hr />
            <div onClick={logout} className="flex items-center gap-x-2 py-3 hover:text-blue-500 cursor-pointer transition-all">
                <LogOut width={20} height={20} />
                <span className="font-semibold">Đăng xuất</span>
            </div>
        </div>
    );
};

export default MePopoverContent;