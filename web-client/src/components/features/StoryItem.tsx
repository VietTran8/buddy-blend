import { FC, useEffect, useState } from "react";
import { EPrivacy, EReactionType, EStoryFont, EStoryType, Story } from "../../types";
import { Avatar, Modal } from "antd";
import { getTimeDiff } from "../../utils";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { ChevronRight, Dot } from "lucide-react";
import { faEarthAmericas, faLock, faUserGroup } from "@fortawesome/free-solid-svg-icons";
import { BackgroundKey, STORY_BACKGROUND, STORY_FONTS } from "../../constants";
import { Link } from "react-router-dom";
import { Action } from "react-insta-stories/dist/interfaces";
import { useDoStoryReact } from "../../hooks";
import StoryViewersModalContent from "./modals/StoryViewersModalContent";

interface IProps {
    story: Story;
    action: Action;
};

const StoryItem: FC<IProps> = ({ story, action }) => {
    const { mutate: doReact, isPending } = useDoStoryReact();
    const [openViewersModal, setOpenViewersModal] = useState<boolean>(false);

    const handleOnReactionClick = (type: EReactionType) => {
        if (!isPending) {
            doReact({
                storyId: story.id,
                type
            });
        }
    }

    useEffect(() => {
        const timer = setTimeout(() => {
            if (openViewersModal) {
                action("pause");
            } else {
                action("play");
            }
        }, 0);

        return () => clearTimeout(timer);
    }, [openViewersModal]);

    return (
        <div className="h-full w-full relative">
            <div style={{ zIndex: 1000 }} className="absolute top-6 px-3 left-0 flex items-start gap-x-3">
                <Link onMouseEnter={() => action("pause")} onMouseLeave={() => action("play")} to={`/user/${story.user.id}`}>
                    <Avatar size={"large"} className="border border-white shadow-md" src={story.user.profilePicture || "/images/default-user.png"} />
                </Link>
                <div className="">
                    <Link onMouseEnter={() => action("pause")} onMouseLeave={() => action("play")} to={`/user/${story.user.id}`}>
                        <h1 className="font-semibold text-white -mt-0.5 text-base line-clamp-1">{story.user.userFullName}</h1>
                    </Link>
                    <div className="flex items-center">
                        <span className="text-xs text-white">{getTimeDiff(story.createdAt)}</span>
                        <Dot size={20} className="text-white pt-1" />
                        <span className="text-xs text-white pt-0.5">
                            <FontAwesomeIcon icon={story.privacy === EPrivacy.PUBLIC ? faEarthAmericas :
                                story.privacy === EPrivacy.ONLY_FRIENDS ? faUserGroup : faLock
                            } className="text-xs" />
                        </span>
                    </div>
                </div>
            </div>
            {
                story.storyType === EStoryType.TYPE_TEXT ?
                    <div className={`w-full h-full flex items-center justify-center ${STORY_BACKGROUND[(story.background || "bgPrimary") as BackgroundKey]}`}>
                        <h1 className={`text-white text-lg w-[80%] text-center ${STORY_FONTS[story.font as EStoryFont]} text-wrap`}>{story.content}</h1>
                    </div> :
                    <div className={`w-full h-full flex items-center justify-center`}>
                        <img src={story.mediaUrl} className="object-cover w-full h-auto" />
                    </div>
            }
            {story.mine ? <div style={{ zIndex: 1000 }} className="p-4 w-full absolute bottom-0 bg-gradient-to-t from-gray-950 to-transparent">
                <span onClick={() => {
                    action("play");
                    setOpenViewersModal(true);
                }} className="text-white font-semibold text-base flex gap-x-1 items-center cursor-pointer w-fit">
                    <span className="">{`${story.viewCount} người xem`}</span>
                    <ChevronRight className="mt-0.5" size={19} />
                </span>
            </div> : <div onMouseEnter={() => action("pause")} onMouseLeave={() => action("play")} style={{ zIndex: 1000 }} className="px-2 w-full absolute bottom-2">
                <div className="backdrop-filter backdrop-blur-sm w-full px-1 py-2 bg-slate-800/20 rounded-full flex gap-x-1 justify-center">
                    <img onClick={() => handleOnReactionClick(EReactionType.LIKE)} src="/icons/reactions/like.png" className={`cursor-pointer hover:scale-125 transition-all w-12 h-12`} />
                    <img onClick={() => handleOnReactionClick(EReactionType.HEART)} src="/icons/reactions/heart.png" className={`cursor-pointer hover:scale-125 transition-all w-12 h-12`} />
                    <img onClick={() => handleOnReactionClick(EReactionType.HAHA)} src="/icons/reactions/haha.png" className={`cursor-pointer hover:scale-125 transition-all w-12 h-12`} />
                    <img onClick={() => handleOnReactionClick(EReactionType.WOW)} src="/icons/reactions/wow.png" className={`cursor-pointer hover:scale-125 transition-all w-12 h-12`} />
                    <img onClick={() => handleOnReactionClick(EReactionType.SAD)} src="/icons/reactions/sad.png" className={`cursor-pointer hover:scale-125 transition-all w-12 h-12`} />
                    <img onClick={() => handleOnReactionClick(EReactionType.ANGRY)} src="/icons/reactions/angry.png" className={`cursor-pointer hover:scale-125 transition-all w-12 h-12`} />
                </div>
            </div>}

            <Modal
                title={<h1 className="md:text-lg mb-5 text-base text-center font-semibold">Lượt xem tin</h1>}
                centered
                open={openViewersModal}
                onCancel={() => setOpenViewersModal(false)}
                width={550}
                footer
            >
                <StoryViewersModalContent story={story} />
            </Modal>
        </div>
    );
};

export default StoryItem;