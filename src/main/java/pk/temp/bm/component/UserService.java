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

import pk.temp.bm.models.User;
import pk.temp.bm.repositories.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;
	
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

}
