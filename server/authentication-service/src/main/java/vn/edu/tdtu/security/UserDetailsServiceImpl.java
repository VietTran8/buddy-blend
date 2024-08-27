package vn.edu.tdtu.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import vn.edu.tdtu.models.User;
import vn.edu.tdtu.services.UserService;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserService userService;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User foundUser = userService.getUserInfo(email);
        if(foundUser == null)
            throw new UsernameNotFoundException("User not found with username: " + email);
        return UserDetailsImpl.build(foundUser);
    }
}
