import { Field, Form, Formik, FormikHelpers } from "formik";
import { FC, useContext, useState } from "react";
import Input from "../../shared/Input";
import { changePasswordSchema } from "../../../validations";
import { useCreateChangePassOtp } from "../../../hooks";
import { AuthContext } from "../../../context";
import { Modal } from "antd";
import ChangePassOTPModalContent from "../modals/ChangePassOTPModalContent";
import { getErrorRespMsg } from "../../../utils";

interface IProps { };

type EditPassword = {
    oldPassword: string;
    newPassword: string;
    confirmNewPassword: string;
}

const initialValues: EditPassword = {
    oldPassword: "",
    newPassword: "",
    confirmNewPassword: ""
}

const EditPasswordForm: FC<IProps> = ({ }) => {
    const { user } = useContext(AuthContext);
    const { mutate, isPending } = useCreateChangePassOtp();

    const [openOtpModal, setOpenOtpModal] = useState<boolean>(false);

    const handleEditPassword = (values: EditPassword, helper: FormikHelpers<EditPassword>) => {
        mutate({
            email: user?.email || "",
            oldPassword: values.oldPassword
        }, {
            onSuccess: () => {
                handleOpenOtpModal();
            },
            onError(error: any) {
                helper.setErrors({
                    oldPassword: getErrorRespMsg(error)
                });
            },
        });
    }

    const handleOpenOtpModal = () => setOpenOtpModal(true);

    const handlePasswordChanged = (handleReset: (e?: React.SyntheticEvent<any>) => void) => {
        setOpenOtpModal(false);
        handleReset();
    }

    return (
        <>
            <Formik
                initialValues={initialValues}
                onSubmit={handleEditPassword}
                validationSchema={changePasswordSchema}
            >
                {({ values, handleReset }) => (
                    <>
                        <Form className="grid grid-cols-2 gap-3">
                            <div className="col-span-full">
                                <label htmlFor="oldPassword" className="font-semibold text-gray-400">Mật khẩu cũ</label>
                                <Field
                                    component={Input}
                                    name="oldPassword"
                                    placeholder="Nhập mật khẩu cũ..."
                                    id="oldPassword"
                                    type="password"
                                    className="mt-2"
                                />
                            </div>
                            <div className="col-span-1">
                                <label htmlFor="newPassword" className="font-semibold text-gray-400">Mật khẩu mới</label>
                                <Field
                                    component={Input}
                                    name="newPassword"
                                    placeholder="Nhập mật khẩu mới..."
                                    id="newPassword"
                                    type="password"
                                    className="mt-2"
                                />
                            </div>
                            <div className="col-span-1">
                                <label htmlFor="confirmNewPassword" className="font-semibold text-gray-400">Nhập lại mật khẩu mới</label>
                                <Field
                                    component={Input}
                                    name="confirmNewPassword"
                                    placeholder="Nhập lại mật khẩu mới..."
                                    id="confirmNewPassword"
                                    type="password"
                                    className="mt-2"
                                />
                            </div>
                            <div className="col-span-full flex mt-5">
                                <button type="submit" disabled={isPending} className="btn-primary ms-auto">{`${isPending ? 'Đang kiểm tra...' : 'Cập nhật'}`}</button>
                            </div>
                        </Form>

                        <Modal
                            title={<h1 className="md:text-lg mb-5 text-base text-center font-semibold">Nhập mã OTP</h1>}
                            centered
                            open={openOtpModal}
                            onCancel={() => setOpenOtpModal(false)}
                            width={550}
                            footer
                            destroyOnClose
                        >
                            <ChangePassOTPModalContent onPasswordChanged={() => handlePasswordChanged(handleReset)} newPassword={values.newPassword} />
                        </Modal>
                    </>
                )}
            </Formik>
        </>
    )
};

export default EditPasswordForm;