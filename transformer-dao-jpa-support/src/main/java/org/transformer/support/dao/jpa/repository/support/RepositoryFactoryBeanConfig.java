package org.transformer.support.dao.jpa.repository.support;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * 配置自定义的JPA的RepositoryFactoryBeanClass. 
 * repositoryFactoryBeanClass = BaseRepositoryFactoryBean.class
 * basePackages 配置
 */
@Configuration
@EnableJpaRepositories(repositoryFactoryBeanClass = BaseRepositoryFactoryBean.class,
    basePackages = { "org.transformer.*.dao" })
public class RepositoryFactoryBeanConfig {

}
