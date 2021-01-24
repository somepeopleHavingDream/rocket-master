package org.rocket.pay.a.config.mybatis;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import javax.sql.DataSource;
import java.util.Objects;

/**
 * @author yangxin
 * 2020/07/30 21:21
 */
@Configuration
public class MybatisDataSourceConfig {

    private final DataSource dataSource;

    @Autowired
    public MybatisDataSourceConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactoryBean() {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        // 添加XML目录
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            bean.setMapperLocations(resolver.getResources("classpath*:org/rocket/pay/a/mapping/*.xml"));
            SqlSessionFactory sqlSessionFactory = bean.getObject();
            Objects.requireNonNull(sqlSessionFactory).getConfiguration().setCacheEnabled(Boolean.TRUE);

            return sqlSessionFactory;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
