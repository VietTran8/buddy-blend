import { AdminHeader, AdminSideBar } from "@/components";
import { FC } from "react";
import { Outlet } from "react-router-dom";

interface IProps { };

const AdminLayout: FC<IProps> = ({ }) => {
    return <div className="bg-gray-50 w-full h-dvh">
        <AdminHeader />
        <div className="flex mt-2 relative">
            <AdminSideBar />
            <div className="container bg-white rounded shadow-md ">
                <div className="p-5">
                    <Outlet />
                </div>
            </div>
        </div>
    </div>
};

export default AdminLayout;