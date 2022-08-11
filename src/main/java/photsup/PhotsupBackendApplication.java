package photsup;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import photsup.dao.user.UserDao;
import photsup.model.entity.GitHubUser;
import photsup.model.entity.User;

@SpringBootApplication
public class PhotsupBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(PhotsupBackendApplication.class, args);
	}

	@Bean
	public CommandLineRunner runner(UserDao userDao){
		return args -> {
			User user = new GitHubUser();
			user.setUsername("user1");
			user.setUniqueKey("unique_key");
			user.setAvatarUrl("user avatar url");
			userDao.saveUser(user);
		};
	}

}
