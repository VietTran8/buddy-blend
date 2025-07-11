package vn.edu.tdtu.service.intefaces;

import vn.edu.tdtu.dto.request.CreateBannedWordReq;
import vn.tdtu.common.viewmodel.ResponseVM;

public interface BannedWordService {
    ResponseVM<?> saveBannedWord(CreateBannedWordReq request);

    ResponseVM<?> removeBannedWord(CreateBannedWordReq request);
}
