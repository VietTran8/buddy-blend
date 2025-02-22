import { FC } from "react";
import { EStoryFont, EStoryType, Story } from "../../../types";
import { useQueryStoryViewers } from "../../../hooks";
import { Avatar } from "antd";
import { getReactionIcon } from "../../../utils";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faEye, faPlus } from "@fortawesome/free-solid-svg-icons";
import { BackgroundKey, STORY_BACKGROUND, STORY_FONTS } from "../../../constants";
import { Link } from "react-router-dom";

interface IProps {
    story: Story;
    className?: string;
};

const StoryViewersModalContent: FC<IProps> = ({ story, className }) => {
    const { data: viewersResponse, isLoading } = useQueryStoryViewers(story.id);

    const viewers = viewersResponse?.data;

    return (
        <div className={`w-full ${className} max-h-[450px] overflow-y-auto no-scrollbar`}>
            <div className="mb-5 flex gap-x-3 justify-center">
                <div className="h-[300px] w-[35%] rounded-md overflow-hidden bg-gray-950">
                    {
                        story.storyType === EStoryType.TYPE_TEXT ?
                            <div className={`w-full h-full flex items-center justify-center ${STORY_BACKGROUND[(story.background || "bgPrimary") as BackgroundKey]}`}>
                                <h1 className={`text-white md:text-sm text-xs w-[80%] text-center select-none ${STORY_FONTS[story.font as EStoryFont]} text-wrap`}>{story.content}</h1>
                            </div> :
                            <div className={`w-full h-full flex items-center justify-center`}>
                                <img src={story.mediaUrl} className="object-cover w-full h-auto" />
                            </div>
                    }
                </div>
                <div className={`h-[300px] w-[35%] rounded-md overflow-hidden bg-blue-200 flex justify-center items-center`}>
                    <Link to={'/story/create'} className="flex flex-col items-center gap-y-2 cursor-pointer group">
                        <div className="h-14 w-14 flex justify-center items-center rounded-full flex-shrink-0 bg-gray-100">
                            <FontAwesomeIcon icon={faPlus} className="text-lg text-blue-600" />
                        </div>
                        <div>
                            <p className="font-medium text-base text-blue-600 transition-all">Tạo tin mới</p>
                        </div>
                    </Link>
                </div>
            </div>
            <h2 className="font-semibold flex items-center gap-x-2 text-gray-500 mb-5">
                <span className="text-base">{`Người xem`}</span>
                <FontAwesomeIcon icon={faEye} className="mt-0.5 " />
                <span className="text-base">{viewers?.length}</span>
            </h2>
            <div className="flex flex-col gap-y-2">
                {viewers?.map((viewer) => (
                    <Link to={`/user/${viewer.user.id}`} key={viewer.id} className="flex items-center cursor-pointer gap-x-3 p-2 hover:bg-gray-100 rounded-md transition-all">
                        <Avatar size={50} src={viewer.user.profilePicture || "/images/default-user.png"} />
                        <div>
                            <h1 className="text-gray-700 font-semibold text-base">{viewer.user.userFullName}</h1>
                            {viewer.reactions.length > 0 && <div className="flex items-center rounded-full mt-0.5 py-1 px-2 bg-gray-100 w-fit">
                                {viewer.reactions.map((reaction, index) => (
                                    <img key={reaction.id} src={getReactionIcon(reaction.type)} className={`h-6 w-6 object-cover ${index != 0 && '-ms-2'} outline-4 outline-white`} />
                                ))}
                            </div>}
                        </div>
                    </Link>
                ))}
                {isLoading && <span className="font-semibold text-center py-10">Loading...</span>}
            </div>
        </div>
    );
};

export default StoryViewersModalContent;