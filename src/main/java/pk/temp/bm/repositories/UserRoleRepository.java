package pk.temp.bm.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import pk.temp.bm.models.Role;
import pk.temp.bm.models.User;
import pk.temp.bm.models.UsersRole;

@Repository
public interface UserRoleRepository extends CrudRepository<UsersRole, Integer>{
	
	List<UsersRole> findByUser(User user);
	UsersRole findByUserAndRole(User user, Role role);

}
