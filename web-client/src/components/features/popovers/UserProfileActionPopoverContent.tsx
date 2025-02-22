import { useHandleBanUser } from "@/hooks";
import { faBan } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { Modal } from "antd";
import { Info } from "lucide-react";
import { FC } from "react";
import toast from "react-hot-toast";

interface IProps {
    userId: string;
};

const UserProfileActionPopoverContent: FC<IProps> = ({ userId }) => {
    const [modal, contextHolder] = Modal.useModal();
    const { mutate: banUser } = useHandleBanUser();

    const handleOnBan = () => {
        return new Promise((resolve, reject) => {
            banUser(userId, {
                onSuccess: (data) => {
                    toast.success("Chặn người dùng thành công!");

                    resolve(data);
                },
                onError: reject
            });
        }).catch(() => console.log("some errors happen"));
    }

    const confirm = () => {
        modal.confirm({
            title: 'Xác nhận',
            icon: <Info className="me-2 text-orange-400" />,
            content: <p className="font-semibold text-gray-500 pb-4">Bạn có chắc là muốn chặn người dùng này?</p>,
            okText: 'Chặn',
            okButtonProps: {
                className: "btn-primary"
            },
            cancelButtonProps : {
                className: "btn-danger"
            },
            cancelText: 'Hủy',
            centered: true,
            destroyOnClose: true,
            onOk: handleOnBan
        });
    };

    return <>
        <ul>
            <li onClick={confirm} className="text-red-500 hover:bg-gray-100 font-semibold cursor-pointer transition-all rounded p-2">
                <FontAwesomeIcon icon={faBan} className="me-2" />
                <span>Chặn người dùng này</span>
            </li>
            {contextHolder}
        </ul>
    </>
};

export default UserProfileActionPopoverContent;