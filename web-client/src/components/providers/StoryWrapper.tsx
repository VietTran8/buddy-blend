import { FC } from "react";
import { useQueryStories } from "../../hooks";
import { Outlet } from "react-router-dom";
import { StoryContext } from "../../context/story";

interface IProps { };

const StoryWrapper: FC<IProps> = ({ }) => {
    const { data: storiesResponse, isLoading: isLoadingStories } = useQueryStories();
    const latestStories = storiesResponse?.data;

    return <StoryContext.Provider value={{ latestStories: latestStories || [], isLoadingStories }}>
        <Outlet />
    </StoryContext.Provider>
};

export default StoryWrapper;