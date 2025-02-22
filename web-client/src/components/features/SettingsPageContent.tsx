import { Image } from "antd";
import { FC, useContext } from "react";
import EditGeneralInfoForm from "./forms/EditGeneralInfoForm";
import EditPasswordForm from "./forms/EditPasswordForm";
import { useGetUserProfile, useLocalStorage, useUpdateUserCoverPic, useUpdateUserProfilePic } from "../../hooks";
import { ACCESS_TOKEN_PREFIX } from "../../constants";
import { AuthContext } from "../../context";
import EditDisplayInfoForm from "./forms/EditDisplayInfoForm";

interface IProps { };


const SettingPageContent: FC<IProps> = ({ }) => {
    const [token] = useLocalStorage<string>(ACCESS_TOKEN_PREFIX, "");
    const { setUser } = useContext(AuthContext);
    const { data: userResponse } = useGetUserProfile(token);
    const { mutate: updateProfilePic, isPending: isUpdatingProfilePic } = useUpdateUserProfilePic();
    const { mutate: updateCoverPic, isPending: isUpdatingCoverPic } = useUpdateUserCoverPic();

    const user = userResponse?.data;

    const handleAvatarChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const file = event.target.files?.[0];
        if (file) {
            updateProfilePic(file, {
                onSuccess: (data) => {
                    setUser(prev => (prev && {
                        ...prev,
                        profilePicture: data.data.profilePicture || ""
                    }));
                }
            });
        }
    };

    const handleCoverChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const file = event.target.files?.[0];
        if (file) {
            updateCoverPic(file);
        }
    };

    return (
        <div className="pb-20">
            <div className="bg-white w-full rounded-md p-4">
                <h3 className="font-semibold lg:text-xl text-base">Cài đặt</h3>
                <p className="text-gray-400">Cài đặt thông tin tài khoản, thông tin xác thực người dùng</p>
            </div>

            <div className="bg-white rounded-md mt-3 p-4">
                <h4 className="text-base font-semibold mb-3">Ảnh đại diện</h4>
                <div className="w-full flex flex-col gap-3 items-center">
                    <div className="w-[100px] h-[100px] rounded-md overflow-hidden">
                        <Image preview={{
                            mask: <div className="cursor-pointer"></div>
                        }} width={100} height={100} src={user?.profilePicture || "/images/default-user.png"} className="object-cover" />
                    </div>
                    <label htmlFor="avatarInput" className="btn-primary cursor-pointer">
                        {isUpdatingProfilePic ? 'Đang cập nhật...' : 'Thay đổi ảnh'}
                    </label>
                    <input
                        id="avatarInput"
                        type="file"
                        accept="image/*"
                        onChange={handleAvatarChange}
                        className="hidden"
                        disabled={isUpdatingProfilePic}
                    />
                </div>
            </div>
            <div className="bg-white rounded-md mt-3 p-4">
                <h4 className="text-base font-semibold mb-3">Ảnh bìa</h4>
                <div className="w-full flex flex-col gap-3 items-center">
                    <div className="w-full h-[250px] rounded-md overflow-hidden">
                        <Image preview={{
                            mask: <div className="cursor-pointer"></div>
                        }} width={"100%"} height={250} src={user?.coverPicture} className="object-cover" />
                    </div>
                    <label htmlFor="coverInput" className="btn-primary self-end">
                        {isUpdatingCoverPic ? 'Đang cập nhật...' : 'Thay đổi ảnh'}
                    </label>
                    <input
                        id="coverInput"
                        type="file"
                        accept="image/*"
                        onChange={handleCoverChange}
                        className="hidden"
                        disabled={isUpdatingCoverPic}
                    />
                </div>
            </div>
            <div className="bg-white rounded-md mt-3 p-4">
                <h4 className="text-base font-semibold mb-5">Thông tin chung</h4>
                <div className="w-full">
                    <EditGeneralInfoForm user={user} />
                </div>
            </div>
            <div className="bg-white rounded-md mt-3 p-4">
                <h4 className="text-base font-semibold mb-5">Thông tin hiển thị</h4>
                <div className="w-full">
                    <EditDisplayInfoForm user={user} />
                </div>
            </div>
            <div className="bg-white rounded-md mt-3 p-4">
                <h4 className="text-base font-semibold mb-5">Thông tin xác thực</h4>
                <div className="w-full">
                    <EditPasswordForm />
                </div>
            </div>
        </div>
    )
};

export default SettingPageContent;