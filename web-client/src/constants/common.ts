export const USER_TOKEN_PREFIX = "utk";

export const PAGE_LIMIT = 10;
export const STORY_FONTS = {
    FONT_NEAT: "font-alata",
    FONT_HEADER: "font-rowdies",
    FONT_STYLIST: "font-dancing-script",
    FONT_NORMAL: "font-lora"
}

export const STORY_BACKGROUND: Record<BackgroundKey, string> = {
    bgPrimary: 'bg-gradient-to-r from-red-400 to-pink-500',
    bgSecondary: 'bg-gradient-to-r from-red-200 to-yellow-200',
    bgAccent: 'bg-gradient-to-r from-green-400 to-blue-500',
    bgHighlight: 'bg-gradient-to-r from-yellow-600 to-red-600',
    bgMuted: 'bg-gradient-to-r from-slate-900 to-slate-700',
    bgContrast: 'bg-gradient-to-r from-violet-400 to-purple-300',
    bgOverlay: 'bg-gradient-to-r from-red-400 to-red-900',
    bgBackdrop: 'bg-gradient-to-b from-rose-400 to-pink-600',
    bgPatterned: 'bg-gradient-to-r from-rose-400 to-red-500',
    bgTextured: 'bg-gradient-to-r from-purple-500 to-purple-900',
    bgCard: 'bg-gradient-to-br from-fuchsia-500 to-rose-500',
    bgHero: 'bg-gradient-to-r from-rose-400 to-orange-300',
    bgSection: 'bg-gradient-to-r from-fuchsia-500 to-pink-500',
    bgContent: 'bg-gradient-to-r from-blue-300 to-blue-800',
    bgFooter: 'bg-gradient-to-tl from-amber-500 to-yellow-400',
    bgHeader: 'bg-gradient-to-r from-blue-800 to-indigo-900',
    bgInteractive: 'bg-gradient-to-r from-blue-500 to-teal-400',
    bgSurface: 'bg-gradient-to-r from-green-200 to-blue-500',
    bgShade: 'bg-gradient-to-b from-pink-400 to-red-500',
    bgTransparent: 'bg-gradient-to-r from-slate-500 to-slate-800',
};

export type BackgroundKey =
| 'bgPrimary'
| 'bgSecondary'
| 'bgAccent'
| 'bgHighlight'
| 'bgMuted'
| 'bgContrast'
| 'bgOverlay'
| 'bgBackdrop'
| 'bgPatterned'
| 'bgTextured'
| 'bgCard'
| 'bgHero'
| 'bgSection'
| 'bgContent'
| 'bgFooter'
| 'bgHeader'
| 'bgInteractive'
| 'bgSurface'
| 'bgShade'
| 'bgTransparent';

