import { useMutation, useQueryClient } from "@tanstack/react-query"
import { ChangePasswordRequest, CreateChangePasswordRequest, CreateForgotPasswordRequest, PasswordCheckingRequest, SignInRequest, SignUpRequest, ValidateOTPRequest } from "../types/request";
import { changePassword, createChangePasswordOtp, createForgotPasswordOtp, login, logout, passwordChecking, signUp, validateOtp } from "../services";
import { BaseResponse, SignInResponse } from "../types";
import { useNavigate } from "react-router-dom";
import { useContext } from "react";
import { AuthContext } from "../context";
import toast from "react-hot-toast";
import useLocalStorage from "./local-storage";
import { AxiosError } from "axios";
import { getErrorRespMsg } from "../utils";
import { USER_TOKEN_PREFIX } from "../constants";
import { disconnectChatSocket } from "@/config/chat-socket";

export type UserTokenType = {
    acs: string;
    sAcs: string;
}

export const useLogin = () => {
    const navigate = useNavigate();
    const [_, setUserToken] = useLocalStorage<UserTokenType | undefined>(USER_TOKEN_PREFIX, undefined);

    return useMutation({
        mutationFn: (payload: SignInRequest) => login(payload),
        onSuccess: (data: BaseResponse<SignInResponse>) => {
            const tokenResponse = data.data.token;

            setUserToken({
                acs: tokenResponse.accessToken,
                sAcs: tokenResponse.socketAccessToken
            });

            toast.success("Đăng nhập thành công!");

            navigate("/");
        },
        onError: (error: AxiosError) => {
            toast.error(getErrorRespMsg(error));
        }
    });
}

export const useSignUp = () => {
    const navigate = useNavigate();

    return useMutation({
        mutationFn: (payload: SignUpRequest) => signUp(payload),
        onSuccess: () => {
            toast.success("Đăng ký tài khoản thành công");

            navigate("/login");
        },
        onError: (error: AxiosError) => {
            toast.error(getErrorRespMsg(error));
        }
    })
}

export const useLogout = () => {
    const [_, __, removeUserToken] = useLocalStorage<UserTokenType | undefined>(USER_TOKEN_PREFIX, undefined);
    const { setUser } = useContext(AuthContext);
    const navigate = useNavigate();

    const queryClient = useQueryClient();

    return async () => {
        try {
            await logout();

            queryClient.removeQueries({
                queryKey: ['news-feed']
            });

            removeUserToken();
            setUser?.(null);

            disconnectChatSocket();

            navigate("/login");
        } catch (logoutError) {
            toast.error("Error when logout: " + (logoutError as AxiosError).message);
        }
    };
}

export const useCreateChangePassOtp = () => {
    return useMutation({
        mutationFn: (payload: CreateChangePasswordRequest) => createChangePasswordOtp(payload)
    });
}

export const useCreateForgotPassOtp = () => {
    return useMutation({
        mutationFn: (payload: CreateForgotPasswordRequest) => createForgotPasswordOtp(payload),
        onError: (error: AxiosError) => {
            toast.error(getErrorRespMsg(error), {
                position: "bottom-left"
            });
        }
    });
}

export const useChangePassword = () => {
    return useMutation({
        mutationFn: (payload: ChangePasswordRequest) => changePassword(payload),
        onSuccess: () => {
            toast.success("Thay đổi mật khẩu thành công!", {
                position: "bottom-left"
            });
        }
    });
}

export const useValidateOtp = () => {
    return useMutation({
        mutationFn: (payload: ValidateOTPRequest) => validateOtp(payload),
    });
}

export const useCheckPassword = () => {
    return useMutation({
        mutationFn: (payload: PasswordCheckingRequest) => passwordChecking(payload),
    })
}