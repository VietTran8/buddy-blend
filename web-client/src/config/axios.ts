import axios from "axios";
import { SERVER_URL, USER_TOKEN_PREFIX } from "../constants";
import { refreshToken } from "@/services";
import { TokenResponse } from "@/types";
import { UserTokenType } from "@/hooks";

const tokenType = 'Bearer';

export const getAccessToken = () => {
    const userTokenString = localStorage.getItem(USER_TOKEN_PREFIX);
    const userToken: UserTokenType = JSON.parse(userTokenString ? userTokenString : "{}");

    return userToken ? userToken.acs : undefined;
};

export const getSocketAccessToken = () => {
    const userTokenString = localStorage.getItem(USER_TOKEN_PREFIX);
    const userToken: UserTokenType = JSON.parse(userTokenString ? userTokenString : "{}");

    return userToken ? `Bearer ${userToken.sAcs}`  : undefined;
};

export const http = axios.create({
    baseURL: SERVER_URL,
    withCredentials: true
})

http.interceptors.request.use(
    config => {
        const token = getAccessToken();

        if (token) {
            config.headers["Authorization"] = `${tokenType} ${token}`;
        }

        return config;
    },
    error => Promise.reject(error)
);

let isRefreshing = false;
let refreshSubscribers: ((token: string) => void)[] = [];

function subscribeTokenRefresh(cb: (token: string) => void) {
    refreshSubscribers.push(cb);
}

function onRefreshed(token: string) {
    refreshSubscribers.forEach(cb => cb(token));
    refreshSubscribers = [];
}

http.interceptors.response.use(
    response => response.data,
    async error => {
        const originalRequest = error.config;

        if (error.response && error.response.status === 401 && !originalRequest._retry) {
            originalRequest._retry = true;

            const socketAccessToken = getSocketAccessToken();

            localStorage.removeItem(USER_TOKEN_PREFIX);

            if (isRefreshing) {
                return new Promise((resolve) => {
                    subscribeTokenRefresh((newToken) => {
                        originalRequest.headers['Authorization'] = newToken;
                        resolve(http(originalRequest));
                    });
                });
            }

            isRefreshing = true;

            try {
                if (window.location.pathname !== "/login") {
                    const refreshResponse = await refreshToken();
                    const tokenResponse: TokenResponse = refreshResponse.data.token;

                    const accessToken = tokenResponse.accessToken;

                    localStorage.setItem(USER_TOKEN_PREFIX, JSON.stringify({
                        acs: accessToken,
                        sAcs: socketAccessToken
                    }));

                    originalRequest.headers['Authorization'] = accessToken;

                    onRefreshed(accessToken);

                    return http(originalRequest);
                }

                return Promise.reject(error);

            } catch (refreshError) {
                window.location.href = "/login";
                return Promise.reject(refreshError);
            } finally {
                isRefreshing = false;
            }
        }

        return Promise.reject(error);
    }
);