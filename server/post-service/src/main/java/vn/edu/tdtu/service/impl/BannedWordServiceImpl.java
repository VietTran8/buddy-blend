package vn.edu.tdtu.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dto.request.CreateBannedWordReq;
import vn.edu.tdtu.model.BannedWord;
import vn.edu.tdtu.repository.BannedWordRepository;
import vn.edu.tdtu.service.intefaces.BannedWordService;
import vn.tdtu.common.exception.BadRequestException;
import vn.tdtu.common.utils.MessageCode;
import vn.tdtu.common.viewmodel.ResponseVM;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BannedWordServiceImpl implements BannedWordService {
    private final BannedWordRepository bannedWordRepository;

    @Override
    public ResponseVM<?> saveBannedWord(CreateBannedWordReq request) {
        if (!bannedWordRepository.existsByWord(request.getWord().toLowerCase())) {
            BannedWord word = new BannedWord();
            word.setWord(request.getWord().toLowerCase());

            bannedWordRepository.save(word);
        }

        Map<String, String> data = new HashMap<>();
        data.put("savedWord", request.getWord());

        return new ResponseVM<Map<String, String>>(
                MessageCode.Post.BANNED_WORD_SAVED,
                data,
                HttpServletResponse.SC_CREATED
        );
    }

    @Override
    public ResponseVM<?> removeBannedWord(CreateBannedWordReq request) {
        ResponseVM<?> response = new ResponseVM<>();

        BannedWord word = bannedWordRepository.findByWord(request.getWord().toLowerCase())
                .orElseThrow(() -> new BadRequestException(MessageCode.Post.BANNED_WORD_NOT_FOUND_WORD, request.getWord()));

        bannedWordRepository.delete(word);

        response.setCode(HttpServletResponse.SC_OK);
        response.setData(null);
        response.setMessage(MessageCode.Post.BANNED_WORD_DELETED);

        return response;
    }
}
