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
                return r.getPricePerNight();
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
                return "Id";
            case 1:
                return "Number";
            case 2:
                return "Capacity";
            case 3:
                return "Price per night";
            case 4:
                return "Type";
            case 5:
                return "Bathroom";
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
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                return true;
            case 0:
                return false;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    public void addRooms(List<Room> rooms) {
        System.out.println("Adding all rooms of size " + rooms.size());
        for (Room r : rooms) {
            addRoom(r);
        }
    }

    public void addRoom(Room r) {
        System.out.println(r);
        rooms.add(r);
        int lastRow = rooms.size() - 1;
        fireTableRowsInserted(lastRow, lastRow);
    }

}
