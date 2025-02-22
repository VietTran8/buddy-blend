import axios from "axios";
import { ACCESS_TOKEN_PREFIX, SERVER_URL } from "../constants";

export const getAccessToken = () => {
    const accessToken = localStorage.getItem(ACCESS_TOKEN_PREFIX);

    return accessToken?.slice(1, accessToken.length - 1)
};

export const http = axios.create({
    baseURL: SERVER_URL,
})

http.interceptors.request.use(
    config => {
        const token = getAccessToken();

        if (token) {
            config.headers["Authorization"] = token;
        }

        return config;
    },
    error => Promise.reject(error)
);

http.interceptors.response.use(
    response => response.data,
    error => {
        if(error.response && error.response.status === 401) {
            localStorage.removeItem(ACCESS_TOKEN_PREFIX);
            
            if (window.location.pathname !== "/login") {
                window.location.href = "/login";
            }
        }

        return Promise.reject(error);
    }
)