package capstone._4.service.token;

import capstone._4.domain.User;
import capstone._4.domain.UserPrincipal;
import capstone._4.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserdetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            User user = userRepository.findById(Integer.parseInt(username)).orElseThrow(
                    () -> new UsernameNotFoundException("해당 이름을 가진 유저를 찾을수 없습니다."));
            return new UserPrincipal(user);

    }

}
