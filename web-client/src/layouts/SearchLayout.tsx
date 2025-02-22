import { FC } from "react";
import { Header, SearchSideBar } from "../components";
import { Outlet, useSearchParams } from "react-router-dom";
import { useSearchResult } from "@/hooks";
import { SearchResponse } from "@/types";

interface IProps { };

export type SearchOutletContextType = {
    searchResults: SearchResponse;
    isLoading: boolean;
}

const SearchLayout: FC<IProps> = ({ }) => {
    const [ searchParams ] = useSearchParams();
    const query = searchParams.get("q");

    const { data: searchResponse, isLoading } = useSearchResult(query);

    const searchResults = searchResponse?.data || { posts: [], users: [] };

    const context: SearchOutletContextType = { searchResults, isLoading }

    return (
        <div className="flex flex-col">
            <Header className="fixed top-0"/>
            <div className="grid-cols-12 gap-x-4 mt-24 rounded-md grid relative pb-10">
                <SearchSideBar className="md:col-span-3 md:mb-0 mb-2 col-span-full md:sticky top-24" />
                <div className="md:col-span-9 lg:col-span-8 xl:col-span-7 2xl:col-span-6 col-span-full">
                    <main className="">
                        <Outlet context={context} />
                    </main>
                </div>
            </div>
        </div>
    );
};

export default SearchLayout;