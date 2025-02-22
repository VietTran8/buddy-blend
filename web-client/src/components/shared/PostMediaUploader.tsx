import { GetProp, Upload, UploadFile, UploadProps, Image } from "antd";
// import ImgCrop from "antd-img-crop";
import { FC, useState } from "react";
import { Plus } from "lucide-react";
import { FieldProps } from "formik";

interface IProps {
    setOpen: React.Dispatch<React.SetStateAction<boolean>>;
    multiple?: boolean,
    isUpdate?: boolean,
    hideLabelDisabled?: boolean,
    accept?: string,
    className?: string
};

type FileType = Parameters<GetProp<UploadProps, 'beforeUpload'>>[0];

const getBase64 = (file: FileType): Promise<string> =>
    new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onload = () => resolve(reader.result as string);
        reader.onerror = (error) => reject(error);
    });

const PostMediaUploader: FC<IProps & FieldProps<any>> = ({
    setOpen,
    multiple = true,
    form,
    field,
    isUpdate = false,
    hideLabelDisabled = false,
    className,
    accept
}) => {
    const [previewOpen, setPreviewOpen] = useState(false);
    const [previewImage, setPreviewImage] = useState('');

    const onChange: UploadProps['onChange'] = ({ fileList: newFileList }) => {
        form.setFieldValue(field.name, (multiple || newFileList.length == 0) ? newFileList : [newFileList[newFileList.length - 1]]);
    };

    const handleUpload = async (options: any) => {
        const { onSuccess } = options;

        onSuccess();
    };

    const handlePreview = async (file: UploadFile) => {
        if (!file.url && !file.preview) {
            file.preview = await getBase64(file.originFileObj as FileType);
        }

        setPreviewImage(file.url || (file.preview as string));
        setPreviewOpen(true);
    };

    return (
        <>
            <div className="flex flex-col gap-y-2">
                <Upload
                    customRequest={handleUpload}
                    listType="picture-card"
                    fileList={field.value}
                    onChange={onChange}
                    accept={accept}
                    multiple={multiple}
                    onPreview={handlePreview}
                    className={className}
                >
                    <span className="flex gap-x-1 items-center"><Plus size={20} /> Tải lên</span>
                </Upload>
                {!hideLabelDisabled && !isUpdate && <span onClick={() => setOpen(false)} className="text-[--primary-color] cursor-pointer hover:underline w-fit transition-all">Ẩn</span>}
            </div>
            {
                previewImage && (
                    <Image
                        wrapperStyle={{ display: 'none' }}
                        preview={{
                            visible: previewOpen,
                            onVisibleChange: (visible) => setPreviewOpen(visible),
                            afterOpenChange: (visible) => !visible && setPreviewImage(''),
                        }}
                        src={previewImage}
                    />
                )
            }
        </>
    );
};

export default PostMediaUploader;