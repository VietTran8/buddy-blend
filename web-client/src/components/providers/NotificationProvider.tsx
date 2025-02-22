import { FC, useEffect } from "react";
import { Outlet } from "react-router-dom";
import { connectNotificationSocket, disconnectNotificationSocket, notificationSocket } from "../../config";
import { InfiniteData, useQueryClient } from "@tanstack/react-query";
import { Avatar, notification } from "antd";
import { MessageNotification, NotificationSocketMessage } from "../../types/notification";
import { BaseResponse, ENotificationType, PaginationResponse, Post } from "../../types";
import { getNotificationIconSrc } from "../../utils";

interface IProps { };


const NotificationProvider: FC<IProps> = ({ }) => {
    const [api, contextHolder] = notification.useNotification();
    const queryClient = useQueryClient();
    
    const openNotification = (notification: NotificationSocketMessage) => {
        api.info({
            message: <h1 className="font-semibold text-base mb-2">{notification.title}</h1>,
            description: <div className="flex gap-x-4 items-center">
                <div className="relative h-fit w-fit flex-shrink-0">
                    <Avatar size="large" src={notification.avatarUrl || "/images/default-user.png"} />
                    <Avatar
                        size={(notification.type === ENotificationType.COMMENT || notification.type === ENotificationType.SHARE) ? 21 : 25}
                        className="absolute -bottom-2 -right-2" src={`/icons/${getNotificationIconSrc(notification.type)}`}
                    />
                </div>
                <p>
                    <span className="font-semibold">{notification.userFullName} </span>
                    <span>{notification.content.replace(notification.userFullName, "")}</span>
                </p>
            </div>,
            placement: "bottomRight",
            showProgress: true,
            pauseOnHover: true
        });
    };

    const openChatNotification = (notification: MessageNotification) => {
        api.info({
            message: <h1 className="font-semibold text-base mb-2">{`Tin nhắn mới từ ${notification.title}`}</h1>,
            description: <div className="flex gap-x-4 items-center">
                <div className="relative h-fit w-fit flex-shrink-0">
                    <Avatar size="large" src={notification.avatar || "/images/default-user.png"} />
                </div>
                <p>
                    <span>{notification.content}</span>
                </p>
            </div>,
            placement: "bottomRight",
            showProgress: true,
            pauseOnHover: true
        });
    };

    useEffect(() => {
        if (!notificationSocket.connected) {
            connectNotificationSocket();
        }

        return () => {
            disconnectNotificationSocket();
        }
    }, []);

    useEffect(() => {
        notificationSocket.on("connect", () => {
            console.log("Connected to socket");
        });

        notificationSocket.on("disconnect", (reason) => {
            console.log("Disconnected from socket:", reason);
        });

        notificationSocket.on("connect_error", (error) => {
            console.error("Connection error:", error.message);
        });

        notificationSocket.on("notification", (value) => {
            queryClient.invalidateQueries({
                queryKey: ['user-notifications']
            });
            queryClient.refetchQueries({
                queryKey: ['user-notifications']
            });
            
            openNotification(value);
        });

        notificationSocket.on("new_message", (value) => {
            queryClient.invalidateQueries({
                queryKey: ['chat-rooms']
            });
            openChatNotification(value);
        });

        notificationSocket.on("new_post", (newPost: Post) => {
            queryClient.setQueryData(["news-feed"], (oldData: InfiniteData<BaseResponse<PaginationResponse<Post>>>) => {
                if(!oldData) return oldData;

                const newPages = [...oldData.pages];

                const lastPage = newPages[newPages.length - 1];

                const updatedLastPage: BaseResponse<PaginationResponse<Post>> = {
                    ...lastPage,
                    data: {
                        ...lastPage.data,
                        data: [...lastPage.data.data, newPost]
                    }
                };

                newPages[newPages.length - 1] = updatedLastPage;

                return {
                    ...oldData,
                    pages: newPages
                }
            });
        })

        return () => {
            notificationSocket.off("connect");
            notificationSocket.off("disconnect");
            notificationSocket.off("connect_error");
            notificationSocket.off("notification");
            notificationSocket.off("new_message");
            notificationSocket.off("new_post");
        }
    }, []);

    return <>
        {contextHolder}
        <Outlet />
    </>
};

export default NotificationProvider;