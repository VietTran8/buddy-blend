import { TreeSelect } from "antd";
import { Field, Form, Formik } from "formik";
import { FC } from "react";
import { genderData, provinces } from "../../../constants";
import Input from "../../shared/Input";
import { UserDetails } from "../../../types";
import { generalInfoSchema } from "../../../validations";
import { useUpdateUserInfo } from "../../../hooks";

interface IProps {
    user?: UserDetails
};

const EditGeneralInfoForm: FC<IProps> = ({ user }) => {
    const { mutate: update, isPending: isUpdating } = useUpdateUserInfo();

    const initialValues: Partial<UserDetails> = {
        fromCity: user?.fromCity,
        gender: user?.gender,
        email: user?.email,
        phone: user?.phone
    }

    const handleSubmitGeneralInfo = (values: Partial<UserDetails>) => {
        update({
            fromCity: values.fromCity || "",
            gender: values.gender || "Khác",
            phone: values.phone || ""
        });
    }

    return (
        <Formik
            onSubmit={handleSubmitGeneralInfo}
            initialValues={initialValues}
            validationSchema={generalInfoSchema}
        >
            {({ values, setFieldValue }) => (
                <Form className="grid grid-cols-2 gap-3">
                    <div className="">
                        <label htmlFor="gender" className="font-semibold text-gray-400">Giới tính</label>
                        <TreeSelect
                            style={{ width: '100%' }}
                            className="lg:h-[55px] h-[40px] mt-2"
                            value={values.gender}
                            dropdownStyle={{ maxHeight: 400, overflow: 'auto' }}
                            treeData={genderData}
                            placeholder="Chọn giới tính"
                            treeDefaultExpandAll
                            onChange={(newValue) => setFieldValue("gender", newValue)}
                        />
                    </div>
                    <div className="">
                        <label htmlFor="phone" className="font-semibold text-gray-400">Số điện thoại</label>
                        <Field
                            component={Input}
                            name="phone"
                            placeholder="Nhập số điện thoại..."
                            id="phone"
                            className="mt-2"
                        />
                    </div>
                    <div className="col-span-full">
                        <label htmlFor="email" className="font-semibold text-gray-400">Email</label>
                        <Field
                            disabled
                            component={Input}
                            name="email"
                            placeholder="Nhập email..."
                            id="email"
                            className="mt-2"
                        />
                    </div>
                    <div className="col-span-full">
                        <label htmlFor="address" className="font-semibold text-gray-400">Địa chỉ</label>
                        <TreeSelect
                            style={{ width: '100%' }}
                            className="lg:h-[55px] h-[40px] mt-2"
                            value={values.fromCity}
                            dropdownStyle={{ maxHeight: 400, overflow: 'auto' }}
                            treeData={provinces}
                            placeholder="Chọn nơi ở"
                            treeDefaultExpandAll
                            onChange={(newValue) => setFieldValue("fromCity", newValue)}
                        />
                    </div>
                    <div className="col-span-full flex mt-5">
                        <button disabled={isUpdating} type="submit" className="btn-primary ms-auto">{
                            isUpdating ? 'Đang lưu...' : 'Lưu thay đổi'
                        }</button>
                    </div>
                </Form>
            )}
        </Formik>
    )
};

export default EditGeneralInfoForm;