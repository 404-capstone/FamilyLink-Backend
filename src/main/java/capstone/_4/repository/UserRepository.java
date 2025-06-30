package capstone._4.repository;

import capstone._4.domain.User;

public interface UserRepository {
    boolean save(User user);
}
