import { FC, useContext, useState, useEffect } from "react";
import Stories from 'react-insta-stories';
import { StoryContext } from "../../context/story";
import { Link, useNavigate, useParams, useSearchParams } from "react-router-dom";
import { useCountStoryView, useQueryUserStories } from "../../hooks";
import { getTimeDiff, randomUUID } from "../../utils";
import { Header, NotFound, StoryItem } from "../../components";
import { Avatar, Breadcrumb, Spin } from "antd";
import { AuthContext } from "../../context";
import { X } from "lucide-react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faPlus } from "@fortawesome/free-solid-svg-icons";
import { Story } from "react-insta-stories/dist/interfaces";

interface IProps { };

const ViewStory: FC<IProps> = ({ }) => {
    const { id } = useParams();
    const [searchParams] = useSearchParams();

    const { user } = useContext(AuthContext);
    const { latestStories } = useContext(StoryContext);

    const [currentUserId, setCurrentUserId] = useState<string | undefined>(id);
    const [viewedUserIds, setViewedUserIds] = useState<string[]>([]);
    const [storyKey, setStoryKey] = useState(0);

    const navigate = useNavigate();

    const { data: storiesResponse, isLoading } = useQueryUserStories(currentUserId);
    const { mutate: countView, isPending: isCounting } = useCountStoryView();

    const storyId = searchParams.get("sid");

    useEffect(() => {
        setStoryKey(prev => prev + 1);

        if (storiesResponse?.data.length === 0)
            navigate("/");

    }, [storiesResponse]);

    const breadcrumbItems = [
        { title: <Link to="/">Trang chủ</Link> },
        { title: <Link to={`/story/view/${id}`}>Xem tin</Link> }
    ];

    const stories = storiesResponse?.data.map((story) => ({
        content: ({ action }) => <StoryItem
            action={action}
            key={story.id}
            story={story}
        />
    } as Story)) || [{}];

    const handleAllStoriesEnded = () => {
        if (currentUserId) {
            setViewedUserIds((prev) => {
                if (!prev.includes(currentUserId)) {
                    return [...prev, currentUserId];
                }
                return prev;
            });

            if (viewedUserIds.length + 1 >= latestStories.length) {
                navigate("/");
                return;
            }

            let nextUserIndex = (latestStories.findIndex(story => story.user.id === currentUserId) + 1) % latestStories.length;
            let nextUser = latestStories[nextUserIndex].user;

            while (viewedUserIds.includes(nextUser.id)) {
                nextUserIndex = (nextUserIndex + 1) % latestStories.length;
                nextUser = latestStories[nextUserIndex].user;

                if (nextUserIndex === latestStories.findIndex(story => story.user.id === currentUserId)) {
                    navigate("/");
                    return;
                }
            }

            setCurrentUserId(nextUser.id);
        }
    };



    return (
        <>
            {(storiesResponse && (storiesResponse.data.length || 0) > 0) ? <div className="h-screen w-full">
                <div className="flex flex-col h-screen w-full overflow-y-auto">
                    <Header />
                    <div className="grid grid-cols-12 gap-4 h-screen overflow-y-auto">
                        <div className="md:col-span-3 col-span-full p-5 bg-white overflow-y-auto custom-scroll md:block hidden">
                            <Breadcrumb items={breadcrumbItems} className="mb-2" />
                            <div className="flex items-center gap-x-3">
                                <Link to="/">
                                    <div className="h-12 w-12 flex items-center bg-gray-100 hover:bg-gray-200 cursor-pointer transition-all justify-center rounded-full">
                                        <X size={20} className="text-gray-500" />
                                    </div>
                                </Link>
                                <h1 className="lg:text-xl text-lg font-bold">Xem tin</h1>
                            </div>
                            <div className="flex items-center gap-x-3 my-4">
                                <Avatar size="large" className="flex-shrink-0" src={user?.profilePicture || "/images/default-user.png"} />
                                <h1 className="font-semibold text-base -mb-1">{user?.userFullName}</h1>
                            </div>
                            <hr />
                            <Link to={'/story/create'} className="flex items-center gap-x-3 my-3 cursor-pointer group">
                                <div className="h-14 w-14 flex justify-center items-center rounded-full flex-shrink-0 bg-gray-200">
                                    <FontAwesomeIcon icon={faPlus} className="text-lg text-[--primary-color]" />
                                </div>
                                <div>
                                    <p className="font-medium text-base group-hover:text-[--primary-color] transition-all">Tạo tin mới</p>
                                    <span className="text-gray-500 text-sm">Bạn có thể chia sẻ hình ảnh, hay viết gì đó...</span>
                                </div>
                            </Link>
                            <hr />
                            <div className="mt-3">
                                <h2 className="lg:text-lg text-base font-bold mb-1 text-gray-600">Tất cả tin</h2>
                                <div className="flex flex-col gap-y-2">
                                    {latestStories.map(story => (
                                        <div
                                            key={randomUUID()}
                                            onClick={() => {
                                                setViewedUserIds([]);
                                                setCurrentUserId(story.user.id);
                                            }}
                                            className={`p-3 flex hover:bg-gray-50 ${story.user.id === currentUserId && 'bg-gray-100'} rounded-md transition-all cursor-pointer gap-x-2 items-center`}>
                                            <Avatar className={`border-4 ${story.seenAll ? 'border-neutral-300' : 'border-[--dark-primary-color] flex-shrink-0'}`} size={"large"} src={story.user.profilePicture || "/images/default-user.png"} />
                                            <div>
                                                <h3 className="font-semibold text-gray-700 text-base line-clamp-1">{story.user.userFullName}</h3>
                                                <span className="text-sm text-[--primary-color]">{getTimeDiff(story.latestStory.createdAt)}</span>
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            </div>
                        </div>
                        <div className="md:col-span-9 max-h-screen col-span-full flex items-center justify-center bg-black overflow-y-auto py-2">
                            <div className="h-full rounded-md overflow-hidden">
                                <Stories
                                    key={storyKey}
                                    height={"100%"}
                                    onAllStoriesEnd={handleAllStoriesEnded}
                                    stories={stories}
                                    defaultInterval={10000}
                                    currentIndex={storyId ? storiesResponse?.data.findIndex(story => story.id === storyId) : 0}
                                    onStoryStart={(storyIndex: number) => {
                                        const currentStory = storiesResponse?.data[storyIndex];

                                        if (!currentStory?.seen && !currentStory?.mine && !isCounting)
                                            countView(currentStory?.id || "");
                                    }}
                                />
                            </div>
                        </div>
                    </div>
                </div>
            </div> : <>
                {isLoading ? <div className="bg-white w-full h-dvh flex justify-center items-center">
                    <Spin size="large" />
                </div> : <NotFound />}
            </>}
        </>
    )
};

export default ViewStory;
