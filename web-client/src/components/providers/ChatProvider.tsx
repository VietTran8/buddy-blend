import { FC, useEffect, useRef, useState } from "react";
import { Outlet } from "react-router-dom";
import { chatSocket, connectChatSocket, disconnectChatSocket } from "../../config/chat-socket";
import { Avatar, Drawer, Empty } from "antd";
import { ChatContext } from "../../context";
import { ChatMessage, EFileType, JoinRoomSocketMessage, MessageMedia, Room, RoomJoinedMessage, SendSocketMessage } from "../../types";
import { useQueryChatRooms, useQueryRoomMessages } from "../../hooks";
import { getTimeDiff, isImage, randomUUID } from "../../utils";
import { Ellipsis } from "lucide-react";
import Input from "../shared/Input";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faCheck, faCheckCircle, faImage, faInfoCircle, faPaperPlane, faSearch, faSpinner } from "@fortawesome/free-solid-svg-icons";
import { Field, Form, Formik, FormikHelpers } from "formik";
import { useQueryClient } from "@tanstack/react-query";
import ChatItem from "../features/ChatItem";
import PostMediaUploader from "../shared/PostMediaUploader";
import { cn } from "@/lib/utils";
import { uploadFile } from "@/services";
import { format, parse } from "date-fns";

interface IProps { };

type PreparedSendMessage = SendSocketMessage & {
    mediaFiles?: File[]
}

type SeenMessage = {
    fromUserId: string;
    seenTime: string;
}

