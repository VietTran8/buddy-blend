import { FC, useState } from "react";
import { useHandleFavouritePost, useQueryUserFavourites } from "../../../hooks";
import { Radio, RadioChangeEvent, Skeleton } from "antd";
import { Plus } from "lucide-react";
import { Field, Form, Formik, FormikHelpers } from "formik";
import { SaveUserFavouriteRequest } from "../../../types";
import Input from "../../shared/Input";
import { saveFavSchema } from "../../../validations";

interface IProps {
    postId: string;
    onAdded?: () => void
};

const UserFavouritesContent: FC<IProps> = ({ postId, onAdded }) => {
    const { data: response, isLoading } = useQueryUserFavourites();
    const { mutate: handle, isPending } = useHandleFavouritePost();

    const [openNewCollection, setOpenNewCollection] = useState<boolean>(false);
    const [selectedCollection, setSellectedCollection] = useState<string>();

    const userFavourites = response?.data;

    const initialValues: SaveUserFavouriteRequest = {
        name: "",
        postId
    }

    const handleOnSave = (values: SaveUserFavouriteRequest, helper: FormikHelpers<SaveUserFavouriteRequest>) => {
        handle(values, {
            onSuccess: () => onAdded && onAdded()
        });
        helper.resetForm();
    }

    const handleOnDone = () => {
        const payload: SaveUserFavouriteRequest = {
            postId: postId,
            name: selectedCollection || ""
        }

        handle(payload, {
            onSuccess: () => onAdded && onAdded()
        });
    }

    const onCollectionChange = (e: RadioChangeEvent) => {
        setSellectedCollection(e.target.value);
    };

    return <div className="w-full">
        <div className="flex flex-col gap-y-2">
            <Radio.Group onChange={onCollectionChange} value={selectedCollection} className="flex flex-col gap-y-1">
                {userFavourites?.map((item) => (
                    <div key={item.id} className={`flex items-center rounded-md hover:bg-gray-50 transition-all ${selectedCollection === item.name && 'bg-gray-100'}`}>
                        <div className="flex-1 flex gap-x-3 items-center p-2">
                            <div className="rounded-md w-12 h-12 bg-[--primary-color] flex items-center justify-center">
                                <span className="lg:text-lg text-base font-semibold text-white m-auto">{item.name[0].toUpperCase()}</span>
                            </div>
                            <div>
                                <h1 className="text-base font-semibold">{item.name}</h1>
                                <span className="text-xs text-gray-400">{`Đã tạo lúc ${item.createdAt}`}</span>
                            </div>
                        </div>
                        <Radio value={item.name}/>
                    </div>
                ))}
            </Radio.Group>
            {userFavourites && userFavourites.length > 0 && <hr className="mt-4" />}
            {isLoading && <>
                <div className="flex items-center rounded-md gap-x-2 p-2 mt-2 hover:bg-gray-100 transition-all cursor-pointer">
                    <Skeleton.Avatar size="large" active shape="square" />
                    <div className="flex-1 mt-3">
                        <Skeleton.Button active block style={{ width: "150px", height: "14px" }} size="small" />
                        <Skeleton.Input active block style={{ width: "100px", height: "14px" }} size="small" />
                    </div>
                </div>
                <div className="flex items-center rounded-md gap-x-2 p-2 mt-2 hover:bg-gray-100 transition-all cursor-pointer">
                    <Skeleton.Avatar size="large" active shape="square" />
                    <div className="flex-1 mt-3">
                        <Skeleton.Button active block style={{ width: "150px", height: "14px" }} size="small" />
                        <Skeleton.Input active block style={{ width: "100px", height: "14px" }} size="small" />
                    </div>
                </div>
            </>}
        </div>

        {!openNewCollection ? <>
            <div onClick={() => setOpenNewCollection(true)} className="p-2 hover:bg-gray-50 transition-all cursor-pointer flex gap-x-3 my-2 items-center">
                <div className="rounded-md overflow-hidden w-12 h-12 bg-gray-100 flex items-center justify-center">
                    <span className="lg:text-lg text-base font-bold text-gray-400 m-auto">
                        <Plus size={20} />
                    </span>
                </div>
                <div>
                    <h1 className="text-base font-semibold">Tạo bộ sưu tập mới</h1>
                </div>
            </div>
            <hr />
            <div className="w-full mt-2 flex justify-end">
                <button disabled={isPending} type="button" onClick={handleOnDone} className="btn-primary">Xong</button>
            </div>
        </> : <Formik
            initialValues={initialValues}
            onSubmit={handleOnSave}
            validationSchema={saveFavSchema}
        >
            {() => (
                <Form className="mt-4">
                    <Field
                        component={Input}
                        placeholder="Nhập tên cho bộ sưu tập của bạn..."
                        name="name"
                        className="mb-4"
                    />
                    <hr />
                    <div className="mt-2 flex items-center justify-end gap-x-2">
                        <button disabled={isPending} onClick={() => setOpenNewCollection(false)} type="button" className="btn-secondary">Hủy</button>
                        <button disabled={isPending} type="submit" className="btn-primary">Lưu</button>
                    </div>
                </Form>
            )}
        </Formik>
        }
    </div>
};

export default UserFavouritesContent;