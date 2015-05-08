package cz.muni.fi.pv168.project.common;

import cz.muni.fi.pv168.project.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.DERBY;
import org.springframework.transaction.annotation.Transactional;
/**
 *
 * @author Zuzana
 */
@Configuration
@EnableTransactionManagement
@PropertySource(value = { "classpath:derby.properties" })
public class SpringConfig {
    
    @Autowired
    private Environment environment;
    
    @Bean
    public DataSource dataSource(){
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl(environment.getRequiredProperty("derby.url"));
        return ds;
    }
    
    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean
    public GuestManager guestManager() {
        return new GuestManagerImpl(dataSource());
    }

    @Bean
    public RoomManager roomManager() {
        return new RoomManagerImpl(dataSource());
    }

    @Bean
    public StayManager stayManager() {
        StayManagerImpl stayManager = new StayManagerImpl(dataSource());
        return stayManager;
    }
    
}
