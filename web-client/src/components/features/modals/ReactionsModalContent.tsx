import { Tabs } from "antd";
import { FC } from "react";
import ReactionsTabChildren from "../ReactionsTabChildren";
import { EReactionType, Post, Reaction, Comment } from "../../../types";
import { useQueryCmtReactions, useQueryPostReactions } from "../../../hooks";

interface IProps {
    post?: Post,
    comment?: Comment,
};

const ReactionsModalContent: FC<IProps> = ({ post, comment }) => {
    const { data: postReactionResp, isLoading: isPostReactionLoading } = useQueryPostReactions(post?.id);
    const { data: cmtReactionResp, isLoading: isCmtReactionLoading } = useQueryCmtReactions(comment?.id);

    const reactions: Record<EReactionType, Reaction[]> | undefined = cmtReactionResp?.data || postReactionResp?.data;
    const isLoading = isCmtReactionLoading || isPostReactionLoading;

    return (<Tabs
        defaultActiveKey="1"
        items={reactions && Object.keys(reactions).map((type, i) => {
            const id = String(i + 1);
            
            return {
                key: id,
                label: <span className="font-semibold text-gray-600 -ms-2">{reactions[type as EReactionType].length}</span>,
                children: isLoading ? <p className="text-center">Loading...</p> : <ReactionsTabChildren postId={post?.id} cmtId={comment?.id} reactions={reactions[type as EReactionType]} type={type.toLowerCase() as any} />,
                icon: <img src={`/icons/reactions/${type.toLowerCase()}.png`} className="w-7 h-7 object-cover inline-block" />,
            };
        })}
    />)
};

export default ReactionsModalContent;