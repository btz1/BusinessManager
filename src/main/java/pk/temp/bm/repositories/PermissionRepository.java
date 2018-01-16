package pk.temp.bm.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pk.temp.bm.models.Permission;

import java.util.List;

/**
 * Created by Abubakar on 6/13/2017.
 */
@Repository
public interface PermissionRepository extends CrudRepository<Permission,Integer> {

    @Query(value = "select URL from permissions where URL in :urlList", nativeQuery = true)
    List<String> getPermissionsByURL(@Param("urlList") List<String> urlList);

    @Query(value = "select * from permissions where id in:permissionIdList", nativeQuery=true)
    public List<Permission> getAllPermissionsIn(@Param("permissionIdList") List<Integer> permissionIdList);

    @Query(value = "select * from permissions where id NOT in:permissionIdList", nativeQuery=true)
    public List<Permission> getAllPermissionsNotIn(@Param("permissionIdList") List<Integer> permissionIdList);



}
