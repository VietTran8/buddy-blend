import { io } from 'socket.io-client';
import { getSocketAccessToken } from './axios';

export const notificationSocket = io('http://localhost:8098', {
    autoConnect: false,
    reconnectionAttempts: 5,
    reconnectionDelay: 1000,
});

export const connectNotificationSocket = () => {
    notificationSocket.io.opts.extraHeaders = {
        "Authorization": getSocketAccessToken() || ""
    };
    notificationSocket.connect();
};

export const disconnectNotificationSocket = () => {
    notificationSocket.disconnect();
};