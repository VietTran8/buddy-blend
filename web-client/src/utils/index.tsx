import MD5 from "crypto-js/md5";
import { faCircleCheck, faEarthAmericas, faLock, faUserCheck, faUserClock, faUserGroup, faUserPlus } from "@fortawesome/free-solid-svg-icons";
import { BaseResponse, EditGroupInfoType, EditInfoType, EFriendStatus, ENotificationType, EPrivacy, EReactionType, Group, SavedImageData, User, UserDetails } from "../types";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { AxiosError } from "axios";
import { parse } from "date-fns";
import { v4 as uuidv4 } from 'uuid';
import { Tooltip, UploadFile } from "antd";
import { Link } from "react-router-dom";
import React from "react";

export const getGeneralInfoPlaceHolder = (type?: EditInfoType) => {
    if (!type)
        return "Vui lòng nhập thông tin..."
    switch (type) {
        case "address":
            return "Nhập địa chỉ..."
        case "email":
            return "Nhập địa chỉ email..."
        case "phone":
            return "Nhập số điện thoại..."
        case "gender":
            return "Chọn giới tính..."
        case "bio":
            return "Nhập tiểu sử..."
    }
}

export const getGroupInfoPlaceHolder = (type?: EditGroupInfoType) => {
    if (!type)
        return "Vui lòng nhập thông tin..."
    switch (type) {
        case "name":
            return "Vui lòng nhập tên nhóm...";
        case "about":
            return "Vui lòng nhập thông tin giới thiệu";
    }
}

export const getUserInfoValue = (type?: EditInfoType, user?: UserDetails) => {
    if (!type)
        return ""
    switch (type) {
        case "address":
            return user?.fromCity
        case "email":
            return user?.email
        case "phone":
            return user?.phone
        case "gender":
            return user?.gender
        case "bio":
            return user?.bio
    }
}

export const getGroupInfoValue = (type?: EditGroupInfoType, group?: Group) => {
    if (!type)
        return ""
    switch (type) {
        case "name":
            return group?.name;
        case "about":
            return group?.description;
    }
}

export const getPrivacyDesc = (value?: EPrivacy, iconClassName?: string) => {
    const className = `!${iconClassName} text-gray-600`;

    if (value === undefined)
        return {
            text: "Công khai",
            icon: <FontAwesomeIcon className={className} icon={faEarthAmericas} />
        };

    switch (value as EPrivacy) {
        case EPrivacy.PUBLIC:
            return {
                text: "Công khai",
                icon: <FontAwesomeIcon className={className} icon={faEarthAmericas} />
            }
        case EPrivacy.ONLY_FRIENDS:
            return {
                text: "Bạn bè",
                icon: <FontAwesomeIcon className={className} icon={faUserGroup} />
            }
        case EPrivacy.PRIVATE:
            return {
                text: "Chỉ mình tôi",
                icon: <FontAwesomeIcon className={className} icon={faLock} />
            }
    }
}

export const getReactionIcon = (reactType: EReactionType) => {
    switch (reactType) {
        case EReactionType.ANGRY:
            return "/icons/reactions/angry.png";

        case EReactionType.HAHA:
            return "/icons/reactions/haha.png";

        case EReactionType.HEART:
            return "/icons/reactions/heart.png";

        case EReactionType.LIKE:
            return "/icons/reactions/like.png";

        case EReactionType.SAD:
            return "/icons/reactions/sad.png";

        case EReactionType.WOW:
            return "/icons/reactions/wow.png";

    }
}

export const getReactionDesc = (reactType: EReactionType) => {
    switch (reactType) {
        case EReactionType.ANGRY:
            return "Phẫn nộ";

        case EReactionType.HAHA:
            return "Haha";

        case EReactionType.HEART:
            return "Yêu thích";

        case EReactionType.LIKE:
            return "Thích";

        case EReactionType.SAD:
            return "Buồn";

        case EReactionType.WOW:
            return "Wow";

    }
}

export const getReactionTextColor = (reactType: EReactionType) => {
    switch (reactType) {
        case EReactionType.ANGRY:
            return "text-orange-600";

        case EReactionType.HAHA:
            return "text-yellow-400";

        case EReactionType.HEART:
            return "text-red-400";

        case EReactionType.LIKE:
            return "text-[--primary-color]";

        case EReactionType.SAD:
            return "text-yellow-400";

        case EReactionType.WOW:
            return "text-yellow-400";

    }
}

export const hashMD5 = (input: string): string => {
    return MD5(input).toString();
}

export const getErrorRespMsg = (error: AxiosError): string => {
    if (error.response && error.response.data)
        return (error.response.data as BaseResponse<any>).message || "Yêu cầu thất bại.";
    return error.message;
}

export const isImage = (file: File | UploadFile) => {
    return file?.type?.startsWith("image/");
}

export const isVideo = (file: File | UploadFile) => {
    return file?.type?.startsWith("video/");
}

