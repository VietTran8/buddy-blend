import { http } from "../config";
import { BaseResponse, ChangePasswordRequest, CreateChangePasswordRequest, CreateForgotPasswordRequest, PasswordCheckingRequest, PasswordCheckingResponse, SignInRequest, SignInResponse, SignUpRequest, SignUpResponse, ValidateOTPRequest } from "../types";
import { hashMD5 } from "../utils";

export const login = async (payload: SignInRequest): Promise<BaseResponse<any>> => {
    const response: BaseResponse<SignInResponse> = await http.post("/auth/login", {
        ...payload,
        password: hashMD5(payload.password)
    });

    return response;
}

export const signUp = async (payload: SignUpRequest): Promise<BaseResponse<SignUpResponse>> => {
    const response: BaseResponse<SignUpResponse> = await http.post("/auth/sign-up", {
        ...payload,
        password: hashMD5(payload.password)
    });

    return response;
}

export const createChangePasswordOtp = async (payload: CreateChangePasswordRequest): Promise<BaseResponse<any>> => {
    const response: BaseResponse<any> = await http.post("/auth/create-change-pass", {
        ...payload,
        oldPassword: hashMD5(payload.oldPassword)
    });

    return response;
}

export const createForgotPasswordOtp = async (payload: CreateForgotPasswordRequest): Promise<BaseResponse<any>> => {
    const response: BaseResponse<any> = await http.post("/auth/create-forgot-pass", payload);

    return response;
}

export const changePassword = async (payload: ChangePasswordRequest): Promise<BaseResponse<any>> => {
    const response: BaseResponse<any> = await http.post("/auth/change-pass", {
        ...payload,
        newPassword: hashMD5(payload.newPassword)
    });

    return response;
}

export const validateOtp = async (payload: ValidateOTPRequest): Promise<BaseResponse<any>> => {
    const response: BaseResponse<any> = await http.post("/auth/validate-otp", payload);

    return response;
}

export const passwordChecking = async (payload: PasswordCheckingRequest): Promise<BaseResponse<PasswordCheckingResponse | undefined>> => {
    const response: BaseResponse<PasswordCheckingResponse | undefined> = await http.post("/auth/password-checking", {
        ...payload,
        password: hashMD5(payload.password)
    });

    return response;
}