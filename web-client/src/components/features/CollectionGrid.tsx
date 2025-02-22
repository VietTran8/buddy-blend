import { FC } from "react";
import { STORY_BACKGROUND } from "../../constants";
import { useDeleteUserFavourite, useQueryUserFavourites } from "../../hooks";
import { Empty, Popover, Skeleton } from "antd";
import { Link } from "react-router-dom";
import { EllipsisVertical } from "lucide-react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faTrash } from "@fortawesome/free-solid-svg-icons";

interface IProps { };

const backgroundValues = Object.values(STORY_BACKGROUND);

const CollectionGrid: FC<IProps> = ({ }) => {
    const { data: response, isLoading } = useQueryUserFavourites();
    const { mutate: deleteCollection } = useDeleteUserFavourite();

    const collections = response?.data;

    const handleDeleteCollection = (id: string) => {
        deleteCollection(id);
    }

    return <div className="grid grid-cols-2 gap-3 w-full">
        {collections && collections.length > 0 ? collections.map((collection, index) => (
            <div key={index} className="rounded-md p-2 bg-white group transition-all">
                <div className="w-full h-[100px] rounded-md overflow-hidden relative">
                    <div className={`group-hover:scale-125 transition-all h-full flex justify-center items-center ${backgroundValues[index % backgroundValues.length]}`}>
                        <p className="text-center w-full text-white font-bold text-[30px]">{collection.name[0].toUpperCase()}</p>
                    </div>\
                    <Popover trigger={"click"} content={(<ul className="">
                        <li onClick={() => handleDeleteCollection(collection.id)} className="hover:bg-gray-50 p-2 cursor-pointer">
                            <FontAwesomeIcon icon={faTrash} className="text-[16px] text-gray-500" />
                            <span className="text-gray-500 font-semibold ms-2">Xóa bộ sưu tập</span>
                        </li>
                    </ul>)}>
                        <div className="p-1 rounded-full cursor-pointer bg-gray-100 pointer-cursor absolute z-50 top-2 right-2">
                            <EllipsisVertical size={20} className="text-gray-400" />
                        </div>
                    </Popover>
                </div>
                <Link to={`/favourites/${collection.id}`} className="px-2 block mt-2 cursor-pointer">
                    <h1 className="font-semibold text-base group-hover:text-[--primary-color] transition-all">{collection.name}</h1>
                    <span className="text-sm text-gray-400">{`${collection.postCount} bài viết`}</span>
                </Link>
            </div>
        )) : (!isLoading && <div className="col-span-full p-44">
            <Empty description>
                <span className="text-gray-400 font-semibold text-base">Bộ sưu tập hơi trống trải...</span>
            </Empty>
        </div>)}
        {isLoading && Array(6).fill(null).map((_, index) => (
            <div key={index} className="rounded-md p-2 bg-white">
                <Skeleton.Node
                    children
                    className="!w-full h-[100px]"
                    active />
                <div className="mt-2 px-2">
                    <Skeleton.Node className="!w-[90%] !h-[16px]" children active />
                    <Skeleton.Node className="!w-[50%] !h-[12px]" children active />
                </div>
            </div>
        ))}
    </div>
};

export default CollectionGrid;