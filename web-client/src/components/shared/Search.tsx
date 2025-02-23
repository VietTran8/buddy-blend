import { FC, useState, useRef, useEffect } from "react";
import { BasicUserItemSkeleton, HtmlText, Input } from "..";
import { Book, Clock, Search as SearchIcon, X } from "lucide-react";
import { Avatar, Empty } from "antd";
import { useClearHistory, useDebounce, useDeleteHistory, useFetchSearchResult, useQuerySearchHistory } from "@/hooks";
import { Link, useNavigate } from "react-router-dom";

interface IProps { }

const Search: FC<IProps> = () => {
    const [isFocused, setIsFocused] = useState(false);
    const [searchTerm, setSearchTerm] = useState("");

    const debouncedSearchTerm = useDebounce(searchTerm, 500);

    const searchRef = useRef<HTMLDivElement>(null);

    const navigate = useNavigate();

    const { data: historyResponse, isLoading: isHistoryResponseLoading } = useQuerySearchHistory();
    const { data: searchResultResponse, isLoading: isFetchingSearchResult } = useFetchSearchResult(debouncedSearchTerm);

    const { mutate: deleteHistory } = useDeleteHistory();
    const { mutate: clearHistory } = useClearHistory();

    const histories = historyResponse?.data || [];
    const searchResults = searchResultResponse?.data || { posts: [], users: [] };

    const handleOnDeleteHistory = (id: string) => {
        deleteHistory(id);
    }

    const handleClearHistory = () => {
        clearHistory();
    }

    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
            if (
                searchRef.current &&
                !searchRef.current.contains(event.target as Node)
            ) {
                setIsFocused(false);
            }
        };

        document.addEventListener("mousedown", handleClickOutside);
        return () => {
            document.removeEventListener("mousedown", handleClickOutside);
        };
    }, []);

    const handleSubmitSearch = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        handleSearch(searchTerm);
    };

    const handleSearch = (query: string) => {
        if (query.trim()) {
            navigate(`/search/top?q=${encodeURIComponent(query)}`);

            setIsFocused(false);
        }
    }

    return (
        <div className="relative w-fit" ref={searchRef}>
            {isFocused && (
                <div
                    className="fixed inset-0 bg-black bg-opacity-30 transition-opacity duration-300 z-40"
                    onClick={() => setIsFocused(false)}
                ></div>
            )}

            <form onSubmit={handleSubmitSearch}>
                <Input
                    placeholder="Bạn đang tìm gì?"
                    startDrawable={
                        <SearchIcon className="text-gray-400" width={20} height={20} />
                    }
                    endDrawable={
                        <>
                            {searchTerm && <div className="rounded-full w-8 h-8 hover:bg-gray-200 flex justify-center items-center cursor-pointer transition-all" onClick={() => setSearchTerm("")}>
                                <X size={20} className="text-gray-500" />
                            </div>}
                        </>
                    }
                    onChange={(e) => setSearchTerm(e.target.value)}
                    value={searchTerm}
                    className="md:block hidden relative z-50 lg:w-[430px]"
                    onFocus={() => setIsFocused(true)}
                />
            </form>

            <div
                className={`absolute w-full min-h-[100px] flex flex-col gap-y-1 px-2 pb-2 overflow-y-auto max-h-[80vh] custom-scroll-no-hover z-50 shadow-md rounded-b-lg mt-1 bg-white border transition-all duration-300 ${isFocused
                    ? "opacity-100 translate-y-0 visible"
                    : "opacity-0 -translate-y-5 invisible"
                    }`}
            >
                {!searchResults || [...searchResults.posts, ...searchResults.users].length == 0 && <div className="px-4 pt-3 pb-4 flex items-center bg-white">
                    <h1 className="font-semibold lg:text-lg text-base flex-1 text-gray-700">Tìm kiếm gần đây</h1>
                    <div onClick={handleClearHistory} className="px-2 py-1 hover:bg-gray-50 transition-all rounded-md">
                        <span className="text-[--dark-primary-color] font-medium cursor-pointer">Xóa tất cả</span>
                    </div>
                </div>}
                {searchResults && [...searchResults.posts, ...searchResults.users].length > 0 ? <>
                    {searchResults.users.length > 0 && <h3 className="font-semibold text-gray-500 m-3">Mọi người</h3>}
                    {searchResults.users.map(user => (<Link to={`/user/${user.id}`} key={user.id} className="flex items-center rounded-md px-4 py-3 hover:bg-gray-50 transition-all cursor-pointer">
                        <Avatar size={"large"} src={user.profilePicture || "/images/default-user.png"} />
                        <p className="flex-1 px-4 line-clamp-1 text-gray-600 font-semibold text-base">
                            <HtmlText>
                                {user.userFullName}
                            </HtmlText>
                        </p>
                        <div className="h-8 w-8 flex-shrink-0 flex items-center justify-center rounded-lg hover:bg-gray-200 transition-all">
                            <X size={20} className="text-gray-500" />
                        </div>
                    </Link>))}
                    {searchResults.posts.length > 0 && <h3 className="font-semibold text-gray-500 mt-4 mx-3 mb-3">Bài viết</h3>}
                    {searchResults.posts.map(post => (<Link to={`/post/${post.id}`} key={post.id} className="flex items-center rounded-md px-4 py-3 hover:bg-gray-50 transition-all cursor-pointer">
                        <div className="h-12 w-12 flex-shrink-0 flex items-center justify-center rounded-full bg-gray-200">
                            <Book size={20} className="text-gray-500" />
                        </div>
                        <p className="flex-1 px-4 line-clamp-1 text-gray-600 font-semibold text-base">{post.content}</p>
                        <div className="h-8 w-8 flex-shrink-0 flex items-center justify-center rounded-lg hover:bg-gray-200 transition-all">
                            <X size={20} className="text-gray-500" />
                        </div>
                    </Link>))}
                </> : <>
                    {!debouncedSearchTerm && histories?.map((item) => (
                        <div key={item.id} className="flex items-center rounded-md px-4 py-3 hover:bg-gray-50 transition-all cursor-pointer">
                            <div className="h-12 w-12 flex-shrink-0 flex items-center justify-center rounded-full bg-gray-200">
                                <Clock size={20} className="text-gray-500" />
                            </div>
                            <p onClick={() => handleSearch(item.query)} className="flex-1 px-4 line-clamp-1 text-gray-600 font-semibold text-base">{item.query}</p>
                            <div onClick={() => handleOnDeleteHistory(item.id)} className="h-8 w-8 flex-shrink-0 flex items-center justify-center rounded-lg hover:bg-gray-200 transition-all">
                                <X size={20} className="text-gray-500" />
                            </div>
                        </div>
                    ))}
                </>}
                {isHistoryResponseLoading && Array(10).fill(null).map((_, index) => <BasicUserItemSkeleton key={index}/>)}
                {isFetchingSearchResult && Array(10).fill(null).map((_, index) => <BasicUserItemSkeleton key={index}/>)}
                {!isHistoryResponseLoading && histories && histories.length === 0 && <Empty
                    className="p-10"
                    description
                >
                    <span className="font-semibold text-gray-300">Lịch sử tìm kiêm hơi trống trải...</span>
                </Empty>}
                {!debouncedSearchTerm || (!isFetchingSearchResult && searchResults && [...searchResults.posts, ...searchResults.users].length === 0) && <Empty
                    className="p-10"
                    description
                >
                    <span className="font-semibold text-gray-300">Không tìm thấy kết quả phù hợp...</span>
                </Empty>}
            </div>
        </div>
    );
};

export default Search;
