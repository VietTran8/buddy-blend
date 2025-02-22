import { FC } from "react";
import { Button } from "antd";
import { Link } from "react-router-dom";

interface IProps { };

const NotFound: FC<IProps> = ({ }) => {
    return <div className="w-full flex flex-col px-10 justify-center items-center" style={{ height: "calc(100vh - var(--header-height))" }}>
        <img src="/images/broken-link.png" className="w-36" />
        <h1 className="font-semibold text-xl mt-3">Trang này hiện không thể hiển thị</h1>
        <p className="text-gray-400 text-base text-center">Có thể liên kết đã hỏng hoặc trang đã bị gỡ. Hãy kiểm tra xem liên kết mà bạn đang cố mở có chính xác không.</p>
        <Link to="/">
            <Button size="large" type="primary" className="mt-3">
                <span className="font-semibold">Đi tới bảng tin</span>
            </Button>
        </Link>
    </div>
};

export default NotFound;