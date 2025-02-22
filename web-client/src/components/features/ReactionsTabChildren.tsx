import { FC } from "react";
import { Reaction } from "../../types";
import { randomUUID } from "../../utils";
import ReactedUserItem from "./ReactedUserItem";

interface IProps {
    type: "haha" | "sad" | "like" | "hearth" | "wow" | "angry";
    reactions: Reaction[];
    postId?: string;
    cmtId?: string;
};

const ReactionsTabChildren: FC<IProps> = ({ type, reactions, postId, cmtId }) => {
    return <div className="max-h-[70vh] overflow-y-auto no-scrollbar">
        {reactions.map((reaction) => (
            <ReactedUserItem key={randomUUID()} postId={postId} cmtId={cmtId} reaction={reaction} type={type} />
        ))}
    </div>
};

export default ReactionsTabChildren;