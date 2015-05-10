/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.hotel.gui;

import cz.muni.fi.pv168.project.Guest;
import cz.muni.fi.pv168.project.GuestManager;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Locale;
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
    private JDatePickerImpl datePicker;
    private DataLabelFormater formater = new DataLabelFormater();

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
        
        datePicker = this.context.setDatePickerBirth();
        datePicker.setVisible(true);
        datePicker.setBounds(142, 208, 200,30);
        datePicker.setLocale(Locale.getDefault());
       // datePicker.getModel().getValue();
        jPanelGuestCreation.add(datePicker);
        //initialize values for edit
        //beware of NULLpointer exception when converting to string!!!
        if (guest != null) {
            jTextFieldGuestName.setText(guest.getName());
            //TODO picker
             LocalDate ld = guest.getDateOfBirth();
             Instant instant = ld.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
             Date time = Date.from(instant);
             Object o = (Object) time;
            datePicker.getModel().setDate(guest.getDateOfBirth().getYear(), guest.getDateOfBirth().getMonthValue(),guest.getDateOfBirth().getDayOfMonth());
            datePicker.getModel().setSelected(true);
            //formater.stringToValue();
           // jTextFieldDateOfBirth.setText(guest.getDateOfBirth().toString());
            jTextFieldEmail.setText(guest.getEmail());
            jTextFieldPassportNumber.setText(guest.getPassportNo());
            jTextFieldPhone.setText(guest.getPhone());
        }



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
                    log.error(java.util.ResourceBundle.getBundle("texts").getString("WRONG DATA ENTERED :"));
                throw new IllegalArgumentException(java.util.ResourceBundle.getBundle("texts").getString("WRONG DATA ENTERED!"));
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
                throw new IllegalArgumentException(java.util.ResourceBundle.getBundle("texts").getString("WRONG DATA ENTERED!"));
            }
            guestManager.updateGuest(g);
            return g;
        }

        @Override
        protected void done() {
            try {
                Guest g = get();
                
                guestsModel.updateGuest(g, rowIndex);
                log.info(java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("texts").getString("GUEST {0} UPDATED"), new Object[] {g}));
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
            warning(java.util.ResourceBundle.getBundle("texts").getString("NAME IS REQUIRED!"));
            return null;
        }
        String pass = jTextFieldPassportNumber.getText();
        String email = jTextFieldEmail.getText();
        String phone = jTextFieldPhone.getText();
        //TODO picker
        
        LocalDate date;
        try {
            Date d = (Date)datePicker.getModel().getValue();
            LocalDate ld = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            String dateStr = ld.toString();
            //log.debug(dateStr);
            
            date = LocalDate.parse(dateStr);
        } catch (DateTimeParseException ex) {
            log.debug("Error why parsing date in bad format");
            warning(java.util.ResourceBundle.getBundle("texts").getString("WRONG DATE FORMAT ENTERED! SUPPORTED FORMAT IS YYYY-MM-DD"));
            date = null;
            return null;
        }catch (NullPointerException ex){
            warning(java.util.ResourceBundle.getBundle("texts").getString("WRONG DATE FORMAT ENTERED! SUPPORTED FORMAT IS YYYY-MM-DD"));
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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanelGuestCreation.setMinimumSize(new java.awt.Dimension(350, 500));

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("texts"); // NOI18N
        jLabel1.setText(bundle.getString("NAME:")); // NOI18N

        jLabel2.setText(bundle.getString("PASSPORT NUMBER:")); // NOI18N

        jButtonCreateGuest.setText(bundle.getString("CREATE")); // NOI18N
        jButtonCreateGuest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCreateGuestActionPerformed(evt);
            }
        });

        jLabel4.setText(bundle.getString("EMAIL:")); // NOI18N

        jLabel5.setText(bundle.getString("PHONE:")); // NOI18N

        jLabel6.setText(bundle.getString("DATE OF BIRTH:")); // NOI18N

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
                            .addComponent(jTextFieldPhone, javax.swing.GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE)
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
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 274, Short.MAX_VALUE)
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
        if (action.equals(java.util.ResourceBundle.getBundle("texts").getString("CREATE"))) {
            CreateGuestWorker w = new CreateGuestWorker();
            w.execute();
        } else if (action.equals(java.util.ResourceBundle.getBundle("texts").getString("UPDATE"))) {
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
    private javax.swing.JTextField jTextFieldEmail;
    private javax.swing.JTextField jTextFieldGuestName;
    private javax.swing.JTextField jTextFieldPassportNumber;
    private javax.swing.JTextField jTextFieldPhone;
    // End of variables declaration//GEN-END:variables
}
