/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.hotel.gui;

import static cz.muni.fi.pv168.hotel.gui.HotelApp.guestManager;
import cz.muni.fi.pv168.project.*;
import cz.muni.fi.pv168.project.common.SpringConfig;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ExecutionException;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.AbstractTableModel;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 *
 * @author Zuzana
 */
public class GuestsTableModel extends AbstractTableModel {

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

    public Guest getGuest(int index) {
        return guests.get(index);
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

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return Integer.class;
            case 1:
            case 2:
            case 3:
            case 4:
                return String.class;
            case 5:
                return LocalDate.class;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Guest g = guests.get(rowIndex);
        switch (columnIndex) {
            case 0:
                g.setId((Long) aValue);
                break;
            case 1:
                g.setName((String) aValue);
                break;
            case 2:
                g.setEmail((String) aValue);
                break;
            case 3:
                g.setPassportNo((String) aValue);
                break;
            case 4:
                g.setPhone((String) aValue);
                break;
            case 5:
                g.setDateOfBirth((LocalDate) aValue);
                break;
            default:
                throw new IllegalArgumentException("columnIndex");
        }

        UpdateGuestSwingWorker updateGuestSW = new UpdateGuestSwingWorker(g);
        updateGuestSW.execute();
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 3:
            case 2:
            case 4:
                return true;
            case 0:
            case 1:
            case 5:
                return false;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    public void addGuest(Guest g) {
        guests.add(g);
        int lastRow = guests.size() - 1;
        fireTableRowsInserted(lastRow, lastRow);
    }

    public void deleteGuest(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= getRowCount()) {
            throw new IllegalArgumentException("rowIndex");
        }
        guests.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    public void setGuests(List<Guest> guests) {
        this.guests = guests;
        fireTableDataChanged();

    }

    void deleteGuests(int[] indexes) {
        for (int i : indexes) {
            guests.remove(i);
        }
        fireTableRowsDeleted(indexes[0], indexes[indexes.length - 1]);
    }

    private class UpdateGuestSwingWorker extends SwingWorker<Void, Void> {

        private final Guest guest;

        public UpdateGuestSwingWorker(Guest g) {
            guest = g;
        }

        @Override
        protected Void doInBackground() {
            guestManager.updateGuest(guest);
            return null;
        }
    }
}
