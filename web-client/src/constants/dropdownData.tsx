import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { EPrivacy, EStoryFont, GroupPrivacy } from "../types";
import { faEarthAmericas, faLock, faUserGroup } from "@fortawesome/free-solid-svg-icons";

export const genderData = [
    {
        title: 'Nam',
        value: 'Nam',
    },
    {
        title: 'Nữ',
        value: 'Nữ',
    },
    {
        title: 'Khác',
        value: 'Khác',
    },
];

export const groupPrivacyData = [
    {
        title: <div className="flex gap-x-3 p-3">
            <FontAwesomeIcon icon={faEarthAmericas} className="text-gray-500 text-2xl mt-1" />
            <div className="flex flex-col gap-y-1">
                <span className="font-semibold text-gray-500">Công khai</span>
                <span className="lg:text-sm text-xs text-gray-400">Bất kỳ ai cũng có thể nhìn thấy mọi người trong nhóm và những gì họ đăng</span>
            </div>
        </div>,
        value: GroupPrivacy.PRIVACY_PUBLIC,
    },
    {
        title: <div className="flex gap-x-3 p-3">
            <FontAwesomeIcon icon={faLock} className="text-gray-500 text-2xl mt-1" />
            <div className="flex flex-col gap-y-1">
                <span className="font-semibold text-gray-500">Riêng tư</span>
                <span className="lg:text-sm text-xs text-gray-400">Chỉ thành viên mới nhìn thấy mọi người trong nhóm và những gì họ đăng</span>
            </div>
        </div>,
        value: GroupPrivacy.PRIVACY_PRIVATE,
    },
];

export const storyPrivacyData = [
    {
        title: <div className="flex gap-x-3 p-3">
            <FontAwesomeIcon icon={faEarthAmericas} className="text-gray-500 text-2xl mt-1" />
            <div className="flex flex-col gap-y-1">
                <span className="font-semibold text-gray-500">Công khai</span>
                <span className="lg:text-sm text-xs text-gray-400">Bất kỳ ai cũng có thể nhìn thấy tin của bạn</span>
            </div>
        </div>,
        value: EPrivacy.PUBLIC,
    },
    {
        title: <div className="flex gap-x-3 p-3">
            <FontAwesomeIcon icon={faUserGroup} className="text-gray-500 text-2xl mt-1" />
            <div className="flex flex-col gap-y-1">
                <span className="font-semibold text-gray-500">Bạn bè</span>
                <span className="lg:text-sm text-xs text-gray-400">Chỉ bạn bè của bạn mới thấy tin này</span>
            </div>
        </div>,
        value: EPrivacy.ONLY_FRIENDS
    },
    {
        title: <div className="flex gap-x-3 p-3">
            <FontAwesomeIcon icon={faLock} className="text-gray-500 text-2xl mt-1" />
            <div className="flex flex-col gap-y-1">
                <span className="font-semibold text-gray-500">Riêng tư</span>
                <span className="lg:text-sm text-xs text-gray-400">Chỉ bạn mới thấy tin này</span>
            </div>
        </div>,
        value: EPrivacy.PRIVATE,
    },

];

export const storyFontData = [
    {
        title: <span>Gọn gàng</span>,
        value: EStoryFont.FONT_NEAT,
    },
    {
        title: <span>Bình thường</span>,
        value: EStoryFont.FONT_NORMAL,
    },
    {
        title: <span>Phong cách</span>,
        value: EStoryFont.FONT_STYLIST,
    },
    {
        title: <span>Tiêu đề</span>,
        value: EStoryFont.FONT_HEADER,
    },
];

