import { FC } from "react";
import { SearchPeopleItem } from "../../components";
import { useOutletContext } from "react-router-dom";
import { SearchOutletContextType } from "@/layouts/SearchLayout";
import { Empty } from "antd";

interface IProps { };

const People: FC<IProps> = ({ }) => {
    const { searchResults, isLoading } = useOutletContext<SearchOutletContextType>();


    return (
        <div className="">
            <h1 className="lg:text-lg text-base font-semibold md:mb-4 my-2 p-5 bg-white rounded-md">Mọi người</h1>
            <div className="rounded bg-white p-5">
                {searchResults.users.map((user) => (
                    <SearchPeopleItem user={user} key={user.id} />
                ))}
                {!isLoading && searchResults.users.length === 0 && <Empty description>
                    <span className="font-semibold text-gray-400">Không tìm thấy người dùng nào phù hợp...</span>
                </Empty>}
                {isLoading && <p className="text-center my-3">Loading...</p>}
            </div>
        </div>
    );
};

export default People;