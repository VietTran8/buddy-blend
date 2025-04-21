package vn.edu.tdtu.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.request.CreateBannedWordReq;
import vn.edu.tdtu.model.BannedWord;
import vn.edu.tdtu.repository.BannedWordRepository;
import vn.edu.tdtu.service.intefaces.BannedWordService;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BannedWordServiceImpl implements BannedWordService {
    private final BannedWordRepository bannedWordRepository;

    @Override
    public ResDTO<?> saveBannedWord(CreateBannedWordReq request){
        if(!bannedWordRepository.existsByWord(request.getWord().toLowerCase())){
            BannedWord word = new BannedWord();
            word.setWord(request.getWord().toLowerCase());

            bannedWordRepository.save(word);
        }

        Map<String, String> data = new HashMap<>();
        data.put("savedWord", request.getWord());

        return new ResDTO<Map<String, String>>(
                "saved banned word successfully",
                data,
                HttpServletResponse.SC_CREATED
        );
    }

    @Override
    public ResDTO<?> removeBannedWord(CreateBannedWordReq request){
        ResDTO<?> response = new ResDTO<>();

        BannedWord word = bannedWordRepository.findByWord(request.getWord().toLowerCase())
                        .orElseThrow(() -> new IllegalArgumentException(String.format("'%s' can not be found in banned words", request.getWord())));

        bannedWordRepository.delete(word);

        response.setCode(HttpServletResponse.SC_OK);
        response.setData(null);
        response.setMessage("deleted");

        return response;
    }
}
