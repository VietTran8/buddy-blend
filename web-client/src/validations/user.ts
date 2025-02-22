import * as yup from 'yup';

export const generalInfoSchema = yup.object().shape({
    fromCity: yup.string().required('Vui lòng chọn thành phố.'),
    gender: yup.string()
        .oneOf(['Nam', 'Nữ', 'Khác'], 'Giới tính không hợp lệ')
        .required('Vui lòng chọn giới tính.'),
    phone: yup.string()
        .matches(/^[0-9]{10}$/, 'Số điện thoại không hợp lệ')
        .required('Vui lòng nhập số điện thoại.')
});