import { Avatar } from "antd";
import { FC, useContext, useState } from "react";
import Input from "../shared/Input";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faPaperPlane, faSpinner } from "@fortawesome/free-solid-svg-icons";
import { Image } from "lucide-react";
import { Field, Form, Formik, FormikHelpers } from "formik";
import PostMediaUploader from "../shared/PostMediaUploader";
import { Comment, Post, PostCommentRequest, User } from "../../types";
import { usePostComment } from "../../hooks";
import { AuthContext } from "../../context";

interface IProps {
    className?: string;
    placeholder?: string;
    post: Post,
    parentId?: string;
    replyUser?: User;
    replyCmt?: Comment;
    onCommented?: () => void
};

const CommentCreator: FC<IProps> = ({ className, placeholder, post, parentId, onCommented}) => {
    const [openUploadSection, setOpenUploadSection] = useState<boolean>(false);
    const { user } = useContext(AuthContext);
    const { mutate: postComment, isPending } = usePostComment();

    const initialValues: PostCommentRequest = {
        content: "",
        postId: post.id,
        imageUrls: [],
        parentId
    }

    const handlePostCmt = (values: any, helpers: FormikHelpers<PostCommentRequest>) => {
        postComment(values, {
            onSuccess: () => {
                helpers.resetForm();
                onCommented && onCommented();
            }
        });
    }

    return <div className={`flex gap-x-3 ${className}`}>
        <Avatar className="flex-shrink-0 self-start" size="large" src={user?.profilePicture || "/images/default-user.png"} />
        <Formik
            onSubmit={handlePostCmt}
            initialValues={initialValues}
        >
            {({ }) => (
                <Form className="flex flex-col gap-y-2 w-full">
                    <div className="flex gap-x-2 w-full">
                        <Field
                            component={Input}
                            name="content"
                            className="w-full flex-1"
                            placeholder={placeholder}
                        />
                        <button type="button" onClick={() => setOpenUploadSection(true)} className="p-3 select-none flex items-center justify-center rounded-md cursor-pointer flex-shrink-0 hover:bg-gray-100 bg-gray-50 transition-all">
                            <Image className="text-gray-400 w-9" />
                        </button>
                        <button disabled={isPending} type="submit" className="p-3 select-none flex items-center justify-center flex-shrink-0 rounded-md cursor-pointer hover:opacity-80 bg-[--primary-color] transition-all">
                            <FontAwesomeIcon icon={isPending ? faSpinner : faPaperPlane} spin={isPending} className="text-white w-9" />
                        </button>
                    </div>
                    {openUploadSection && <Field
                        name="imageUrls"
                        multiple={false}
                        component={PostMediaUploader}
                        setOpen={setOpenUploadSection}
                    />}
                </Form>
            )}
        </Formik>
    </div>
};

export default CommentCreator;