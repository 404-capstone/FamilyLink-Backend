package capstone._4.repository.user;

import capstone._4.domain.User;

import java.util.Optional;

public interface UserRepository {
    boolean save(User user);
    Optional<User> findByUsername(String username);

    Optional<User> findById(int id);

    Optional<User> findByEmail(String email);
}
