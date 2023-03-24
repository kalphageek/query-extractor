package me.kalpha.jdbctemplate.common;

import com.zaxxer.hikari.HikariDataSource;
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
        entityManagerFactoryRef = "catalogEntityManagerFactory",
        transactionManagerRef = "catalogTransactionManager",
        basePackages = {"me.kalpha.jdbctemplate.catalog"}//repositories
)
@EnableTransactionManagement
public class CatalogDataSourceConfig {
    private final JpaProperties jpaProperties;
    private final HibernateProperties hibernateProperties;

    @Autowired
    public CatalogDataSourceConfig(JpaProperties jpaProperties, HibernateProperties hibernateProperties) {
        this.jpaProperties = jpaProperties;
        this.hibernateProperties = hibernateProperties;
    }
    @Bean
    @Primary
    @ConfigurationProperties("app.datasource.catalog")
    public DataSourceProperties catalogDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    @ConfigurationProperties("app.datasource.catalog.hikari")
    public DataSource catalogDataSource() {
        return catalogDataSourceProperties().initializeDataSourceBuilder()
                .type(HikariDataSource.class).build();
    }

    /**
     * CATALOG DB EntityManager Setup
     * @param builder
     * @return
     */
    @Primary
    @Bean(name = "catalogEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean catalogEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        hibernateProperties.setDdlAuto("create");
        jpaProperties.setDatabasePlatform("org.hibernate.dialect.H2Dialect");

        var properties = hibernateProperties.determineHibernateProperties(
                jpaProperties.getProperties(), new HibernateSettings());
        return builder
                .dataSource(catalogDataSource())
                .properties(properties)
// em.createNativeQuery를 사용하는 경우에 만 필요
//                .persistenceUnit(Constants.CATALOG_UNIT_NAME)
                .packages("me.kalpha.jdbctemplate.catalog")//entities
                .build();
    }
    @Primary
    @Bean
    public PlatformTransactionManager catalogTransactionManager(
            final @Qualifier("catalogEntityManagerFactory") LocalContainerEntityManagerFactoryBean catalogEntityManagerFactory) {
        return new JpaTransactionManager(catalogEntityManagerFactory.getObject());
    }
}