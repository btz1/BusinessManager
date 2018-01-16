package pk.temp.bm.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import pk.temp.bm.models.Role;

/**
 * Created by Abubakar on 6/14/2017.
 */
@Repository
public interface RoleRepository extends CrudRepository<Role, Integer>{
}
