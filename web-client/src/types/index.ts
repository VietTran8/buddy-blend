import { ReactNode } from "react"

export * from "./response";
export * from "./request";
export * from "./user";
export * from "./post";
export * from "./enum";
export * from "./reaction";
export * from "./comment";
export * from "./chat";
export * from "./friend";
export * from "./group";
export * from "./context";
export * from "./upload";
export * from "./story";
export * from "./favourite";
export * from "./search";

export type EditInfoType = "gender" | "email" | "phone" | "address" | "bio";
export type EditGroupInfoType = "name" | "about";
export type Gender = "male" | "female" | "other";
export type FileType = "img" | "vid" | "other";

export type MenuItem = {
    name: string;
    linkTo: string;
    icon: ReactNode;
}

export type SavedImageData = {
    name: string;
    extension: string;
    mimeType: string;
    fullName?: string;
    height?: number;
    width?: number;
    imageBase64?: string;
    imageCanvas?: HTMLCanvasElement;
    quality?: number;
    cloudimageUrl?: string;
}