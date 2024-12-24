package vn.edu.tdtu.service.intefaces;

import vn.edu.tdtu.enums.EFileType;

public interface FileService {
    public void delete(String url, EFileType type);
}
