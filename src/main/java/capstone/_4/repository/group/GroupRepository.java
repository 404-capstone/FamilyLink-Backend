package capstone._4.repository.group;

import capstone._4.domain.Groups;


import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.print.Pageable;

public interface GroupRepository extends JpaRepository<Groups, Integer>,GroupCustomRepository {
    //Page<Groups> findAll(Pageable pageable);
}
