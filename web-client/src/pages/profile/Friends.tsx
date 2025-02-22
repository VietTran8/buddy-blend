import { FC, useEffect, useState } from "react";
import { ContactSection, Input } from "../../components";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faSearch } from "@fortawesome/free-solid-svg-icons";
import { Avatar } from "antd";
import { Ellipsis } from "lucide-react";
import { Link, useParams } from "react-router-dom";
import { useDebounce, useQueryFriendList } from "../../hooks";
import { useQueryClient } from "@tanstack/react-query";
import { BaseResponse, User } from "@/types";
import { normalizeVietnamese } from "@/utils";

interface IProps { };

const Friends: FC<IProps> = ({ }) => {
    const queryClient = useQueryClient();
    const { id } = useParams();
    const { data: friendListResponse, isLoading, refetch } = useQueryFriendList(id);
    const [searchTerm, setSearchTerm] = useState("");

    const deboucedValue = useDebounce(searchTerm, 300);

    useEffect(() => {
        if(friendListResponse) {
            if(deboucedValue.trim()) {
                queryClient.setQueryData(["friend-list", id], (oldData: BaseResponse<User[]>) => ({
                    ...oldData,
                    data: oldData.data.filter(user => normalizeVietnamese(user.userFullName).includes(normalizeVietnamese(deboucedValue)))
                }));
            } else {
                refetch();
            }
        }

    }, [deboucedValue]);

    const friendList = friendListResponse?.data;

    return (
        <section className="grid grid-cols-12 mb-10 gap-3 relative">
            <div className="md:col-span-9 col-span-full mt-3 h-fit">
                <div className="bg-white rounded-md flex items-center p-2">
                    <h1 className="font-semibold ms-3 text-base md:text-large flex-1">Bạn bè</h1>
                    <Input onChange={(e) => setSearchTerm(e.target.value)} startDrawable={<FontAwesomeIcon icon={faSearch} />} placeholder="Tìm kiếm" className="!w-[280px] text-gray-400" />
                </div>
                <div className="mt-3 grid md:grid-cols-2 grid-cols-1 gap-3">
                    {isLoading && <p className="text-center mt-10">Loading...</p>}
                    {friendList?.map((user, index) => (
                        <div key={index} className="flex p-2 items-center rounded-md bg-white gap-x-3">
                            <Avatar shape="square" size={80} src={user.profilePicture || "/images/default-user.png"} />
                            <div className="flex-1">
                                <Link to={`/user/${user.id}`}>
                                    <h2 className="font-semibold text-base hover:text-[--primary-color] transition-all">{user.userFullName}</h2>
                                </Link>
                                <p className="text-sm text-gray-500">{`${user.mutualFriends.length} bạn chung`}</p>
                            </div>
                            <Ellipsis size={35} className="p-2 rounded-md cursor-pointer transition-all hover:bg-gray-50" />
                        </div>
                    ))}
                </div>
            </div>
            <div className="col-span-3 md:block hidden">
                <ContactSection className="mt-3"/>
            </div>
        </section>
    )
};

export default Friends;