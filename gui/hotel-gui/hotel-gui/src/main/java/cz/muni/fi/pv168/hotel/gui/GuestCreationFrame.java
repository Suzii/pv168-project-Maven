/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.hotel.gui;

import cz.muni.fi.pv168.project.Guest;
import cz.muni.fi.pv168.project.GuestManager;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;

import org.jdatepicker.impl.UtilDateModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Zuzana
 */
public class GuestCreationFrame extends javax.swing.JFrame {

    private final static Logger log = LoggerFactory.getLogger(HotelApp.class);
    private static GuestManager guestManager = AppCommons.getGuestManager();
    private HotelApp context;
    private GuestsTableModel guestsModel;
    private Guest guest;
    private String action;
    private int rowIndex;

    /**
     * Creates new form GuestCreationFrame
     */
    public GuestCreationFrame(HotelApp context, Guest guest, int rowIndex, String action) {
        initComponents();
        this.context = context;
        this.guest = guest;
        this.rowIndex = rowIndex;
        this.action = action;
        this.guestsModel = context.getGuestsModel();
        jButtonCreateGuest.setText(action);
        //initialize values for edit
        //beware of NULLpointer exception when converting to string!!!
        if (guest != null) {
            jTextFieldGuestName.setText(guest.getName());
            //TODO picker
            //jTextFieldDateOfBirth.setText(guest.getDateOfBirth().toString());
            jTextFieldEmail.setText(guest.getEmail());
            jTextFieldPassportNumber.setText(guest.getPassportNo());
            jTextFieldPhone.setText(guest.getPhone());
        }

        UtilDateModel model = new UtilDateModel();
        model.setDate(2014, 04, 01);
        // Need this...
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        // Don't know about the formatter, but there it is...
        JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DataLabelFormater());
        datePicker.setVisible(true);
        datePicker.setBounds(100, 100, 100, 100);
        datePicker.getModel().getValue();
        jPanelGuestCreation.add(datePicker);

