/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.hotel.gui;

import cz.muni.fi.pv168.project.*;
import cz.muni.fi.pv168.project.common.SpringConfig;
import java.util.*;
import javax.swing.table.AbstractTableModel;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 *
 * @author Zuzana
 */
public class GuestsTableModel extends AbstractTableModel {

    ApplicationContext appContext = new AnnotationConfigApplicationContext(SpringConfig.class);
    GuestManager guestManager = appContext.getBean("guestManager", GuestManager.class);
    private List<Guest> guests = new ArrayList<Guest>();
    private static final int GUESTS_PARAMS = 6;

    @Override
    public int getRowCount() {
        return guests.size();
    }

    @Override
    public int getColumnCount() {
        return GUESTS_PARAMS;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < 0 || rowIndex >= getRowCount()) {
            throw new IllegalArgumentException("rowIndex");
        }
        Guest g = guests.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return g.getId();
            case 1:
                return g.getName();
            case 2:
                return g.getEmail();
            case 3:
                return g.getPassportNo();
            case 4:
                return g.getPhone();
            case 5:
                return g.getDateOfBirth();
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return "Id";
            case 1:
                return "Name";
            case 2:
                return "Email";
            case 3:
                return "Passport No.";
            case 4:
                return "Phone";
            case 5:
                return "Date of birth";
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    public void addGuest(Guest g) {
        guestManager.createGuest(g);
        guests.add(g);
    }

}
