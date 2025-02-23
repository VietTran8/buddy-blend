import { FC } from "react";
import { Empty, Image, Skeleton } from "antd";
import { ContactSection, VideoPlayer } from "../../components";
import { useQueryAlbum } from "@/hooks";
import { EFileType, ProfileOutletContextType } from "@/types";
import { useOutletContext } from "react-router-dom";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faPlay } from "@fortawesome/free-solid-svg-icons";


interface IProps { };

const Photos: FC<IProps> = ({ }) => {
    const { user } = useOutletContext<ProfileOutletContextType>();

    const { data, isLoading, isFetchingNextPage, fetchNextPage, hasNextPage } = useQueryAlbum(user?.id);

    const mediaPages = data?.pages

    return (
        <section className="grid grid-cols-12 mb-10 gap-3 relative">
            <div className="md:col-span-9 col-span-full bg-white rounded-md mt-3 p-5 h-fit">
                <h1 className="font-semibold text-base md:text-large">Hình ảnh</h1>
                <div className="grid md:grid-cols-3 grid-cols-2 gap-3 mt-5">
                    {isLoading && Array(10).fill(null).map((_, index) => <Skeleton.Node key={index} className="!h-[190px] !w-full" active children />)}
                    {mediaPages?.map(page => page.data.map(media => media.type === EFileType.TYPE_IMG ?
                        <div className="rounded-md overflow-hidden h-[190px]">
                            <Image preview={{
                                mask: <></>
                            }} height={190} width={"100%"} src={media.url} className="object-cover" />
                        </div> :
                        <div className="rounded-md cursor-pointer overflow-hidden h-[190px] relative">
                            <Image height={190} preview={{
                                destroyOnClose: true,
                                mask: <FontAwesomeIcon icon={faPlay} className="text-[30px]" />,
                                imageRender: () => (<VideoPlayer className="w-auto h-[90dvh]" src={media.url} />)
                            }} width={"100%"} src={media.thumbnail} className="object-cover" />

                        </div>
                    ))}
                    {isFetchingNextPage && Array(10).fill(null).map((_, index) => <Skeleton.Node key={index} className="!h-[190px] !w-full" active children />)}
                </div>
                {!isLoading && mediaPages?.length === 1 && mediaPages[0].data.length === 0 && <Empty description>
                    <p className="font-semibold text-gray-300">{`${user?.lastName} chưa tải lên ảnh nào...`}</p>
                </Empty>}
                {hasNextPage && <button onClick={() => fetchNextPage()} className="btn-secondary w-full mt-5">Xem thêm</button>}
            </div>
            <div className="col-span-3 md:block hidden">
                <ContactSection className="mt-3" />
            </div>
        </section>
    )
};

export default Photos;