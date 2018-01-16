package pk.temp.bm.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import pk.temp.bm.repositories.UserRepository;
import pk.temp.bm.models.User;

@Service
public class MyUserDetailService implements UserDetailsService {

	@Autowired
	private UserRepository userRepo;

	@Override
	public MyUserDetail loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepo.findByUsernameAndEnabled(username,true);
		if (user != null) {
			MyUserDetail userDetail = new MyUserDetail(user.getUsername(), user.getPassword(), user.getFullName(),user.getPhone(), 
					user.getEnabled(), true, true, true, null);
			return userDetail;
		} else {
			throw new UsernameNotFoundException("User Name is not found");
		}
	}

}