        /* JFrame f = new JFrame();
         f.add(datePicker);
         f.setVisible(true);*/
        //context.setVisible(false);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setVisible(true);
    }

    private class CreateGuestWorker extends SwingWorker<Guest, Integer> {

        @Override
        protected Guest doInBackground() throws Exception {
            Guest g = getGuestFromCreateForm();
            if (g == null) {
                    log.error("Wrong data entered :");
                throw new IllegalArgumentException("Wrong data entered!");
            }
            guestManager.createGuest(g);
            return g;
        }

        @Override
        protected void done() {
            try {
                Guest g = get();
                
                guestsModel.addGuest(g);
                log.info("Guest " + g + " created");
                context.refreshComboBoxesGuests();
                GuestCreationFrame.this.dispose();
            } catch (IllegalArgumentException ex) {
                warning(ex.getMessage());
                return;
            } catch (ExecutionException ex) {
                log.error("Exception thrown in doInBackground of CreateGuest: " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("doInBackground of CreateGuest interrupted: " + ex.getCause());
                throw new RuntimeException("Operation interrupted.. findAllGuests");
            }
        }
    }

    private class UpdateGuestWorker extends SwingWorker<Guest, Void> {

        @Override
        protected Guest doInBackground() {
            Guest g = getGuestFromCreateForm();
            if (g == null) {
                    log.error("Wrong data entered :");
                throw new IllegalArgumentException("Wrong data entered!");
            }
            guestManager.updateGuest(g);
            return g;
        }

        @Override
        protected void done() {
            try {
                Guest g = get();
                
                guestsModel.updateGuest(g, rowIndex);
                log.info("Guest " + g + " updated");
                GuestCreationFrame.this.dispose();
            } catch (IllegalArgumentException ex) {
                warning(ex.getMessage());
                return;
            } catch (ExecutionException ex) {
                log.error("Exception thrown in doInBackground of UpdateGuest: " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("doInBackground of UpdateGuest interrupted: " + ex.getCause());
                throw new RuntimeException("Operation interrupted.. UpdateGuest");
            }
        }
    }

    private Guest getGuestFromCreateForm() {
        //retrieve data
        String name = jTextFieldGuestName.getText();
        if (name == null || name.trim().length() == 0) {
            warning("Name is required!");
            return null;
        }
        String pass = jTextFieldPassportNumber.getText();
        String email = jTextFieldEmail.getText();
        String phone = jTextFieldPhone.getText();
        //TODO picker
        String dateStr = jTextFieldDateOfBirth.getText();
        LocalDate date;
        try {
            date = LocalDate.parse(dateStr);
        } catch (DateTimeParseException ex) {
            log.debug("Error why parsing date in bad format");
            warning("Wrong date format entered! Supported format is YYYY-MM-DD");
            date = null;
            return null;
        }

        //create guest
        if (this.guest == null) {
            this.guest = new Guest();
        }
        guest.setName(name);
        guest.setPassportNo(pass);
        guest.setEmail(email);
        guest.setPhone(phone);
        guest.setDateOfBirth(date);
        return guest;
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

        jPanelGuestCreation = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextFieldGuestName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextFieldEmail = new javax.swing.JTextField();
        jButtonCreateGuest = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jTextFieldPassportNumber = new javax.swing.JTextField();
        jTextFieldPhone = new javax.swing.JTextField();
        jTextFieldDateOfBirth = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanelGuestCreation.setMinimumSize(new java.awt.Dimension(350, 500));

        jLabel1.setText("Name:");

        jLabel2.setText("Passport number:");

        jButtonCreateGuest.setText("Create");
        jButtonCreateGuest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCreateGuestActionPerformed(evt);
            }
        });

        jLabel4.setText("Email:");

        jLabel5.setText("Phone:");

        jLabel6.setText("Date of birth:");

        jTextFieldDateOfBirth.setText("YYYY-MM-DD");

        javax.swing.GroupLayout jPanelGuestCreationLayout = new javax.swing.GroupLayout(jPanelGuestCreation);
        jPanelGuestCreation.setLayout(jPanelGuestCreationLayout);
        jPanelGuestCreationLayout.setHorizontalGroup(
            jPanelGuestCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelGuestCreationLayout.createSequentialGroup()
                .addGroup(jPanelGuestCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelGuestCreationLayout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addGroup(jPanelGuestCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelGuestCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 98, Short.MAX_VALUE)
                                .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jLabel5))
                        .addGap(18, 18, 18)
                        .addGroup(jPanelGuestCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextFieldPhone)
                            .addComponent(jTextFieldDateOfBirth, javax.swing.GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE)
                            .addComponent(jTextFieldPassportNumber)
                            .addComponent(jTextFieldEmail)
                            .addComponent(jTextFieldGuestName)))
                    .addGroup(jPanelGuestCreationLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonCreateGuest)))
                .addGap(31, 31, 31))
        );
        jPanelGuestCreationLayout.setVerticalGroup(
            jPanelGuestCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGuestCreationLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(jPanelGuestCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextFieldGuestName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanelGuestCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(18, 18, 18)
                .addGroup(jPanelGuestCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextFieldPassportNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanelGuestCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jTextFieldPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanelGuestCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(jTextFieldDateOfBirth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 268, Short.MAX_VALUE)
                .addComponent(jButtonCreateGuest)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelGuestCreation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelGuestCreation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonCreateGuestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCreateGuestActionPerformed
        if (action.equals("Create")) {
            CreateGuestWorker w = new CreateGuestWorker();
            w.execute();
        } else if (action.equals("Update")) {
            UpdateGuestWorker w = new UpdateGuestWorker();
            w.execute();
        }
    }//GEN-LAST:event_jButtonCreateGuestActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCreateGuest;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanelGuestCreation;
    private javax.swing.JTextField jTextFieldDateOfBirth;
    private javax.swing.JTextField jTextFieldEmail;
    private javax.swing.JTextField jTextFieldGuestName;
    private javax.swing.JTextField jTextFieldPassportNumber;
    private javax.swing.JTextField jTextFieldPhone;
    // End of variables declaration//GEN-END:variables
}
