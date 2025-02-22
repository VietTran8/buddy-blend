import { BaseResponse, DoReactRequest, EReactionType, PaginationResponse, Post, TopReact } from "@/types"
import { useState } from "react";
import { useDoReact } from "./reaction";
import { InfiniteData, useQueryClient } from "@tanstack/react-query";
import toast from "react-hot-toast";

type UseReactionType = {
    post?: Post;
    postIndex?: number;
    pageIndex?: number;
    setOpenReactionPopover: (value: React.SetStateAction<boolean>) => void;
}

export const useReaction = ({ post, postIndex, pageIndex, setOpenReactionPopover }: UseReactionType) => {
    const queryClient = useQueryClient();

    const [currentReaction, setCurrentReaction] = useState<EReactionType | undefined>(post?.reacted);
    const [currentTopReacts, setCurrentTopReacts] = useState<{
        topReacts?: TopReact[],
        noReactions?: number;
    }>({ topReacts: post?.topReacts, noReactions: post?.noReactions });

    const { mutate: doReact } = useDoReact();

    const handleOnReactionChange = (type: EReactionType) => {
        setCurrentReaction(type === currentReaction ? undefined : type);

        const doReactPayload: DoReactRequest = {
            postId: post?.id || "",
            type
        }

        doReact(doReactPayload, {
            onSuccess: (data) => {
                toast.success(data.message);

                setCurrentTopReacts(prev => {
                    const currentNoReaction = prev.noReactions || 0;

                    let reaction = {
                        noReactions: currentNoReaction,
                        topReacts: data.data
                    }

                    if (!currentReaction)
                        reaction = {
                            noReactions: currentNoReaction + 1,
                            topReacts: data.data
                        }

                    else if (type === currentReaction) {
                        reaction = {
                            noReactions: currentNoReaction - 1,
                            topReacts: data.data
                        }
                    }

                    else {
                        reaction = {
                            noReactions: prev.noReactions || 0,
                            topReacts: data.data
                        }
                    }

                    if (pageIndex !== undefined && postIndex !== undefined) {
                        queryClient.setQueryData(['news-feed'], (oldData: InfiniteData<BaseResponse<PaginationResponse<Post>>>) => {
                            if (!oldData) return oldData;

                            const newData = { ...oldData };
                            const currentPost = newData.pages[pageIndex]?.data.data[postIndex];

                            if (currentPost) {
                                newData.pages[pageIndex].data.data[postIndex] = {
                                    ...currentPost,
                                    noReactions: reaction.noReactions,
                                    topReacts: reaction.topReacts,
                                    reacted: type === currentReaction ? undefined : type
                                };
                            }

                            return newData;
                        });
                    }

                    return reaction;
                });
            },
            onError: () => {
                setCurrentReaction(post?.reacted);
                toast.error("Lỗi khi bày tỏ cảm xúc, vui lòng thử lại!");
            }
        })

        setOpenReactionPopover(false);
    }

    return {
        currentReaction,
        currentTopReacts,
        handleOnReactionChange
    }
}