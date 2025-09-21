package capstone._4.repository.group;

import capstone._4.domain.Groups;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface GroupRepository extends JpaRepository<Groups, Integer>,GroupCustomRepository {
    //Page<Groups> findAll(Pageable pageable);

//    @Query("select g from Groups g left join fetch g.groupQuestions")
//    Page<Groups> findAllWithQuestion(List<Integer> ids);
//
//    @Query("select g.gup_id from Groups g")
//    Page<Integer> findAllGroupIds(Pageable pageable);
}
