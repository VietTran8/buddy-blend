import { faCaretDown } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { Avatar, Button, Modal } from "antd";
import { FC, useContext, useState } from "react";
import PrivacySelectionModalContent from "./PrivacySelectionModalContent";
import { EPrivacy, Post, SharePostRequest } from "../../../types";
import { getPrivacyDesc } from "../../../utils";
import { useSharePost } from "../../../hooks";
import { Field, Form, Formik, FormikErrors } from "formik";
import { AuthContext } from "../../../context";

interface IProps {
    post?: Post;
};

const ShareModalContent: FC<IProps> = ({ post }) => {
    const { user } = useContext(AuthContext);
    const [openPrivacyModal, setOpenPrivacyModal] = useState<boolean>(false);
    const { mutate: share, isPending: isSharing } = useSharePost();

    const initialValues: SharePostRequest = {
        postId: post?.id || "",
        status: "",
        privacy: EPrivacy.PUBLIC
    }

    const handleOpenPrivacyModal = () => {
        setOpenPrivacyModal(true);
    }

    const handlePrivacyChange = (value: EPrivacy, setFieldValue: (field: string, value: any, shouldValidate?: boolean) => Promise<void | FormikErrors<SharePostRequest>>) => {
        setFieldValue("privacy", value);
    }

    const handleSharePost = (values: SharePostRequest) => {
        console.log(values);

        share(values);
    }

    const renderPrivacyOptionBtnContent = (privacy: EPrivacy) => {
        const privacyDesc = getPrivacyDesc(privacy);

        return (
            <>
                {privacyDesc.icon}
                <span className="text-gray-600 text-sm font-semibold">{privacyDesc.text}</span>
                <FontAwesomeIcon className="text-gray-600" icon={faCaretDown} />
            </>
        )
    }

    return (
        <div className="max-h-[65vh] overflow-y-auto no-scrollbar">
            <Formik
                onSubmit={handleSharePost}
                initialValues={initialValues}
            >
                {({ values, setFieldValue }) => (
                    <Form>
                        <div>
                            <div className="flex w-full items-center gap-x-3">
                                <Avatar className="flex-shrink-0" size={45} src={user?.profilePicture || "images/default-user.png"} />
                                <div className="w-full">
                                    <h3 className="text-base font-semibold mb-1">{user?.userFullName}</h3>
                                    <button type="button" onClick={handleOpenPrivacyModal} className="px-3 py-1 rounded-md hover:bg-gray-200 transition-all bg-gray-100 flex items-center gap-x-2">
                                        {renderPrivacyOptionBtnContent(values.privacy)}
                                    </button>
                                </div>
                            </div>
                            <Field
                                component={"textarea"}
                                name="status"
                                rows={5}
                                className="bg-transparent custom-scroll w-full outline-none mt-4"
                                placeholder="Hãy nói gì đó về nội dung này..."
                            />
                            <div className="flex justify-end mt-2">
                                <Button loading={isSharing} htmlType="submit" type="primary" size="large"><span className="font-semibold">Chia sẻ ngay</span></Button>
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
                            <PrivacySelectionModalContent onPrivacyChange={(privacy: EPrivacy) => handlePrivacyChange(privacy, setFieldValue)} />
                        </Modal>
                    </Form>
                )}
            </Formik>
        </div>
    )
};

export default ShareModalContent;