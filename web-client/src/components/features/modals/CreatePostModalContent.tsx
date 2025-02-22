import { FC, useContext, useState } from "react";
import { CreatePostRequest, EFileType, EPostType, EPrivacy, Media, User } from "../../../types";
import { getErrorRespMsg, getPrivacyDesc, isImage, removeExtraSpaces, renderTaggingTitle } from "../../../utils";
import { Avatar, Button, Modal } from "antd";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faCaretDown, faImage, faPaperPlane, faTag } from "@fortawesome/free-solid-svg-icons";
import PrivacySelectionModalContent from "./PrivacySelectionModalContent";
import TagUserModalContent from "./TagUserModalContent";
import PostMediaUploader from "../../shared/PostMediaUploader";
import { AuthContext } from "../../../context";
import { Field, Form, Formik, FormikHelpers } from "formik";
import { useCreatePost } from "../../../hooks";
import { uploadFile } from "../../../services";
import toast from "react-hot-toast";
import { STORY_BACKGROUND } from "@/constants";
import EditableDiv from "../EditableDiv";

interface IProps {
    isGroupPost?: boolean,
    groupId?: string,
    onCreated?: () => void
};

const renderPrivacyUI = (privacy: EPrivacy) => {
    const privacyDesc = getPrivacyDesc(privacy);

    return (
        <>
            {privacyDesc.icon}
            <span className="text-gray-600 text-sm font-semibold">{privacyDesc.text}</span>
            <FontAwesomeIcon className="text-gray-600" icon={faCaretDown} />
        </>
    )
}

