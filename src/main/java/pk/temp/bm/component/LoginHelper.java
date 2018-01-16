package pk.temp.bm.component;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pk.temp.bm.models.AllowedSubMenu;
import pk.temp.bm.models.Role;
import pk.temp.bm.models.User;
import pk.temp.bm.models.AllowedMenu;

@Service
public class LoginHelper {
	
	@Autowired
	private PermissionService permissionService;
	
	@Autowired
	private RoleServices roleServices;
	
	@Autowired
	private UserService userService;
	
	public boolean existsMenue(List<AllowedMenu> menueList,AllowedMenu menue){
		for(AllowedMenu menuItem : menueList){
			if(menuItem.getSideMenu().equals(menue.getSideMenu()))
				return true;
		}
		return false;
	}
	
	public boolean existsSubMenue(List<AllowedSubMenu> menueList, AllowedSubMenu menue){
		for(AllowedSubMenu menuItem : menueList){
			if(menuItem.getSide_submenu().equals(menue.getSide_submenu()))
				return true;
		}
		return false;
	}

	public JSONArray getUserRoleInfo(User user) throws Exception {
		
		List<Role> userRoles = roleServices.getUserRoles(user);
		
		JSONArray roleInfoArray = new JSONArray();
		
		List<AllowedMenu> finalUserMenuList  = new ArrayList<AllowedMenu>();
		List<AllowedSubMenu> finalUserSubMenuList = new ArrayList<AllowedSubMenu>();
		
		for (Role userRole : userRoles) {
			
			for(AllowedMenu menuItem : permissionService.getRoleMenus(userRole))
				if(!existsMenue(finalUserMenuList,menuItem))
					finalUserMenuList.add(menuItem);
			
			for(AllowedSubMenu SubMenuItem : permissionService.getRoleSubMenus(userRole))
				if(!existsSubMenue(finalUserSubMenuList,SubMenuItem))
					finalUserSubMenuList.add(SubMenuItem);
			
		}
		for (AllowedMenu menuItem : finalUserMenuList) {
			JSONObject object1 = new JSONObject();
			object1.put("Id", menuItem.getSideMenu().getId());
			object1.put("name", menuItem.getSideMenu().getName());
			object1.put("state", menuItem.getSideMenu().getState());
			object1.put("icon", menuItem.getSideMenu().getIcon());
			JSONArray array2 = new JSONArray();
			for (AllowedSubMenu subMenuItem : finalUserSubMenuList) {
				if (subMenuItem.getSide_submenu().getParent_id() == menuItem.getSideMenu().getId()) {
					JSONObject object2 = new JSONObject();
					object2.put("name", subMenuItem.getSide_submenu().getName());
					object2.put("state", subMenuItem.getSide_submenu().getState());
					array2.add(object2);
				}
			}
			object1.put("sub_views", array2);
			roleInfoArray.add(object1);
		}
		return roleInfoArray;
	}

	public JSONArray getUsersFrontEndPermissions(User user) throws Exception {

		List<Role> userRoles = roleServices.getUserRoles(user);

		JSONArray frontEndPermissionsArray = new JSONArray();

		List<AllowedMenu> finalUserMenuList  = new ArrayList<AllowedMenu>();
		List<AllowedSubMenu> finalUserSubMenuList = new ArrayList<AllowedSubMenu>();

		for (Role userRole : userRoles) {

			for(AllowedMenu menuItem : permissionService.getRoleMenus(userRole))
				if(!existsMenue(finalUserMenuList,menuItem))
					finalUserMenuList.add(menuItem);

			for(AllowedSubMenu SubMenuItem : permissionService.getRoleSubMenus(userRole))
				if(!existsSubMenue(finalUserSubMenuList,SubMenuItem))
					finalUserSubMenuList.add(SubMenuItem);

		}
		for (AllowedMenu menuItem : finalUserMenuList) {
			frontEndPermissionsArray.add(menuItem.getSideMenu().getPermissionName());
			for (AllowedSubMenu subMenuItem : finalUserSubMenuList) {
				if (subMenuItem.getSide_submenu().getParent_id() == menuItem.getSideMenu().getId()) {
					frontEndPermissionsArray.add(subMenuItem.getSide_submenu().getPermissionName());
				}
			}
		}
		return frontEndPermissionsArray;
	}


	public String getUserData(User user) throws Exception{
		JSONObject returnObject = new JSONObject();
		
		returnObject.put("username", user.getUsername());
		returnObject.put("fullname", user.getFullName());
		returnObject.put("role_info", getUserRoleInfo(user));
		returnObject.put("frontEndPermissions", getUsersFrontEndPermissions(user));
		return returnObject.toString();
	}
	

}