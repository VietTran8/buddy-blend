import { FC } from "react";
import { Post, PostCreator, PostSkeleton } from "../../components";
import { GroupPrivacy, OutletGroupContextType } from "../../types";
import { useOutletContext } from "react-router-dom";
import { useQueryGroupPosts } from "../../hooks";
import { Empty } from "antd";
import { randomUUID } from "../../utils";

interface IProps { };

const GroupDetails: FC<IProps> = ({ }) => {
    const { group } = useOutletContext<OutletGroupContextType>();
    const { data, isLoading, isFetchingNextPage } = useQueryGroupPosts(group?.id);

    const groupPages = data?.pages;

    return (
        <>
            {(group?.privacy === GroupPrivacy.PRIVACY_PUBLIC || group?.joined) ? <>
                {group.joined && <PostCreator isGroupPost groupId={group.id} />}
                <div className="flex flex-col gap-y-3">
                    {groupPages?.map((page) => (
                        <div key={randomUUID()} className="flex flex-col gap-y-3">
                            {page.data.data.map((post) => <Post isPrivateGroupPost={group.privacy === GroupPrivacy.PRIVACY_PRIVATE} insideGroup key={randomUUID()} post={post} />)}
                        </div>
                    ))}
                </div>
                {groupPages?.length === 1 && groupPages[0].data.data.length === 0 && <Empty description={false}>
                    <p className="font-semibold text-center text-gray-400">Chưa có bài viết nào trong nhóm.</p>
                </Empty>}
                {isLoading && Array(5).fill(null).map((_, index) => <PostSkeleton key={index}/>)}
                {isFetchingNextPage &&  Array(3).fill(null).map((_, index) => <PostSkeleton key={index}/>)}
            </> : <Empty description={false}>
                <p className="font-semibold text-center text-gray-400">Tham gia nhóm để xem các bài viết.</p>
            </Empty>}
        </>
    );
};

export default GroupDetails;