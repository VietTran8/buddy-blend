import { TreeSelect } from "antd";
import { Field, Form, Formik } from "formik";
import { ChangeEvent, FC, memo } from "react";
import { CreateStoryRequest, EPrivacy, EStoryFont, EStoryType } from "../../../types";
import { STORY_BACKGROUND, storyFontData, storyPrivacyData } from "../../../constants";
import { Link } from "react-router-dom";
import { useCreateStory } from "../../../hooks";

interface IProps {
    className?: string;
    onContentChange?: (content: string) => void;
    onFontChange?: (font: EStoryFont) => void;
    onBackgroundChange?: (background: string) => void;
};

const initialValues: CreateStoryRequest & { mediaFile: File | null } = {
    privacy: EPrivacy.PUBLIC,
    storyType: EStoryType.TYPE_TEXT,
    font: EStoryFont.FONT_NORMAL,
    background: "bgPrimary",
    content: "",
    mediaFile: null
}

const CreateStoryForm: FC<IProps> = ({ className, onContentChange, onFontChange, onBackgroundChange }) => {
    const { mutate: create, isPending: isCreating } = useCreateStory();

    const handleCreateGroup = (values: CreateStoryRequest & { mediaFile: File | null }) => {
        create(values);
    }

    return (
        <div className={`${className}`}>
            <Formik
                onSubmit={handleCreateGroup}
                initialValues={initialValues}
            >
                {({ values, setFieldValue }) => (
                    <Form className="flex flex-col gap-y-4">
                        <div className="">
                            <label className="font-semibold text-gray-500" htmlFor="content">Nội dung</label>
                            <Field
                                id="content"
                                name="content"
                                component="textarea"
                                onChange={(e: ChangeEvent<HTMLTextAreaElement>) => {
                                    setFieldValue("content", e.target.value);
                                    onContentChange && onContentChange(e.target.value);
                                }}
                                className="mt-1 transition-all w-full ps-5 pt-5 outline-none focus:outline-1 focus:outline-blue-500 bg-gray-100 rounded-lg text-gray-600"
                                placeholder="Nhập nội dung..."
                                rows={5}
                            />
                        </div>
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
                        <div className="">
                            <label className="font-semibold text-gray-500" htmlFor="privacy">Phông chữ</label>
                            <TreeSelect
                                style={{ width: '100%' }}
                                className="lg:h-[68px] h-[53px] mt-2"
                                value={values.font}
                                dropdownStyle={{ maxHeight: 400, overflow: 'auto' }}
                                treeData={storyFontData}
                                placeholder="Chọn phông chữ"
                                treeDefaultExpandAll
                                onChange={(newValue) => {
                                    setFieldValue("font", newValue);
                                    onFontChange && onFontChange(newValue);
                                }}
                            />
                        </div>
                        <div className="">
                            <label className="font-semibold text-gray-500" htmlFor="content">Màu nền</label>
                            <div className="mt-1 w-full flex-wrap ps-5 pt-5 flex gap-2">
                                {Object.entries(STORY_BACKGROUND).map(([key, value]) => <span
                                    className={`w-8 h-8 transition-all  flex-shrink-0 rounded-full cursor-pointer ${value} ${key === values.background && 'scale-125 border-[--primary-color] border-4'}`}
                                    onClick={() => {
                                        setFieldValue("background", key);
                                        onBackgroundChange && onBackgroundChange(value);
                                    }}
                                ></span>)}
                            </div>
                        </div>
                        <div className="flex items-center gap-x-2 mt-4">
                            <Link className="w-full" to="/">
                                <button disabled={isCreating} type="button" className="btn-secondary-lg w-full">Hủy</button>
                            </Link>
                            <button disabled={isCreating} type="submit" className="btn-primary-lg w-full">{isCreating ? 'Đang tạo tin...' : 'Tạo tin mới'}</button>
                        </div>
                    </Form>
                )}
            </Formik>
        </div>
    )
};

export default memo(CreateStoryForm);