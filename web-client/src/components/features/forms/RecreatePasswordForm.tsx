import { Button, Input } from "@/components";
import { useChangePassword } from "@/hooks";
import { ChangePasswordRequest } from "@/types";
import { getErrorRespMsg } from "@/utils";
import { recreatePasswordSchema } from "@/validations";
import { Field, Form, Formik } from "formik";
import { FC } from "react";
import toast from "react-hot-toast";
import { useNavigate } from "react-router-dom";

interface IProps { 
    correctOtp: string;
    email: string;
};

const RecreatePasswordForm: FC<IProps> = ({ correctOtp, email }) => {
    const { mutate, isPending } = useChangePassword();
    const navigate = useNavigate();

    let initialValues: ChangePasswordRequest & { confirmNewPassword: string } = {
        email,
        newPassword: "",
        otp: correctOtp,
        confirmNewPassword: ""
    }

    const handleSubmit = (values: ChangePasswordRequest & { confirmNewPassword: string }) => {
        const { confirmNewPassword, ...payload } = values;

        mutate(payload, {
            onSuccess: () => {
               navigate("/login");
            },
            onError: (error: any) => {
                toast.error(getErrorRespMsg(error));
            }
        });
    }

    return <Formik
        onSubmit={handleSubmit}
        initialValues={initialValues}
        validationSchema={recreatePasswordSchema}
        validateOnBlur
        validateOnMount
        validateOnChange
    >
        {({ }) => (
            <>
                <Form>
                    <label className="font-semibold text-gray-400" htmlFor="email">Mật khẩu mới</label>
                    <Field
                        component={Input}
                        placeholder="Mật khẩu mới"
                        id="newPassword"
                        type="password"
                        className="w-full mt-2 mb-4"
                        name="newPassword"
                    />

                    <label className="font-semibold text-gray-400" htmlFor="email">Nhập lại mật khẩu mới</label>
                    <Field
                        component={Input}
                        placeholder="Nhập lại mật khẩu mới"
                        id="confirmNewPassword"
                        className="w-full mt-2"
                        type="password"
                        name="confirmNewPassword"
                    />
                    <Button isLoading={isPending}>Thay đổi</Button>
                </Form>
            </>
        )}
    </Formik>
};

export default RecreatePasswordForm;