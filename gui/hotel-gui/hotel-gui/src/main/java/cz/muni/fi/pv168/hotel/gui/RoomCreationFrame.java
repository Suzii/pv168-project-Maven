/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.hotel.gui;

import cz.muni.fi.pv168.project.Room;
import cz.muni.fi.pv168.project.RoomManager;
import cz.muni.fi.pv168.project.RoomType;
import java.math.BigDecimal;
import java.time.format.DateTimeParseException;
import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Zuzana
 */
public class RoomCreationFrame extends javax.swing.JFrame {

    private final static Logger log = LoggerFactory.getLogger(HotelApp.class);
    private static RoomManager roomManager = AppCommons.getRoomManager();
    private HotelApp context;
    private RoomsTableModel roomsModel;
    private String action;
    private Room room;
    private int rowIndex;

    /**
     * Creates new form RoomCreationFrame
     */
    public RoomCreationFrame(HotelApp context, Room room, int rowIndex, String action) {
        log.debug("Room creation frame initialized.");
        initComponents();
        this.action = action;
        this.room = room;
        this.rowIndex = rowIndex;
        this.context = context;
        this.roomsModel = context.getRoomsModel();

        if (room != null) {
            jTextFieldRoomNumber.setText(room.getNumber());
            jTextFieldPricePerNight.setText(room.getPricePerNight().toString());
            jTextFieldCapacity.setText("" + room.getCapacity());
            jComboBoxRoomType.setSelectedItem(room.getType());
        }

        jButtonRoomCreate.setText(action);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setVisible(true);
    }

    private class CreateRoomWorker extends SwingWorker<Room, Integer> {

        @Override
        protected Room doInBackground() throws Exception {
            Room r = getRoomFromCreateForm();
            if (r == null) {
                log.error("Wrong data entered.");
                throw new IllegalArgumentException(java.util.ResourceBundle.getBundle("texts").getString("WRONG DATA ENTERED!"));
            }
            roomManager.createRoom(r);
            return r;
        }

        @Override
        protected void done() {
            try {
                Room r = get();

                roomsModel.addRoom(r);
                log.info(java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("texts").getString("ROOM {0} CREATED"), new Object[] {r}));
                context.refreshComboBoxesRooms();
                RoomCreationFrame.this.dispose();
            } catch (IllegalArgumentException ex) {
                warning(ex.getMessage());
                return;
            } catch (ExecutionException ex) {
                log.error("Exception thrown in doInBackground of CreateRoom: " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("doInBackground of CreateRoom interrupted: " + ex.getCause());
                throw new RuntimeException("Operation interrupted.. CreateRoom");
            }
        }
    }

    private class UpdateRoomWorker extends SwingWorker<Room, Integer> {

        @Override
        protected Room doInBackground() throws Exception {
            Room r = getRoomFromCreateForm();
            if (r == null) {
                log.error("Wrong data entered.");
                throw new IllegalArgumentException(java.util.ResourceBundle.getBundle("texts").getString("WRONG DATA ENTERED!"));
            }
            roomManager.updateRoom(r);
            return r;
        }

        @Override
        protected void done() {
            try {
                Room r = get();

                roomsModel.updateRoom(r, rowIndex);
                log.info("Room " + r + " updated");
                RoomCreationFrame.this.dispose();
            } catch (IllegalArgumentException ex) {
                warning(ex.getMessage());
                return;
            } catch (ExecutionException ex) {
                log.error("Exception thrown in doInBackground of UpdateRoom: " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("doInBackground of CreateRoom interrupted: " + ex.getCause());
                throw new RuntimeException("Operation interrupted.. UpdateRoom");
            }
        }
    }

    private Room getRoomFromCreateForm() {
        // TODO add validation, if invalid, return null
        String number = jTextFieldRoomNumber.getText();
        if (number == null || number.trim().length() < 4) {
            warning(java.util.ResourceBundle.getBundle("texts").getString("NUMBER IS REQUIRED, AT LEAST 4 CHARACTERS LONG!"));
            return null;
        }
        int capacity = 0;
        try {
            capacity = Integer.parseInt(jTextFieldCapacity.getText());
            if (capacity <= 0) {
                throw new IllegalArgumentException("arg");
            }
        } catch (Exception ex) {
            log.debug("Wrong capacity entered");
            warning(java.util.ResourceBundle.getBundle("texts").getString("CAPACITY MUST BE A POSITIVE NUMBER!"));
        }
        BigDecimal price = null;
        try{
            price = new BigDecimal(jTextFieldPricePerNight.getText());
            if(price.signum() < 1)
                throw new IllegalArgumentException(java.util.ResourceBundle.getBundle("texts").getString("PRICE MUST BE POSITIVE."));
        }catch (Exception ex) {
            log.debug("Wrong price entered");
            warning(java.util.ResourceBundle.getBundle("texts").getString("PRICE MUST BE A NUMBER!"));
        }
        
        //number conversion must be in try-catch
        if (this.room == null) {
            this.room = new Room();
        }
        room.setNumber(number);
        room.setCapacity(capacity);
        room.setPricePerNight(price);
        room.setType((RoomType) jComboBoxRoomType.getSelectedItem());
        room.setBathroom((Boolean) jRadioButton1.isSelected());
        return room;
    }

