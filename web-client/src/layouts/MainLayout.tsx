import { FC } from "react";
import { Outlet } from "react-router-dom";
import { Header, MainSideBar, RightMainSideBar } from "../components";

interface IProps { };

const MainLayout: FC<IProps> = ({ }) => {
    return (
        <>
            <Header className="sticky top-0" />
            <div className="relative">
                <div className="grid grid-cols-12 gap-x-3 relative">
                    <MainSideBar className="sticky top-24 lg:col-span-3 lg:ms-0 ms-3 md:col-span-4 md:block hidden" />
                    <div className="col-span-1 lg:block hidden"></div>
                    <main className="mt-3 col-span-full lg:col-span-4 md:col-span-8 md:ms-0 lg:me-0 me-3 ms-3">
                        <Outlet />
                    </main>
                    <div className="col-span-1 lg:block hidden"></div>
                    <RightMainSideBar className="col-span-3 sticky top-24 lg:block hidden" />
                </div>
            </div>
        </>
    );
};

export default MainLayout;