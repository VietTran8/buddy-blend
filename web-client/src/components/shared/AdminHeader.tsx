import { Avatar } from "antd";
import { FC } from "react";

interface IProps {};

const AdminHeader:FC<IProps> = ({ }) => {
    return <div className="w-full h-300px z-50 bg-white shadow-sm p-3 sticky top-0 left-0">
        <div className="flex items-center justify-between px-10">
            <div className="flex items-center gap-3">
                <img src="/logos/brand.png" alt="Logo" className="h-12 rounded-md" />

                <h1 className="text-[--primary-color] font-bold text-lg uppercase">Buddy Blend Admin Site</h1>
            </div>
            <Avatar src="/images/default-user.png" alt="Admin Avatar" size={40} className="cursor-pointer" />
        </div>
    </div>
};

export default AdminHeader;