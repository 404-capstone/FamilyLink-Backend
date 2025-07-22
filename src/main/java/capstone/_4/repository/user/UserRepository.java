package capstone._4.repository.user;

import capstone._4.domain.User;

import java.util.Optional;

public interface UserRepository {
    boolean save(User user);
    Optional<User> findByUsername(String username);
<<<<<<< HEAD:src/main/java/capstone/_4/repository/UserRepository.java

=======
>>>>>>> cc2d5996a4dfd7c7daa82f73d60063b1d0b176fb:src/main/java/capstone/_4/repository/user/UserRepository.java
    //암호화된 이메일로 유저 찾기
    Optional<User> findById(int id);
    Optional<User> findByEmail(String email);
}
