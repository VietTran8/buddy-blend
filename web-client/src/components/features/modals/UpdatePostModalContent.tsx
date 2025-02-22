import { FC, useContext, useState } from "react";
import { EFileType, EPrivacy, Media, Post, UpdatePostRequest, User } from "../../../types";
import { getPrivacyDesc, isImage, randomUUID, removeExtraSpaces, renderTaggingTitle } from "../../../utils";
import { Avatar, Button, Modal, UploadFile } from "antd";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faCaretDown, faSave, faSmile, faTag } from "@fortawesome/free-solid-svg-icons";
import PrivacySelectionModalContent from "./PrivacySelectionModalContent";
import TagUserModalContent from "./TagUserModalContent";
import PostMediaUploader from "../../shared/PostMediaUploader";
import { AuthContext } from "../../../context";
import { Field, Form, Formik } from "formik";
import { useUpdatePost } from "../../../hooks";
import { STORY_BACKGROUND } from "../../../constants";
import { uploadFile } from "../../../services";
import toast from "react-hot-toast";
import EditableDiv from "../EditableDiv";

interface IProps {
    post: Post;
    onUpdated?: () => void;
};

type ExtraUploadFile = UploadFile & { metadata?: Media }

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

const UpdatePostModalContent: FC<IProps> = ({ post, onUpdated }) => {
    const { user } = useContext(AuthContext);
    const [openPrivacyModal, setOpenPrivacyModal] = useState<boolean>(false);
    const [openTagUserModal, setOpenTagUserModal] = useState<boolean>(false);
    const [isUploading, setIsUploading] = useState<boolean>(false);

    const { mutate: updatePost, isPending } = useUpdatePost(post.groupInfo && post.groupInfo.id, user?.id);

    const initialValues: UpdatePostRequest & {
        mediaFiles: ExtraUploadFile[]
    } = {
        id: post.id,
        content: post.content,
        medias: post.medias,
        taggingUsers: post.taggedUsers,
        privacy: post.privacy,
        background: post.background,
        mediaFiles: post.medias?.map(media => ({
            uid: randomUUID(),
            url: media.url,
            name: media.url,
            metadata: media
        }))
    }

    const handleOpenPrivacyModal = () => {
        setOpenPrivacyModal(true);
    }

    const handleOpenTagUserModal = () => {
        setOpenTagUserModal(true);
    }

    const handlePrivacyChange = (value: EPrivacy, setFieldValue: any) => {
        setFieldValue("privacy", value);
    }

    const handleOnTaggingComplete = (users: User[], setFieldValue: any) => {
        setFieldValue("taggingUsers", users);
        setOpenTagUserModal(false);
    }

    const onUpdatePost = async (values: UpdatePostRequest & { mediaFiles: ExtraUploadFile[] }) => {
        setIsUploading(true);

        const { mediaFiles, ...rest } = values;

        const oldMedias = mediaFiles.filter(file => !!file.metadata);
        const newMedias = mediaFiles.filter(file => !!!file.metadata);
        try {
            const uploadedMedias: Media[] = [] 
            
            for (const media of newMedias) {
                const { metadata, ...file } = media;
            
                const response = await uploadFile(isImage(file) ? "img" : "vid", [file]);
            
                uploadedMedias.push({
                    url: response?.data[0].url || "",
                    thumbnail: response?.data[0].thumbnailUrl || "",
                    type: isImage(file) ? EFileType.TYPE_IMG : EFileType.TYPE_VIDEO,
                    ownerId: post.groupInfo ? post.groupInfo.id : post.user.id
                });
            }

            const updatedMedias = [...(oldMedias.map(file => file.metadata!)), ...uploadedMedias]

            const finalPayload: UpdatePostRequest = {
                ...rest,
                medias: updatedMedias,
                taggingUsers: (values.taggingUsers as User[]).map(user => user.id),
            }

            updatePost(finalPayload, {
                onSuccess: () => onUpdated && onUpdated()
            });

        } catch (error) {
            console.error("error updating post: ", error);
            toast.error(`Lỗi khi cập nhật bài viết: ${error}`)
        } finally {
            setIsUploading(false);
        }
    }

    const handleOnBackgroundChange = (setFieldValue: any, selectedBackground: string, currentBackground?: string) => {
        if (selectedBackground === currentBackground) {
            setFieldValue("background", undefined);
        } else {
            setFieldValue("background", selectedBackground);
        }
    }

    return (
        <div className="max-h-[68vh] overflow-y-auto no-scrollbar">
            <Formik
                onSubmit={onUpdatePost}
                initialValues={initialValues}
            >
                {({ values, setFieldValue }) => (<Form>
                    <div>
                        <div className="flex w-full items-center gap-x-3">
                            <Avatar className="flex-shrink-0" size={45} src={user?.profilePicture || "/images/default-user.png"} />
                            <div className="w-full">
                                <h3 className="text-base font-semibold mb-1">
                                    <span>{user?.userFullName}</span>
                                    {values.taggingUsers.length > 0 && <span>
                                        <span className="font-normal"> cùng với </span>
                                        {renderTaggingTitle((values.taggingUsers as User[]))}
                                    </span>}
                                </h3>
                                <button type="button" onClick={handleOpenPrivacyModal} className="px-3 py-1 rounded-md hover:bg-gray-200 transition-all bg-gray-100 flex items-center gap-x-2">
                                    {renderPrivacyUI(values.privacy)}
                                </button>
                            </div>
                        </div>
                        {(values.background && (values.medias.length + values.mediaFiles.length) === 0) ? <EditableDiv
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
                        {(values.medias.length + values.medias.length) === 0 &&
                            <div className="mb-3 flex flex-wrap gap-3 px-2">
                                {Object.entries(STORY_BACKGROUND).map(([key, value]) => <span
                                    className={`w-10 h-10 transition-all flex-shrink-0 rounded-md cursor-pointer ${value} ${key === values.background && 'scale-125 border-gray-300 border-4'}`}
                                    onClick={() => {
                                        handleOnBackgroundChange(setFieldValue, key, values.background);
                                    }}
                                ></span>)}
                            </div>}
                        <div className="my-4">
                            <Field
                                name="mediaFiles"
                                component={PostMediaUploader}
                                isUpdate={true}
                            />
                        </div>
                        <div className="flex flex-col gap-2">
                            <div onClick={handleOpenTagUserModal} className="flex items-center gap-x-2 hover:bg-gray-50 cursor-pointer transition-all text-gray-500 px-4 py-3 font-semibold rounded-md border-2">
                                <FontAwesomeIcon className="text-lg text-red-400" icon={faTag} />
                                <p>Gắn thẻ</p>
                            </div>
                            <div className="flex items-center gap-x-2 hover:bg-gray-50 cursor-pointer transition-all text-gray-500 px-4 py-3 font-semibold rounded-md border-2">
                                <FontAwesomeIcon className="text-lg text-yellow-400" icon={faSmile} />
                                <p>Cảm xúc</p>
                            </div>
                        </div>
                        <Button loading={isPending || isUploading} type="primary" htmlType="submit" className="w-full mt-5" size="large">
                            <FontAwesomeIcon icon={faSave} />
                            <span className="font-semibold">Lưu bài viết</span>
                        </Button>
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
                            prevUsers={values.taggingUsers as User[]}
                            onComplete={(users) => handleOnTaggingComplete(users, setFieldValue)}
                        />
                    </Modal>
                </Form>)}
            </Formik>
        </div >
    );
};

export default UpdatePostModalContent;