package pk.temp.bm.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;

@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

	private static final String RESOURCE_ID = "SHOPISTAN-OMNIENGINE-API";
	
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) {
		resources.resourceId(RESOURCE_ID).stateless(false);
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.
		csrf()
			.disable()
			.authorizeRequests()
			.antMatchers("/encryptExistingPasswords").permitAll()
			.antMatchers("/dashboard/UpdateDashboardOrders").permitAll()
			.antMatchers("/dashboard/UpdateDashboardVisits").permitAll()
			.antMatchers("/orders/downloadBulkOrderFileAndDispatchNote/**").permitAll()
			.antMatchers("/orders/downloadSingleOrderFileAndDispatchNote/**").permitAll()
			.antMatchers("/test").permitAll()
			.antMatchers("/agent/**").permitAll()
			.antMatchers("/inventory/updateInventory").permitAll()
			.antMatchers(HttpMethod.OPTIONS, "**").authenticated()
			.antMatchers(HttpMethod.DELETE, "**").denyAll()
			.antMatchers(HttpMethod.PUT, "**").denyAll()
			.antMatchers(HttpMethod.PATCH, "**").denyAll()
			.antMatchers("/**").authenticated()
		.and()
			.exceptionHandling()
			.accessDeniedHandler(new OAuth2AccessDeniedHandler())
		.and()
	         .headers()
	         .frameOptions()
	         	.disable()
	    .and()
	    	.addFilterAfter(new SimpleCorsFilter(), ChannelProcessingFilter.class)
			.addFilterBefore(new RequestWrapperFilter(), ChannelProcessingFilter.class);
	}

}