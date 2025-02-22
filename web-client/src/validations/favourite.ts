import * as yup from 'yup';

export const saveFavSchema = yup.object().shape({
    name: yup.string().required("Vui lòng nhập tên bộ sưu tập")
});