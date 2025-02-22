import { FC } from "react";
import { EditGroupInfoType, Group, UpdateGroupRequest } from "../../../types";
import Input from "../../shared/Input";
import { Button } from "antd";
import { Field, Form, Formik } from "formik";
import { getGroupInfoPlaceHolder, getGroupInfoValue } from "../../../utils";
import { useUpdateGroup } from "../../../hooks";
import toast from "react-hot-toast";

interface IProps {
    type?: EditGroupInfoType;
    group?: Group;
    onUpdated?: () => void
};

const EditUserInfoModalContent: FC<IProps> = ({ type, group, onUpdated }) => {
    const { mutate: update, isPending: isUpdating } = useUpdateGroup();

    const handleOnSubmit = ({ content }: { content?: string }) => {
        let updatePayload: UpdateGroupRequest = {}

        switch (type) {
            case "about":
                updatePayload = {
                    description: content
                };
                break;
            case "name":
                updatePayload = {
                    name: content
                };
                break;
        }

        update({
            groupId: group?.id || "",
            payload: updatePayload
        }, {
            onSuccess: () => {
                onUpdated?.()
                toast.success("Cập nhật thông tin thành công");
            }
        });
    }

    return <div className="">
        <Formik
            onSubmit={handleOnSubmit}
            initialValues={{
                content: getGroupInfoValue(type, group)
            }}
        >
            {({ values }) => (
                <Form className="flex flex-col gap-y-4">
                    <Field
                        component={type !== "about" ? Input : "textarea"}
                        className={`${type === "about" && ' transition-all w-full ps-5 pt-5 outline-none focus:outline-1 focus:outline-blue-500 bg-gray-100 rounded-lg text-gray-600'}`}
                        name="content"
                        rows={type === "about" ? 5 : undefined}
                        placeholder={getGroupInfoPlaceHolder(type)}
                    />
                    <Button htmlType="submit" disabled={!!!values.content || isUpdating} type="primary" size="large"><span className="text-sm font-semibold">Lưu</span></Button>
                </Form>
            )}
        </Formik>
    </div>
};

export default EditUserInfoModalContent;