import { UploadFile } from "antd";
import { http } from "../config"
import { BaseResponse, FileType, UploadFileResponse } from "../types"

const convertToFiles = (uploadFiles: any[]): File[] => {
    return uploadFiles
        .map((file) => {
            if(file instanceof File)
                return file;

            return file.originFileObj as File
        })
        .filter((file): file is File => !!file);
};

export const uploadFile = async (type: FileType, files?: (File | UploadFile)[]): Promise<BaseResponse<UploadFileResponse[]> | undefined> => {
    if (!files || files.length === 0) {
        return;
    }

    const newFiles = convertToFiles(files);

    const formData = new FormData();
    
    newFiles.forEach(file => {
        formData.append('files', file);
    });

    try {
        const response: BaseResponse<UploadFileResponse[]> = await http.post(`/file/upload-all/${type}`, formData);

        return response;
    } catch (error) {
        throw new Error(`Upload failed: ${error}`);
    }
}
