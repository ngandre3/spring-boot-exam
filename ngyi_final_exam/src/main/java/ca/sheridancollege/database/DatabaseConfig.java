package ca.sheridancollege.database;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

/*
 * Name: Yin Tung Ng
 * Student #: 991602581
 * Assignment: Final Exam
 * Course: PROG32758 - Java 3
 */

@Configuration
public class DatabaseConfig {

	// Used in our DatabaseAccess class to submit JDBC Query Strings
	@Bean
	public NamedParameterJdbcTemplate namedParameterJbdcTemplate(DataSource dataSource) {
		return new NamedParameterJdbcTemplate(dataSource);
	}
	
	//returned DataSource object will be inserted into namedParameterJbdcTemplate(), ABOVE
	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.h2.Driver");
		dataSource.setUrl("jdbc:h2:mem:testdb");
		dataSource.setUsername("sa");
		dataSource.setPassword("");
		return dataSource;		
	}
	
	//returned DataSource object will be inserted into namedParameterJbdcTemplate(), ABOVE
	@Bean
	public DataSource loadSchema() {
		return new EmbeddedDatabaseBuilder()
				.setType(EmbeddedDatabaseType.H2)
				.addScript("classpath:securitysetup.sql")
				.addScript("classpath:guessthatmovie.sql")
			//You can add additional .addScript() for multiple SQL files.
				.build();
	}
}
