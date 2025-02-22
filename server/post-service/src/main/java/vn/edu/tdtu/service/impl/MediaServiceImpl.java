package vn.edu.tdtu.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.response.PaginationResponse;
import vn.edu.tdtu.model.Media;
import vn.edu.tdtu.repository.MediaRepository;
import vn.edu.tdtu.service.intefaces.MediaService;
import vn.edu.tdtu.util.SecurityContextUtils;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {
    private final MediaRepository mediaRepository;

    @Override
    public PaginationResponse<Media> getAlbum(String ownerId, int page, int size) {
        if(Objects.isNull(ownerId))
            ownerId = SecurityContextUtils.getUserId();

        Page<Media> medias = mediaRepository.findByOwnerIdAndDetachedNot(ownerId, true, PageRequest.of(page - 1, size));

        return new PaginationResponse<>(
                page,
                size,
                medias.getTotalPages(),
                medias.get().toList()
        );
    }
}
