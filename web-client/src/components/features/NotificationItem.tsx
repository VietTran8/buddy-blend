import { FC, useState } from "react";
import { InteractNotification } from "../../types/notification";
import { getNotificationIconSrc, getTimeDiff } from "../../utils";
import { faEllipsis } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { Avatar, Popover } from "antd";
import { format } from "date-fns";
import { useNavigate } from "react-router-dom";
import NotificationActionPopoverContent from "./popovers/NotificationActionPopoverContent";
import { ENotificationType } from "../../types";
import { useReadNotification } from "@/hooks";
import { cn } from "@/lib/utils";


interface IProps {
    notification: InteractNotification
};

const getRefLink = (notification: InteractNotification) => {
    if (notification.type === ENotificationType.MODERATION)
        return notification.refId ? `/post/violated/${notification.refId}` : ``;

    if (notification.type === ENotificationType.INVITE_USERS)
        return `/group/${notification.refId}`

    if (notification.type === ENotificationType.FRIEND_REQUEST)
        return `/friends/requests`

    if (notification.content.replace(notification.fromUser.userFullName, "").includes("tin"))
        return `/story/view/${notification.toUserId}?sid=${notification.refId}`;

    return `/post/${notification.refId}`;
}

const NotificationItem: FC<IProps> = ({ notification: item }) => {
    const [openAction, setOpenAction] = useState<boolean>(false);
    const { mutate, isPending } = useReadNotification();
    const navigate = useNavigate();

    const handleOnOpenActionChange = (open: boolean) => {
        setOpenAction(open);
    }
    
    const handleOnDetached = () => {
        setOpenAction(false);
    }

    const handleOnNotificationClick = () => {
        if(!item.hasRead)
            mutate(item.id, {
                onSuccess: () => {
                    navigate(getRefLink(item));
                }
            });
        else
            navigate(getRefLink(item));
    }

    return (
        <div className={cn(
            item.hasRead ? "bg-white hover:bg-gray-100" : "bg-sky-50 hover:bg-sky-100",
            isPending ? "cursor-wait" : "cursor-auto",
            "w-full hover:text-[#333] min-h-[68px] flex items-center p-2 gap-x-4 rounded-md transition-all cursor-pointer"
        )}>
            <div className={`relative ${item.type === ENotificationType.MODERATION && 'self-start'}`}>
                <Avatar size={55} src={(item.type === ENotificationType.MODERATION ? "/logos/brand.png" : item.fromUser.profilePicture) || "/images/default-user.png"} />
                <Avatar
                    size={(item.type === ENotificationType.COMMENT || item.type === ENotificationType.SHARE || item.type === ENotificationType.MODERATION || item.type === ENotificationType.FRIEND_REQUEST) ? 26 : 30}
                    className="absolute -bottom-2 -right-2" src={`/icons/${getNotificationIconSrc(item.type)}`}
                />
            </div>
            <div onClick={handleOnNotificationClick} className="flex-1 flex flex-col min-w-0 !text-[#333]">
                <span>
                    {item.type === ENotificationType.MODERATION ? <span>{item.content}</span> : <>
                        <strong>{item.fromUser.userFullName} </strong>
                        <span>{item.content.replace(item.fromUser.userFullName, "")}</span>
                    </>}
                </span>
                <span className="text-xs text-[--primary-color] mt-1">{getTimeDiff(format(new Date(Number(item.createAt)), "dd/MM/yyyy HH:mm:ss"))}</span>
            </div>
            <Popover
                open={openAction}
                onOpenChange={handleOnOpenActionChange}
                trigger={"click"}
                content={<NotificationActionPopoverContent onDetached={handleOnDetached} notificationId={item.id} />}
            >
                <span className="me-1 px-2 pt-1 pb-0.5 transition-all rounded-md hover:bg-gray-200 relative z-50">
                    <FontAwesomeIcon icon={faEllipsis} className="text-lg text-gray-400" />
                </span>
            </Popover>
        </div>
    );
};

export default NotificationItem;