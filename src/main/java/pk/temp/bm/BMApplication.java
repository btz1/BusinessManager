package pk.temp.bm;

import java.util.concurrent.Executor;

import javax.servlet.MultipartConfigElement;

import javafx.application.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class BMApplication extends SpringBootServletInitializer implements AsyncConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(BMApplication.class, args);
	}

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(Application.class);
    }


    @Bean
	MultipartConfigElement multipartConfigElement() {
		MultipartConfigFactory factory = new MultipartConfigFactory();
		factory.setMaxFileSize("200MB");
		factory.setMaxRequestSize("200MB");
		return factory.createMultipartConfig();
	}
	
	@Override
	public Executor getAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(10);
		executor.setThreadNamePrefix("OEAsyncTasks-");
		executor.initialize();
		return executor;
	}


	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return new AsyncCustomExceptionHandler();
	}


}

class AsyncCustomExceptionHandler implements AsyncUncaughtExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(AsyncCustomExceptionHandler.class.getName());

	@Override
	public void handleUncaughtException(Throwable arg0, java.lang.reflect.Method arg1, Object... arg2) {
		logger.error("Exception thrown info:");
		for (Object param : arg2) {
			logger.error("\tParameter Value: " + param);
		}
	}

}

