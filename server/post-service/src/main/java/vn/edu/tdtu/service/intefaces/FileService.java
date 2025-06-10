package vn.edu.tdtu.service.intefaces;

import vn.tdtu.common.enums.post.EFileType;

public interface FileService {
    public void delete(String url, EFileType type);
}
