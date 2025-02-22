import { FC } from "react";
import { FriendPageItem } from "../../components";
import { useQueryFriendSuggestions } from "@/hooks";
import { EFriendStatus } from "@/types";

interface IProps { };

const SuggestsPage: FC<IProps> = ({ }) => {
    const { data: friendSuggestionsResponse, isLoading } = useQueryFriendSuggestions();

    const friendSuggestions = friendSuggestionsResponse?.data;

    return (
        <div className="grid grid-cols-12 gap-3 w-full pb-10">
            {friendSuggestions?.map((request) => {
                if(request.friendStatus !== EFriendStatus.SENT_TO_YOU)
                    return (
                        <div key={request.id} className="md:col-span-4 col-span-6">
                            <FriendPageItem type="suggest" user={request} />
                        </div>
                    );
            })}
            {isLoading && <p className="my-10">Loading...</p>}
        </div>
    );
};

export default SuggestsPage;