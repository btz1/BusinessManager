package pk.temp.bm.component;

import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import pk.temp.bm.models.Role;
import pk.temp.bm.models.User;
import pk.temp.bm.repositories.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleServices roleServices;
	
	public User createUser(String id, String email, String name, String password, String phone, Boolean enabled) {

		User user = userRepository.findByUsername(email);
		if (user != null && ( id == null || "".equals(id) || Integer.valueOf(id) != user.getId()) ){
			user =  null;
		}else{
			try{
				if( null == user ){
					user = new User();
					user.setPassword(new BCryptPasswordEncoder().encode(password));
				}
				else if( !user.getPassword().equals(password)){
					user.setPassword(new BCryptPasswordEncoder().encode(password));
				}
				
				user.setFullName(name);
				user.setUsername(email);
				user.setEnabled(enabled);
				user.setPhone(phone);
				userRepository.save(user);
				
			}catch(Exception es){
				user = null;
			}
		}
		return user;
	}
	
	public JSONObject getUser() throws JsonProcessingException, ParseException {

		List<User> user = (List<User>) userRepository.findAll();
	/*	boolean last = user.isLast();
		List<User> userList = user.getContent();*/

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		
		String result = objectMapper.writeValueAsString(user);
		JSONArray array = (JSONArray) new JSONParser().parse(result);

		for (int i = 0; i < user.size(); i++) {
			List<Role> role = roleServices.getUserRoles(user.get(i));
			JSONObject a = (JSONObject) array.get(i);
			a.put("role", role);
			array.set(i, a);
		}

		JSONObject response = new JSONObject();
		response.put("content", array);
//		response.put("last", last);
		return response;
	}

}
