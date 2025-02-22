import { Form, Formik, FormikHelpers } from "formik";
import { FC } from "react";
import { useValidateOtp } from "../../../hooks";
import { ValidateOTPRequest } from "../../../types";
import { InputOTP, InputOTPGroup, InputOTPSlot } from "@/components/ui/input-otp";
import { REGEXP_ONLY_DIGITS } from "input-otp";
import { getErrorRespMsg } from "@/utils";

interface IProps {
    email: string;
    onOtpCorrect?: (otp: string, email: string) => void;
};

const ForgotPassOTPModalContent: FC<IProps> = ({ email, onOtpCorrect }) => {
    const { mutate, isPending } = useValidateOtp();

    const initialValues: ValidateOTPRequest = {
        otp: "",
        email,
    }

    const handleSubmit = (values: ValidateOTPRequest, helpers: FormikHelpers<ValidateOTPRequest>) => {
        mutate(values, {
            onSuccess: () => {
                onOtpCorrect?.(values.otp, values.email);
            },
            onError: (error: any) => {
                helpers.setErrors({
                    otp: getErrorRespMsg(error)
                })
            }
        });
    }

    const handleOnOtpInputChange = (otp: string, setFieldValue: any) => {
        setFieldValue("otp", otp);
    }

    return <div className="py-3 w-full">
        <Formik
            onSubmit={handleSubmit}
            initialValues={initialValues}
        >
            {({ errors, submitCount, values, setFieldValue, getFieldMeta }) => (<Form>
                <p className="text-gray-500 mb-5">{`Mã OTP đã được gửi về "${email}", vui lòng kiểm tra và nhập mã OTP để tiếp tục. Lưu ý mã chỉ có hiệu lực trong 5 phút.`}</p>
                <div className="w-full flex justify-center py-4">
                    <InputOTP
                        maxLength={6}
                        onChange={(value) => handleOnOtpInputChange(value, setFieldValue)}
                        value={values.otp}
                        pattern={REGEXP_ONLY_DIGITS}
                    >
                        <InputOTPGroup>
                            <InputOTPSlot error={!!errors.otp} index={0} />
                            <InputOTPSlot error={!!errors.otp} index={1} />
                            <InputOTPSlot error={!!errors.otp} index={2} />
                            <InputOTPSlot error={!!errors.otp} index={3} />
                            <InputOTPSlot error={!!errors.otp} index={4} />
                            <InputOTPSlot error={!!errors.otp} index={5} />
                        </InputOTPGroup>
                    </InputOTP>
                </div>
                {((submitCount && submitCount > 0) || getFieldMeta("otp").touched) && errors.otp && <p className="text-red-400 text-center font-semibold text-sm mt-2">{errors.otp}</p>}
                <button type="submit" disabled={values.otp.length !== 6 || isPending} className="btn-primary mt-5 w-full">{`${isPending ? 'Đang thay đổi...' : 'Xác nhận'}`}</button>
            </Form>)}
        </Formik>
    </div>
};

export default ForgotPassOTPModalContent;