import { FC, useContext } from "react";
import { searchMenuItems } from "../../constants";
import { Link, useLocation, useSearchParams } from "react-router-dom";
import { AuthContext } from "@/context";

interface IProps { 
    className?: string;
};

const SearchSideBar: FC<IProps> = ({ className }) => {
    const { user: authUser } = useContext(AuthContext);
    const { pathname } = useLocation();
    const [searchParams] = useSearchParams();

    const query = searchParams.get("q") || "";

    return (
        <aside className={`p-4 bg-white md:min-h-[80vh] h-fit rounded-br-md rounded-tr-md ${className}`}>
            <div className="">
                <h1 className="font-bold lg:text-2xl text-base">{`Kết quả tìm kiếm cho "${query}"`}</h1>
                <p className="text-gray-500 mt-2">{authUser?.userFullName}</p>
            </div>
            <hr className="my-4" />
            <div>
                <h3 className="font-semibold lg:text-lg text-sm">Bộ lọc</h3>
                <div className="mt-2">
                    {searchMenuItems.map((item, index) => (
                        <Link key={index} to={`${item.linkTo}?q=${query}`}>
                            <div className={`rounded-md mt-1 hover:bg-gray-100 p-3 flex items-center gap-x-3 ${pathname.includes(item.linkTo) && 'bg-gray-100'}`}>
                                {item.icon}
                                <span className="font-semibold">{item.name}</span>
                            </div>
                        </Link>
                    ))}
                </div>
            </div>
        </aside>
    )
};

export default SearchSideBar;