import { FC, useState } from "react";
import { faEnvelope, faLocationDot, faMarsAndVenus, faPen, faPhone } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { ContactSection, EditUserInfoModalContent } from "../../components";
import { Modal } from "antd";
import { EditInfoType, ProfileOutletContextType } from "../../types";
import { useOutletContext } from "react-router-dom";

interface IProps { };

const About: FC<IProps> = ({ }) => {
    const { user } = useOutletContext<ProfileOutletContextType>();
    const [openEditInfoModal, setOpenEditInfoModal] = useState<boolean>(false);
    const [editInfoType, setEditInfoType] = useState<EditInfoType | undefined>(undefined);

    const handleOpenEditModal = (type: EditInfoType) => {
        setOpenEditInfoModal(true);
        setEditInfoType(type);
    }

    const handleInfoUpdated = () => setOpenEditInfoModal(false);

    return (
        <>
            <section className="grid grid-cols-12 mb-10 gap-3 relative">
                <div className="md:col-span-9 col-span-full bg-white rounded-md mt-3 p-5 h-fit">
                    <h1 className="font-semibold text-base md:text-large">Tiểu sử</h1>
                    <p className="mt-2 text-gray-500">"<span>{user?.bio}</span>"</p>
                    <div className="text-justify mt-4">
                        <div className="flex items-center gap-x-3">
                            <FontAwesomeIcon icon={faMarsAndVenus} />
                            <p className="flex-1">Giới tính: <span className="font-semibold">{user?.gender}</span></p>
                            {user?.myAccount && <FontAwesomeIcon onClick={() => handleOpenEditModal("gender")} className="text-gray-400 rounded hover:bg-gray-50 transition-all cursor-pointer p-3" icon={faPen} />}
                        </div>
                        <div className={`flex items-center gap-x-3 ${user?.myAccount ? 'mt-1' : 'mt-4'}`}>
                            <FontAwesomeIcon icon={faEnvelope} />
                            <p className="flex-1">Email: <span className="font-semibold">{user?.email}</span></p>
                            {user?.myAccount && <FontAwesomeIcon onClick={() => handleOpenEditModal("email")} className="text-gray-400 rounded hover:bg-gray-50 transition-all cursor-pointer p-3" icon={faPen} />}
                        </div>
                        <div className={`flex items-center gap-x-3 ${user?.myAccount ? 'mt-1' : 'mt-4'}`}>
                            <FontAwesomeIcon icon={faPhone} />
                            <p className="flex-1">Điện thoại: <span className="font-semibold">{user?.phone}</span></p>
                            {user?.myAccount && < FontAwesomeIcon onClick={() => handleOpenEditModal("phone")} className="text-gray-400 rounded hover:bg-gray-50 transition-all cursor-pointer p-3" icon={faPen} /> }
                        </div>
                        <div className={`flex items-center gap-x-3 ${user?.myAccount ? 'mt-1' : 'mt-4'}`}>
                            <FontAwesomeIcon icon={faLocationDot} />
                            <p className="flex-1">Đến từ: <span className="font-semibold">{user?.fromCity}</span></p>
                            {user?.myAccount && <FontAwesomeIcon onClick={() => handleOpenEditModal("address")} className="text-gray-400 rounded hover:bg-gray-50 transition-all cursor-pointer p-3" icon={faPen} />}
                        </div>
                    </div>
                </div>
                <div className="col-span-3 md:block hidden">
                    <ContactSection className="mt-3" />
                </div>
            </section>
            <Modal
                title={<h1 className="md:text-lg mb-5 text-base text-center font-semibold">Chỉnh sửa thông tin</h1>}
                centered
                destroyOnClose
                open={openEditInfoModal}
                onCancel={() => setOpenEditInfoModal(false)}
                width={550}
                footer
            >
                <EditUserInfoModalContent
                    type={editInfoType}
                    user={user}
                    onUpdated={handleInfoUpdated}
                />
            </Modal>
        </>
    )
};

export default About;