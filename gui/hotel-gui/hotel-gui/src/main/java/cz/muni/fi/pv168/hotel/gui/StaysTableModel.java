/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.hotel.gui;

import cz.muni.fi.pv168.project.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author pato
 */
public class StaysTableModel extends AbstractTableModel {

    private List<Stay> stays = new ArrayList<Stay>();
    private static final int STAYS_PARAMS = 9;

    @Override
    public int getRowCount() {
        return stays.size();
    }

    @Override
    public int getColumnCount() {
        return STAYS_PARAMS;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < 0 || rowIndex >= getRowCount()) {
            throw new IllegalArgumentException("rowIndex");
        }
        Stay s = stays.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return s.getId();
            case 1:
                return s.getStartDate();
            case 2:
                return s.getExpectedEndDate();
            case 3:
                return s.getRealEndDate();
            case 4:
                return s.getGuest().getId();
            case 5:
                return s.getGuest().getName();
            case 6:
                return s.getRoom().getId();
            case 7:
                return s.getRoom().getNumber();
            case 8:
                return s.getMinibarCosts();
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
                return "Start date";
            case 2:
                return "Expected end date";
            case 3:
                return "Real end date";
            case 4:
                return "Guest id";
            case 5:
                return "Guest name";
            case 6:
                return "Room id";
            case 7:
                return "Room number";
            case 8:
                return "Minibar costs";
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
                return LocalDate.class;
            case 4:
                return Integer.class;
            case 5:
                return String.class;
            case 6:
                return Integer.class;
            case 7:
                return String.class;
            case 8:
                return BigDecimal.class;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Stay s = stays.get(rowIndex);
        switch (columnIndex) {
            case 0:
                s.setId((Long) aValue);
                break;
            case 1:
                s.setStartDate((LocalDate) aValue);
                break;
            case 2:
                s.setExpectedEndDate((LocalDate) aValue);
                break;
            case 3:
                s.setRealEndDate((LocalDate) aValue);
                break;

            case 8:
                s.setMinibarCosts((BigDecimal) aValue);
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
            case 8:
                return true;
            case 0:
            case 4:
            case 5:
            case 6:
            case 7:
                return false;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    public void addStays(List<Stay> stays) {
        //System.out.println("Adding all rooms of size " + rooms.size());
        for (Stay s : stays) {
            addStay(s);
        }
    }

    public void addStay(Stay s) {
        //System.out.println(r);
        stays.add(s);
        int lastRow = stays.size() - 1;
        fireTableRowsInserted(lastRow, lastRow);
    }

    public void setStays(List<Stay> stays) {
        this.stays = stays;
        fireTableDataChanged();
    }
}
