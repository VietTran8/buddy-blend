import { PinOff } from "lucide-react";
import { FC } from "react";
import { useDetachNotification } from "../../../hooks";

interface IProps {
    notificationId: string;
    onDetached?: () => void;
};

const NotificationActionPopoverContent:FC<IProps> = ({ notificationId, onDetached }) => {
    const { mutate } = useDetachNotification();

    const onDetach = () => {
        mutate(notificationId, {
            onSuccess: () =>  onDetached && onDetached()
        });
    }

    return <ul>
        <li onClick={onDetach} className="font-semibold gap-x-2 flex items-center p-2 hover:bg-gray-200 rounded-md cursor-pointer transition-all">
            <PinOff className="font-bold text-red-500" size={20}/>
            <span className="text-gray-500">Gỡ thông báo</span>
        </li>
    </ul>
};

export default NotificationActionPopoverContent;