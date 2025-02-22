import { FC } from "react";
import { EditInfoType, Gender, UpdateUserInfoRequest, UserDetails } from "../../../types";
import Input from "../../shared/Input";
import { Button, TreeSelect } from "antd";
import { Field, Form, Formik } from "formik";
import { genderData } from "../../../constants";
import { getGeneralInfoPlaceHolder, getUserInfoValue } from "../../../utils";
import { useUpdateUserInfo } from "../../../hooks";

interface IProps {
    type?: EditInfoType;
    user?: UserDetails;
    onUpdated?: () => void
};

const EditUserInfoModalContent: FC<IProps> = ({ type, user, onUpdated }) => {
    const { mutate: update, isPending: isUpdating } = useUpdateUserInfo();

    const onChange = (newValue: Gender, setFieldValue: any) => {
        setFieldValue("content", newValue);
    };

    const handleOnSubmit = ({ content }: { content?: string }) => {
        let updatePayload: UpdateUserInfoRequest = {}

        switch (type) {
            case "address":
                updatePayload = {
                    fromCity: content
                }
                break;
            case "gender":
                updatePayload = {
                    gender: content
                }
                break;
            case "phone":
                updatePayload = {
                    phone: content
                }
                break;
            case "bio":
                updatePayload = {
                    bio: content
                }
                break;
        }

        update(updatePayload, {
            onSuccess: () => onUpdated && onUpdated()
        });
    }

    return <div className="">
        <Formik
            onSubmit={handleOnSubmit}
            initialValues={{
                content: getUserInfoValue(type, user)
            }}
        >
            {({ values, setFieldValue }) => (
                <Form className="flex flex-col gap-y-4">
                    {type && type === "gender" ?
                        <TreeSelect
                            style={{ width: '100%' }}
                            className="lg:h-[55px] h-[40px]"
                            value={values.content}
                            dropdownStyle={{ maxHeight: 400, overflow: 'auto' }}
                            treeData={genderData}
                            placeholder={getGeneralInfoPlaceHolder(type)}
                            treeDefaultExpandAll
                            onChange={(newValue) => onChange(newValue as Gender, setFieldValue)}
                        />
                        : <Field
                            component={type !== "bio" ? Input : "textarea"}
                            className={`${type === "bio" && ' transition-all w-full ps-5 pt-5 outline-none focus:outline-1 focus:outline-blue-500 bg-gray-100 rounded-lg text-gray-600'}`}
                            name="content"
                            rows={type === "bio" ? 5 : undefined}
                            disabled={type === "email"}
                            placeholder={getGeneralInfoPlaceHolder(type)}
                        />}
                    <Button htmlType="submit" disabled={!!!values.content || type === "email" || isUpdating} type="primary" size="large"><span className="text-sm font-semibold">Lưu</span></Button>
                </Form>
            )}
        </Formik>
    </div>
};

export default EditUserInfoModalContent;