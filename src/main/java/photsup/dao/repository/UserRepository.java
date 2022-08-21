package photsup.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import photsup.model.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUniqueKey(String uniqueKey);

    @Query(value = "update User u set u.avatarUrl=:avatarUrl where u.userId=:userId")
    @Modifying
    int updateUser(@Param("userId") Long userId, @Param("avatarUrl") String avatarUrl);
}
