/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.hotel.gui;

import cz.muni.fi.pv168.project.GuestManager;
import cz.muni.fi.pv168.project.RoomManager;
import cz.muni.fi.pv168.project.StayManager;
import cz.muni.fi.pv168.project.common.SpringConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 *
 * @author Zuzana
 */
public abstract class AppCommons {

    protected static ApplicationContext appContext = new AnnotationConfigApplicationContext(SpringConfig.class);
    protected static GuestManager guestManager = appContext.getBean("guestManager", GuestManager.class);
    protected static RoomManager roomManager = appContext.getBean("roomManager", RoomManager.class);
    protected static StayManager stayManager = appContext.getBean("stayManager", StayManager.class);

    public static ApplicationContext getAppContext() {
        return appContext;
    }

    public static GuestManager getGuestManager() {
        return guestManager;
    }


    public static RoomManager getRoomManager() {
        return roomManager;
    }

    public static StayManager getStayManager() {
        return stayManager;
    }

}
