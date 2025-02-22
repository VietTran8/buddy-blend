import React, { createContext } from "react";
import { BaseUser } from "../types";

interface AuthContextType {
    user: BaseUser | null;
    setUser: React.Dispatch<React.SetStateAction<BaseUser | null>>;
    accessTime: Date;
}

export const AuthContext = createContext<AuthContextType>({
    setUser: null as any,
    user: null,
    accessTime: new Date()
});