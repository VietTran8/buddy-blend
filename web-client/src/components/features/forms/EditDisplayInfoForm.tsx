import { Input } from "@/components";
import { RenameUserRequest, UserDetails } from "@/types";
import useModal from "antd/es/modal/useModal";
import { Field, Form, Formik } from "formik";
import { FC } from "react";
import PasswordConfirmForm from "./PasswordConfirmForm";
import { useRenameUser } from "@/hooks";

interface IProps {
    user?: UserDetails
};

type ChangeDisplayInfoType = {
    firstName?: string;
    lastName?: string;
    middleName?: string;
}

const EditDisplayInfoForm: FC<IProps> = ({ user }) => {
    const [modal, contextHolder] = useModal();
    const { mutate, isPending } = useRenameUser();

    const initialValues: ChangeDisplayInfoType = {
        firstName: user?.firstName,
        middleName: user?.middleName,
        lastName: user?.lastName,
    }

    const onSubmit = (values: ChangeDisplayInfoType) => {
        if(values.firstName !== initialValues.firstName || values.middleName !== initialValues.middleName || values.lastName !== initialValues.lastName) {
            const modalInstance = modal.info({
                icon: <></>,
                destroyOnClose: true,
                content: <PasswordConfirmForm onPasswordCorrect={(token) => handleRenameUser({
                    token,
                    firstName: values.firstName || "",
                    lastName: values.lastName || "",
                    middleName: values.middleName || ""
                }, modalInstance)}/>,
                footer: <></>,
                width: 550,
                closable: true,
                centered: true
            });
        }
    }

    const handleRenameUser = (payload: RenameUserRequest, modalInstance: any) => {
        mutate(payload, {
            onSuccess: () => {
                modalInstance.destroy();
            }
        });
    }

    return <Formik
        onSubmit={onSubmit}
        initialValues={initialValues}
    >
        {() => <Form>
            <div className="grid grid-cols-2 gap-x-3">
                <div className="">
                    <label htmlFor="firstName" className="font-semibold text-gray-400">Họ</label>
                    <Field
                        component={Input}
                        name="firstName"
                        placeholder="Nhập họ..."
                        id="firstName"
                        className="mt-2"
                    />
                </div>
                <div>
                    <label htmlFor="middleName" className="font-semibold text-gray-400">Họ đệm</label>
                    <Field
                        component={Input}
                        name="middleName"
                        placeholder="Nhập họ đệm..."
                        id="middleName"
                        className="mt-2"
                    />
                </div>
                <div className="mt-3 col-span-full">
                    <label htmlFor="lastName" className="font-semibold text-gray-400">Tên</label>
                    <Field
                        component={Input}
                        name="lastName"
                        placeholder="Nhập tên..."
                        id="lastName"
                        className="mt-2"
                    />
                </div>
            </div>

            <div className="mt-5 flex">
                <button disabled={isPending} type="submit" className="btn-primary ms-auto">{
                    isPending ? 'Đang lưu...' : 'Lưu thay đổi'
                }</button>
            </div>
            {contextHolder}
        </Form>}
    </Formik>
};

export default EditDisplayInfoForm;