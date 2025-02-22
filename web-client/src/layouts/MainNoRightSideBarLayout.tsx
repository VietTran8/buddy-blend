import { FC } from "react";
import { Header, MainSideBar } from "../components";
import { Outlet } from "react-router-dom";

interface IProps { };

const MainNoRightSideBarLayout: FC<IProps> = ({ }) => {
    return <>
        <Header className="sticky top-0 z-50" />
        <div className="relative">
            <div className="grid grid-cols-12 gap-x-3 relative">
                <MainSideBar className="sticky top-24 lg:col-span-3 lg:ms-0 ms-3 md:col-span-4 md:block hidden" />
                <main className="mt-3 col-span-full lg:col-span-9 md:col-span-8 md:ms-0 lg:me-0 me-3 ms-3">
                    <div className="container">
                        <Outlet />
                    </div>
                </main>
            </div>
        </div>
    </>
};

export default MainNoRightSideBarLayout;