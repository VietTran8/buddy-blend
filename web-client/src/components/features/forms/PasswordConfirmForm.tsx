import Input from "@/components/shared/Input";
import { AuthContext } from "@/context";
import { useCheckPassword } from "@/hooks";
import { PasswordCheckingRequest } from "@/types";
import { Field, Form, Formik, FormikHelpers } from "formik";
import { FC, useContext } from "react";

interface IProps {
    onPasswordCorrect?: (token: string) => void
};

const PasswordConfirmForm: FC<IProps> = ({ onPasswordCorrect }) => {
    const { user: authUser } = useContext(AuthContext);
    const { mutate } = useCheckPassword();

    const initialValues: PasswordCheckingRequest = {
        password: "",
        email: authUser?.email || ""
    }

    const onSubmit = (values: PasswordCheckingRequest, helpers: FormikHelpers<PasswordCheckingRequest>) => {
        mutate(values, {
            onSuccess: (data) => {
                onPasswordCorrect?.(data.data?.token || "")
            },
            onError: () => {
                helpers.setErrors({
                    password: "Mật khẩu không đúng!"
                })
            }
        })
    }

    return <Formik
        onSubmit={onSubmit}
        initialValues={initialValues}
    >
        {({ values }) => <Form>
            <label htmlFor="password" className="font-semibold text-gray-500">Xác nhận mật khẩu</label>
            <Field
                className="mt-4"
                component={Input}
                name="password"
                type="password"
                placeholder="Xác nhận mật khẩu..."
            />

            <button disabled={!!!values.password} className="btn-primary w-full mt-4">Xác nhận</button>
        </Form>}
    </Formik>
};

export default PasswordConfirmForm;