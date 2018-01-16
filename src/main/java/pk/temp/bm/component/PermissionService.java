package pk.temp.bm.component;

import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pk.temp.bm.models.*;
import pk.temp.bm.repositories.AllowedMenuRepository;
import pk.temp.bm.repositories.AllowedSubMenuRepository;
import pk.temp.bm.repositories.PermissionRepository;
import pk.temp.bm.repositories.RolesPermissionRepository;

/**
 * Created by Abubakar on 6/13/2017.
 */
@Service
@Transactional
public class PermissionService {
   
 
    @Autowired
	private PermissionRepository permissionRepository;
    
    @Autowired
    private AllowedMenuRepository allowedMenuRepository;
    
    @Autowired
    private AllowedSubMenuRepository allowedSubMenuRepository;
    
    @Autowired
    private RoleServices roleServices;

    @Autowired
	private RolesPermissionRepository rolesPermissionRepository;

    public static final Logger logger = LoggerFactory.getLogger(PermissionService.class);
    
    public List<Permission> findPermissionsOfUser(User loggedInUser){

    	Set<Permission> permissionList = new HashSet<Permission>();
        List<Role> roles = roleServices.getUserRoles(loggedInUser);
    	for (Role role : roles) {
    		for(Permission p: role.getPermissions()){
    			permissionList.add(p);
    		}
        }
    	List<Permission> permissions = new ArrayList<Permission>();
    	permissions.addAll(permissionList);
        return permissions;
    }
  

	public List<Permission> findAllPermissions() {
		List<Permission> permissionList = new ArrayList<>();
		permissionList = (List<Permission>) permissionRepository.findAll();
		return permissionList;

	}
	
	
	public Permission createPermission(String name , String Url)
	{
		Permission created=new Permission();
		created.setPermission(name);
		created.setPermissionURL(Url);
		permissionRepository.save(created);
		return created;
	}
  
    
    public List<AllowedMenu> getRoleMenus(Role role){
    	List<AllowedMenu> user_role = allowedMenuRepository.findByUserRole(role);
    	return user_role;
    }
    
    public List<AllowedSubMenu> getRoleSubMenus(Role role){
    	List<AllowedSubMenu> user_role = allowedSubMenuRepository.findByUserRole(role);
    	  return user_role;
    }

    public List<String> getPermissionsByURL(List<String> urlList){
    	return permissionRepository.getPermissionsByURL(urlList);
	}

	public List<Permission> saveAllPermissios(List<Permission> permissionList){
    	return (List<Permission>) permissionRepository.save(permissionList);
	}

	public List<RolesPermission> saveAllRolePermissions(List<RolesPermission> rolesPermissionList){
		return (List<RolesPermission>) rolesPermissionRepository.save(rolesPermissionList);
	}
	public void updatePermissionsOfRole(String permission_list,int role_id) throws ParseException {
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject = (JSONObject) jsonParser.parse(permission_list);

			try {
				JSONArray unAssignedPermissionsArray = (JSONArray) jsonObject.get("unAssignedPermissionList");
				logger.info("request is:" + permission_list);
				//List<Integer> unAssignedPermissionList = new ArrayList<Integer>();
				//unAssignedPermissionList.addAll(unAssignedPermissionsArray);
				for (int i = 0; i < unAssignedPermissionsArray.size(); i++) {
					logger.info("json array values" + unAssignedPermissionsArray.get(i));
					Long permissionId = (Long) unAssignedPermissionsArray.get(i);
					rolesPermissionRepository.deletePermissionByRoleId(role_id, permissionId != null ? permissionId.intValue() : 0);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			JSONArray assignedPermissionsArray = (JSONArray) jsonObject.get("assignedPermissionList");
			//List<Integer> assignedPermissionList = new ArrayList<>();
			//assignedPermissionList.addAll(assignedPermissionsArray);
			for (int i = 0; i < assignedPermissionsArray.size(); i++) {

				Long permissionID = (Long) assignedPermissionsArray.get(i);
				RolesPermission rolesPermission = new RolesPermission();
				rolesPermission.setRoleId(role_id);
				rolesPermission.setPermissionId(permissionID != null ? permissionID.intValue() : 0);

				try {
					rolesPermissionRepository.save(rolesPermission);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}


}