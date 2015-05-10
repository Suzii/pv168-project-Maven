/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.hotel.gui;

import cz.muni.fi.pv168.project.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingWorker;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author pato
 */
public class RoomsTableModel extends AbstractTableModel {

    private List<Room> rooms = new ArrayList<Room>();
    private static final int ROOMS_PARAMS = 6;

    @Override
    public int getRowCount() {
        return rooms.size();
    }

    @Override
    public int getColumnCount() {
        return ROOMS_PARAMS;
    }
    
    public Room getRoom(int index){
        return rooms.get(index);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < 0 || rowIndex >= getRowCount()) {
            throw new IllegalArgumentException("rowIndex");
        }
        Room r = rooms.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return r.getId();
            case 1:
                return r.getNumber();
            case 2:
                return r.getCapacity();
            case 3:
                return AppCommons.getNumberFormatter().format(r.getPricePerNight());
            case 4:
                return r.getType();
            case 5:
                return r.hasBathroom();
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return java.util.ResourceBundle.getBundle("texts").getString("ID");
            case 1:
                return java.util.ResourceBundle.getBundle("texts").getString("NUMBER");
            case 2:
                return java.util.ResourceBundle.getBundle("texts").getString("CAPACITY");
            case 3:
                return java.util.ResourceBundle.getBundle("texts").getString("PRICE PER NIGHT");
            case 4:
                return java.util.ResourceBundle.getBundle("texts").getString("TYPE");
            case 5:
                return java.util.ResourceBundle.getBundle("texts").getString("BATHROOM");
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
                return String.class;
            case 2:
                return Integer.class;
            case 3:
                return BigDecimal.class;
            case 4:
                return RoomType.class;
            case 5:
                return Boolean.class;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Room r = rooms.get(rowIndex);
        switch (columnIndex) {
            case 0:
                r.setId((Long) aValue);
                break;
            case 1:
                r.setNumber((String) aValue);
                break;
            case 2:
                r.setCapacity((Integer) aValue);
                break;
            case 3:
                r.setPricePerNight((BigDecimal) aValue);
                break;
            case 4:
                r.setType((RoomType) aValue);
                break;
            case 5:
                r.setBathroom((Boolean) aValue);
                break;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 0:
                return false;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    public void updateRoom(Room r, int rowIndex){
        rooms.set(rowIndex, r);
        fireTableRowsUpdated(rowIndex, rowIndex);
    }
    
    public void addRoom(Room r) {
        rooms.add(r);
        int lastRow = rooms.size() - 1;
        fireTableRowsInserted(lastRow, lastRow);
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
        fireTableDataChanged();
    }

    void deleteRoom(int rowIndex) {
        rooms.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    void deleteRooms(int[] selectedRows) {
        Integer[] indexes = AppCommons.getSortedDesc(selectedRows);
        for (int i : indexes) {
            deleteRoom(i);
        }
    }
}
