import { FC } from "react";
import { useQueryUserFavouritePosts } from "../../hooks";
import { Post, PostSkeleton } from "../../components";
import { Link, useParams } from "react-router-dom";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faCaretLeft } from "@fortawesome/free-solid-svg-icons";
import { Empty } from "antd";

interface IProps { };

const FavouriteDetails: FC<IProps> = ({ }) => {
    const { id } = useParams();
    const { data: response, isLoading } = useQueryUserFavouritePosts(id);

    const collection = response?.data;

    return (
        <div className="pb-10">
            <div className="p-5 bg-white rounded-md flex items-center">
                <h1 className="font-bold flex-1 lg:text-lg text-base">{`Bài viết trong ${collection?.name} (${collection?.postCount})`}</h1>
                <Link to={'/favourites'} className="btn-primary">
                    <FontAwesomeIcon icon={faCaretLeft} className="me-2" />
                    <span>Trở lại</span>
                </Link>
            </div>
            <div className="mt-4 flex flex-col gap-y-3">
                {collection && collection.posts.length > 0 ?
                    <div className="flex flex-col gap-y-2">
                        {collection?.posts.slice().reverse().map((item) => (<div key={item.id}>
                            {item && <Post collectionName={collection.name} isFavouritePost post={item} />}
                        </div>))}
                    </div> : (!isLoading && <div className="py-44">
                        <Empty description>
                            <span className="text-gray-400 font-semibold text-base">Chưa có bài viết nào trong bộ sưu tập này...</span>
                        </Empty>
                    </div>)
                }
                {isLoading && Array(10).fill(null).map((_, index) => <PostSkeleton key={index} />)}
            </div>
        </div>
    )
};

export default FavouriteDetails;