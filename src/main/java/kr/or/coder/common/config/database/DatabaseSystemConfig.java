package kr.or.coder.common.config.database;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.zaxxer.hikari.HikariDataSource;

@Configuration(proxyBeanMethods = false)
public class DatabaseSystemConfig {
    
    @Autowired
	private ConfigurableEnvironment environment;
	
	@Bean(name = "systemDataSourceProperties")
	@ConfigurationProperties(prefix = "database.datasource.system")
	public DataSourceProperties mssqlHikariConfig() {
		return new DataSourceProperties();
	}
	
	@Bean(name = "dataSourceSystem")
	@ConfigurationProperties(prefix = "database.datasource.system.hikari")
	public HikariDataSource dataSourceMssql(@Qualifier("systemDataSourceProperties") DataSourceProperties properties) {
		return properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
	}

	@Bean(name = "sqlSessionFactorySystem")
	public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSourceSystem") HikariDataSource dataSource) throws Exception {
		
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(dataSource);
		sqlSessionFactoryBean.setVfs(SpringBootVFS.class);
		
		ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
		sqlSessionFactoryBean.setConfigLocation(resourcePatternResolver.getResource(environment.getProperty("database.datasource.system.config-location")));
		sqlSessionFactoryBean.setMapperLocations(resourcePatternResolver.getResources(environment.getProperty("database.datasource.system.mapper-location")));
		
		return sqlSessionFactoryBean.getObject();
	}
	
	@Bean(name = "sqlSessionTemplateSystem")
	public SqlSessionTemplate sqlSessionTemplate(@Qualifier("sqlSessionFactorySystem") SqlSessionFactory sqlSessionFactory) throws Exception {
		return new SqlSessionTemplate(sqlSessionFactory);
	}
	
	@Bean(name = "txManagerSystem")
	@Primary
	public DataSourceTransactionManager transactionManager(@Qualifier("dataSourceSystem") DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}
}
