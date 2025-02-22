import { Post, PostSkeleton, SharedPost } from "@/components";
import { AuthContext } from "@/context";
import { useGetViolationInfo } from "@/hooks";
import { EPostType } from "@/types";
import { Avatar, List } from "antd";
import { FC, useContext } from "react";
import { useParams } from "react-router-dom";

interface IProps { };

const PostViolated: FC<IProps> = ({ }) => {
    const { id } = useParams();
    const { user } = useContext(AuthContext);
    const { isLoading, data } = useGetViolationInfo(id);

    const { content, post } = data;

    console.log(content);

    return <div className="container">
        {isLoading ? <p className="text-center">Loading...</p> : <div className="p-5 flex flex-col items-center">
            <h1 className="lg:text-lg text-base font-bold mb-6">Vi phạm tiêu chuẩn cộng đồng</h1>
            <div className="w-full max-w-[650px] rounded-md bg-white p-5 flex flex-col items-center">
                <div className="relative">
                    <Avatar src={user?.profilePicture || `/images/default-user.png`} size={100} />
                    <img src="/icons/alert.png" className="w-8 h-8 absolute -bottom-1 right-1"/>
                </div>
                <p className="mt-5 text-base text-gray-400 text-center">Nội dung bạn vừa tải lên vô tình vi phạm tiêu chuẩn cộng động của chúng tôi, có thể do nguyên nhân:</p>
                <List
                    size="large"
                    className="mt-3"
                    bordered
                    dataSource={content.filter(item => item != "\n")}
                    renderItem={(item) => <List.Item className="font-semibold">{item}</List.Item>}
                />
            </div>
            <h1 className="lg:text-lg text-base font-bold mt-3">Chi tiết nội dung</h1>
            <div className="w-full">
                {post && <>
                    {post.type === EPostType.SHARE ?
                        <SharedPost className="max-w-[650px] mx-auto mt-3" post={post} detached /> :
                        <Post className="mt-3 mx-auto max-w-[650px]" post={post} detached />}
                </>}
                {isLoading && <PostSkeleton className="max-w-[650px] mx-auto mt-3" />}
            </div>
            <p className="mt-5 text-base font-semibold text-gray-400 text-center">Vì vậy chúng tôi xem xét và đã gỡ nội dung của bạn khỏi cộng đồng.</p>
        </div>}
    </div>
};

export default PostViolated;