import { Avatar, Image } from "antd";
import { FC } from "react";
import { getTimeDiff } from "../../utils";
import { cn } from "@/lib/utils";
import { EFileType, MessageMedia } from "@/types";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faPlay } from "@fortawesome/free-solid-svg-icons";
import VideoPlayer from "../shared/VideoPlayer";

interface IProps {
    me: boolean,
    avatar?: string,
    content: string,
    sentAt: string,
    medias?: MessageMedia[]
};

const ChatItem: FC<IProps> = ({ me, avatar, content, sentAt, medias }) => {
    return <div className={`w-full flex ${me ? 'justify-end' : 'justify-start'}`}>
        <div className={`flex items-start gap-x-2`}>
            {!me && <Avatar src={avatar || '/images/default-user.png'} />}
            <div className="flex flex-col">
                <div className={`max-w-[310px] w-fit flex flex-col ${!me ? 'bg-gray-100 rounded-e-xl rounded-bl-xl' : 'rounded-s-xl rounded-br-xl bg-[--dark-primary-color] text-white'}`}>
                    {medias && medias.length > 0 && 
                        <div className={`grid-cols-${medias.length <= 3 ? medias.length : 3} grid gap-1 flex-wrap px-1 pt-1`}>
                            {medias.map(media => <div key={media.url} className={cn(medias.length === 1 ? "max-w-full" : medias.length === 2 ? "h-44" : "h-36", "flex-1 rounded-lg overflow-hidden")}>
                                {media.type === EFileType.TYPE_IMG ?
                                    <Image src={media.url} preview={{ mask: <></> }} className="object-cover" width={"100%"} height={"100%"} /> :
                                    <Image height={"100%"} preview={{
                                        destroyOnClose: true,
                                        mask: <FontAwesomeIcon icon={faPlay} className="text-[30px]" />,
                                        imageRender: () => (<VideoPlayer src={media.url} className="w-auto h-[90dvh]" />)
                                    }} width={"100%"} src={media.thumbnail} className="object-cover" />
                                }
                            </div>)}
                        </div>}
                    <div className="px-3 py-2 flex flex-col">
                        <span className={`${me ? 'self-end' : 'self-start'} text-wrap`}>{content}</span>
                        <span className={`text-xs ${me ? 'self-end text-blue-200' : 'self-start text-gray-400'} mt-1`}>{getTimeDiff(sentAt)}</span>
                    </div>
                </div>
            </div>
        </div>
    </div>
};

export default ChatItem;