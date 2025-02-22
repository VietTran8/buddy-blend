package vn.edu.tdtu.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.dto.ResDTO;
import vn.edu.tdtu.dto.response.BanningResponse;
import vn.edu.tdtu.dto.response.IdResponse;
import vn.edu.tdtu.exception.BadRequestException;
import vn.edu.tdtu.mapper.response.MinimizedUserMapper;
import vn.edu.tdtu.model.Banning;
import vn.edu.tdtu.model.User;
import vn.edu.tdtu.repository.BanningRepository;
import vn.edu.tdtu.repository.UserRepository;
import vn.edu.tdtu.service.interfaces.BanningService;
import vn.edu.tdtu.util.SecurityContextUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BanningServiceImpl implements BanningService {
    private final BanningRepository banningRepository;
    private final UserRepository userRepository;
    private final MinimizedUserMapper minimizedUserMapper;

    @Override
    public ResDTO<?> handleUserBanning(String banUserId) {
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

        List<Banning> banningList = authUser.getBanningList();
        List<Banning> opponentBanningList = banUser.getBanningList();

        if(opponentBanningList.stream()
                .anyMatch(banning -> banning.getBannedUser().getId().equals(authUser.getId())))
            throw new BadRequestException("Người kia đã chặn bạn rồi!");

        ResDTO<IdResponse> response = new ResDTO<>();

        response.setCode(HttpServletResponse.SC_OK);
        response.setData(new IdResponse(banUser.getId()));

        banningList.stream()
                .filter(
                        banning -> banning.getBannedUser().getId().equals(banUser.getId())
                ).findFirst()
                .ifPresentOrElse(
                        banning -> {
                            banningList.remove(banning);

                            userRepository.save(authUser);

                            response.setMessage("Đã hủy chặn người dùng thành công!");
                        },  () -> {
                            Banning newBanning = new Banning();

                            newBanning.setBannedAt(LocalDateTime.now());
                            newBanning.setBannedUser(banUser);
                            newBanning.setBannedByUser(authUser);

                            banningList.add(newBanning);

                            userRepository.save(authUser);

                            response.setMessage("Đã chặn người dùng thành công!");
                        }
                );

        return response;
    }

    @Override
    public ResDTO<?> getBannedUserList() {
        String authUserId = SecurityContextUtils.getUserId();

        User authUser = userRepository.findByIdAndActive(authUserId,  true)
                .orElseThrow(() -> new BadRequestException("Không tìm thấy người dùng!"));

        List<BanningResponse> responseData = authUser.getBanningList()
                .stream()
                .map(banning -> {
                    BanningResponse banningResponse = new BanningResponse();

                    banningResponse.setId(banning.getId());
                    banningResponse.setBannedAt(banning.getBannedAt());
                    banningResponse.setBannedUser(minimizedUserMapper.mapToDTO(banning.getBannedUser()));

                    return banningResponse;
                })
                .toList();

        ResDTO<List<BanningResponse>> response = new ResDTO<>();

        response.setData(responseData);
        response.setCode(HttpServletResponse.SC_OK);
        response.setMessage("Banning user fetched successfully!");

        return response;
    }
}
