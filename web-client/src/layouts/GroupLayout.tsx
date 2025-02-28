import { faCamera, faEarthAmericas, faLock, faPen } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { Avatar, Divider, Image, Modal, Popover } from "antd";
import { Dot, Ellipsis, Link2, LogOut, Plus, History, Users, Earth, Lock, X } from "lucide-react";
import { FC, useState } from "react";
import { Link, Outlet, useLocation, useParams } from "react-router-dom";
import { groupNavItems } from "../constants";
import { EditGroupInfoModalContent, GroupInviteModalContent, GroupLayoutSkeleton, NotFound } from "../components";
import { useCancelOrLeaveGroup, useJoinGroup, useQueryGroupById, useUpdateGroup, useUploadFile } from "../hooks";
import { EditGroupInfoType, EJoinGroupStatus, Group, GroupPrivacy } from "../types";
import toast from "react-hot-toast";
import { getErrorRespMsg } from "../utils";

interface IProps { };

const GroupLayout: FC<IProps> = ({ }) => {
    const { id } = useParams();
    const { data: groupResponse, isLoading } = useQueryGroupById(id);
    const { mutate: joinGroup } = useJoinGroup();
    const { mutate: handleMember } = useCancelOrLeaveGroup();
    const { mutate: update } = useUpdateGroup();
    const { mutate: uploadFile } = useUploadFile();

    const [openJoinedOptions, setOpenJoinedOptions] = useState<boolean>(false);
    const [openGroupAction, setOpenGroupAction] = useState<boolean>(false);
    const [openInviteModal, setOpenInviteModal] = useState<boolean>(false);
    const [openEditModal, setOpenEditModal] = useState<boolean>(false);
    const [editType, setEditType] = useState<EditGroupInfoType>("name");

    const { pathname } = useLocation();

    const group: Group | undefined = groupResponse?.data;

    const handleOpenJoinedOptionsChange = (newOpen: boolean) => {
        setOpenJoinedOptions(newOpen);
    }

    const handleOpenGroupActionChange = (newOpen: boolean) => {
        setOpenGroupAction(newOpen);
    }

    const handleOpenInviteModal = () => {
        setOpenInviteModal(true);
    }

    const handleOpenEditModal = (type: EditGroupInfoType) => {
        setOpenEditModal(true);
        setEditType(type);
    }

    const handleJoinBtnClick = () => {
        group && !group.joined && group.joinStatus !== EJoinGroupStatus.PENDING && joinGroup(group.id);
    }

    const handleMemberGroup = (type: "leave" | "cancel-pending") => {
        group && group.currentMemberId && handleMember({
            payload: {
                groupId: group.id,
                memberId: group.currentMemberId
            }, type
        }, {
            onSettled: () => {
                setOpenJoinedOptions(false);
            }
        });
    }

    const handleOnUpdated = () => {
        setOpenEditModal(false);
    }

    const handleImageChange = (event: React.ChangeEvent<HTMLInputElement>, updateType: "avatar" | "cover") => {
        const file = event.target.files?.[0];

        const toastId = toast.loading("Đang tải ảnh lên", {
            position: "bottom-left"
        })

        if (file)
            uploadFile({
                type: "img",
                files: [file]
            }, {
                onSuccess: (data) => {
                    update({
                        groupId: group?.id || "",
                        payload: updateType === "avatar" ? {
                            avatar: data?.data[0].url
                        } : {
                            cover: data?.data[0].url
                        }
                    }, {
                        onSuccess: () => {
                            toast.success("Đã tải ảnh lên", { id: toastId })
                        },
                        onError: (error: any) => {
                            toast.error(getErrorRespMsg(error), { id: toastId });
                        }
                    })
                },
                onError: (error: any) => {
                    toast.error(getErrorRespMsg(error), { id: toastId });
                }
            })
    }

    return (
        <>
            {isLoading ? <GroupLayoutSkeleton /> : 
                group ? <>
                <div className="rounded-md w-full bg-white p-5">
                    <div className="w-full h-[260px] rounded-lg overflow-hidden mb-4 relative">
                        <Image preview={{
                            mask: <div className="cursor-pointer"></div>
                        }} width={"100%"} height={260} className="object-cover" src={group?.cover || "/images/community.jpg"} />
                        {group?.admin && <>
                            <label htmlFor="group-cover" className="w-12 h-12 absolute bottom-3 right-3 z-30 bg-gray-50 hover:bg-gray-200 rounded-full cursor-pointer transition-all flex justify-center items-center">
                                <FontAwesomeIcon icon={faCamera} className="text-gray-600" />
                            </label>
                            <input onChange={(e) => handleImageChange(e, "cover")} id="group-cover" type="file" hidden accept="image/*" />
                        </>}
                    </div>
                    <div className="flex md:flex-row flex-col items-center gap-x-3 md:mt-0 -mt-14">
                        <div className="w-[80px] h-[80px] group flex-shrink-0 rounded-lg overflow-hidden shadow relative">
                            <Image preview={{
                                mask: <div className="cursor-pointer"></div>
                            }} height={80} width={80} className="object-cover" src={group?.avatar || "/images/community2.jpg"} />
                            {group?.admin && <>
                                <label htmlFor="group-avatar" className="w-12 h-12 opacity-0 group-hover:opacity-100 absolute left-[50%] top-[50%] -translate-x-[50%] -translate-y-[50%] z-30 bg-gray-50/70 hover:bg-gray-200/70 rounded-full cursor-pointer transition-all flex justify-center items-center">
                                    <FontAwesomeIcon icon={faCamera} className="text-gray-600" />
                                </label>
                                <input onChange={(e) => handleImageChange(e, "avatar")} id="group-avatar" type="file" hidden accept="image/*" />
                            </>}
                        </div>
                        <div className="flex-1">
                            <div className="flex items-center gap-x-3 md:mt-0 mt-3">
                                <h1 className="md:text-xl text-lg lg:font-bold font-semibold md:text-left text-center">{group?.name}</h1>
                                {group?.admin && <div onClick={() => handleOpenEditModal("name")} className="w-8 h-8 bg-gray-50 hover:bg-gray-200 rounded-md cursor-pointer transition-all flex justify-center items-center">
                                    <FontAwesomeIcon icon={faPen} className="text-gray-600" />
                                </div>}
                            </div>
                            <div className="flex items-center mt-1">
                                <FontAwesomeIcon icon={group?.privacy === GroupPrivacy.PRIVACY_PUBLIC ? faEarthAmericas : faLock} className="text-sm text-gray-400 me-2" />
                                <span className="text-gray-400">{group?.privacy === GroupPrivacy.PRIVACY_PUBLIC ? 'Nhóm công khai' : 'Nhóm riêng tư'}</span>
                                <Dot size={20} className="mx-1" />
                                <span className="font-semibold text-gray-500">{`${group?.memberCount} thành viên`}</span>
                            </div>
                        </div>
                        <div className="flex items-center gap-x-3 md:mt-0 mt-3">
                            <Popover
                                trigger={(group?.joined || group?.joinStatus === EJoinGroupStatus.PENDING) ? "click" : undefined}
                                content={
                                    <ul>
                                        {
                                            group?.joinStatus === EJoinGroupStatus.SUCCESS ?
                                                <li onClick={() => handleMemberGroup("leave")} className="px-3 py-2 text-sm items-center font-medium text-gray-600 rounded hover:bg-gray-100 flex cursor-pointer transition-all gap-x-2"><LogOut className="text-gray-500 text-sm" /> Rời khỏi nhóm</li> :
                                                <li onClick={() => handleMemberGroup("cancel-pending")} className="px-3 py-2 text-sm items-center font-medium text-gray-600 rounded hover:bg-gray-100 flex cursor-pointer transition-all gap-x-2"><X className="text-gray-500 text-sm" /> Hủy yêu cầu đang chờ</li>
                                        }
                                    </ul>
                                }
                                open={openJoinedOptions}
                                onOpenChange={(newOpen) => group && (group.joined || group.joinStatus === EJoinGroupStatus.PENDING) && handleOpenJoinedOptionsChange(newOpen)}
                            >
                                <button onClick={handleJoinBtnClick} className={group?.joined ? 'btn-success-light' : 'btn-primary'}>{group?.joined ? 'Đã tham gia' : (
                                    group?.joinStatus === EJoinGroupStatus.PENDING ? 'Đang chờ phê duyệt...' : 'Tham gia'
                                )}</button>
                            </Popover>
                            {group?.joined && <button onClick={handleOpenInviteModal} className="btn-secondary flex items-center gap-x-1">
                                <Plus size={17} />
                                <span className="text-sm">Mời</span>
                            </button>}
                            {group?.joined && <Popover
                                trigger="click"
                                content={
                                    <ul>
                                        <li className="px-3 py-2 font-medium text-gray-600 rounded hover:bg-gray-100 flex cursor-pointer transition-all gap-x-2 text-sm items-center"><Link2 className="text-gray-500 text-sm" /> Sao chép địa chỉ liên kết</li>
                                    </ul>
                                }
                                open={openGroupAction}
                                onOpenChange={handleOpenGroupActionChange}
                            >
                                <button className="btn-secondary">
                                    <Ellipsis size={17} />
                                </button>
                            </Popover>}
                        </div>
                    </div>
                    <div className="mt-5 flex md:flex-row flex-col items-center gap-x-3">
                        <Avatar.Group size={32}>
                            {group?.firstTenMembers.map((member, index) => (
                                <Avatar key={index} src={member.user.profilePicture || '/images/default-user.png'} />
                            ))}
                        </Avatar.Group>
                        <p className="font-semibold text-gray-400 md:mt-0 mt-2">
                            {`${group?.firstTenMembers.map(member => member.user.lastName).slice(0, 3).join(", ")} ${group?.memberCount && group.memberCount > 3 ? 'và ' + (group.memberCount - 3) + ' người khác.' : ''}`}
                        </p>
                    </div>
                    <hr className="mt-8" />
                    <div className="flex gap-x-3 items-center mt-4 overflow-y-auto custom-scroll-no-hover md:pb-0 pb-4">
                        {groupNavItems.map((item, index) => (
                            <div key={index} className="flex gap-x-3 items-center">
                                <Link to={item.linkTo}>
                                    <span className={`font-semibold transition-all text-nowrap ${((index === 0 && pathname.split("/").length === 3) || (index !== 0 && pathname.includes(item.linkTo))) && 'text-[--primary-color]'}`}>{item.name}</span>
                                </Link>
                                <Divider type="vertical" />
                            </div>
                        ))}
                    </div>
                </div>
                <main className="mt-3">
                    <div className="grid grid-cols-12 gap-x-3 pb-10">
                        <section className="lg:col-span-8 col-span-full flex flex-col gap-y-3">
                            {group && <Outlet context={{ group }} />}
                        </section>
                        <section className="col-span-4 h-fit lg:block hidden bg-white p-4 rounded-md sticky top-24">
                            <h3 className="font-semibold text-base text-gray-500">Giới thiệu về nhóm</h3>
                            <p className="line-clamp-4 mt-2">{group?.description}</p>
                            {group?.admin && <>
                                <button onClick={() => handleOpenEditModal("about")} className="btn-primary w-full flex items-center justify-center gap-x-1 my-3">
                                    <FontAwesomeIcon icon={faPen} className="text-white" />
                                    <span>Chỉnh sửa</span>
                                </button>
                                <hr className="mb-3" />
                            </>}
                            <div className="mt-4 flex flex-col gap-y-1">
                                <div className="flex items-center gap-x-2 font-semibold text-gray-500">
                                    <History size={20} />
                                    <span>{`Được tạo lúc: ${group?.createdAt}`}</span>
                                </div>
                                <div className="flex items-center gap-x-2 font-semibold text-gray-500">
                                    <Users size={20} />
                                    <span>{`${group?.memberCount} thành viên`}</span>
                                </div>
                                <div className="flex items-center gap-x-2 font-semibold text-gray-500">
                                    {group?.privacy && group.privacy === GroupPrivacy.PRIVACY_PUBLIC ? <Earth size={20} /> : <Lock size={20} />}
                                    <span>{group?.privacy && group.privacy === GroupPrivacy.PRIVACY_PUBLIC ? 'Nhóm công khai' : 'Nhóm riêng tư'}</span>
                                </div>
                            </div>
                        </section>
                    </div>
                </main>
                <Modal
                    title={<h1 className="md:text-lg mb-5 text-base text-center font-semibold">Mời bạn bè tham gia nhóm này</h1>}
                    centered
                    open={openInviteModal}
                    onCancel={() => setOpenInviteModal(false)}
                    width={950}
                    destroyOnClose
                    footer
                >
                    <GroupInviteModalContent groupId={group?.id} />
                </Modal>
                <Modal
                    title={<h1 className="md:text-lg mb-5 text-base text-center font-semibold">Chỉnh sửa thông tin nhóm</h1>}
                    centered
                    open={openEditModal}
                    onCancel={() => setOpenEditModal(false)}
                    width={650}
                    destroyOnClose
                    footer
                >
                    <EditGroupInfoModalContent onUpdated={handleOnUpdated} type={editType} group={group} />
                </Modal>
            </> : <NotFound />}
        </>
    );
};

export default GroupLayout;