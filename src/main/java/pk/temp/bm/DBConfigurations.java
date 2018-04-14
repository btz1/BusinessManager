package pk.temp.bm;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DBConfigurations {

	@Autowired
	private Environment env;

	
	private static String basePackage = "pk.temp.bm.*";
	
	Properties properties(){
		Properties properties = new Properties();
		properties.put("dynamic-update", "true");
		properties.put("hibernate.dialect", env.getRequiredProperty("spring.jpa.database-platform"));
		properties.put("hibernate.ejb.naming_strategy", env.getRequiredProperty("spring.jpa.properties.hibernate.ejb.naming_strategy"));
		properties.put("hibernate.show_sql", env.getRequiredProperty("spring.jpa.properties.hibernate.show-sql"));
		properties.put("hibernate.format_sql", env.getRequiredProperty("spring.jpa.properties.hibernate.format_sql"));
        properties.put("hibernate.cache.use_second_level_cache",false);
        properties.put("hibernate.cache.use_query_cache",false);
        properties.put("hibernate.cache.region.factory_class",env.getRequiredProperty("spring.jpa.properties.hibernate.cache.region.factory_class"));
		return properties;
	}
	
	HikariConfig configuration(){
		
		HikariConfig hikariConfig = new HikariConfig();

		hikariConfig.setDriverClassName(env.getRequiredProperty("spring.datasource.driverClassName"));
		hikariConfig.setJdbcUrl( env.getRequiredProperty("spring.datasource.url") );
		hikariConfig.setUsername(env.getRequiredProperty("user_db"));
		hikariConfig.setPassword(env.getRequiredProperty("password_db"));
		hikariConfig.setConnectionInitSql("SELECT 1");
		hikariConfig.setMaximumPoolSize(50);
		hikariConfig.setAutoCommit(true);
		hikariConfig.setMaxLifetime(30000);
		hikariConfig.setIdleTimeout(3000);
//		hikariConfig.setTransactionIsolation("TRANSACTION_READ_COMMITTED");
		hikariConfig.setPoolName("master-db-pool");

		hikariConfig.setAllowPoolSuspension(true);
		hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
		hikariConfig.addDataSourceProperty("prepStmtCacheSize", "300");
		hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		hikariConfig.addDataSourceProperty("useServerPrepStmts", "false");
		return hikariConfig;
	}
	@Bean
	public DataSource masterDatasource() {

		HikariDataSource dataSource = new HikariDataSource(configuration());
		return dataSource;
	}
	
	@Bean
	public LocalSessionFactoryBean SessionFactory() {
		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
		
		sessionFactory.setDataSource(masterDatasource());
		sessionFactory.setPackagesToScan( basePackage );
		sessionFactory.setHibernateProperties(properties());
		
		return sessionFactory;
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		
		LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
		
		entityManagerFactoryBean.setDataSource(masterDatasource());
		entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		entityManagerFactoryBean.setPackagesToScan( basePackage );
		entityManagerFactoryBean.setJpaProperties(properties());

		return entityManagerFactoryBean;
	}

    @Bean
    public JpaTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return transactionManager;
    }


}
