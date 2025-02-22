import { faBookmark, faFilm, faGear, faHeart, faHouse, faList, faLock, faPaperclip, faPeopleGroup, faUser, faUserFriends, faUserGroup, faUserPlus } from "@fortawesome/free-solid-svg-icons";
import { MenuItem } from "../types";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";

export const mainMenuItems: MenuItem[] = [
    {
        name: "Trang chủ",
        linkTo: "/",
        icon: <FontAwesomeIcon icon={faHouse} />
    },
    {
        name: "Bạn bè",
        linkTo: "/friends/requests",
        icon: <FontAwesomeIcon icon={faUserGroup} />
    },
    {
        name: "Thước phim",
        linkTo: "/videos",
        icon: <FontAwesomeIcon icon={faFilm} />
    },
    {
        name: "Nhóm",
        linkTo: "/groups",
        icon: <FontAwesomeIcon icon={faPeopleGroup} />
    }
]

export const mainSideBarItems: MenuItem[] = [
    {
        name: "Trang chủ",
        linkTo: "/",
        icon: <FontAwesomeIcon icon={faHouse} />
    },
    {
        name: "Bạn bè",
        linkTo: "/friends/requests",
        icon: <FontAwesomeIcon icon={faUserFriends} />
    },
    {
        name: "Nhóm của bạn",
        linkTo: "/groups",
        icon: <FontAwesomeIcon icon={faUserGroup} />
    },
    {
        name: "Yêu thích",
        linkTo: "/favourites",
        icon: <FontAwesomeIcon icon={faHeart} />
    },
    {
        name: "Đã lưu",
        linkTo: "/saved",
        icon: <FontAwesomeIcon icon={faBookmark} />
    },
    {
        name: "Cài đặt",
        linkTo: "/settings",
        icon: <FontAwesomeIcon icon={faGear} />
    }
]

export const friendSideBarItems: MenuItem[] = [
    {
        name: "Yêu cầu kết bạn",
        linkTo: "/friends/requests",
        icon: <FontAwesomeIcon icon={faUser} />
    },
    {
        name: "Đề xuất",
        linkTo: "/friends/suggestions",
        icon: <FontAwesomeIcon icon={faUserPlus} />
    },
    {
        name: "Chặn",
        linkTo: "/friends/blocked",
        icon: <FontAwesomeIcon icon={faLock} />
    },
]

export const searchMenuItems: MenuItem[] = [
    {
        name: "Tất cả",
        linkTo: "top",
        icon: <FontAwesomeIcon icon={faList} />
    },
    {
        name: "Bài viết",
        linkTo: "post",
        icon: <FontAwesomeIcon icon={faPaperclip} />
    },
    {
        name: "Mọi người",
        linkTo: "people",
        icon: <FontAwesomeIcon icon={faUserGroup} />
    }
]

export const profileNavItems: MenuItem[] = [
    {
        name: "Trang chủ",
        linkTo: "",
        icon: undefined
    },
    {
        name: "Giới thiệu",
        linkTo: "about",
        icon: undefined
    },
    {
        name: "Hình ảnh",
        linkTo: "photos",
        icon: undefined
    },
    {
        name: "Bạn bè",
        linkTo: "friends",
        icon: undefined
    }
]

export const groupNavItems: MenuItem[] = [
    {
        name: "Thảo luận",
        linkTo: "",
        icon: undefined
    },
    {
        name: "Giới thiệu",
        linkTo: "about",
        icon: undefined
    },
    {
        name: "Thành viên",
        linkTo: "members",
        icon: undefined
    },
    {
        name: "Hình ảnh",
        linkTo: "photos",
        icon: undefined
    }
]
