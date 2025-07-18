import { http } from "../config";
import { BaseResponse, ChangePasswordRequest, CreateChangePasswordRequest, CreateForgotPasswordRequest, PasswordCheckingRequest, PasswordCheckingResponse, SignInRequest, SignInResponse, SignUpRequest, SignUpResponse, ValidateOTPRequest } from "../types";
import { hashMD5 } from "../utils";

const basePath = "/auth";

export const login = async (payload: SignInRequest): Promise<BaseResponse<any>> => {
    const response: BaseResponse<SignInResponse> = await http.post(`${basePath}/login`, {
        ...payload,
        password: hashMD5(payload.password)
    });

    return response;
}

export const refreshToken = async (): Promise<BaseResponse<SignInResponse>> => {
    const response: BaseResponse<SignInResponse> = await http.post(`${basePath}/refresh-token`, {});

    return response;
}

export const signUp = async (payload: SignUpRequest): Promise<BaseResponse<SignUpResponse>> => {
    const response: BaseResponse<SignUpResponse> = await http.post(`${basePath}/sign-up`, {
        ...payload,
        password: hashMD5(payload.password)
    });

    return response;
}

export const createChangePasswordOtp = async (payload: CreateChangePasswordRequest): Promise<BaseResponse<any>> => {
    const response: BaseResponse<any> = await http.post(`${basePath}/create-change-pass`, {
        ...payload,
        oldPassword: hashMD5(payload.oldPassword)
    });

    return response;
}

export const createForgotPasswordOtp = async (payload: CreateForgotPasswordRequest): Promise<BaseResponse<any>> => {
    const response: BaseResponse<any> = await http.post(`${basePath}/create-forgot-pass`, payload);

    return response;
}

export const changePassword = async (payload: ChangePasswordRequest): Promise<BaseResponse<any>> => {
    const response: BaseResponse<any> = await http.post(`${basePath}/change-pass`, {
        ...payload,
        newPassword: hashMD5(payload.newPassword)
    });

    return response;
}

export const logout = async (): Promise<BaseResponse<any>> => {
    const response: BaseResponse<any> = await http.post(`${basePath}/logout`);

    return response;
}

export const validateOtp = async (payload: ValidateOTPRequest): Promise<BaseResponse<any>> => {
    const response: BaseResponse<any> = await http.post(`${basePath}/validate-otp`, payload);

    return response;
}

export const passwordChecking = async (payload: PasswordCheckingRequest): Promise<BaseResponse<PasswordCheckingResponse | undefined>> => {
    const response: BaseResponse<PasswordCheckingResponse | undefined> = await http.post(`${basePath}/password-checking`, {
        ...payload,
        password: hashMD5(payload.password)
    });

    return response;
}