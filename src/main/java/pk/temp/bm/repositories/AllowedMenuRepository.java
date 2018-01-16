package pk.temp.bm.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import pk.temp.bm.models.AllowedMenu;
import pk.temp.bm.models.Role;

@Repository
public interface AllowedMenuRepository extends CrudRepository<AllowedMenu, Long> {

	List<AllowedMenu> findByUserRole(Role role);
}
