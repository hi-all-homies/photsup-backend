package photsup.dao.user;

import photsup.model.entity.User;

import java.util.Optional;

public interface UserDao {

    User saveUser(User user);

    int updateUser(User user);

    Optional<User> findByUniqueKey(String uniqueKey);

    Optional<User> findById(long userId);
}
