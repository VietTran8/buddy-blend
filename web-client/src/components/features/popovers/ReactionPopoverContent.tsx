import { FC } from "react";
import { EReactionType } from "../../../types";

interface IProps {
    onReactionChange: (type: EReactionType) => void
};

const ReactionPopoverContent: FC<IProps> = ({ onReactionChange }) => {
    const handleOnReactionClick = (type: EReactionType) => {
        onReactionChange(type);
    }

    return <div className="flex gap-x-1 items-center">
        <img onClick={() => handleOnReactionClick(EReactionType.LIKE)} src="/icons/reactions/like.png" className="cursor-pointer hover:scale-150 transition-all w-10 h-10" />
        <img onClick={() => handleOnReactionClick(EReactionType.HEART)} src="/icons/reactions/heart.png" className="cursor-pointer hover:scale-150 transition-all w-10 h-10" />
        <img onClick={() => handleOnReactionClick(EReactionType.HAHA)} src="/icons/reactions/haha.png" className="cursor-pointer hover:scale-150 transition-all w-10 h-10" />
        <img onClick={() => handleOnReactionClick(EReactionType.WOW)} src="/icons/reactions/wow.png" className="cursor-pointer hover:scale-150 transition-all w-10 h-10" />
        <img onClick={() => handleOnReactionClick(EReactionType.SAD)} src="/icons/reactions/sad.png" className="cursor-pointer hover:scale-150 transition-all w-10 h-10" />
        <img onClick={() => handleOnReactionClick(EReactionType.ANGRY)} src="/icons/reactions/angry.png" className="cursor-pointer hover:scale-150 transition-all w-10 h-10" />
    </div>
};

export default ReactionPopoverContent;