package mx.gob.imss.cit.pmc.sisat.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class PersistenceContextOracle {

	@Bean(destroyMethod = "close")
	DataSource dataSource(Environment env) {
		HikariConfig dataSourceConfig = new HikariConfig();

		dataSourceConfig.setDriverClassName("oracle.jdbc.OracleDriver");
		dataSourceConfig.setJdbcUrl(env.getRequiredProperty("oracleUrl"));
		dataSourceConfig.setUsername(env.getRequiredProperty("oracleUsername"));
		dataSourceConfig.setPassword(env.getRequiredProperty("oraclePassword"));

		return new HikariDataSource(dataSourceConfig);
	}

	@Bean
	NamedParameterJdbcTemplate jdbcTemplateOracle(DataSource dataSource) {
		return new NamedParameterJdbcTemplate(dataSource);
	}
}
