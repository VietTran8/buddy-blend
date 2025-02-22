import { FC, memo } from "react";
import VideoPlayer from "../shared/VideoPlayer";
import { randomUUID } from "../../utils";
import { Link } from "react-router-dom";
import { EFileType, Media } from "@/types";
import { Image } from "antd";

interface IProps {
    className?: string;
    medias: Media[];
    postId?: string;
}

const PostGallery: FC<IProps> = ({ className, medias, postId }) => {
    const maxVisibleItems = 5;
    const extraItemsCount = medias.length - maxVisibleItems;

    const getGridClass = () => {
        switch (Math.min(medias.length, maxVisibleItems)) {
            case 1: return "grid-cols-1";
            case 2: case 3:
                return "grid-cols-2";
            case 4: return "grid-cols-2 grid-rows-2";
            default: return "grid-cols-6 grid-rows-2";
        }
    };

    return (
        <div className={`grid gap-1 ${getGridClass()} ${className}`}>
            {medias.slice(0, maxVisibleItems - (extraItemsCount > 0 ? 1 : 0)).map((item, index) => (
                <div key={randomUUID()} className={`${medias.length === 3 && index === 2 && 'col-span-full'} relative overflow-hidden ${medias.length === 1 ? 'h-auto max-h-[500px]' : `h-[230px]`} ${medias.length >= 5 ? `${index < 3 ? 'col-span-2 h-[200px]' : 'col-span-3 h-[230px]'}` : ''}`}>
                    {item.type === EFileType.TYPE_VIDEO ? (
                        <VideoPlayer
                            src={item.url}
                            className="w-full h-full object-cover"
                        />
                    ) : (
                        <div className="h-full w-full block">
                            <Image
                                preview={{ mask: <></> }}
                                src={item.url}
                                alt="post image"
                                height={"100%"}
                                width={"100%"}
                                className="object-cover"
                            />
                        </div>
                    )}
                </div>
            ))}

            {extraItemsCount > 0 && (
                <div className="relative overflow-hidden col-span-3 h-[230px]">
                    {medias[maxVisibleItems - 1].type === EFileType.TYPE_VIDEO ? (
                        <VideoPlayer
                            src={medias[maxVisibleItems - 1].url}
                            className="w-full h-full object-cover opacity-50 rounded-md"
                        />
                    ) : (
                        <img
                            src={medias[maxVisibleItems - 1].url}
                            alt="post image"
                            className="object-cover opacity-50 rounded-md"
                        />
                    )}
                    <Link to={`/post/${postId}/photos`} className="absolute inset-0 block">
                        <div className="bg-black bg-opacity-50 flex items-center h-full justify-center">
                            <span className="text-white text-xl font-semibold">+{extraItemsCount}</span>
                        </div>
                    </Link>
                </div>
            )}
        </div>
    );
};

export default memo(PostGallery);
