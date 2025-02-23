import { FC, useEffect, useState } from "react";
import Input from "../../shared/Input";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faSearch, faTag } from "@fortawesome/free-solid-svg-icons";
import { User } from "../../../types";
import { X } from "lucide-react";
import { Avatar } from "antd";
import { Link } from "react-router-dom";
import { useQueryFriendList } from "../../../hooks";
import BasicUserItemSkeleton from "@/components/skeletons/BasicUserItemSkeleton";

interface IProps {
    prevUsers?: User[];
    onComplete?: (taggedUsers: User[]) => void;
};

const TagUserModalContent: FC<IProps> = ({ prevUsers, onComplete }) => {
    const { data: friendsResponse, isLoading } = useQueryFriendList();
    const [taggedUsers, setTaggedUsers] = useState<User[]>(prevUsers ? prevUsers : []);

    const friends = friendsResponse?.data;

    const addTaggedUser = (user: User) => {
        if (taggedUsers.find(tagged => tagged.id === user.id))
            return;

        setTaggedUsers(prev => [...prev, user])
    }

    const removeTaggedUser = (index: number) => {
        const newTaggedUsers = [...taggedUsers];

        newTaggedUsers.splice(index, 1);

        setTaggedUsers(newTaggedUsers);
    }

    const handleOnDone = () => {
        onComplete && onComplete(taggedUsers);
    }

    useEffect(() => {
        prevUsers && setTaggedUsers([...prevUsers])
    }, [prevUsers])

    return (
        <div className="max-h-[80vh] overflow-y-auto no-scrollbar">
            <div className="flex items-center gap-x-3 p-1">
                <Input startDrawable={<FontAwesomeIcon icon={faSearch} className="text-gray-500" />}
                    placeholder="Tìm kiếm bạn bè..."
                    className="flex-1" />
                <button onClick={handleOnDone} className="btn-primary">Xong</button>
            </div>
            {
                taggedUsers.length > 0 && <div className="mt-3 transition-all">
                    <h3 className="uppercase font-semibold text-base text-gray-500">Đã gắn thẻ</h3>
                    <div className="flex w-full flex-wrap gap-2 items-center mt-2">
                        {taggedUsers.map((user, index) => (
                            <div key={index} className="px-3 py-2 bg-blue-200 flex items-center gap-x-2 rounded">
                                <Link to={`/user/${user.id}`}>
                                    <span className="text-blue-500 font-semibold">{user.userFullName}</span>
                                </Link>
                                <div onClick={() => removeTaggedUser(index)} className="rounded-full cursor-pointer w-7 h-7 hover:bg-blue-300 flex items-center justify-center transition-all">
                                    <X size={18} className="text-blue-400" />
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            }
            <div className="mt-3">
                <h3 className="uppercase font-semibold text-base text-gray-500 mb-2">Gợi ý</h3>
                {
                    friends?.map((user, index) => {
                        return !taggedUsers.some(taggedUser => taggedUser.id === user.id) && <div key={index} className="flex items-center gap-x-2 py-2 hover:bg-gray-50 px-2 rounded transition-all">
                            <Avatar size={50} src={user.profilePicture || "/images/default-user.png"} />
                            <span className="font-semibold text-base flex-1">{user.userFullName}</span>
                            <button onClick={() => addTaggedUser(user as User)} className="btn-primary">
                                <FontAwesomeIcon icon={faTag} />
                                <span className="text-sm ms-2">Gắn thẻ</span>
                            </button>
                        </div>
                    })
                }
                {isLoading && Array(10).fill(null).map((_, index) => <BasicUserItemSkeleton key={index} rightButton />)}
            </div>
        </div>
    );
};

export default TagUserModalContent;