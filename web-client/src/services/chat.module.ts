import { chatSocket } from "../config/chat-socket"

export const emitJoinRoom = (toUserId: string) => {
    chatSocket.emit("join_room", {
        toUserId
    })
}