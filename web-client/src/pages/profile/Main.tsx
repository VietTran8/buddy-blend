import { faEdit, faEnvelope, faLocationDot, faMarsAndVenus, faPhone, faPlay } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { Button, Empty, Image, Modal } from "antd";
import { FC, useState } from "react";
import { ContactSection, EditUserInfoModalContent, Post, PostCreator, PostSkeleton, SharedPost, VideoPlayer } from "../../components";
import { EFileType, EPostType, ProfileOutletContextType } from "../../types";
import { useOutletContext } from "react-router-dom";
import { useQueryTopMedia, useQueryUserPosts } from "../../hooks";
import { randomUUID } from "../../utils";

interface IProps { };

const Main: FC<IProps> = ({ }) => {
    const { user, isFetching } = useOutletContext<ProfileOutletContextType>();
    const { data: userPostPage, isLoading, isFetchingNextPage } = useQueryUserPosts(user?.id || "");
    const { data, isLoading: isMediaLoading } = useQueryTopMedia(user?.id);

    const [openEditInfoModal, setOpenEditInfoModal] = useState<boolean>(false);

    const isEmpty = userPostPage?.pages.length === 1 && userPostPage.pages[0].data.data.length === 0;

    const handleBioUpdated = () => setOpenEditInfoModal(false);

    return (
        <>
            <section className="mt-3 grid grid-cols-12 mb-10 gap-3 relative">
                <div className="md:col-span-3 col-span-full md:sticky md:top-24 h-fit">
                    <div className="rounded-md bg-white p-5 flex flex-col gap-y-3">
                        <h1 className="font-semibold text-base md:text-large">Tiểu sử</h1>
                        <p className="text-gray-500 text-justify">{user?.bio}</p>
                        {user?.myAccount && <Button onClick={() => setOpenEditInfoModal(true)} className="mt-3" type="primary">
                            <FontAwesomeIcon icon={faEdit} />
                            <span className="text-sm font-semibold">Chỉnh sửa</span>
                        </Button>}
                    </div>
                    {isFetching ? <p>Loading...</p> : <div className="rounded-md bg-white p-5 mt-3 flex flex-col gap-y-3">
                        <h1 className="font-semibold text-base md:text-large">Thông tin</h1>
                        <div className="text-gray-500 text-justify">
                            <div className="flex items-center gap-x-3">
                                <FontAwesomeIcon icon={faMarsAndVenus} />
                                <p>Giới tính: <span className="font-semibold">{user?.gender}</span></p>
                            </div>
                            <div className="flex flex-nowrap items-center gap-x-3 mt-1 line-clamp-1">
                                <FontAwesomeIcon icon={faEnvelope} />
                                <p>Email: <span className="font-semibold">{user?.email}</span></p>
                            </div>
                            <div className="flex items-center gap-x-3 mt-1">
                                <FontAwesomeIcon icon={faPhone} />
                                <p>Điện thoại: <span className="font-semibold">{user?.phone}</span></p>
                            </div>
                            <div className="flex items-center gap-x-3 mt-1">
                                <FontAwesomeIcon icon={faLocationDot} />
                                <p>Đến từ: <span className="font-semibold">{user?.fromCity}</span></p>
                            </div>
                        </div>
                    </div>}
                </div>
                <div className="md:col-span-9 lg:col-span-6 col-span-full">
                    {user?.myAccount ? <PostCreator /> : <div className="px-3 py-5 rounded bg-white">
                        <h1 className="text-base font-semibold">{`Bài viết của ${user?.lastName}`}</h1>
                    </div>}
                    <div className={`flex flex-col gap-y-3 mt-3`}>
                        {userPostPage?.pages.map((page, index) => (
                            <div key={index} className="flex flex-col gap-y-3">
                                {page.data.data.map((post) => post.type === EPostType.SHARE ?
                                    <SharedPost post={post} key={randomUUID()} /> :
                                    <Post post={post} key={randomUUID()} />
                                )}
                            </div>
                        ))}
                        {isEmpty && <Empty description={false} className="text-center text-base font-semibold text-gray-400 py-3">{`${user?.lastName} chưa đăng bài viết nào...`}</Empty>}
                        {isLoading && Array(5).fill(null).map((_, index) => <PostSkeleton key={index} />)}
                        {isFetchingNextPage && <PostSkeleton />}
                    </div>
                </div>
                <div className="col-span-3 sticky top-24 h-fit max-h-[88vh] overflow-y-auto no-scrollbar lg:block hidden">
                    <div className="rounded-md bg-white p-5 flex flex-col gap-y-3">
                        <h1 className="font-semibold text-base md:text-large">Hình ảnh</h1>
                        <div className="grid grid-cols-2 gap-3">
                            {data?.data.map(media => (media.type === EFileType.TYPE_IMG ?
                                <div className="rounded-md overflow-hidden h-[120px]">
                                    <Image preview={{ mask: <></> }} height={120} width={"100%"} src={media.url} className="object-cover" />
                                </div> :
                                <div className="rounded-md overflow-hidden h-[120px]">
                                    <Image preview={{
                                        mask: <FontAwesomeIcon icon={faPlay} className="text-[30px]"></FontAwesomeIcon>,
                                        destroyOnClose: true,
                                        imageRender: () => (<VideoPlayer src={media.url}  className="w-auto h-[90dvh]" />)
                                    }} height={120} width={"100%"} src={media.thumbnail} className="object-cover" />
                                </div>))}
                        </div>
                        {!isMediaLoading && data?.data.length === 0 && <Empty description>
                            <p className="font-semibold text-gray-300">{`${user?.lastName} chưa tải lên ảnh nào...`}</p>
                        </Empty>}
                    </div>
                    <ContactSection className="mt-3" />
                </div>
            </section>
            <Modal
                title={<h1 className="md:text-lg mb-5 text-base text-center font-semibold">Chỉnh sửa thông tin</h1>}
                centered
                open={openEditInfoModal}
                onCancel={() => setOpenEditInfoModal(false)}
                width={550}
                destroyOnClose
                footer
            >
                <EditUserInfoModalContent
                    user={user}
                    type={"bio"}
                    onUpdated={handleBioUpdated}
                />
            </Modal>
        </>
    )
};

export default Main;