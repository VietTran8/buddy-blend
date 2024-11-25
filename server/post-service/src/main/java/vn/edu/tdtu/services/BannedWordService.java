package vn.edu.tdtu.services;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dtos.ResDTO;
import vn.edu.tdtu.dtos.request.CreateBannedWordReq;
import vn.edu.tdtu.models.BannedWord;
import vn.edu.tdtu.repositories.BannedWordRepository;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BannedWordService {
    private final BannedWordRepository bannedWordRepository;

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
