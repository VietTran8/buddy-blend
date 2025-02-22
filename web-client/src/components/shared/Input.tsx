import { FieldProps } from "formik";
import React, { FC, ReactNode } from "react";

interface IProps extends React.InputHTMLAttributes<HTMLInputElement> {
    startDrawable?: ReactNode,
    endDrawable?: ReactNode,
};

const getPadding = (startDrawable: ReactNode, endDrawable: ReactNode) => {
    if (startDrawable || endDrawable) {
        let paddingStyle = 'py-5';

        if (startDrawable)
            paddingStyle += ' ps-10';
        else
            paddingStyle += ' ps-5';

        if (endDrawable)
            paddingStyle += ' pe-10';
        else
            paddingStyle += ' pe-5';

        return paddingStyle;
    }

    return 'p-5';
}

const Input: FC<IProps & Partial<FieldProps>> = ({ startDrawable, endDrawable, className, field, form, ...props }) => {
    const meta = form && field ? form.getFieldMeta(field.name) : null;

    return (
        <div className={`${className} `}>
            <div className={`relative w-full h-fit`}>
                {startDrawable && <div className="absolute top-[50%] left-3 -translate-y-[50%]">
                    {startDrawable}
                </div>}
                <input
                    {...field}
                    {...props}
                    className={`${getPadding(startDrawable, endDrawable)} ${((form?.submitCount && form.submitCount > 0) || meta?.touched ) && meta?.error && 'border border-red-400'} transition-all w-full lg:h-[55px] h-[40px] outline-none focus:outline-1 focus:outline-blue-500 bg-gray-100 rounded-lg text-gray-600`} 
                />
                {endDrawable && <div className="absolute top-[50%] right-3 -translate-y-[50%]">
                    {endDrawable}
                </div>}
            </div>
            {((form?.submitCount && form.submitCount > 0) || meta?.touched ) && meta?.error && <p className="text-red-400 font-semibold text-sm mt-2">{meta.error}</p>}
        </div>
    )
};

export default Input;