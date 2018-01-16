package pk.temp.bm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import pk.temp.bm.component.PermissionService;
import pk.temp.bm.models.Permission;
import pk.temp.bm.models.RolesPermission;
import pk.temp.bm.utilities.GlobalAppUtil;
import pk.temp.bm.utilities.GlobalConstants;

@Service
@Configuration
public class ServletInitializer extends SpringBootServletInitializer {

	@Autowired
	private PermissionService permissionService;

	@Autowired
	private Environment environment;

	private static Logger logger = LoggerFactory.getLogger(ServletInitializer.class.getName());


	@EventListener
	public void onApplicationEvent(ContextRefreshedEvent event) {

		/*
		* Following block try to find a new EndPoint,
		* create its permission, and assign that
		* permission to OEAdmin Role, with roleId=GlobalConstants.OE_ADMIN_DB_ID.
		* -- Abubakar Iqbal
		* */
		ApplicationContext applicationContext = ((ContextRefreshedEvent) event).getApplicationContext();
		applicationContext.getBean(RequestMappingHandlerMapping.class).getHandlerMethods();
		RequestMappingHandlerMapping requestMappingHandlerMapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
		createPermissionIfRequired(requestMappingHandlerMapping);
	}
	
	@PostConstruct
	public void init() throws Exception{
	}
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(BMApplication.class);
	}

	private void createPermissionIfRequired(RequestMappingHandlerMapping requestMappingHandlerMapping	){
		List<String> endPointList = new ArrayList<>();
		for(Map.Entry<?,?> entry : requestMappingHandlerMapping.getHandlerMethods().entrySet()){
			RequestMappingInfo requestMappingInfo = (RequestMappingInfo) entry.getKey();
			String endPointPattern = requestMappingInfo.getPatternsCondition().toString();
			String endPoint = endPointPattern.replaceAll("\\[","")
					.replaceAll("\\]","").replaceAll("\\{.*?\\}", "")
					.replaceAll("/+","/").replaceAll(" ","");
			if(endPoint.contains("||")){
				String[] array = endPoint.split("\\|\\|");
				for(String element : array){
					endPointList.add(element);
				}
			}
			else{
				endPointList.add(endPoint);
			}
		}
		List<String> existingEndPoints = permissionService.getPermissionsByURL(endPointList);
		List<Permission> newPermissions = new ArrayList<>();
		for(String endPoint : endPointList){
			if(!existingEndPoints.contains(endPoint)){
				String camelEndPointName = endPoint.replaceAll("\\/"," ");
				String simpleEndPointName = GlobalAppUtil.decodeCamelCase(camelEndPointName);
				Permission permission = new Permission();
				permission.setPermission(simpleEndPointName);
				permission.setPermissionURL(endPoint);
				newPermissions.add(permission);
			}
		}

		if(!newPermissions.isEmpty()){
			logger.info("New Permissions Found...");
			newPermissions = permissionService.saveAllPermissios(newPermissions);
			List<RolesPermission> rolesPermissionList = new ArrayList<>();
			for(Permission permission : newPermissions){
				logger.info("Added "+permission.getPermissionURL() + " To Admin Role");
				RolesPermission rolesPermission = new RolesPermission();
				rolesPermission.setPermissionId(permission.getId());
				rolesPermission.setRoleId(GlobalConstants.OE_ADMIN_DB_ID);
				rolesPermissionList.add(rolesPermission);
			}
			permissionService.saveAllRolePermissions(rolesPermissionList);
		}
	}

}
