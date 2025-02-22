import { FC } from "react";
import { Header, NotFound as NotFoundComponent } from "../components";

interface IProps { };

const NotFound: FC<IProps> = ({ }) => {
    return (<>
        <Header className="sticky top-0 z-50" />
        <NotFoundComponent />
    </>)
};

export default NotFound;