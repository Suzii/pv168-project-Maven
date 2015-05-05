/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.hotel.gui;

import cz.muni.fi.pv168.project.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author pato
 */
public class RoomTableModel extends AbstractTableModel {
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
        if(rowIndex < 0 || rowIndex >= getRowCount()){
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
    
    public void addRoom(Room r){
        rooms.add(r);
    }
    
}
