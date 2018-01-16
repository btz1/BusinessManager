package pk.temp.bm.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import pk.temp.bm.models.AllowedSubMenu;
import pk.temp.bm.models.Role;

@Repository
public interface AllowedSubMenuRepository extends CrudRepository<AllowedSubMenu, Long> {

	List<AllowedSubMenu> findByUserRole(Role role);
}
