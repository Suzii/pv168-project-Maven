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
import javax.swing.SwingWorker;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author pato
 */
public class StaysTableModel extends AbstractTableModel {

    private List<Stay> stays = new ArrayList<Stay>();
    private static final int STAYS_PARAMS = 7;

    @Override
    public int getRowCount() {
        return stays.size();
    }

    @Override
    public int getColumnCount() {
        return STAYS_PARAMS;
    }
    
    public Stay getStay(int index){
        return stays.get(index);
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
                return s.getStartDate().format(AppCommons.getDateTimeFormatter());
            case 2:
                return s.getExpectedEndDate().format(AppCommons.getDateTimeFormatter());
            case 3:
                return s.getRealEndDate().format(AppCommons.getDateTimeFormatter());
            case 4:
                return s.getGuest().getName();
            case 5:
                return s.getRoom().getNumber();
            case 6:
                return AppCommons.getNumberFormatter().format(s.getMinibarCosts());
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
                return java.util.ResourceBundle.getBundle("texts").getString("START DATE");
            case 2:
                return java.util.ResourceBundle.getBundle("texts").getString("EXPECTED END DATE");
            case 3:
                return java.util.ResourceBundle.getBundle("texts").getString("REAL END DATE");
            case 4:
                return java.util.ResourceBundle.getBundle("texts").getString("GUEST NAME");
            case 5:
                return java.util.ResourceBundle.getBundle("texts").getString("ROOM NUMBER");
            case 6:
                return java.util.ResourceBundle.getBundle("texts").getString("MINIBAR COSTS");
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
                return String.class;
            case 5:
                return String.class;
            case 6:
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

            case 6:
                s.setMinibarCosts((BigDecimal) aValue);
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
            case 0:
            case 4:
            case 5:
            case 6:
                return false;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    public void updateStay(Stay s, int rowIndex){
        stays.set(rowIndex, s);
        fireTableRowsUpdated(rowIndex, rowIndex);
    }
    
    public void addStay(Stay s) {
        stays.add(s);
        int lastRow = stays.size() - 1;
        fireTableRowsInserted(lastRow, lastRow);
    }

    public void setStays(List<Stay> stays){
        this.stays = stays;
        fireTableDataChanged();
    }
    
    void deleteStay(int rowIndex){
        stays.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    void deleteStays(int[] selectedRows) {
        Integer[] indexes = AppCommons.getSortedDesc(selectedRows);
        for (int i : indexes) {
            deleteStay(i);
        }
    }
}
