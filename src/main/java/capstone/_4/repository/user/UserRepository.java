package capstone._4.repository.user;

import capstone._4.domain.User;

import java.util.Optional;

public interface UserRepository {
    boolean save(User user);
    Optional<User> findByUsername(String username);
<<<<<<< HEAD
    //암호화된 이메일로 유저 찾기
=======

    Optional<User> findById(int id);

>>>>>>> f2aefd714ce702ffa645784ffd33e424f5d75f89
    Optional<User> findByEmail(String email);
}
