import { Form, Formik } from "formik";
import { FC } from "react";
import { Link } from "react-router-dom";
import { CreateStoryRequest, EMediaType, EPrivacy, EStoryType } from "../../../types";
import { TreeSelect } from "antd";
import { storyPrivacyData } from "../../../constants";
import { useCreateStory } from "../../../hooks";

interface IProps {
    className?: string;
    editedFile: File | null;
};

const initialValues: CreateStoryRequest = {
    privacy: EPrivacy.PUBLIC,
    storyType: EStoryType.TYPE_MEDIA,
    mediaUrl: "",
    thumbnailUrl: "",
    mediaType: EMediaType.TYPE_IMAGE,
}

const CreateImageStoryForm: FC<IProps> = ({ className, editedFile }) => {
    const { mutate: create, isPending: isCreating } = useCreateStory();

    const handleCreateStory = (values: CreateStoryRequest) => {
        if (editedFile)
            create({
                ...values,
                mediaFile: editedFile
            });
    }

    return <Formik
        onSubmit={handleCreateStory}
        initialValues={initialValues}
    >
        {({ values, setFieldValue }) => (
            <Form className={`flex flex-col gap-y-4 ${className}`}>
                <div className="">
                    <label className="font-semibold text-gray-500" htmlFor="privacy">Quyền riêng tư</label>
                    <TreeSelect
                        style={{ width: '100%' }}
                        className="lg:h-[68px] h-[53px] mt-2"
                        value={values.privacy}
                        dropdownStyle={{ maxHeight: 400, overflow: 'auto' }}
                        treeData={storyPrivacyData}
                        placeholder="Chọn quyền riêng tư"
                        treeDefaultExpandAll
                        onChange={(newValue) => {
                            setFieldValue("privacy", newValue);
                        }}
                    />
                </div>
                <p className="text-sm -mt-2 text-gray-400">
                    {values.privacy === EPrivacy.PUBLIC ?
                        'Mọi người có thể xem được tin của bạn.' : values.privacy === EPrivacy.ONLY_FRIENDS ?
                            'Chỉ có bạn bè của bạn mới có thể xem được tin của bạn.' :
                            'Chỉ bạn mới có thể xem được tin.'}
                </p>
                <div className='flex items-center gap-x-2 mt-4'>
                    <Link className="w-full" to="/">
                        <button disabled={isCreating} type="button" className="btn-secondary-lg w-full">Hủy</button>
                    </Link>
                    <button disabled={!!!editedFile || isCreating} type="submit" className="btn-primary-lg w-full">{isCreating ? 'Đang tạo tin' :'Tạo tin mới'}</button>
                </div>
            </Form>
        )}
    </Formik>
};

export default CreateImageStoryForm;