import { createContext } from "react"
import { Room } from "../types";

export type ChatContextType = {
    openChatDrawer: (toUserId?: string) => void,
    rooms: Room[]
}

export const ChatContext = createContext<ChatContextType>({
    openChatDrawer: () => {},
    rooms: []
});