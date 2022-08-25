package photsup.dao.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import photsup.dao.repository.UserRepository;
import photsup.model.entity.User;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserDaoImpl implements UserDao {
    private final UserRepository userRepo;

    @Override
    public User saveUser(User user) {
        return this.userRepo.save(user);
    }

    @Override
    @Transactional
    public int updateUser(User user) {
        return this.userRepo.updateUser(user.getUserId(), user.getAvatarUrl());
    }

    @Override
    public Optional<User> findByUniqueKey(String uniqueKey) {
        return this.userRepo.findByUniqueKey(uniqueKey);
    }

    @Override
    public Optional<User> findById(long userId) {
        return this.userRepo.findById(userId);
    }
}
