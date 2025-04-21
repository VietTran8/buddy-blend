import { FC, useEffect } from "react";
import { Button, Input } from "../../components";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faUser } from "@fortawesome/free-solid-svg-icons";
import { Link, useNavigate } from "react-router-dom";
import { Field, Form, Formik } from "formik";
import { loginSchema } from "../../validations";
import { useLogin } from "../../hooks";
import { SignInRequest } from "../../types";
import { getAccessToken } from "@/config";

interface IProps { };

const initialValues: SignInRequest = {
    email: "",
    password: "",
};

const Login: FC<IProps> = ({ }) => {
    const { mutate, isPending } = useLogin();
    const userToken = getAccessToken();
    const navigate = useNavigate();

    useEffect(() => {
        if(userToken)
            navigate("/");
    }, []);

    const handleOnLogin = (values: SignInRequest) => {
        mutate(values);
    }

    return (
        <div className="grid grid-cols-12 bg-white min-h-[100vh] relative">
            <div className="md:col-span-8 col-span-full sticky top-0 h-fit bg-black">
                <img src="/images/community.jpg" alt="community" className="w-full md:h-[100vh] h-[30vh] object-cover" />
            </div>
            <div className="md:col-span-4 col-span-full flex flex-col items-center md:justify-center p-8">
                <h1 className="font-bold text-3xl self-start">Đăng nhập</h1>
                <p className="mt-2 self-start text-gray-500">Để kết nối với mọi người trên Buddy Blend, bạn cần phải đăng nhập trước nhé!</p>

                <div className="w-full px-3 mt-6">
                    <Formik
                        onSubmit={handleOnLogin}
                        initialValues={initialValues}
                        validationSchema={loginSchema}
                        validateOnBlur
                        validateOnMount
                        validateOnChange
                    >
                        {({ }) => (
                            <Form>
                                <label className="font-semibold text-gray-400" htmlFor="email">Email</label>
                                <Field 
                                    component={Input}
                                    startDrawable={<FontAwesomeIcon className="text-gray-400" icon={faUser} />} 
                                    placeholder="Email" 
                                    id="email"
                                    className="w-full mt-2 mb-5"
                                    name="email"
                                />

                                <label className="font-semibold text-gray-400" htmlFor="password">Mật khẩu</label>
                                <Field 
                                    component={Input}
                                    placeholder="Mật khẩu..." 
                                    type="password" 
                                    name="password"
                                    id="password"
                                    className="w-full mt-2"
                                />

                                <Button isLoading={isPending}>Đăng nhập</Button>
                            </Form>
                        )}
                    </Formik>
                </div>
                <Link to='/forgot-password' className="mt-3">
                    <span className="text-[--primary-color] font-semibold">Quên mật khẩu?</span>
                </Link>
                <hr />
                <Link to='/sign-up'>
                    <button className="btn-success-lg mt-10">Tạo tài khoản mới</button>
                </Link>
            </div>
        </div>
    );
};

export default Login;