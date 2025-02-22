import { Button, ForgotPassOTPModalContent, Input, RecreatePasswordForm } from "@/components";
import { useCreateForgotPassOtp } from "@/hooks";
import { CreateForgotPasswordRequest } from "@/types";
import { getErrorRespMsg } from "@/utils";
import { forgotPasswordSchema } from "@/validations";
import { faUser } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { Modal } from "antd";
import { Field, Form, Formik, FormikHelpers } from "formik";
import { FC, useState } from "react";
import { Link } from "react-router-dom";

interface IProps { };

const ForgotPassword: FC<IProps> = ({ }) => {
    const { mutate, isPending } = useCreateForgotPassOtp();

    const [openOtpModal, setOpenOtpModal] = useState<boolean>(false);
    const [showRecreatePassword, setShowRecreatePassword] = useState<boolean>(false);
    const [correctOtp, setCorrectOtp] = useState<string>("");
    const [email, setEmail] = useState<string>("");

    const initialCreateOtpValues: CreateForgotPasswordRequest = {
        email: ""
    }

    const handleOpenOtpModal = () => setOpenOtpModal(true);

    const handleSubmit = (values: CreateForgotPasswordRequest, helper: FormikHelpers<CreateForgotPasswordRequest>) => {
        mutate(values, {
            onSuccess: () => {
                handleOpenOtpModal();
            },
            onError: (error: any) => {
                helper.setErrors({
                    email: getErrorRespMsg(error)
                })
            }
        })
    }

    const handleOtpCorrect = (otp: string, email: string) => {
        setCorrectOtp(otp);
        setEmail(email);
        setOpenOtpModal(false);
        setShowRecreatePassword(true);
    }

    return <div className="grid grid-cols-12 bg-white min-h-[100vh] relative">
        <div className="md:col-span-8 col-span-full sticky top-0 h-fit bg-black">
            <img src="/images/community.jpg" alt="community" className="w-full md:h-[100vh] h-[30vh] object-cover" />
        </div>
        <div className="md:col-span-4 col-span-full flex flex-col items-center md:justify-center p-8">
            <h1 className="font-bold text-3xl self-start">Quên mật khẩu</h1>
            <p className="mt-2 self-start text-gray-500">Bạn có thể lấy lại mật khẩu sử dụng tài khoản gmail của bạn!</p>

            <div className="w-full px-3 mt-6">
                {
                    showRecreatePassword ? <RecreatePasswordForm email={email} correctOtp={correctOtp} /> : <>
                        <Formik
                            onSubmit={handleSubmit}
                            initialValues={initialCreateOtpValues}
                            validationSchema={forgotPasswordSchema}
                            validateOnBlur
                            validateOnMount
                            validateOnChange
                        >
                            {({ values }) => (
                                <>
                                    <Form>
                                        <label className="font-semibold text-gray-400" htmlFor="email">Email</label>
                                        <Field
                                            component={Input}
                                            startDrawable={<FontAwesomeIcon className="text-gray-400" icon={faUser} />}
                                            placeholder="Email"
                                            id="email"
                                            className="w-full mt-2"
                                            name="email"
                                        />
                                        <Button isLoading={isPending}>Lấy lại mật khẩu</Button>
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
                                        <ForgotPassOTPModalContent email={values.email} onOtpCorrect={handleOtpCorrect} />
                                    </Modal>
                                </>
                            )}
                        </Formik>
                    </>
                }
            </div>
            <div className="flex items-center gap-x-2 w-full justify-center">
                <Link to='/sign-up'>
                    <button className="btn-secondary-lg mt-10">Đăng ký tài khoản mới</button>
                </Link>
                <Link to='/login'>
                    <button className="btn-success-lg mt-10">Có tài khoản khác? Đăng nhập</button>
                </Link>
            </div>
        </div>
    </div>
};

export default ForgotPassword;