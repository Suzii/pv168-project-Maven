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
import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.DERBY;
import org.springframework.transaction.annotation.Transactional;
/**
 *
 * @author Zuzana
 */
@Configuration
@EnableTransactionManagement
public class SpringConfig {
    
    @Bean
    public DataSource dataSource(){
        /*return new EmbeddedDatabaseBuilder()
                .setType(DERBY)
                .addScript("classpath:createTables.sql")
                .addScript("classpath:test-data.sql")
                .build();
        */
        BasicDataSource ds = new BasicDataSource();
        //we will use in memory database
        ds.setUrl("jdbc:derby://localhost:1527/hotel");
        return ds;
    }
    
    @Bean //potřeba pro @EnableTransactionManagement
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean //náš manager, bude obalen řízením transakcí
    public GuestManager guestManager() {
        return new GuestManagerImpl(dataSource());
    }

    @Bean
    //@Transactional
    public RoomManager roomManager() {
        return new RoomManagerImpl(/*new TransactionAwareDataSourceProxy(*/dataSource()/*)*/);
    }

    @Bean
    public StayManager stayManager() {
        StayManagerImpl stayManager = new StayManagerImpl(dataSource());
        //leaseManager.setBookManager(bookManager());
        //leaseManager.setCustomerManager(customerManager());
        return stayManager;
    }
    
}
