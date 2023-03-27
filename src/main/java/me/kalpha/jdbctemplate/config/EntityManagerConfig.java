package me.kalpha.jdbctemplate.config;

import me.kalpha.jdbctemplate.common.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * DB별 EntityManager를 PersistenceContext Named Bean으로 등록한다.
 */
@Configuration
public class EntityManagerConfig {
    @Autowired
    @PersistenceContext(unitName = Constants.BATCH_UNIT_NAME)
    EntityManager batchEntityManager;

    @Autowired
    @PersistenceContext(unitName = Constants.EHUB_UNIT_NAME)
    EntityManager ehubEntityManager;

    @Bean(name = Constants.SYS_BATCH)
    public EntityManager getBatchEntityManager() {
        return batchEntityManager;
    }

    @Bean(name = Constants.SYS_EHUB)
    public EntityManager getEhubEntityManager() {
        return ehubEntityManager;
    }
}
