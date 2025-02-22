import * as yup from 'yup';

export const loginSchema = yup.object().shape({
    email: yup.string().email("Vui lòng nhập email hợp lệ").required("Vui lòng nhập email"),
    password: yup.string()
        // .min(8, "Mật khẩu phải có ít nhất 8 kí tự")
        // .matches(/[a-z]/, "Mật khẩu phải chứa ít nhất một chữ thường")
        // .matches(/[A-Z]/, "Mật khẩu phải chứa ít nhất một chữ hoa")
        // .matches(/\d/, "Mật khẩu phải chứa ít nhất một số")
        .required("Vui lòng nhập mật khẩu")
});

export const changePasswordSchema = yup.object().shape({
    oldPassword: yup.string()
        // .min(8, "Mật khẩu phải có ít nhất 8 kí tự")
        // .matches(/[a-z]/, "Mật khẩu phải chứa ít nhất một chữ thường")
        // .matches(/[A-Z]/, "Mật khẩu phải chứa ít nhất một chữ hoa")
        // .matches(/\d/, "Mật khẩu phải chứa ít nhất một số")
        .required("Vui lòng nhập mật khẩu cũ"),
    newPassword: yup.string().required("Vui lòng nhập mật khẩu mới"),
    confirmNewPassword: yup.string()
        .oneOf([yup.ref('newPassword'), undefined], "Mật khẩu xác nhận không khớp")
        .required("Vui lòng xác nhận mật khẩu mới"),
});

export const forgotPasswordSchema = yup.object().shape({
    email: yup.string().email("Vui lòng nhập email hợp lệ").required("Vui lòng nhập email"),
});

export const recreatePasswordSchema = yup.object().shape({
    newPassword: yup.string()
        .min(8, "Mật khẩu phải có ít nhất 8 kí tự")
        .matches(/[a-z]/, "Mật khẩu phải chứa ít nhất một chữ thường")
        .matches(/[A-Z]/, "Mật khẩu phải chứa ít nhất một chữ hoa")
        .matches(/\d/, "Mật khẩu phải chứa ít nhất một số")
        .required("Vui lòng nhập mật khẩu"),
    confirmNewPassword: yup.string()
        .oneOf([yup.ref('newPassword'), undefined], "Mật khẩu xác nhận không khớp")
        .required("Vui lòng xác nhận mật khẩu"),
});

export const signUpSchema = yup.object().shape({
    email: yup.string().email("Vui lòng nhập email hợp lệ").required("Vui lòng nhập email"),
    password: yup.string()
        .min(8, "Mật khẩu phải có ít nhất 8 kí tự")
        .matches(/[a-z]/, "Mật khẩu phải chứa ít nhất một chữ thường")
        .matches(/[A-Z]/, "Mật khẩu phải chứa ít nhất một chữ hoa")
        .matches(/\d/, "Mật khẩu phải chứa ít nhất một số")
        .required("Vui lòng nhập mật khẩu"),
    confirmPassword: yup.string()
        .oneOf([yup.ref('password'), undefined], "Mật khẩu xác nhận không khớp")
        .required("Vui lòng xác nhận mật khẩu"),
    firstName: yup.string().required("Vui lòng nhập tên"),
    lastName: yup.string().required("Vui lòng nhập họ"),
    middleName: yup.string().nullable(),
    gender: yup.string().oneOf(['Nam', 'Nữ', 'Khác'], "Vui lòng chọn giới tính hợp lệ").required("Vui lòng chọn giới tính")
});
