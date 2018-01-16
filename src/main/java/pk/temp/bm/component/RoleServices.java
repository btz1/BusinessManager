package pk.temp.bm.component;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pk.temp.bm.models.*;
import pk.temp.bm.repositories.PermissionRepository;
import pk.temp.bm.repositories.RoleRepository;
import pk.temp.bm.repositories.RolesPermissionRepository;
import pk.temp.bm.repositories.UserRoleRepository;

@Service
public class RoleServices {

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UserRoleRepository userRoleRepository;

	@Autowired
	private PermissionRepository permissionRepository;

	@Autowired
	private RolesPermissionRepository rolesPermissionRepository;

	public Role createRoleWithPermissions(String name, List<Permission> Perm) {
		try {
			Role role = new Role();
			role.setPermissions(Perm);
			role.setRole(name);
			roleRepository.save(role);
			return role;
		} catch (Exception es) {
			return null;
		}
	}

	public List<Role> findAllRoles() {

		List<Role> rolesList = (List<Role>) roleRepository.findAll();
		return rolesList;
	}

	public List<Role> getUserRoles(User user) {
		List<UsersRole> user_role = userRoleRepository.findByUser(user);
		List<Role> roles = (List<Role>) CollectionUtils.collect(user_role, new BeanToPropertyValueTransformer("role"));
		return roles;
	}

	public UsersRole getUserRole(User user, Role role) {
		return userRoleRepository.findByUserAndRole(user, role);
	}

	public Role EditRole(Role role, List<Permission> perm) {
		try {
			role.setPermissions(perm);
			roleRepository.save(role);
			return role;
		} catch (Exception es) {
			return null;
		}

	}

	public void AddRole(Role role, User user) {
		try {
			UsersRole roles = getUserRole(user, role);
			if (roles == null)
				roles = new UsersRole();
			roles.setRole(role);
			roles.setUser(user);
			userRoleRepository.save(roles);
		} catch (Exception es) {

		}
	}

	public void RemoveRole(Role role, User user) {
		try {
			UsersRole roles = getUserRole(user, role);
			userRoleRepository.delete(roles.getId());
		} catch (Exception es) {

		}
	}
	public JSONObject getPermissionsOfRole(int role){
		List<RolesPermission> rolesPermissionList= rolesPermissionRepository.findByRoleId(role);
		List<Integer> assignedPermissionIdList = (List<Integer>) CollectionUtils.collect(rolesPermissionList,new BeanToPropertyValueTransformer("permissionId"));
		List<Permission> assignPermissionList=permissionRepository.getAllPermissionsIn(assignedPermissionIdList);
		List<Permission> unAssignPermissionList=permissionRepository.getAllPermissionsNotIn(assignedPermissionIdList);

		JSONArray jsonArrayForAssignedPermissions= getJsonArray(assignPermissionList);
		JSONArray jsonArrayForUnAssignedPermissions=getJsonArray(unAssignPermissionList);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("assignedPermissionsList",jsonArrayForAssignedPermissions);
		jsonObject.put("unAssignedPermissionsList",jsonArrayForUnAssignedPermissions);

		return jsonObject;
	}
	public JSONArray getJsonArray(List<Permission> permissionList){
		JSONArray jsonArray = new JSONArray();
		for(Permission permission : permissionList){
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id",permission.getId());
			jsonObject.put("name",permission.getPermission());
			jsonArray.add(jsonObject);
		}
		return jsonArray;
	}

	public void createRole (String roleName, String assignPermissionList) throws ParseException {

		Role role = new Role();
		role.setRole(roleName);
		role =	roleRepository.save(role);

		List<RolesPermission> permissionsToAdd = new ArrayList<>();

		String[] parts = assignPermissionList.split("\\[");
		if(!(parts.length > 1)){
			assignPermissionList = "["+assignPermissionList+"]";
		}
		JSONParser jsonParser = new JSONParser();
		JSONArray assignPermissionJsonArray = (JSONArray) jsonParser.parse(assignPermissionList);
		for (int i=0 ; i < assignPermissionJsonArray.size(); i++){
			RolesPermission rolesPermission = new RolesPermission();
			rolesPermission.setRoleId(role.getId());
			Long permID = (Long) assignPermissionJsonArray.get(i);
			rolesPermission.setPermissionId(permID != null ? permID.intValue() : 0);
			permissionsToAdd.add(rolesPermission);

		}
		rolesPermissionRepository.save(permissionsToAdd);
	}

}
