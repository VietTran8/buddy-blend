export enum EPrivacy {
    PUBLIC = "PUBLIC",
    PRIVATE = "PRIVATE",
    ONLY_FRIENDS = "ONLY_FRIENDS"
}

export enum EPostType {
    NORMAL = "NORMAL",
    AVATAR_UPDATING = "AVATAR_UPDATING",
    SHARE = "SHARE",
    GROUP = "GROUP"
}

export enum EReactionType {
    HAHA = "HAHA",
    ANGRY = "ANGRY",
    WOW = "WOW",
    SAD = "SAD",
    LIKE = "LIKE",
    HEART = "HEART"
}

export enum GroupPrivacy {
    PRIVACY_PRIVATE = "PRIVACY_PRIVATE", 
    PRIVACY_PUBLIC = "PRIVACY_PUBLIC"
}

export enum EMediaType {
    TYPE_IMAGE = "TYPE_IMAGE",
    TYPE_VIDEO = "TYPE_VIDEO"
}

export enum EStoryType {
    TYPE_MEDIA = "TYPE_MEDIA",
    TYPE_TEXT = "TYPE_TEXT"
}

export enum EStoryFont {
    FONT_NEAT = "FONT_NEAT",
    FONT_NORMAL = "FONT_NORMAL",
    FONT_STYLIST = "FONT_STYLIST",
    FONT_HEADER = "FONT_HEADER"
}

export enum EFriendReqStatus {
    PENDING = "PENDING", 
    ACCEPTED = "ACCEPTED", 
    CANCELLED = "CANELLED", 
    DENIED = "DENIED",
}

export enum EFriendStatus {
    IS_FRIEND = "IS_FRIEND",
    SENT_TO_YOU = "SENT_TO_YOU",
    SENT_BY_YOU = "SENT_BY_YOU",
    NOT_YET = "NOT_YET"
}

export enum ENotificationType {
    HAHA = "HAHA",
    SAD = "SAD",
    ANGRY = "ANGRY",
    HEART = "HEART",
    LIKE = "LIKE",
    WOW = "WOW",
    COMMENT = "COMMENT",
    SHARE = "SHARE",
    INVITE_USERS = "INVITE_USERS",
    MODERATION = "MODERATION",
    FRIEND_REQUEST = "FRIEND_REQUEST"
}

export enum EMemberAcceptation {
    AGREE = "AGREE",
    REJECT = "REJECT"
};

export enum EJoinGroupStatus {
    SUCCESS = "SUCCESS",
    PENDING = "PENDING",
    NOT_YET = "NOT_YET"
}

export enum EFileType  {
    TYPE_IMG = "TYPE_IMG",
    TYPE_VIDEO = "TYPE_VIDEO"
}