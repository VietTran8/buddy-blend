package vn.edu.tdtu.service.intefaces;

import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.request.CreateBannedWordReq;

public interface BannedWordService {
    public ResDTO<?> saveBannedWord(CreateBannedWordReq request);
    public ResDTO<?> removeBannedWord(CreateBannedWordReq request);
}
