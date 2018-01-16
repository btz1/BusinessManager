package pk.temp.bm.repositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pk.temp.bm.models.RolesPermission;

import java.util.List;

/**
 * Created by Abubakar on 11/1/2017.
 */
@Repository
public interface RolesPermissionRepository extends CrudRepository<RolesPermission,Integer>{

    List<RolesPermission> findByRoleId(int roleId);

    @Transactional
    @Modifying
    @Query(value = "delete from roles_permissions where role_id=:roleId AND permission_id=:permissionId" , nativeQuery = true)
    public void deletePermissionByRoleId(@Param("roleId") int roleId,@Param("permissionId") int permissionId);
}
