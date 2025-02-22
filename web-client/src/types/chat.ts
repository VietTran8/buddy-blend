import { EFileType } from "./enum";

export type Room = {
    id: string;
    latestMessage?: ChatMessage;
    lastSentByYou: boolean;
    roomName: string;
    roomImage: string;
    opponentUserId: string;
    online: boolean;
}

export type ChatMessage = {
    id: string;
    createdAt: string;
    content: string;
    medias: MessageMedia[];
    fromUserId: string;
    toUserId: string;
    sentByYou: boolean;
}

export type JoinRoomSocketMessage = {
    toUserId: string;
}

export type SendSocketMessage = {
    content: string;
    medias: MessageMedia[];
    toUserId: string;
}

export type RoomJoinedMessage = {
    roomId: string;
}

export type MessageMedia = {
    url: string;
    thumbnail: string;
    type: EFileType;
}