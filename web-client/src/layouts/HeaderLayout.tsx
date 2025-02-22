import { FC } from "react";
import { Header } from "../components";
import { Outlet } from "react-router-dom";

interface IProps { };

const HeaderLayout: FC<IProps> = ({ }) => {
    return <>
        <Header className="sticky top-0" />
        <div className="w-full">
            <Outlet />
        </div>
    </>
};

export default HeaderLayout;