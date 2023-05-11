package me.kalpha.jdbctemplate.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import me.kalpha.jdbctemplate.common.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        entityManagerFactoryRef = Constants.BATCH_UNIT_NAME,
        transactionManagerRef = "batchTransactionManager"
)
@EnableTransactionManagement
@RequiredArgsConstructor
public class BatchDataSourceConfig {
    private final JpaProperties jpaProperties;
    private final HibernateProperties hibernateProperties;

    @Bean
    @ConfigurationProperties("app.datasource.batch")
    public DataSourceProperties batchDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = Constants.SYS_BATCH)
    @ConfigurationProperties("app.datasource.batch.hikari")
    public DataSource batchDataSource() {
        return batchDataSourceProperties().initializeDataSourceBuilder()
                .type(HikariDataSource.class).build();
    }

    /**
     * BATCH DB EntityManager Setup
     * @param builder
     * @return
     */
    @Bean(name = Constants.BATCH_UNIT_NAME)
    public LocalContainerEntityManagerFactoryBean batchEntityManagerFactory(EntityManagerFactoryBuilder builder) {
//        hibernateProperties.setDdlAuto("none");
        jpaProperties.setDatabasePlatform("org.hibernate.dialect.H2Dialect");
        var properties = hibernateProperties.determineHibernateProperties(
                jpaProperties.getProperties(), new HibernateSettings());
        return builder
                .dataSource(batchDataSource())
                .properties(properties)
//                .persistenceUnit(Constants.BATCH_UNIT_NAME)
                .packages("me.kalpha.jdbctemplate.batch")//entities
                .build();
    }

    @Bean
    public PlatformTransactionManager batchTransactionManager(
            final @Qualifier(Constants.BATCH_UNIT_NAME) LocalContainerEntityManagerFactoryBean batchEntityManagerFactory) {
        return new JpaTransactionManager(batchEntityManagerFactory.getObject());
    }
}