export const getTimeDiff = (dateStr?: string): string => {
    if (!dateStr)
        return "unknown";

    const date = parse(dateStr, "dd/MM/yyyy HH:mm:ss", new Date());

    const now = new Date();
    const diff = now.getTime() - date.getTime();

    const seconds = Math.floor(diff / 1000);
    const minutes = Math.floor(diff / (1000 * 60));
    const hours = Math.floor(diff / (1000 * 60 * 60));
    const days = Math.floor(diff / (1000 * 60 * 60 * 24));
    const weeks = Math.floor(diff / (1000 * 60 * 60 * 24 * 7));

    if (seconds < 60) return "Vài giây trước";
    if (minutes < 60) return `${minutes} phút trước`;
    if (hours < 24) return `${hours} giờ trước`;
    if (days < 7) return `${days} ngày trước`;
    if (weeks < 4) return `${weeks} tuần trước`;
    return dateStr;
};

export const getFriendRequestBtnDesc = (status?: EFriendStatus) => {
    switch (status) {
        case EFriendStatus.IS_FRIEND:
            return {
                text: "Bạn bè",
                icon: faUserCheck
            }
        case EFriendStatus.NOT_YET:
            return {
                text: "Gửi lời mời",
                icon: faUserPlus
            }
        case EFriendStatus.SENT_BY_YOU:
            return {
                text: "Đã gửi lời mời",
                icon: faUserClock
            }
        case EFriendStatus.SENT_TO_YOU:
            return {
                text: "Phản hồi",
                icon: faCircleCheck
            }
        default:
            return {
                text: "Gửi yêu cầu",
                icon: faUserPlus
            }
    }
}

export const getNotificationIconSrc = (type: ENotificationType) => {
    switch (type) {
        case ENotificationType.ANGRY:
            return "reactions/angry.png";

        case ENotificationType.HAHA:
            return "reactions/haha.png";

        case ENotificationType.HEART:
            return "reactions/heart.png";

        case ENotificationType.LIKE:
            return "reactions/like.png";

        case ENotificationType.SAD:
            return "reactions/sad.png";

        case ENotificationType.WOW:
            return "reactions/wow.png";

        case ENotificationType.COMMENT:
            return "comment.png";

        case ENotificationType.SHARE:
            return "share.png";

        case ENotificationType.INVITE_USERS:
            return "groups.png";

        case ENotificationType.MODERATION:
            return "shield.png";

        case ENotificationType.FRIEND_REQUEST:
            return "add-friend.png"
    }
}

export const randomUUID = () => {
    return uuidv4();
}

export const renderTaggingTitle = (users: User[]) => {
    if (users.length === 0) return null;

    const renderUserLink = (user: User, index: number) => (
        <React.Fragment key={user.id}>
            <Link to={`/user/${user.id}`}>{user.userFullName}</Link>
            {index < users.length - 1 && <span>, </span>}
        </React.Fragment>
    );

    if (users.length === 1) {
        return <Link to={`/user/${users[0].id}`}>{users[0].userFullName}</Link>;
    }

    if (users.length === 2) {
        return (
            <span>
                <Link to={`/user/${users[0].id}`}>{users[0].userFullName}</Link>
                <span className="font-normal mx-1">và</span>
                <Link to={`/user/${users[1].id}`}>{users[1].userFullName}</Link>
            </span>
        );
    }

    if (users.length < 4) {
        return (
            <span>
                {users.map((user, index) => renderUserLink(user, index))}
            </span>
        );
    }

    return (
        <span>
            {users.slice(0, 3).map((user, index) => renderUserLink(user, index))}
            <span className="font-normal"> và </span>
            <Tooltip placement="topRight" title={users.slice(3).map(user => user.userFullName).join(', ')}>
                <span className="cursor-pointer">{`${users.length - 3} người khác`}</span>
            </Tooltip>
        </span>
    );
};

export function savedImageDataToFile(data: SavedImageData): File | null {
    let blob: Blob;

    if (data.imageBase64) {
        const byteString = atob(data.imageBase64.split(',')[1]);
        const mimeType = data.mimeType || 'application/octet-stream';
        const ab = new ArrayBuffer(byteString.length);
        const ia = new Uint8Array(ab);

        for (let i = 0; i < byteString.length; i++) {
            ia[i] = byteString.charCodeAt(i);
        }

        blob = new Blob([ab], { type: mimeType });
    }
    else if (data.imageCanvas) {
        const quality = data.quality ?? 0.92;
        const mimeType = data.mimeType || 'image/png';
        const base64 = data.imageCanvas.toDataURL(mimeType, quality);
        const byteString = atob(base64.split(',')[1]);
        const ab = new ArrayBuffer(byteString.length);
        const ia = new Uint8Array(ab);

        for (let i = 0; i < byteString.length; i++) {
            ia[i] = byteString.charCodeAt(i);
        }

        blob = new Blob([ab], { type: mimeType });
    } else {
        console.error('No valid image source found');
        return null;
    }

    const fullName = data.fullName || `${data.name}.${data.extension}`;

    return new File([blob], fullName, { type: data.mimeType });
}

export const removeExtraSpaces = (input?: string): string | undefined => {
    return input && input.trim().replace(/\s+/g, ' ');
}

export const countCharacters = (text: string, includeSpaces: boolean = true): number => {
    if (includeSpaces) {
        return text.length;
    } else {
        return text.replace(/\s+/g, "").length;
    }
}

export const normalizeVietnamese = (str: string): string => {
    return str
        .normalize("NFD") 
        .replace(/[\u0300-\u036f]/g, "")
        .toLowerCase(); 
};
