import { Avatar, Button } from "antd";
import { ArrowLeft, ArrowRight, Plus } from "lucide-react";
import { FC, useContext, useRef } from "react";
import { Link } from "react-router-dom";
import { AuthContext } from "../../context";
import { EStoryFont, EStoryType } from "../../types";
import { BackgroundKey, STORY_BACKGROUND, STORY_FONTS } from "../../constants";
import { StoryContext, StoryContextType } from "../../context/story";

interface IProps { };

const StoryList: FC<IProps> = ({ }) => {
    const { user: authUser } = useContext(AuthContext);
    const { latestStories } = useContext<StoryContextType>(StoryContext);
    const containerRef = useRef<HTMLDivElement>(null);

    const scrollToNextItem = (direction: string) => {
        if (containerRef.current) {
            const scrollAmount = containerRef.current.clientWidth;
            containerRef.current.scrollBy({
                left: direction === "right" ? scrollAmount : -scrollAmount,
                behavior: "smooth",
            });
        }
    };

    return (
        <section className="w-full relative group">
            <div className="flex gap-x-3 w-full overflow-y-auto no-scrollbar" ref={containerRef}>
                <Link to="/story/create" className="cursor-pointer border border-[--primary-color] flex-shrink-0 rounded-md md:w-[150px] w-[120px] md:h-[235px] h-[200px] overflow-hidden relative">
                    <img src={authUser?.profilePicture || "/images/default-user.png"} className="w-full h-full object-cover"></img>
                    <div className="bg-gradient-to-t from-zinc-900 to-transparent absolute bottom-0 left-0 h-[50%] w-full"></div>
                    <div className="absolute bottom-0 left-0 w-full flex flex-col items-center mb-3">
                        <Button shape="circle"><Plus size={24} /></Button>
                        <p className="mt-2 font-semibold text-white">Tạo Story</p>
                    </div>
                </Link>
                {latestStories?.map((story, index) => (
                    <Link to={`/story/view/${story.user.id}`} key={index} className="cursor-pointer rounded-md flex-shrink-0 md:w-[150px] w-[120px] md:h-[235px] h-[200px] overflow-hidden relative">
                        {story.latestStory.storyType === EStoryType.TYPE_MEDIA ? <img
                            src={story.latestStory.mediaUrl}
                            className="w-full h-full object-cover">
                        </img> : <div className={`w-full h-full flex items-center justify-center ${(STORY_BACKGROUND[(story.latestStory.background || "bgPrimary") as BackgroundKey])}`}>
                            <span className={`text-xs text-wrap ${STORY_FONTS[story.latestStory.font as EStoryFont]} select-none text-white text-center mx-3`}>{story.latestStory.content}</span>
                        </div>}
                        <div className="bg-gradient-to-t from-zinc-900 to-transparent absolute bottom-0 left-0 h-[50%] w-full"></div>
                        <div className="absolute bottom-3 left-3">
                            <Avatar className={`${story.seenAll ? 'border-neutral-300' : 'border-[--dark-primary-color]'} border-4`} size="large" src={story.user.profilePicture || "/images/default-user.png"} />
                            <p className="mt-2 font-semibold text-white line-clamp-1 w-full">{story.user.userFullName}</p>
                        </div>
                    </Link>
                ))}
            </div>
            <Button
                shape="circle"
                size="large"
                type="primary"
                className="absolute group-hover:opacity-100 transition-all opacity-0 top-[50%] -translate-y-[50%] left-5"
                onClick={() => scrollToNextItem("left")}
            >
                <ArrowLeft size={24} className="text-white" />
            </Button>
            <Button
                shape="circle"
                size="large"
                type="primary"
                className="group-hover:opacity-100 transition-all opacity-0 absolute top-[50%] -translate-y-[50%] right-5"
                onClick={() => scrollToNextItem("right")}
            >
                <ArrowRight size={24} className="text-white" />
            </Button>

        </section >
    );

};

export default StoryList;