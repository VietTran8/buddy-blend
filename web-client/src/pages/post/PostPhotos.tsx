import { FC, useEffect, useRef, useState } from "react";
import { Link, useNavigate, useParams, useSearchParams } from "react-router-dom";
import { useQueryPost } from "../../hooks";
import { Avatar, Button, Popover } from "antd";
import { getPrivacyDesc, getTimeDiff } from "../../utils";
import { ArrowLeft, ArrowRight, BookImage, Dot, X } from "lucide-react";
import { PostActionPopoverContent, VideoPlayer } from "../../components";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faEllipsis } from "@fortawesome/free-solid-svg-icons";
import { EFileType, Media } from "@/types";

interface IProps {

};

const PostPhotos: FC<IProps> = ({ }) => {
    const navigate = useNavigate();
    const { id } = useParams();
    const [params] = useSearchParams();
    const index = params.get("i") || 0;

    const { data: postResponse } = useQueryPost(id);

    const [currentIndex, setCurrentIndex] = useState<number>(0)

    const containerRef = useRef<HTMLDivElement | null>(null);

    const fetchedPost = postResponse?.data;
    const mediaLists: Media[] = fetchedPost?.medias || [];

    useEffect(() => {
        if (mediaLists) {
            const parsedIndex = parseInt(index || "0", 10);
            console.log(parsedIndex);

            if (Number.isNaN(parsedIndex) || parsedIndex > (mediaLists.length - 1)) {
                setCurrentIndex(0);
            } else {
                setCurrentIndex(parsedIndex);
            }
        }
    }, [index, fetchedPost]);

    const handleChangeMedia = (type: "next" | "prev") => {
        if(type === "next") {
            setCurrentIndex((currentIndex + 1) % (mediaLists?.length || 0))
        } else {
            setCurrentIndex((currentIndex - 1) >= 0 ? (currentIndex - 1) : (mediaLists?.length  || 0) - 1)
        }
    }

    return <div className="w-full h-screen-except-header grid-cols-12 grid">
        <div ref={containerRef} className="col-span-9 h-screen-except-header flex items-center justify-center group bg-black relative">
            {mediaLists && (mediaLists[currentIndex]?.type === EFileType.TYPE_IMG ? 
                <img src={mediaLists[currentIndex]?.url} className={"max-w-full max-h-full object-contain"}/>
            : <VideoPlayer className="max-w-full max-h-full object-contain" poster={mediaLists[currentIndex]?.thumbnail} src={mediaLists[currentIndex]?.url} />)}
            <Button
                onClick={() => handleChangeMedia("prev")}
                shape="circle"
                size="large"
                type="primary"
                className="absolute group-hover:opacity-100 transition-all opacity-0 top-[50%] -translate-y-[50%] left-10"
            >
                <ArrowLeft size={24} className="text-white" />
            </Button>
            <Button
                onClick={() => handleChangeMedia("next")}
                shape="circle"
                size="large"
                type="primary"
                className="group-hover:opacity-100 transition-all opacity-0 absolute top-[50%] -translate-y-[50%] right-10"
            >
                <ArrowRight size={24} className="text-white" />
            </Button>
            <div onClick={() => navigate(-1)} className="h-12 w-12 rounded-full group-hover:opacity-65 cursor-pointer flex items-center justify-center bg-white opacity-0 transition-all absolute top-4 left-5">
                <X size={20} className="text-black"/>
            </div>
        </div>
        <div className="col-span-3 bg-white p-4">
            <hr />
            <div className="py-5 flex items-center gap-x-2">
                <BookImage className="text-gray-500" />
                <span className="text-gray-500 text-sm flex-1">Ảnh này nằm trong một bài viết</span>
                <Link to={`/post/${fetchedPost?.id}`} className="font-semibold">Xem bài viết</Link>
            </div>
            <hr />
            <div className="flex gap-x-3 items-center mt-4">
                <Avatar className="flex-shrink-0" size={50} src={fetchedPost?.user.profilePicture || "/images/default-user.png"} />
                <div className="flex-1">
                    <h1 className="text-base font-semibold line-clamp-1">{fetchedPost?.user.userFullName}</h1>
                    <div className="flex gap-x-1 items-center text-gray-400">
                        <span className="text-sm">{getTimeDiff(fetchedPost?.createdAt)}</span>
                        <Dot size={20} />
                        {getPrivacyDesc(fetchedPost?.privacy, "text-sm !text-gray-400").icon}
                    </div>
                </div>
                <Popover
                    content={<PostActionPopoverContent post={fetchedPost} />}
                    trigger={"click"}
                >
                    <FontAwesomeIcon icon={faEllipsis} className="text-gray-500 cursor-pointer me-3" />
                </Popover>
            </div>
            <p className="py-4">{fetchedPost?.content}</p>
            <hr />
        </div>
    </div>
};

export default PostPhotos;