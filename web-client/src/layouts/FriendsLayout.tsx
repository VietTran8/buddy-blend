import { FC } from "react";
import { Outlet } from "react-router-dom";
import { FriendsSideBar, Header } from "../components";

interface IProps { };

const FriendsLayout: FC<IProps> = ({ }) => {
    return <>
        <Header className="sticky top-0 z-50 mb-2"/>
        <div className="container relative">
            <div className="grid grid-cols-12 gap-x-3 relative">
                <FriendsSideBar className="sticky top-24 lg:ms-0 ms-3 md:col-span-3 md:block hidden"/>
                <main className="md:col-span-9 col-span-full">
                    <Outlet />
                </main>
            </div>
        </div>
    </>
};

export default FriendsLayout;