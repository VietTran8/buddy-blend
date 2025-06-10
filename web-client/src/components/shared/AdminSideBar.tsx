import { adminMenuItems } from "@/constants";
import { cn } from "@/lib/utils";
import { FC } from "react";
import { Link, useLocation } from "react-router-dom";

interface IProps {
    className?: string;
};

const AdminSideBar: FC<IProps> = ({ className }) => {
    const { pathname } = useLocation();

    return <div className={cn("w-64 h-dvh bg-white shadow-md p-5 rounded-tr rounded-br", className)}>
        {adminMenuItems.map((item, index) => (
            <Link
                to={item.linkTo}
                key={index}
                className={cn("flex items-center gap-3 p-3 cursor-pointer hover:bg-gray-200 rounded transition-all",
                    index !== 0 && pathname.includes(item.linkTo) ? "bg-gray-200" : 
                    index == 0 && (pathname === "/admin" || pathname === "/admin/") ? "bg-gray-200" : "bg-white")}>
                {item.icon}
                <p className="text-gray-600 font-semibold">
                    {item.name}
                </p>
            </Link>
        ))}
    </div>
};

export default AdminSideBar;