const CreatePostModalContent: FC<IProps> = ({ isGroupPost = false, groupId, onCreated }) => {
    const { user } = useContext(AuthContext);
    const [openPrivacyModal, setOpenPrivacyModal] = useState<boolean>(false);
    const [openTagUserModal, setOpenTagUserModal] = useState<boolean>(false);
    const [openUploadSection, setOpenUploadSection] = useState<boolean>(false);
    const [isUploading, setIsUploading] = useState<boolean>(false);

    const { mutate: create, isPending: isCreating } = useCreatePost();

    const initialValues: CreatePostRequest & {
        mediaFiles: File[]
    } = {
        active: true,
        content: "",
        medias: [],
        postTags: [],
        privacy: EPrivacy.PUBLIC,
        type: isGroupPost ? EPostType.GROUP : EPostType.NORMAL,
        groupId,
        mediaFiles: []
    }

    const handleOpenPrivacyModal = () => {
        setOpenPrivacyModal(true);
    }

    const handleOpenTagUserModal = () => {
        setOpenTagUserModal(true);
    }

    const handleOpenUploadSection = () => {
        setOpenUploadSection(true);
    }

    const handlePrivacyChange = (value: EPrivacy, setFieldValue: any) => {
        setFieldValue("privacy", value);
    }

    const handleOnTaggingComplete = (users: User[], setFieldValue: any) => {
        setFieldValue("postTags", users);
        setOpenTagUserModal(false);
    }

    const handleOnBackgroundChange = (setFieldValue: any, selectedBackground: string, currentBackground?: string) => {
        if (selectedBackground === currentBackground) {
            setFieldValue("background", undefined);
        } else {
            setFieldValue("background", selectedBackground);
        }
    }

    const onCreatePost = async (values: CreatePostRequest & { mediaFiles: File[] }, helper: FormikHelpers<CreatePostRequest & { mediaFiles: File[] }>) => {
        const toastId = toast.loading("Đang đăng bài viết của bạn lên, vui lòng không đóng trình duyệt hay tải lại trang...", {
            position: "bottom-left"
        })

        setIsUploading(true);

        onCreated && onCreated();

        try {
            const { postTags, mediaFiles, ...rest } = values;

            let uploadedMedias: Media[] = [];

            for (const file of mediaFiles) {
                const response = await uploadFile(isImage(file) ? "img" : "vid", [file]);
            
                uploadedMedias.push({
                    url: response?.data[0].url || "",
                    thumbnail: response?.data[0].thumbnailUrl || "",
                    type: isImage(file) ? EFileType.TYPE_IMG : EFileType.TYPE_VIDEO,
                    ownerId: (isGroupPost ? groupId : user?.id) || ""
                });
            }

            const finalPayload = {
                ...rest,
                medias: uploadedMedias,
                postTags: (postTags as User[]).map(tag => ({ taggedUserId: tag.id })),
            };

            create(finalPayload, {
                onSuccess: () => {
                    toast.success(`Đã đăng bài viết`, { id: toastId });
                    helper.resetForm();
                    setOpenUploadSection(false);
                },
                onError: (error: any) => {
                    toast.error(getErrorRespMsg(error), { id: toastId });
                }
            });

        } catch (error) {
            console.error("Error creating post: ", error);
            toast.error(`Lỗi khi tạo bài đăng: ${error}`, { id: toastId });
        } finally {
            setIsUploading(false);
        }
    }

    return (
        <div className="max-h-[68vh] overflow-y-auto no-scrollbar">
            <Formik
                onSubmit={onCreatePost}
                initialValues={initialValues}
            >
                {({ values, setFieldValue }) => (<Form>
                    <div className="relative">
                        <div className="flex w-full items-center gap-x-3">
                            <Avatar className="flex-shrink-0" size={45} src={user?.profilePicture || "/images/default-user.png"} />
                            <div className="w-full">
                                <h3 className="text-base font-semibold mb-1">
                                    <span>{user?.userFullName}</span>
                                    {values.postTags.length > 0 && <span>
                                        <span className="font-normal"> cùng với </span>
                                        {renderTaggingTitle((values.postTags as User[]))}
                                    </span>}
                                </h3>
                                <button type="button" onClick={handleOpenPrivacyModal} className="px-3 py-1 rounded-md hover:bg-gray-200 transition-all bg-gray-100 flex items-center gap-x-2">
                                    {renderPrivacyUI(values.privacy)}
                                </button>
                            </div>
                        </div>
                        {(values.background && values.medias.length === 0 && !openUploadSection) ? <EditableDiv
                            background={values.background}
                            setFieldValue={setFieldValue}
                            value={values.content}
                            placeholder={`${removeExtraSpaces(user?.userFullName)} ơi, bạn đang nghĩ gì thế...`}
                        /> : <Field
                            component="textarea"
                            rows={7}
                            name="content"
                            className="bg-transparent custom-scroll w-full outline-none mt-4"
                            placeholder={`${removeExtraSpaces(user?.userFullName)} ơi, bạn đang nghĩ gì thế...`}
                        />}
                        {openUploadSection ?
                            <div>
                                <Field
                                    name="mediaFiles"
                                    component={PostMediaUploader}
                                    setOpen={setOpenUploadSection}
                                />
                            </div> :
                            <div className="w-full">
                                <div className="mb-3 flex flex-wrap gap-3 px-2">
                                    {Object.entries(STORY_BACKGROUND).map(([key, value]) => <span
                                        className={`w-10 h-10 transition-all flex-shrink-0 rounded-md cursor-pointer ${value} ${key === values.background && 'scale-125 border-gray-300 border-4'}`}
                                        onClick={() => {
                                            handleOnBackgroundChange(setFieldValue, key, values.background);
                                        }}
                                    ></span>)}
                                </div>
                                <div className="flex flex-col gap-2">
                                    <div onClick={handleOpenUploadSection} className="flex items-center gap-x-2 hover:bg-gray-50 cursor-pointer transition-all text-gray-500 px-4 py-3 font-semibold rounded-md border-2">
                                        <FontAwesomeIcon className="text-lg text-green-400" icon={faImage} />
                                        <p>Ảnh/Video</p>
                                    </div>
                                    <div onClick={handleOpenTagUserModal} className="flex items-center gap-x-2 hover:bg-gray-50 cursor-pointer transition-all text-gray-500 px-4 py-3 font-semibold rounded-md border-2">
                                        <FontAwesomeIcon className="text-lg text-red-400" icon={faTag} />
                                        <p>Gắn thẻ</p>
                                    </div>
                                </div>
                            </div>}
                        <div className="w-full sticky bottom-0 bg-white">
                            <Button disabled={!!!values.content && values.medias.length === 0} loading={isCreating || isUploading} type="primary" htmlType="submit" className="w-full mt-5" size="large">
                                <FontAwesomeIcon icon={faPaperPlane} />
                                <span className="font-semibold">Đăng</span>
                            </Button>
                        </div>
                    </div>
                    <Modal
                        title={<h1 className="md:text-lg mb-5 text-base text-center font-semibold">Đối tượng của bài viết</h1>}
                        centered
                        open={openPrivacyModal}
                        onCancel={() => setOpenPrivacyModal(false)}
                        width={660}
                        footer={<Button type="primary" size="large" onClick={() => setOpenPrivacyModal(false)}><span>Đồng ý</span></Button>}
                    >
                        <PrivacySelectionModalContent onPrivacyChange={(privacy) => handlePrivacyChange(privacy, setFieldValue)} />
                    </Modal>
                    <Modal
                        title={<h1 className="md:text-lg mb-5 text-base text-center font-semibold">Gắn thẻ bạn bè</h1>}
                        centered
                        open={openTagUserModal}
                        onCancel={() => setOpenTagUserModal(false)}
                        width={660}
                        footer
                        destroyOnClose
                    >
                        <TagUserModalContent
                            prevUsers={values.postTags as User[]}
                            onComplete={(users) => handleOnTaggingComplete(users, setFieldValue)}
                        />
                    </Modal>
                </Form>)}
            </Formik>
        </div >
    );
};

export default CreatePostModalContent;