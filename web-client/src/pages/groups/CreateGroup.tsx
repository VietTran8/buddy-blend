import { FC, useCallback, useContext, useState } from "react";
import { CreateGroupForm, Header, PostCreator } from "../../components";
import { Dot, X } from "lucide-react";
import { Link } from "react-router-dom";
import { Avatar, Breadcrumb, Divider } from "antd";
import { GroupPrivacy } from "../../types";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faEarthAmericas, faEye, faLock } from "@fortawesome/free-solid-svg-icons";
import { AuthContext } from "../../context";

interface IProps { };

const breadcrumbItems = [
    {
        title: <Link to="/groups">Nhóm</Link>,
    },
    {
        title: <Link to="/groups/create">Tạo nhóm</Link>,
    }
]

const CreateGroup: FC<IProps> = ({ }) => {
    const { user } = useContext(AuthContext);

    const [groupInfo, setGroupInfo] = useState({
        name: "",
        privacy: GroupPrivacy.PRIVACY_PUBLIC
    });

    const handleNameChange = useCallback((name: string) => {
        setGroupInfo(prev => ({ ...prev, name }))
    }, [])

    const handlePrivacyChange = useCallback((privacy: GroupPrivacy) => {
        setGroupInfo(prev => ({ ...prev, privacy }))
    }, [])

    return (
        <div className="flex flex-col h-[100vh]">
            <Header />
            <div className="grid grid-cols-12 gap-4 h-full overflow-y-auto">
                <div className="md:col-span-4 col-span-full p-5 bg-white">
                    <Breadcrumb items={breadcrumbItems} className="mb-2" />
                    <div className="flex items-center gap-x-3">
                        <Link to="/groups">
                            <div className="h-12 w-12 flex items-center bg-gray-100 hover:bg-gray-200 cursor-pointer transition-all justify-center rounded-full">
                                <X size={20} className="text-gray-500" />
                            </div>
                        </Link>
                        <h1 className="lg:text-xl text-lg font-bold">Tạo nhóm mới</h1>
                    </div>
                    <div className="flex gap-x-3 mt-4">
                        <Avatar size="large" src={user?.profilePicture || "/images/default-user.png"} />
                        <div className="">
                            <h1 className="font-semibold text-base -mb-1">{user?.userFullName}</h1>
                            <span className="font-medium text-sm text-gray-400">Quản trị viên</span>
                        </div>
                    </div>
                    <CreateGroupForm
                        onNameChange={handleNameChange}
                        onPrivacyChange={handlePrivacyChange}
                        className="mt-4"
                    />
                </div>
                <div className="md:col-span-8 col-span-full md:block hidden container">
                    <div className="w-full bg-white p-5 h-[70vh] flex flex-col md:mt-10 mt-2">
                        <h1 className="font-semibold text-base mb-5">Xem trước</h1>
                        <div className="rounded-lg border flex-1 overflow-y-auto bg-gray-50 custom-scroll-no-hover` pb-10">
                            <div className="bg-white">
                                <img src="/images/community.jpg" className="object-cover w-full h-[250px] rounded-b-lg" />
                                <div className="p-5">
                                    <h1 className="font-bold">
                                        {groupInfo.name === "" ? <span className="text-gray-300 text-2xl ">Tên nhóm</span> : <span className="text-2xl">{groupInfo.name}</span>}
                                    </h1>
                                    <div className="flex items-center mt-2">
                                        <FontAwesomeIcon icon={groupInfo.privacy === GroupPrivacy.PRIVACY_PUBLIC ? faEarthAmericas : faLock} className="text-sm text-gray-400 me-2" />
                                        <span className="text-gray-400">{groupInfo.privacy === GroupPrivacy.PRIVACY_PUBLIC ? 'Nhóm công khai' : 'Nhóm riêng tư'}</span>
                                        <Dot size={20} className="mx-1" />
                                        <span className="font-semibold text-gray-500">1 thành viên</span>
                                    </div>
                                    <Divider className="my-5" />
                                    <div className="flex items-center gap-x-10 px-5">
                                        <span className="font-semibold text-gray-400 cursor-default">Giới thiệu</span>
                                        <span className="font-semibold text-gray-400 cursor-default">Bài viết</span>
                                        <span className="font-semibold text-gray-400 cursor-default">Thành viên</span>
                                    </div>
                                </div>
                            </div>
                            <div className="mx-5 mt-5">
                                <div className="rounded-md bg-white p-4">
                                    <h1 className="text-lg font-semibold">Giới thiệu</h1>
                                    <div className="flex gap-x-3 mt-3">
                                        <FontAwesomeIcon icon={groupInfo.privacy === GroupPrivacy.PRIVACY_PUBLIC ? faEarthAmericas : faLock} className="text-base text-gray-500 mt-1" />
                                        <div className="">
                                            <p className="font-semibold text-base">{groupInfo.privacy === GroupPrivacy.PRIVACY_PUBLIC ? 'Công khai' : 'Riêng tư'}</p>
                                            <p className="text-sm text-gray-400">{groupInfo.privacy === GroupPrivacy.PRIVACY_PUBLIC ?
                                                'Bất kỳ ai cũng có thể thấy mọi người trong nhóm và những gì họ đăng' :
                                                'Chỉ thành viên mới nhìn thấy mọi người trong nhóm và những gì họ đăng'}</p>
                                        </div>
                                    </div>
                                    <div className="flex gap-x-3 mt-3">
                                        <FontAwesomeIcon icon={faEye} className="text-base text-gray-500 mt-1" />
                                        <div className="">
                                            <p className="font-semibold text-base">Hiển thị</p>
                                            <p className="text-sm text-gray-400">Ai cũng có thể nhìn thấy nhóm này</p>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div className="mx-5 mt-5">
                                <PostCreator disabled />
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default CreateGroup;