package pk.temp.bm.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import pk.temp.bm.models.User;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Integer> {
	User findByUsernameAndEnabled(String username, boolean enabled);
	User findByUsernameAndPassword(String user, String password);
	User findByUsername(String user);
}
