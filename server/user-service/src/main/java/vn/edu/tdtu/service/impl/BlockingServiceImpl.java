package vn.edu.tdtu.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.response.BlockingResponse;
import vn.edu.tdtu.dto.response.IdResponse;
import vn.edu.tdtu.exception.BadRequestException;
import vn.edu.tdtu.mapper.response.MinimizedUserMapper;
import vn.edu.tdtu.model.Blocking;
import vn.edu.tdtu.model.User;
import vn.edu.tdtu.repository.BlockingRepository;
import vn.edu.tdtu.repository.UserRepository;
import vn.edu.tdtu.service.interfaces.BlockingService;
import vn.edu.tdtu.util.SecurityContextUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlockingServiceImpl implements BlockingService {
    private final BlockingRepository blockingRepository;
    private final UserRepository userRepository;
    private final MinimizedUserMapper minimizedUserMapper;

    @Override
    public ResDTO<?> handleUserBlocking(String banUserId) {
        String authUserId = SecurityContextUtils.getUserId();

        Map<String, User> userMap = userRepository.findByIdInAndActive(List.of(banUserId, authUserId), true)
                .stream().collect(Collectors.toMap(
                        User::getId,
                        user -> user
                ));

        if(userMap.size() != 2)
            throw new BadRequestException("Không tìm thấy người dùng");

        User authUser = userMap.get(authUserId);
        User banUser = userMap.get(banUserId);

        List<Blocking> blockingList = authUser.getBlockingList();
        List<Blocking> opponentBlockingList = banUser.getBlockingList();

        if(opponentBlockingList.stream()
                .anyMatch(blocking -> blocking.getBlockedUser().getId().equals(authUser.getId())))
            throw new BadRequestException("Người kia đã chặn bạn rồi!");

        ResDTO<IdResponse> response = new ResDTO<>();

        response.setCode(HttpServletResponse.SC_OK);
        response.setData(new IdResponse(banUser.getId()));

        blockingList.stream()
                .filter(
                        blocking -> blocking.getBlockedUser().getId().equals(banUser.getId())
                ).findFirst()
                .ifPresentOrElse(
                        blocking -> {
                            blockingList.remove(blocking);

                            userRepository.save(authUser);

                            response.setMessage("Đã hủy chặn người dùng thành công!");
                        },  () -> {
                            Blocking newBlocking = new Blocking();

                            newBlocking.setBlockedAt(LocalDateTime.now());
                            newBlocking.setBlockedUser(banUser);
                            newBlocking.setBlockedByUser(authUser);

                            blockingList.add(newBlocking);

                            userRepository.save(authUser);

                            response.setMessage("Đã chặn người dùng thành công!");
                        }
                );

        return response;
    }

    @Override
    public ResDTO<?> getBlockedUserList() {
        String authUserId = SecurityContextUtils.getUserId();

        User authUser = userRepository.findByIdAndActive(authUserId,  true)
                .orElseThrow(() -> new BadRequestException("Không tìm thấy người dùng!"));

        List<BlockingResponse> responseData = authUser.getBlockingList()
                .stream()
                .map(blocking -> {
                    BlockingResponse blockingResponse = new BlockingResponse();

                    blockingResponse.setId(blocking.getId());
                    blockingResponse.setBlockedAt(blocking.getBlockedAt());
                    blockingResponse.setBlockedUser(minimizedUserMapper.mapToDTO(blocking.getBlockedUser()));

                    return blockingResponse;
                })
                .toList();

        ResDTO<List<BlockingResponse>> response = new ResDTO<>();

        response.setData(responseData);
        response.setCode(HttpServletResponse.SC_OK);
        response.setMessage("Banning user fetched successfully!");

        return response;
    }
}
