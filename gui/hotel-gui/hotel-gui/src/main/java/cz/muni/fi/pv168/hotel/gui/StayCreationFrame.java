/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.hotel.gui;

import cz.muni.fi.pv168.project.GuestManager;
import cz.muni.fi.pv168.project.*;
import cz.muni.fi.pv168.project.StayManager;
import java.math.BigDecimal;
import java.time.LocalDate;
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
public class StayCreationFrame extends javax.swing.JFrame {

    private final static Logger log = LoggerFactory.getLogger(HotelApp.class);
    private static StayManager stayManager = AppCommons.getStayManager();
    private HotelApp context;
    private StaysTableModel staysModel;
    private String action;
    private Stay stay;
    private int rowIndex;

    /**
     * Creates new form StayCreationFrame
     */
    public StayCreationFrame(HotelApp context, Stay stay, int rowIndex, String action) {
        initComponents();
        this.action = action;
        this.stay = stay;
        this.rowIndex = rowIndex;
        this.context = context;
        staysModel = context.getStaysModel();
        jButtonCreateStay.setText(action);
        jComboBoxStayCreation_guests.setModel(context.getGuestsComboBoxModel());
        jComboBoxStayCreation_room.setModel(context.getRoomsComboBoxModel());

        if (stay != null) {
            //TODO picker start
            //TODO picer end exp
            //TODO picer end real
            jComboBoxStayCreation_guests.setSelectedItem(stay.getGuest());
            jComboBoxStayCreation_room.setSelectedItem(stay.getRoom());
            jTextFieldMinibarCosts.setText("" + stay.getMinibarCosts());
        }

        //context.setVisible(false);
        this.setVisible(true);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private class CreateStayWorker extends SwingWorker<Stay, Integer> {

        @Override
        protected Stay doInBackground() throws Exception {
            Stay s = getStayFromCreateForm();
            if (s == null) {
                log.error("Wrong data entered :");
                throw new IllegalArgumentException("Wrong data entered!");
            }
            stayManager.createStay(s);
            return s;
        }

        @Override
        protected void done() {
            try {
                Stay s = get();
                
                staysModel.addStay(s);
                log.info("Stay " + s + " created.");
                StayCreationFrame.this.dispose();
            }  catch (IllegalArgumentException ex) {
                warning(ex.getMessage());
                return;
            } catch (ExecutionException ex) {
                log.error("Exception thrown in doInBackground of CreateStay: " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("doInBackground of CreateRoom interrupted: " + ex.getCause());
                throw new RuntimeException("Operation interrupted.. CreateStay");
            }
        }
    }

    private class UpdateStayWorker extends SwingWorker<Stay, Integer> {

        @Override
        protected Stay doInBackground() throws Exception {
            Stay s = getStayFromCreateForm();
            if (s == null) {
                log.error("Wrong data entered :");
                throw new IllegalArgumentException("Wrong data entered!");
            }
            stayManager.updateStay(s);
            return s;
        }

        @Override
        protected void done() {
            try {
                Stay s = get();
                
                staysModel.updateStay(s, rowIndex);
                log.info("Stay " + s + " updated.");
                StayCreationFrame.this.dispose();
            }  catch (IllegalArgumentException ex) {
                warning(ex.getMessage());
                return;
            } catch (ExecutionException ex) {
                log.error("Exception thrown in doInBackground of UpdateStay: " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("doInBackground of UpdateStay interrupted: " + ex.getCause());
                throw new RuntimeException("Operation interrupted.. UpdateStay");
            }
        }
    }

    private Stay getStayFromCreateForm() {
        //TODO pickers
        LocalDate start = null;
        LocalDate exEnd = null;
        LocalDate rEnd = null;
        
        //expected End date specified and not greater then start date
        if ((exEnd != null) && (start.compareTo(exEnd) == 1)) {
            throw new IllegalArgumentException("Expected end date must be after start date");
        }
        //real end date specified and not greater then start date
        if ((rEnd != null) && (start.compareTo(rEnd) == 1)) {
            throw new IllegalArgumentException("Real end date must be after start date");
        }
        
        BigDecimal minibar = null;
        try{
            minibar = new BigDecimal(jTextFieldMinibarCosts.getText());
            if(minibar.signum() == -1)
                throw new IllegalArgumentException("Price must not be negative.");
        }catch (Exception ex) {
            log.debug("Wrong price entered");
            warning("Price must be a number!");
        }
        
        if (stay == null) {
            stay = new Stay();
        }
        stay.setGuest((Guest) jComboBoxStayCreation_guests.getSelectedItem());
        stay.setRoom((Room) jComboBoxStayCreation_room.getSelectedItem());
        stay.setStartDate(start);
        stay.setExpectedEndDate(exEnd);
        stay.setRealEndDate(rEnd);
        stay.setMinibarCosts(minibar);

        return stay;
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

        jPanelStayCreation = new javax.swing.JPanel();
        jButtonCreateStay = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jTextFieldMinibarCosts = new javax.swing.JTextField();
        jComboBoxStayCreation_room = new javax.swing.JComboBox();
        jComboBoxStayCreation_guests = new javax.swing.JComboBox();
        jTextFieldRealEndDate = new javax.swing.JTextField();
        jTextFieldExpectedEndDate = new javax.swing.JTextField();
        jTextFieldStartDate = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButtonCreateStay.setText("Create");
        jButtonCreateStay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCreateStayActionPerformed(evt);
            }
        });

        jLabel18.setText("Start date:");

        jLabel19.setText("Expected end date:");

        jLabel20.setText("Real end date:");

        jLabel21.setText("Guest:");

        jLabel22.setText("Room:");

        jLabel23.setText("Minibar costs:");

        jComboBoxStayCreation_room.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jComboBoxStayCreation_guests.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jTextFieldRealEndDate.setText("jTextField2");

        jTextFieldExpectedEndDate.setText("jTextField3");

        jTextFieldStartDate.setText("jTextField4");

        javax.swing.GroupLayout jPanelStayCreationLayout = new javax.swing.GroupLayout(jPanelStayCreation);
        jPanelStayCreation.setLayout(jPanelStayCreationLayout);
        jPanelStayCreationLayout.setHorizontalGroup(
            jPanelStayCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelStayCreationLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelStayCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelStayCreationLayout.createSequentialGroup()
                        .addGroup(jPanelStayCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel23)
                            .addComponent(jLabel22)
                            .addComponent(jLabel21))
                        .addGap(50, 50, 50)
                        .addGroup(jPanelStayCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBoxStayCreation_guests, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jComboBoxStayCreation_room, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jTextFieldMinibarCosts)))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelStayCreationLayout.createSequentialGroup()
                        .addGroup(jPanelStayCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel20)
                            .addComponent(jLabel19)
                            .addComponent(jLabel18))
                        .addGap(21, 21, 21)
                        .addGroup(jPanelStayCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextFieldExpectedEndDate, javax.swing.GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE)
                            .addComponent(jTextFieldStartDate, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jTextFieldRealEndDate)))
                    .addGroup(jPanelStayCreationLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButtonCreateStay)))
                .addGap(27, 27, 27))
        );
        jPanelStayCreationLayout.setVerticalGroup(
            jPanelStayCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelStayCreationLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(jPanelStayCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(jTextFieldStartDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanelStayCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(jTextFieldExpectedEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanelStayCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(jTextFieldRealEndDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanelStayCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(jComboBoxStayCreation_guests, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanelStayCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(jComboBoxStayCreation_room, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanelStayCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(jTextFieldMinibarCosts, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 42, Short.MAX_VALUE)
                .addComponent(jButtonCreateStay)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelStayCreation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelStayCreation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonCreateStayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCreateStayActionPerformed
        if (action.equals("Create")) {
            CreateStayWorker w = new CreateStayWorker();
            w.execute();
        } else if (action.equals("Update")) {
            UpdateStayWorker w = new UpdateStayWorker();
            w.execute();
        }

    }//GEN-LAST:event_jButtonCreateStayActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCreateStay;
    private javax.swing.JComboBox jComboBoxStayCreation_guests;
    private javax.swing.JComboBox jComboBoxStayCreation_room;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JPanel jPanelStayCreation;
    private javax.swing.JTextField jTextFieldExpectedEndDate;
    private javax.swing.JTextField jTextFieldMinibarCosts;
    private javax.swing.JTextField jTextFieldRealEndDate;
    private javax.swing.JTextField jTextFieldStartDate;
    // End of variables declaration//GEN-END:variables
}
