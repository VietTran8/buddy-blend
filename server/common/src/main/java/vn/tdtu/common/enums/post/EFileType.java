package vn.tdtu.common.enums.post;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EFileType {
    TYPE_IMG("img"), TYPE_VIDEO("video");

    private final String type;
}
