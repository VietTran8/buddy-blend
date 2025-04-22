package vn.edu.tdtu.enums;

import lombok.Getter;

@Getter
public enum EUploadFolder {
    FOLDER_VIDEO("buddy-blend/videos", "video"),
    FOLDER_IMG("buddy-blend/images", "image"),
    FOLDER_OTHERS("buddy-blend/others", "raw");
    private final String folderName;
    private final String resourceType;

    EUploadFolder(String folderName, String resourceType) {
        this.folderName = folderName;
        this.resourceType = resourceType;
    }
}
