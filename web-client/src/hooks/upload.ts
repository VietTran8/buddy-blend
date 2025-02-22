import { useMutation } from "@tanstack/react-query"
import { uploadFile } from "../services";
import { FileType } from "../types";
import { UploadFile } from "antd";

export const useUploadFile = () => {
    return useMutation({
        mutationFn: ({ type, files }: { type: FileType, files?: (File | UploadFile)[] }) => uploadFile(type, files)
    });
}