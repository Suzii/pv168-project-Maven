/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.project;

import cz.muni.fi.pv168.project.common.SpringConfig;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.apache.derby.impl.tools.sysinfo.Main;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 *
 * @author Zuzana
 */
public class HotelProject {

    protected static ApplicationContext appContext = new AnnotationConfigApplicationContext(SpringConfig.class);
    protected static GuestManager guestManager = appContext.getBean("guestManager", GuestManager.class);
    protected static RoomManager roomManager = appContext.getBean("roomManager", RoomManager.class);
    protected static StayManager stayManager = appContext.getBean("stayManager", StayManager.class);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        /* DataSource dataSource = new DataSource();
         dataSource.setConnectionProperties("create=true");
         Properties myconf = new Properties();
         myconf.load(Main.class.getResourceAsStream("/conf.properties"));

         DataSource ds = new DataSource(); 
         ds.setUrl(myconf.getProperty("jdbc.url"));
         ds.setUsername(myconf.getProperty("jdbc.user"));
         ds.setPassword(myconf.getProperty("jdbc.password"));*/

        //print(stayManager.findStaysByDate(LocalDate.of(2015,1,1), LocalDate.of(2015,1,2)));
        //print(stayManager.findStaysForRoomByDate(roomManager.getRoomById(new Long(4)), LocalDate.of(2015,1,1)));
        //print(stayManager.findGuestsForRoomByDate(roomManager.getRoomById(new Long(4)), LocalDate.of(2015,1,1)));
        //print(stayManager.findRoomsForGuestByDate(guestManager.getGuestById(new Long(5)), LocalDate.of(2015,1,1)));
        //print(stayManager.findStayingGuestsByDate(LocalDate.of(2015,1,2)));
        print(stayManager.findFreeRoomsByDateAndLen(LocalDate.of(1, 3,1), 1));
        //print(stayManager.findFreeRoomsByDate(LocalDate.of(1, 3,1)));
       
    }
    
    private static void print(List<?> list){
        for(Object o: list){
            System.out.println(o);
        }
    }

}
