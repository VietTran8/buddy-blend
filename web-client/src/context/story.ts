import { createContext } from "react";
import { LatestStoryResponse } from "../types";

export type StoryContextType = {
    latestStories: LatestStoryResponse[],
    isLoadingStories: boolean
}

export const StoryContext = createContext<StoryContextType>({
    latestStories: [],
    isLoadingStories: false
});