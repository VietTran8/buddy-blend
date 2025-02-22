import { io } from "socket.io-client";
import { getAccessToken } from "./axios";

export const chatSocket = io('http://localhost:8095', {
    autoConnect: false,
    reconnectionAttempts: 5,
    reconnectionDelay: 1000,
});

export const connectChatSocket = () => {
    chatSocket.io.opts.extraHeaders = {
        "Authorization": getAccessToken() || ""
    }
    chatSocket.connect();
}

export const disconnectChatSocket = () => {
    chatSocket.disconnect();
}