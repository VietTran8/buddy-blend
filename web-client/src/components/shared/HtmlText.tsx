import { FC, ReactNode } from "react";

interface IProps {
    children?: ReactNode
};

const HtmlText:FC<IProps> = ({ children }) => {
    return <span dangerouslySetInnerHTML={{ __html: children as string }}></span>
};

export default HtmlText;