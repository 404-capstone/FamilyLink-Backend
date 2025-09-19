package capstone._4.repository.user;

import capstone._4.domain.Groups;
import capstone._4.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    boolean save(User user);
    Optional<User> findByUsername(String username);

    //암호화된 이메일로 유저 찾기
    Optional<User> findById(int id);
    Optional<User> findByEmail(String email);
    Optional<User> findBySocialEmail(String email,String social);

    Optional<Groups> findGroupById(int userId);

    List<User> findByIds(List<Integer> usersId);
    List<User> findAllById(List<Long> ids);

    boolean deleteById(int id);  // 삭제 메서드 추가
}
