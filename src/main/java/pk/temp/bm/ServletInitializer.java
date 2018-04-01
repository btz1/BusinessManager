package pk.temp.bm;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@Configuration
public class ServletInitializer extends SpringBootServletInitializer {


	private static Logger logger = LoggerFactory.getLogger(ServletInitializer.class.getName());


	@EventListener
	public void onApplicationEvent(ContextRefreshedEvent event) {
	}
	
	@PostConstruct
	public void init() throws Exception{
	}
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(BMApplication.class);
	}

}