    private void warning(String msg) {
        JOptionPane.showMessageDialog(rootPane, msg,
                null, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelRoomCreation = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jTextFieldRoomNumber = new javax.swing.JTextField();
        jButtonRoomCreate = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jTextFieldCapacity = new javax.swing.JTextField();
        jTextFieldPricePerNight = new javax.swing.JTextField();
        jRadioButton1 = new javax.swing.JRadioButton();
        jComboBoxRoomType = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanelRoomCreation.setMinimumSize(new java.awt.Dimension(350, 500));

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("texts"); // NOI18N
        jLabel3.setText(bundle.getString("NUMBER:")); // NOI18N

        jButtonRoomCreate.setText(bundle.getString("CREATE")); // NOI18N
        jButtonRoomCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRoomCreateActionPerformed(evt);
            }
        });

        jLabel9.setText(bundle.getString("CAPACITY:")); // NOI18N

        jLabel10.setText(bundle.getString("PRICE PER NIGHT:")); // NOI18N

        jLabel11.setText(bundle.getString("ROOM TYPE:")); // NOI18N

        jLabel12.setText(bundle.getString("BATHROOM:")); // NOI18N

        jRadioButton1.setText(bundle.getString("INCLUDED")); // NOI18N

        for(RoomType t: RoomType.values()){
            jComboBoxRoomType.addItem(t);
        }

        javax.swing.GroupLayout jPanelRoomCreationLayout = new javax.swing.GroupLayout(jPanelRoomCreation);
        jPanelRoomCreation.setLayout(jPanelRoomCreationLayout);
        jPanelRoomCreationLayout.setHorizontalGroup(
            jPanelRoomCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelRoomCreationLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonRoomCreate)
                .addGap(33, 33, 33))
            .addGroup(jPanelRoomCreationLayout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(jPanelRoomCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelRoomCreationLayout.createSequentialGroup()
                        .addGroup(jPanelRoomCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(17, 17, 17)
                        .addGroup(jPanelRoomCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextFieldRoomNumber, javax.swing.GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE)
                            .addComponent(jTextFieldCapacity)))
                    .addGroup(jPanelRoomCreationLayout.createSequentialGroup()
                        .addGroup(jPanelRoomCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(jPanelRoomCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelRoomCreationLayout.createSequentialGroup()
                                .addGap(17, 17, 17)
                                .addComponent(jTextFieldPricePerNight))
                            .addGroup(jPanelRoomCreationLayout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(jPanelRoomCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jComboBoxRoomType, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(jPanelRoomCreationLayout.createSequentialGroup()
                                        .addComponent(jRadioButton1)
                                        .addGap(0, 188, Short.MAX_VALUE)))))))
                .addContainerGap())
        );
        jPanelRoomCreationLayout.setVerticalGroup(
            jPanelRoomCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelRoomCreationLayout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addGroup(jPanelRoomCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextFieldRoomNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelRoomCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jTextFieldCapacity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelRoomCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jTextFieldPricePerNight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelRoomCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jComboBoxRoomType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelRoomCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jRadioButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 276, Short.MAX_VALUE)
                .addComponent(jButtonRoomCreate)
                .addGap(29, 29, 29))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelRoomCreation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelRoomCreation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonRoomCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRoomCreateActionPerformed
        if (action.equals(java.util.ResourceBundle.getBundle("texts").getString("CREATE"))) {
            CreateRoomWorker w = new CreateRoomWorker();
            w.execute();
        } else if (action.equals(java.util.ResourceBundle.getBundle("texts").getString("UPDATE"))) {
            UpdateRoomWorker w = new UpdateRoomWorker();
            w.execute();
        }
    }//GEN-LAST:event_jButtonRoomCreateActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonRoomCreate;
    private javax.swing.JComboBox jComboBoxRoomType;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanelRoomCreation;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JTextField jTextFieldCapacity;
    private javax.swing.JTextField jTextFieldPricePerNight;
    private javax.swing.JTextField jTextFieldRoomNumber;
    // End of variables declaration//GEN-END:variables
}
