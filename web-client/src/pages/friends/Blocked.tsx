import { FC } from "react";
import { Avatar, Empty, Modal } from "antd";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faBan } from "@fortawesome/free-solid-svg-icons";
import { useHandleBlockUser, useQueryBlockingUsers } from "@/hooks";
import { Blocking } from "@/types";
import { Info } from "lucide-react";
import toast from "react-hot-toast";

interface IProps { };

const BlockedPage: FC<IProps> = ({ }) => {
    const { data: blockingUserResponse, isLoading } = useQueryBlockingUsers();

    const blockingList: Blocking[] = blockingUserResponse?.data || [];

    const [modal, contextHolder] = Modal.useModal();
    const { mutate: banUser } = useHandleBlockUser();

    const handleOnUnBan = (userId: string) => {
        return new Promise((resolve, reject) => {
            banUser(userId, {
                onSuccess: (data) => {
                    toast.success("Bỏ chặn người dùng thành công!");

                    resolve(data);
                },
                onError: reject
            })
        }).catch(() => console.log("some errors happen"));
    }

    const confirm = (userId: string) => {
        modal.confirm({
            title: 'Xác nhận',
            icon: <Info className="me-2 text-orange-400" />,
            content: <p className="font-semibold text-gray-500 pb-4">Bạn có chắc là muốn bỏ chặn người dùng này?</p>,
            okText: 'Đồng ý',
            okButtonProps: {
                className: "btn-primary"
            },
            cancelButtonProps : {
                className: "btn-danger"
            },
            cancelText: 'Hủy',
            centered: true,
            destroyOnClose: true,
            onOk: () => handleOnUnBan(userId)
        });
    };

    return (
        <section className="bg-white w-full py-3 px-5 rounded-md">
            <div className="mb-5">
                <h1 className="font-bold lg:text-lg text-base">Danh sách chặn</h1>
                <p className="md:text-sm text-xs text-gray-500">Danh sách người dùng bị chặn</p>
            </div>
            {blockingList.map((blocking, index) => (
                <div key={blocking.id}>
                    <div className="py-3 flex justify-between items-center">
                        <div className="flex gap-x-3 items-center">
                            <Avatar src={blocking.blockedUser.profilePicture || "/images/default-user.png"} alt="user avatar" shape="square" size={50} />
                            <h1 className="text-base font-semibold">{blocking.blockedUser.userFullName}</h1>
                        </div>
                        <p className="text-gray-500 font-medium">{`Chặn lúc ${blocking.blockedAt}`}</p>
                        <div className="flex">
                            <button onClick={() => confirm(blocking.blockedUser.id)} className="btn-danger">
                                <span>
                                    <FontAwesomeIcon icon={faBan} className="text-sm" />
                                    <span className="ms-2 text-sm">Bỏ chặn</span>
                                </span>
                            </button>
                        </div>
                    </div>
                    {index !== blockingList.length - 1 && <hr />}
                </div>
            ))}
            {!isLoading && blockingList.length === 0 && <Empty description className="my-10">
                <span className="font-semibold text-gray-400">Hiện tại bạn chưa chặn ai cả</span>
            </Empty>}
            {isLoading && <span className="my-10">Loading...</span>}
            {contextHolder}
        </section>
    );
};

export default BlockedPage;