export const FILE_EDITOR_TRANSLATIONS = {
    name: 'Tên',
    save: 'Lưu',
    saveAs: 'Lưu dưới dạng',
    back: 'Quay lại',
    loading: 'Đang tải...',
    resetOperations: 'Đặt lại/xóa tất cả thao tác',
    changesLoseWarningHint: 'Nếu bạn nhấn nút “đặt lại”, các thay đổi sẽ bị mất. Bạn có muốn tiếp tục không?',
    discardChangesWarningHint: 'Nếu bạn đóng cửa sổ, thay đổi cuối cùng sẽ không được lưu.',
    cancel: 'Hủy',
    apply: 'Áp dụng',
    warning: 'Cảnh báo',
    confirm: 'Xác nhận',
    discardChanges: 'Bỏ thay đổi',
    undoTitle: 'Hoàn tác thao tác cuối',
    redoTitle: 'Làm lại thao tác cuối',
    showImageTitle: 'Hiển thị hình ảnh gốc',
    zoomInTitle: 'Phóng to',
    zoomOutTitle: 'Thu nhỏ',
    toggleZoomMenuTitle: 'Chuyển đổi menu thu phóng',
    adjustTab: 'Thao tác',
    finetuneTab: 'Chỉnh',
    filtersTab: 'Bộ lọc',
    watermarkTab: 'Chèn watermark',
    annotateTabLabel: 'Chèn',
    resize: 'Thay đổi kích thước',
    resizeTab: 'Thay đổi kích thước',
    imageName: 'Tên hình ảnh',
    invalidImageError: 'Hình ảnh không hợp lệ.',
    uploadImageError: 'Lỗi khi tải lên hình ảnh.',
    areNotImages: 'không phải là hình ảnh',
    isNotImage: 'không phải là hình ảnh',
    toBeUploaded: 'để tải lên',
    cropTool: 'Cắt',
    original: 'Gốc',
    custom: 'Tùy chỉnh',
    square: 'Vuông',
    landscape: 'Ngang',
    portrait: 'Dọc',
    ellipse: 'Hình elip',
    classicTv: 'TV cổ điển',
    cinemascope: 'Cinemascope',
    arrowTool: 'Mũi tên',
    blurTool: 'Làm mờ',
    brightnessTool: 'Độ sáng',
    contrastTool: 'Độ tương phản',
    ellipseTool: 'Hình elip',
    unFlipX: 'Bỏ lật X',
    flipX: 'Lật X',
    unFlipY: 'Bỏ lật Y',
    flipY: 'Lật Y',
    hsvTool: 'HSV',
    hue: 'Sắc độ',
    brightness: 'Độ sáng',
    saturation: 'Độ bão hòa',
    value: 'Giá trị',
    imageTool: 'Hình ảnh',
    importing: 'Đang nhập...',
    addImage: '+ Thêm hình ảnh',
    uploadImage: 'Tải lên hình ảnh',
    fromGallery: 'Từ thư viện',
    lineTool: 'Đường',
    penTool: 'Bút',
    polygonTool: 'Đa giác',
    sides: 'Cạnh',
    rectangleTool: 'Hình chữ nhật',
    cornerRadius: 'Bán kính góc',
    resizeWidthTitle: 'Chiều rộng tính bằng pixel',
    resizeHeightTitle: 'Chiều cao tính bằng pixel',
    toggleRatioLockTitle: 'Chuyển đổi khóa tỷ lệ',
    resetSize: 'Đặt lại kích thước gốc của hình ảnh',
    rotateTool: 'Xoay',
    textTool: 'Văn bản',
    textSpacings: 'Khoảng cách văn bản',
    textAlignment: 'Căn chỉnh văn bản',
    fontFamily: 'Phông chữ',
    size: 'Kích thước',
    letterSpacing: 'Khoảng cách chữ',
    lineHeight: 'Chiều cao dòng',
    warmthTool: 'Độ ấm',
    addWatermark: '+ Thêm watermark',
    addTextWatermark: '+ Thêm watermark văn bản',
    addWatermarkTitle: 'Chọn loại watermark',
    uploadWatermark: 'Tải lên watermark',
    addWatermarkAsText: 'Thêm dưới dạng văn bản',
    padding: 'Đệm',
    paddings: 'Đệm',
    shadow: 'Bóng',
    horizontal: 'Ngang',
    vertical: 'Dọc',
    blur: 'Làm mờ',
    opacity: 'Độ mờ',
    transparency: 'Độ trong suốt',
    position: 'Vị trí',
    stroke: 'Nét viền',
    saveAsModalTitle: 'Lưu dưới dạng',
    extension: 'Phần mở rộng',
    format: 'Định dạng',
    nameIsRequired: 'Tên là bắt buộc.',
    quality: 'Chất lượng',
    imageDimensionsHoverTitle: 'Kích thước ảnh lưu (rộng x cao)',
    cropSizeLowerThanResizedWarning: 'Lưu ý, khu vực cắt đã chọn nhỏ hơn kích thước thay đổi, có thể làm giảm chất lượng',
    actualSize: 'Kích thước thực (100%)',
    fitSize: 'Kích thước vừa',
    addImageTitle: 'Chọn hình ảnh để thêm...',
    mutualizedFailedToLoadImg: 'Không thể tải hình ảnh.',
    tabsMenu: 'Menu',
    download: 'Tải xuống',
    width: 'Chiều rộng',
    height: 'Chiều cao',
    plus: '+',
    cropItemNoEffect: 'Không có xem trước cho mục cắt này'
}