const ChatProvider: FC<IProps> = ({ }) => {
    const queryClient = useQueryClient();
    const { data: chatRoomResponse } = useQueryChatRooms();

    const [selectedRoom, setSelectedRoom] = useState<Room>();
    const [joinDate, setJoinDate] = useState<Date>(new Date());
    const [newMessages, setNewMessages] = useState<ChatMessage[]>([]);
    const [openUploadSection, setOpenUploadSection] = useState<boolean>(false);
    const [open, setOpen] = useState(false);
    const [sentByYou, setSentByYou] = useState<boolean>(false);
    const [recipientSeen, setRecipientSeen] = useState<boolean>(false);
    const [isUploading, setIsUploading] = useState<boolean>(false);

    const { data: roomMessageResponse, hasNextPage, fetchNextPage, isFetched } = useQueryRoomMessages(joinDate, selectedRoom?.id);

    const chatRooms = chatRoomResponse?.data;
    const messagePages = roomMessageResponse?.pages;

    const endRef = useRef<HTMLDivElement>(null);

    const scrollToBottom = () => {
        if (endRef.current) {
            endRef.current.scrollIntoView({ behavior: "smooth", block: "end" });
        }
    };

    const initialValues: PreparedSendMessage = {
        content: "",
        medias: [],
        toUserId: "",
    }

    const onClose = () => {
        setOpen(false);
        setSelectedRoom(undefined);
        setNewMessages([]);

        chatSocket.emit("exit_rooms", {
            toUserId: ""
        });
    };

    const handleOnRoomClick = (room: Room) => {
        const joinRoomEmitMsg: JoinRoomSocketMessage = {
            toUserId: room.opponentUserId
        }

        if (room.id !== selectedRoom?.id)
            chatSocket.emit("join_room", joinRoomEmitMsg);
    };

    const openChatDrawer = (toUserId?: string) => {
        setOpen(true);

        scrollToBottom();

        if (toUserId && chatRooms) {
            chatSocket.emit("join_room", toUserId);
        }
    }

    const handleOnSubmitMessage = async (values: PreparedSendMessage, helper: FormikHelpers<PreparedSendMessage>) => {
        if (!selectedRoom) {
            return;
        }

        let sendMessagePayload: SendSocketMessage = {
            content: values.content,
            medias: values.medias,
            toUserId: selectedRoom.opponentUserId
        }

        if (values.mediaFiles) {
            setIsUploading(true);

            let uploadedMedias: MessageMedia[] = [];

            for (const file of values.mediaFiles) {
                const response = await uploadFile(isImage(file) ? "img" : "vid", [file]);

                uploadedMedias.push({
                    url: response?.data[0].url || "",
                    thumbnail: response?.data[0].thumbnailUrl || "",
                    type: isImage(file) ? EFileType.TYPE_IMG : EFileType.TYPE_VIDEO
                });
            }

            sendMessagePayload = { ...sendMessagePayload, medias: uploadedMedias }

            chatSocket.emit("send_message", sendMessagePayload);

            setIsUploading(false);
            helper.resetForm();
            setOpenUploadSection(false);
            setRecipientSeen(false);

        } else {
            if (sendMessagePayload.content) {
                chatSocket.emit("send_message", sendMessagePayload);
                setRecipientSeen(false);
                helper.resetForm();
            }
        }
    }

    useEffect(() => {
        setSentByYou(!!selectedRoom?.lastSentByYou);
        setRecipientSeen(() => {
            if (!selectedRoom || !selectedRoom.latestMessage || !selectedRoom.opponentLastSeenTime) {
                return false;
            }

            const latestMsgTime = parse(
                selectedRoom.latestMessage.createdAt,
                "dd/MM/yyyy HH:mm:ss",
                new Date()
            );

            const opponentLastSeenTime = parse(
                selectedRoom.opponentLastSeenTime,
                "dd/MM/yyyy HH:mm:ss",
                new Date()
            );

            return opponentLastSeenTime >= latestMsgTime;
        });
    }, [selectedRoom])

    useEffect(() => {
        scrollToBottom();

    }, [newMessages]);

    useEffect(() => {
        if (isFetched)
            scrollToBottom();

    }, [isFetched])

    useEffect(() => {
        if (!chatSocket.connected)
            connectChatSocket();

        return () => {
            disconnectChatSocket();
        }
    }, []);

    useEffect(() => {
        chatSocket.on("receive_message", (message: ChatMessage) => {
            setNewMessages(prev => [...prev, message]);

            if (message.sentByYou) {
                queryClient.invalidateQueries({ queryKey: ["chat-rooms"] });
            } else {
                chatSocket.emit("seen", {
                    fromUserId: message.fromUserId,
                    seenTime: format(new Date(), "dd/MM/yyyy HH:mm:ss")
                } as SeenMessage)
            }

            setSentByYou(message.sentByYou);
        });

        chatSocket.on("joined", (message: RoomJoinedMessage) => {
            setJoinDate(new Date());

            if (chatRooms) {
                const foundIndex = chatRooms.findIndex(room => room.id === message.roomId);

                setSelectedRoom(chatRooms[foundIndex]);
                setNewMessages([]);
                scrollToBottom();
            }
        });

        chatSocket.on("recipient_seen", (_: any) => {
            setRecipientSeen(true);
            queryClient.invalidateQueries({ queryKey: ["chat-rooms"] });
        });

        return () => {
            chatSocket.off("receive_message");
            chatSocket.off("joined");
            chatSocket.off("recipient_seen");
        }
    }, [chatRooms]);

    return <>
        <ChatContext.Provider value={{ rooms: chatRooms || [], openChatDrawer }}>
            <Outlet />
        </ChatContext.Provider>
        <Drawer
            title="Tin nhắn"
            placement="right"
            size="large"
            width={1200}
            height={"100vh"}
            destroyOnClose
            onClose={onClose}
            open={open}
        >
            <div className="grid grid-cols-12 h-full gap-x-2 relative">
                <div className="col-span-4 bg-white pe-2 pt-1 ps-1 max-h-screen custom-scroll-no-hover overflow-auto border-e-2">
                    <Input className="mb-4" placeholder="Tìm kiếm trong tin nhắn" endDrawable={<FontAwesomeIcon icon={faSearch} className="text-[--primary-color]" />} />
                    <h1 className="font-semibold lg:text-lg text-base my-2 text-gray-600">{`Danh sách tin nhắn (${chatRooms?.length})`}</h1>
                    {chatRooms?.map((room) => (<div onClick={() => handleOnRoomClick(room)} key={randomUUID()} className={`rounded-md hover:bg-gray-100 ${room.id === selectedRoom?.id && 'bg-gray-100'} cursor-pointer transition-all p-3 flex items-center gap-x-2`}>
                        <div className="relative">
                            <Avatar size={"large"} src={room.roomImage || `/images/default-user.png`} />
                            {room.online && <span className="absolute -bottom-0.5 -right-0.5 w-4 border-2 border-white h-4 rounded-full bg-green-500"></span>}
                        </div>
                        <div className="flex-1">
                            <h1 className="text-base font-semibold flex-1 line-clamp-1 text-gray-600">{room.roomName || "Người dùng BuddyBlend"}</h1>
                            <div className="flex items-center gap-x-2">
                                <p className="text-sm text-gray-500 flex-1 line-clamp-1">{room.latestMessage ? (`${room.lastSentByYou ? 'Bạn: ' : ''} ${room.latestMessage.content || `Đã gửi ${room.latestMessage.medias.length} ảnh`}`) :
                                    `Các bạn vừa được kết nối...`}</p>
                                <span className="text-xs text-gray-400">{room.latestMessage && getTimeDiff(room.latestMessage.createdAt)}</span>
                            </div>
                        </div>
                        <Ellipsis size={20} />
                    </div>))}
                </div>
                <div className="col-span-8 bg-white rounded-md overflow-y-hidden pb-50 h-full">
                    {selectedRoom ? <>
                        <div className="bg-white p-3 m-2 rounded-md shadow-lg flex gap-x-3 absolute top-0 w-[65%] z-10">
                            <Avatar size={"large"} src={selectedRoom?.roomImage || "/images/default-user.png"} />
                            <div className="flex-1">
                                <h1 className="font-semibold text-base text-gray-700">{selectedRoom?.roomName || "Người dùng BuddyBlend"}</h1>
                                {selectedRoom.online ? <p className="text-green-500 font-semibold text-xs ">Đang hoạt động</p> : <p className="text-gray-400 font-semibold text-xs ">Ngoại tuyến</p>}
                            </div>
                            <button className="btn-secondary">
                                <FontAwesomeIcon icon={faInfoCircle} />
                            </button>
                        </div>

                        <div className="flex flex-col gap-y-3 px-2 overflow-y-auto custom-scroll-no-hover h-full relative">
                            <div className="pt-16 mb-2"></div>
                            <div className="flex flex-col gap-y-3 h-fit">
                                {hasNextPage && <p className="text-[--primary-color] cursor-pointer transition-all text-center hover:text-[--dark-primary-color] p-2 my-3 font-semibold" onClick={() => fetchNextPage()}>Tải thêm tin nhắn</p>}
                                {messagePages?.slice().reverse().map(page => (<div className="flex flex-col gap-y-3 h-fit" key={randomUUID()}>
                                    {page.data.data.slice().reverse().map(message => (
                                        <ChatItem
                                            key={message.id}
                                            me={message.sentByYou}
                                            content={message.content}
                                            sentAt={message.createdAt}
                                            avatar={selectedRoom?.roomImage}
                                            medias={message.medias}
                                        />
                                    ))}
                                </div>))}
                            </div>
                            {newMessages.length > 0 && <div className="flex flex-col gap-y-3">
                                {newMessages.map(message => (
                                    <ChatItem
                                        key={message.id}
                                        me={message.sentByYou}
                                        content={message.content}
                                        sentAt={message.createdAt}
                                        avatar={selectedRoom?.roomImage}
                                        medias={message.medias}
                                    />
                                ))}
                            </div>}
                            {sentByYou && <span className={cn(
                                "px-3 py-1 rounded-full -mt-2 flex items-center w-fit gap-x-1 self-end font-semibold text-xs",
                                recipientSeen ? "bg-blue-100 text-[--dark-primary-color] italic" : "bg-gray-100 text-gray-400 "
                            )}><FontAwesomeIcon icon={
                                recipientSeen ? faCheckCircle : faCheck
                            } />{
                                    recipientSeen ? "Đã xem" : "Đã gửi"
                                }</span>}
                            <div ref={endRef} className={cn(sentByYou ? "pb-20" : "pb-20")}></div>
                        </div>

                        <div className="p-2 m-2 rounded-md absolute bottom-0 w-[65%] shadow-lg bg-white">
                            <Formik
                                onSubmit={handleOnSubmitMessage}
                                initialValues={initialValues}
                            >
                                {() => (<Form className="w-full">
                                    <div className="w-full flex gap-x-2">
                                        <Field
                                            component={Input}
                                            name="content"
                                            className="flex-1"
                                            placeholder="Nhập tin nhắn..."
                                        />
                                        <button onClick={() => setOpenUploadSection(prev => !prev)} type="button" className={cn(openUploadSection && "border-gray-300 border-4", "w-14 btn-secondary items-center flex justify-center")}>
                                            <FontAwesomeIcon icon={faImage} />
                                        </button>
                                        <button type="submit" disabled={isUploading} className="w-14 btn-primary">
                                            <FontAwesomeIcon icon={isUploading ? faSpinner : faPaperPlane} spin={isUploading} />
                                        </button>
                                    </div>
                                    {openUploadSection && <div>
                                        <Field
                                            name="mediaFiles"
                                            component={PostMediaUploader}
                                            setOpen={setOpenUploadSection}
                                            hideLabelDisabled
                                            className="mt-2"
                                        />
                                    </div>}
                                </Form>)}
                            </Formik>
                        </div>
                    </> :
                        <div className="h-full w-full flex justify-center items-center">
                            <Empty className="my-auto" description={false}>
                                <p className="font-semibold text-gray-400">Chọn một người để nhắn tin nhé!</p>
                            </Empty>
                        </div>}

                    {selectedRoom && messagePages?.length === 1 && messagePages[0].data.totalElements === 0 && <div className="h-full w-full flex items-center justify-center">
                        <Empty className="" description={false}>
                            <p className="font-semibold text-gray-400">Các bạn vừa được kết nối...</p>
                        </Empty>
                    </div>}
                </div>
            </div>
        </Drawer>
    </>
};

export default ChatProvider;