import { Field, Form, Formik } from "formik";
import { ChangeEvent, FC, memo } from "react";
import Input from "../../shared/Input";
import { TreeSelect } from "antd";
import { CreateGroupRequest } from "../../../types/request";
import { groupPrivacyData } from "../../../constants";
import { GroupPrivacy } from "../../../types";
import { createGroupSchema } from "../../../validations";
import { useCreateGroup } from "../../../hooks";

interface IProps {
    className?: string;
    onNameChange?: (name: string) => void,
    onPrivacyChange?: (privacy: GroupPrivacy) => void,
};

const initialValues: CreateGroupRequest = {
    description: '',
    name: '',
    privacy: GroupPrivacy.PRIVACY_PUBLIC
};

const CreateGroupForm: FC<IProps> = ({ className, onNameChange, onPrivacyChange }) => {
    const { mutate: createGroup, isPending: isCreating } = useCreateGroup();

    const handleCreateGroup = (values: CreateGroupRequest) => {
        createGroup(values);
        console.log(values);
    }
    
    console.log(isCreating);

    return (
        <div className={`${className}`}>
            <Formik
                onSubmit={handleCreateGroup}
                initialValues={initialValues}
                validationSchema={createGroupSchema}
            >
                {({ values, setFieldValue }) => (
                    <Form className="flex flex-col gap-y-4">
                        <div className="">
                            <label className="font-semibold text-gray-500" htmlFor="groupName">Tên nhóm</label>
                            <Field
                                id="groupName"
                                name="name"
                                component={Input}
                                className="mt-1"
                                onChange={(e: ChangeEvent<HTMLInputElement>) => {
                                    setFieldValue("name", e.target.value);
                                    onNameChange && onNameChange(e.target.value);
                                }}
                                placeholder="Nhập tên nhóm..."
                            />
                        </div>
                        <div className="">
                            <label className="font-semibold text-gray-500" htmlFor="groupDescription">Mô tả nhóm</label>
                            <Field
                                id="groupDescription"
                                name="description"
                                component="textarea"
                                className="mt-1 transition-all w-full ps-5 pt-5 outline-none focus:outline-1 focus:outline-blue-500 bg-gray-100 rounded-lg text-gray-600"
                                placeholder="Nhập mô tả..."
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
                                treeData={groupPrivacyData}
                                placeholder="Chọn quyền riêng tư"
                                treeDefaultExpandAll
                                onChange={(newValue) => {
                                    onPrivacyChange && onPrivacyChange(newValue);
                                    setFieldValue("privacy", newValue);
                                }}
                            />
                        </div>
                        <p className="text-sm -mt-2 text-gray-400">
                            {values.privacy === GroupPrivacy.PRIVACY_PUBLIC ? 
                            'Thành viên và khách truy cập có thể đăng bài trong nhóm. Quản trị viên có thể xét duyệt người lần đầu tham gia.' : 
                            'Để bảo vệ quyền riêng tư của thành viên, nhóm riêng tư không thể chuyển thành công khai.'}
                        </p>
                        <button disabled={isCreating} type="submit" className="btn-primary-lg w-full mt-4">{isCreating ? 'Đang tạo nhóm...' : 'Tạo'}</button>
                    </Form>
                )}
            </Formik>
        </div>
    )
};

export default memo(CreateGroupForm);