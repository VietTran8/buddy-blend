package vn.edu.tdtu.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dto.response.BlockingResponse;
import vn.edu.tdtu.mapper.response.MinimizedUserMapper;
import vn.edu.tdtu.model.Blocking;
import vn.edu.tdtu.model.User;
import vn.edu.tdtu.repository.UserRepository;
import vn.edu.tdtu.service.interfaces.BlockingService;
import vn.tdtu.common.exception.BadRequestException;
import vn.tdtu.common.utils.MessageCode;
import vn.tdtu.common.utils.SecurityContextUtils;
import vn.tdtu.common.viewmodel.IDResponseVM;
import vn.tdtu.common.viewmodel.ResponseVM;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlockingServiceImpl implements BlockingService {
    private final UserRepository userRepository;
    private final MinimizedUserMapper minimizedUserMapper;

    @Override
    public ResponseVM<?> handleUserBlocking(String blockUserId) {
        String authUserId = SecurityContextUtils.getUserId();

        Map<String, User> userMap = userRepository.findByIdInAndActive(List.of(blockUserId, authUserId), true)
                .stream().collect(Collectors.toMap(
                        User::getId,
                        user -> user
                ));

        if (userMap.size() != 2)
            throw new BadRequestException(MessageCode.User.USER_NOT_FOUND);

        User authUser = userMap.get(authUserId);
        User blockUser = userMap.get(blockUserId);

        List<Blocking> blockingList = authUser.getBlockingList();
        List<Blocking> opponentBlockingList = blockUser.getBlockingList();

        if (opponentBlockingList.stream()
                .anyMatch(blocking -> blocking.getBlockedUser().getId().equals(authUser.getId())))
            throw new BadRequestException(MessageCode.User.BLOCKING_OPPONENT_BLOCKED);

        ResponseVM<IDResponseVM> response = new ResponseVM<>();

        response.setCode(HttpServletResponse.SC_OK);
        response.setData(new IDResponseVM(blockUser.getId()));

        blockingList.stream()
                .filter(
                        blocking -> blocking.getBlockedUser().getId().equals(blockUser.getId())
                ).findFirst()
                .ifPresentOrElse(
                        blocking -> {
                            blockingList.remove(blocking);

                            userRepository.save(authUser);

                            response.setMessage(MessageCode.User.BLOCKING_UNBLOCKED);
                        }, () -> {
                            Blocking newBlocking = new Blocking();

                            newBlocking.setBlockedAt(LocalDateTime.now());
                            newBlocking.setBlockedUser(blockUser);
                            newBlocking.setBlockedByUser(authUser);

                            blockingList.add(newBlocking);

                            userRepository.save(authUser);

                            response.setMessage(MessageCode.User.BLOCKING_BLOCKED);
                        }
                );

        return response;
    }

    @Override
    public ResponseVM<?> getBlockedUserList() {
        String authUserId = SecurityContextUtils.getUserId();

        User authUser = userRepository.findByIdAndActive(authUserId, true)
                .orElseThrow(() -> new BadRequestException(MessageCode.User.USER_NOT_FOUND));

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

        ResponseVM<List<BlockingResponse>> response = new ResponseVM<>();

        response.setData(responseData);
        response.setCode(HttpServletResponse.SC_OK);
        response.setMessage(MessageCode.User.BLOCKING_FETCHED);

        return response;
    }
}
