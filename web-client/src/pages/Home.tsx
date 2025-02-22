import { FC } from "react";
import { NewsFeed, PostCreator, StoryList } from "../components";

interface IProps {};

const Home:FC<IProps> = ({ }) => {
    return (
        <div className="w-full">
            <StoryList />
            <PostCreator className="mt-3"/>
            <NewsFeed />
        </div>
    )
};

export default Home;