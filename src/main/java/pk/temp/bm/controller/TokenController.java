package pk.temp.bm.controller;

import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
public class TokenController {

	
	@Resource(name="tokenServices")
	ConsumerTokenServices tokenServices;
	     
	@RequestMapping(value = "/oauth/token/revoke", method = RequestMethod.POST)
	public @ResponseBody boolean revoke(@RequestParam("token") String token) {
		return tokenServices.revokeToken(token);
	}
}