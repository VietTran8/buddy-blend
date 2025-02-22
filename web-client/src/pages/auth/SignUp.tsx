import { FC } from "react";
import { Link } from "react-router-dom";
import { Radio } from "antd";
import { Field, Form, Formik } from "formik";
import { SignUpRequest } from "../../types/request";
import { signUpSchema } from "../../validations";
import { Button, Input } from "../../components";
import { useSignUp } from "../../hooks";

interface IProps { };

const initialValues: SignUpRequest & { confirmPassword?: string } = {
    gender: "male",
    email: "",
    firstName: "",
    lastName: "",
    middleName: "",
    password: "",
}; 

const SignUp: FC<IProps> = ({ }) => {
    const { mutate, isPending } = useSignUp();

    const onSignUp = (values: SignUpRequest & { confirmPassword?: string }) => {
        const { confirmPassword, ...payload } = values;

        mutate(payload);
    }

    return (
        <div className="grid grid-cols-12 bg-white relative">
            <div className="md:col-span-7 col-span-full h-fit sticky top-0">
                <img src="/images/community2.jpg" alt="community" className="w-full md:h-[100vh] h-[30vh] object-cover" />
            </div>
            <div className="md:col-span-5 col-span-full flex flex-col items-center justify-center p-8 overflow-y-auto custom-scroll">
                <h1 className="font-bold text-3xl self-start md:mt-24">Tạo tài khoản mới</h1>
                <p className="mt-2 self-start text-gray-500">Hãy tạo tài khoản để cùng tham gia cộng đồng Buddy Blend nhé!</p>

                <div className="w-full px-3 mt-8">
                    <Formik
                        onSubmit={onSignUp}
                        initialValues={initialValues}
                        validationSchema={signUpSchema}
                    >
                        {({ values, setFieldValue }) => (
                            <Form>
                                <div className="grid grid-cols-2 gap-3">
                                    <div className="col-span-1">
                                        <label className="font-semibold text-gray-400" htmlFor="firstName">Họ</label>
                                        <Field name="firstName" component={Input} placeholder="Trần" id="firstName" className="w-full mt-2" />
                                    </div>
                                    <div className="col-span-1">
                                        <label className="font-semibold text-gray-400" htmlFor="middleName">Họ đệm</label>
                                        <Field name="middleName" component={Input} placeholder="Phan Hoàn" id="middleName" className="w-full mt-2" />
                                    </div>
                                    <div className="col-span-full">
                                        <label className="font-semibold text-gray-400" htmlFor="lastName">Tên</label>
                                        <Field name="lastName" component={Input} placeholder="Việt" id="lastName" className="w-full mt-2" />
                                    </div>
                                    <div className="col-span-full flex flex-col gap-y-2">
                                        <label className="font-semibold text-gray-400" htmlFor="lastName">Giới tính</label>
                                        <Radio.Group onChange={(e) => setFieldValue("gender", e.target.value)} value={values.gender} className="flex gap-x-4">
                                            <Radio value="Nam">Nam</Radio>
                                            <Radio value="Nữ">Nữ</Radio>
                                            <Radio value="Khác">Khác</Radio>
                                        </Radio.Group>
                                    </div>
                                    <div className="col-span-full">
                                        <label className="font-semibold text-gray-400" htmlFor="email">Email</label>
                                        <Field name="email" component={Input} placeholder="nguyenvana@gmail.com" id="email" className="w-full mt-2" />
                                    </div>
                                    <div className="col-span-1">
                                        <label className="font-semibold text-gray-400" htmlFor="password">Mật khẩu</label>
                                        <Field name="password" component={Input} id="password" type="password" className="w-full mt-2" />
                                    </div>
                                    <div className="col-span-1">
                                        <label className="font-semibold text-gray-400" htmlFor="confirmPassword">Nhập lại mật khẩu</label>
                                        <Field name="confirmPassword" component={Input} id="confirmPassword" type="password" className="w-full mt-2" />
                                    </div>
                                </div>

                                <Button type="submit" isLoading={isPending}>Đăng ký</Button>
                            </Form>
                        )}
                    </Formik>
                </div>
                <Link to='/forgot-password' className="mt-3">
                    <span className="text-[--primary-color] font-semibold">Quên mật khẩu?</span>
                </Link>
                <hr />
                <Link to='/login'>
                    <button className="btn-success-lg mt-10">Có tài khoản? Đăng nhập</button>
                </Link>
            </div>
        </div>
    );
};

export default SignUp;