package vn.edu.tdtu.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EFileType {
    TYPE_IMG("img"), TYPE_VIDEO("vid");

    private final String type;
}
