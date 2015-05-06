/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.hotel.gui;

import cz.muni.fi.pv168.project.*;
import java.util.*;
import cz.muni.fi.pv168.project.common.SpringConfig;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.concurrent.ExecutionException;
import javax.management.RuntimeErrorException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 *
 * @author Zuzana
 */
public class HotelApp extends javax.swing.JFrame {
    
    private final static Logger log = LoggerFactory.getLogger(HotelApp.class);
    protected static GuestManager guestManager = AppCommons.getGuestManager();
    protected static RoomManager roomManager = AppCommons.getRoomManager();
    protected static StayManager stayManager = AppCommons.getStayManager();
    protected GuestsTableModel guestsModel;
    protected RoomsTableModel roomsModel;
    protected StaysTableModel staysModel;
    //find all workers
    private FindAllGuestsWorker findAllGuestsWorker;
    private FindAllRoomsWorker findAllRoomsWorker;
    private FindAllStaysWorker findAllStaysWorker;

    /**
     * Creates new form HotelApp
     */
    public HotelApp() {
        initComponents();

        //init guest table
        guestsModel = (GuestsTableModel) jTableGuests.getModel();
        findAllGuestsWorker = new FindAllGuestsWorker();
        findAllGuestsWorker.execute();

        //init room table
        roomsModel = (RoomsTableModel) jTableRooms.getModel();
        findAllRoomsWorker = new FindAllRoomsWorker();
        findAllRoomsWorker.execute();

        //init stay table
        staysModel = (StaysTableModel) jTableStays.getModel();
        findAllStaysWorker = new FindAllStaysWorker();
        findAllStaysWorker.execute();
    }

    // ********************* WORKERS FOR FINDING ALL *****************************
    private class FindAllGuestsWorker extends SwingWorker<List<Guest>, Integer> {
        
        @Override
        protected List<Guest> doInBackground() throws Exception {
            List<Guest> result;
            result = guestManager.findAllGuests();
            return result;
        }
        
        @Override
        protected void done() {
            try {
                log.debug("Setting guestsModel to all guests. Size: " + get().size());
                guestsModel.setGuests(get());
            } catch (ExecutionException ex) {
                log.error("Exception thrown in doInBackground of FindAlGuests: " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("doInBackground of FindAlGuests interrupted: " + ex.getCause());
                throw new RuntimeException("Operation interrupted.. findAllGuests");
            }
        }
    }
    
    private class FindAllRoomsWorker extends SwingWorker<List<Room>, Integer> {
        
        @Override
        protected List<Room> doInBackground() throws Exception {
            List<Room> result;
            result = roomManager.findAllRooms();
            return result;
        }
        
        @Override
        protected void done() {
            try {
                log.debug("Setting roomsModel to all rooms. Size: " + get().size());
                roomsModel.setRooms(get());
            } catch (ExecutionException ex) {
                log.error("Exception thrown in doInBackground of FindAlGuests: " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("doInBackground of FindAlRooms interrupted: " + ex.getCause());
                throw new RuntimeException("Operation interrupted.. findAllRooms");
            }
        }
    }
    
    private class FindAllStaysWorker extends SwingWorker<List<Stay>, Integer> {
        
        @Override
        protected List<Stay> doInBackground() throws Exception {
            List<Stay> result;
            result = stayManager.findAllStays();
            return result;
        }
        
        @Override
        protected void done() {
            try {
                log.debug("Setting staysModel to all stays. Size: " + get().size());
                staysModel.setStays(get());
            } catch (ExecutionException ex) {
                log.error("Exception thrown in doInBackground of FindAlStays: " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("doInBackground of FindAlStays interrupted: " + ex.getCause());
                throw new RuntimeException("Operation interrupted.. findAllStays");
            }
        }
    }

    // ********************* WORKERS FOR CREATION*****************************
    private class CreateGuestWorker extends SwingWorker<Guest, Integer> {
        
        @Override
        protected Guest doInBackground() throws Exception {
            Guest g = getGuestFromCreateForm();
            try {
                guestManager.createGuest(g);
                return g;
            } catch (Exception ex) {
                log.error("Exception thrown in doInBackground of CreateGuest: " + ex.getCause());
                throw ex;
            }
        }
        
        @Override
        protected void done() {
            try {
                
                jTextFieldGuestName.setText("");
                jTextFieldEmail.setText("");
                jTextFieldPassportNumber.setText("");
                jTextFieldPhone.setText("");
                //TODO delete all other fields

                guestsModel.addGuest(get());
                jFrameGuestCreation.setVisible(false);
            } catch (DateTimeParseException ex) {
                //do not close window

            } catch (ExecutionException ex) {
                log.error("Exception thrown in doInBackground of CreateGuest: " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("doInBackground of CreateGuest interrupted: " + ex.getCause());
                throw new RuntimeException("Operation interrupted.. findAllGuests");
            }
        }
    }

// ********************* WORKERS FOR DELETE*****************************
    private class DeleteGuestWorker extends SwingWorker<Integer, Void> {
        
        @Override
        protected Integer doInBackground() {
            int selectedRow = jTableGuests.getSelectedRow();
            if (selectedRow >= 0) {
                Guest g = guestsModel.getGuest(selectedRow);
                guestManager.deleteGuest(g);
                return selectedRow;
            }
            return null;
        }
        
        @Override
        protected void done() {
            try {
                Integer index = get();
                if (index != null) {
                    guestsModel.deleteGuest(index);
                    
                }
            } catch (ExecutionException ex) {
                //TODO
            } catch (InterruptedException ex) {
                //TODO
            }
        }
    }

// ********************* WORKERS FOR GUEST UTIL *****************************
    private class FindGuestByNameWorker extends SwingWorker<List<Guest>, Integer> {
        
        private String name;
        
        public FindGuestByNameWorker(String name) {
            this.name = name;
        }
        
        @Override
        protected List<Guest> doInBackground() throws Exception {
            List<Guest> result;
            log.debug("WORKER: Searching for guests: " + name);
            result = guestManager.findGuestByName(name);
            return result;
        }
        
        @Override
        protected void done() {
            try {
                log.debug("Setting guestsModel to guests with name " + name + "Size: " + get().size());
                guestsModel.setGuests(get());
                jTextFieldSearchGuest.setText("");
            } catch (ExecutionException ex) {
                log.error("Exception thrown in doInBackground of FindGuestByName: " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("doInBackground of FindGuestByName interrupted: " + ex.getCause());
                throw new RuntimeException("Operation interrupted.. FindGuestByName");
            }
        }
    }
    
    private class FindTop3GuestsWorker extends SwingWorker<List<Guest>, Integer> {
        
        @Override
        protected List<Guest> doInBackground() throws Exception {
            List<Guest> result;
            result = stayManager.findTop3Guests();
            return result;
        }
        
        @Override
        protected void done() {
            try {
                guestsModel.setGuests(get());
            } catch (ExecutionException ex) {
                log.error("Exception thrown in doInBackground of FindTop3Guests: " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("doInBackground of FindTop3Guests interrupted: " + ex.getCause());
                throw new RuntimeException("Operation interrupted.. FindTop3Guests");
            }
        }
    }
    
    private class FindStayingGuestsByDateWorker extends SwingWorker<List<Guest>, Integer> {
        
        private LocalDate date;
        
        public FindStayingGuestsByDateWorker(LocalDate date) {
            this.date = date;
        }
        
        @Override
        protected List<Guest> doInBackground() throws Exception {
            List<Guest> result;
            result = stayManager.findStayingGuestsByDate(date);
            return result;
        }
        
        @Override
        protected void done() {
            try {
                guestsModel.setGuests(get());
            } catch (ExecutionException ex) {
                log.error("Exception thrown in doInBackground of FindStayingGuestsByDate: " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("doInBackground of FindStayingGuestsByDate interrupted: " + ex.getCause());
                throw new RuntimeException("Operation interrupted.. FindStayingGuestsByDate");
            }
        }
    }
    
    private class FindGuestsForRoomByDateWorker extends SwingWorker<List<Guest>, Integer> {
        
        private Room room;
        private LocalDate date;
        
        public FindGuestsForRoomByDateWorker(Room room, LocalDate date) {
            this.room = room;
            this.date = date;
        }
        
        @Override
        protected List<Guest> doInBackground() throws Exception {
            List<Guest> result;
            result = stayManager.findGuestsForRoomByDate(room, date);
            return result;
        }
        
        @Override
        protected void done() {
            try {
                guestsModel.setGuests(get());
            } catch (ExecutionException ex) {
                log.error("Exception thrown in doInBackground of findGuestsForRoomByDate: " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("doInBackground of findGuestsForRoomByDate interrupted: " + ex.getCause());
                throw new RuntimeException("Operation interrupted.. findGuestsForRoomByDate");
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFrameRoomCreation = new javax.swing.JFrame();
        jPanel2 = new javax.swing.JPanel();
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
        jFrameGuestCreation = new javax.swing.JFrame();
        jPanel1 = new javax.swing.JPanel();
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
        jTabbedPaneGuests = new javax.swing.JTabbedPane();
        jPanelGuests = new javax.swing.JPanel();
        jScrollPaneGuests = new javax.swing.JScrollPane();
        jTableGuests = new javax.swing.JTable();
        jTextFieldSearchGuest = new javax.swing.JTextField();
        jButtonSerachGuestByName = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jButtonDeleteSelectedGuests = new javax.swing.JButton();
        jButtonFindAllGuests = new javax.swing.JButton();
        jButtonTop3Guests = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        jTextFieldFindStayingGuestsByDate = new javax.swing.JTextField();
        jButtonFindStayingGuestsByDate = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        jTextFieldFindGuestsForRoomByDate_date = new javax.swing.JTextField();
        jComboBoxFindGuestsFormRoomByDate_room = new javax.swing.JComboBox();
        jButtonFindGuestsForRoomByDate = new javax.swing.JButton();
        jPanelRooms = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableRooms = new javax.swing.JTable();
        jLabel8 = new javax.swing.JLabel();
        jTextFieldSearchRoomNumber = new javax.swing.JTextField();
        jButtonSearchRoomNumber = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableStays = new javax.swing.JTable();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItemGuestCreate = new javax.swing.JMenuItem();
        jMenuItemRoomCreation = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();

        jPanel2.setMinimumSize(new java.awt.Dimension(350, 500));

        jLabel3.setText("Number:");

        jButtonRoomCreate.setText("Create");
        jButtonRoomCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRoomCreateActionPerformed(evt);
            }
        });

        jLabel9.setText("Capacity:");

        jLabel10.setText("Price per night:");

        jLabel11.setText("Room type:");

        jLabel12.setText("Bathroom:");

        jRadioButton1.setText("included");

        for(RoomType t: RoomType.values()){
            jComboBoxRoomType.addItem(t);
        }

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonRoomCreate)
                .addGap(33, 33, 33))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(17, 17, 17)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextFieldRoomNumber, javax.swing.GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE)
                            .addComponent(jTextFieldCapacity)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(17, 17, 17)
                                .addComponent(jTextFieldPricePerNight))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jComboBoxRoomType, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jRadioButton1)
                                        .addGap(0, 188, Short.MAX_VALUE)))))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextFieldRoomNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jTextFieldCapacity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jTextFieldPricePerNight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jComboBoxRoomType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jRadioButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 276, Short.MAX_VALUE)
                .addComponent(jButtonRoomCreate)
                .addGap(29, 29, 29))
        );

        javax.swing.GroupLayout jFrameRoomCreationLayout = new javax.swing.GroupLayout(jFrameRoomCreation.getContentPane());
        jFrameRoomCreation.getContentPane().setLayout(jFrameRoomCreationLayout);
        jFrameRoomCreationLayout.setHorizontalGroup(
            jFrameRoomCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jFrameRoomCreationLayout.setVerticalGroup(
            jFrameRoomCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jFrameGuestCreation.setMinimumSize(new java.awt.Dimension(300, 300));

        jPanel1.setMinimumSize(new java.awt.Dimension(350, 500));

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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
                                .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jLabel5))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextFieldPhone)
                            .addComponent(jTextFieldDateOfBirth, javax.swing.GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE)
                            .addComponent(jTextFieldPassportNumber)
                            .addComponent(jTextFieldEmail)
                            .addComponent(jTextFieldGuestName)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonCreateGuest)))
                .addGap(31, 31, 31))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextFieldGuestName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextFieldPassportNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jTextFieldPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(jTextFieldDateOfBirth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 268, Short.MAX_VALUE)
                .addComponent(jButtonCreateGuest)
                .addContainerGap())
        );

        javax.swing.GroupLayout jFrameGuestCreationLayout = new javax.swing.GroupLayout(jFrameGuestCreation.getContentPane());
        jFrameGuestCreation.getContentPane().setLayout(jFrameGuestCreationLayout);
        jFrameGuestCreationLayout.setHorizontalGroup(
            jFrameGuestCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jFrameGuestCreationLayout.setVerticalGroup(
            jFrameGuestCreationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTableGuests.setModel(new GuestsTableModel());
        jScrollPaneGuests.setViewportView(jTableGuests);

        jButtonSerachGuestByName.setText("Search");
        jButtonSerachGuestByName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSerachGuestByNameActionPerformed(evt);
            }
        });

        jLabel7.setText("Search by name:");

        jButtonDeleteSelectedGuests.setText("Delete Selected");
        jButtonDeleteSelectedGuests.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteSelectedGuestsActionPerformed(evt);
            }
        });

        jButtonFindAllGuests.setText("List all");
        jButtonFindAllGuests.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFindAllGuestsActionPerformed(evt);
            }
        });

        jButtonTop3Guests.setText("List top 3");
        jButtonTop3Guests.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTop3GuestsActionPerformed(evt);
            }
        });

        jLabel13.setText("Find staying guests by date:");

        jTextFieldFindStayingGuestsByDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldFindStayingGuestsByDateActionPerformed(evt);
            }
        });

        jButtonFindStayingGuestsByDate.setText("Search");
        jButtonFindStayingGuestsByDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFindStayingGuestsByDateActionPerformed(evt);
            }
        });

        jLabel14.setText("Find guests for room by date:");

        List<Room> rooms = AppCommons.getRoomManager().findAllRooms();
        for(Room r: rooms){
            jComboBoxFindGuestsFormRoomByDate_room.addItem(r);
        }

        jButtonFindGuestsForRoomByDate.setText("Serach");
        jButtonFindGuestsForRoomByDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFindGuestsForRoomByDateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelGuestsLayout = new javax.swing.GroupLayout(jPanelGuests);
        jPanelGuests.setLayout(jPanelGuestsLayout);
        jPanelGuestsLayout.setHorizontalGroup(
            jPanelGuestsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGuestsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelGuestsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelGuestsLayout.createSequentialGroup()
                        .addComponent(jButtonFindAllGuests)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButtonTop3Guests)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonDeleteSelectedGuests))
                    .addGroup(jPanelGuestsLayout.createSequentialGroup()
                        .addGroup(jPanelGuestsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addComponent(jLabel13))
                        .addGap(34, 34, 34)
                        .addGroup(jPanelGuestsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanelGuestsLayout.createSequentialGroup()
                                .addComponent(jTextFieldFindGuestsForRoomByDate_date, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jComboBoxFindGuestsFormRoomByDate_room, 0, 118, Short.MAX_VALUE))
                            .addComponent(jTextFieldSearchGuest)
                            .addComponent(jTextFieldFindStayingGuestsByDate))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanelGuestsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButtonFindStayingGuestsByDate, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jButtonSerachGuestByName, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jButtonFindGuestsForRoomByDate, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addContainerGap())
            .addGroup(jPanelGuestsLayout.createSequentialGroup()
                .addGroup(jPanelGuestsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPaneGuests, javax.swing.GroupLayout.PREFERRED_SIZE, 527, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanelGuestsLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel14)))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanelGuestsLayout.setVerticalGroup(
            jPanelGuestsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGuestsLayout.createSequentialGroup()
                .addComponent(jScrollPaneGuests, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanelGuestsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonFindAllGuests)
                    .addComponent(jButtonDeleteSelectedGuests)
                    .addComponent(jButtonTop3Guests))
                .addGap(18, 18, 18)
                .addGroup(jPanelGuestsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldSearchGuest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonSerachGuestByName)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelGuestsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(jTextFieldFindStayingGuestsByDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonFindStayingGuestsByDate))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelGuestsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(jTextFieldFindGuestsForRoomByDate_date, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxFindGuestsFormRoomByDate_room, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonFindGuestsForRoomByDate))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        jTabbedPaneGuests.addTab("Guests", jPanelGuests);

        jTableRooms.setModel(new RoomsTableModel());
        jScrollPane1.setViewportView(jTableRooms);

        jLabel8.setText("Search by room number:");

        jButtonSearchRoomNumber.setText("Search");

        javax.swing.GroupLayout jPanelRoomsLayout = new javax.swing.GroupLayout(jPanelRooms);
        jPanelRooms.setLayout(jPanelRoomsLayout);
        jPanelRoomsLayout.setHorizontalGroup(
            jPanelRoomsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 527, Short.MAX_VALUE)
            .addGroup(jPanelRoomsLayout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextFieldSearchRoomNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonSearchRoomNumber)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelRoomsLayout.setVerticalGroup(
            jPanelRoomsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelRoomsLayout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 123, Short.MAX_VALUE)
                .addGroup(jPanelRoomsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jTextFieldSearchRoomNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonSearchRoomNumber))
                .addContainerGap())
        );

        jTabbedPaneGuests.addTab("Rooms", jPanelRooms);

        jTableStays.setModel(new StaysTableModel());
        jScrollPane2.setViewportView(jTableStays);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 527, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 162, Short.MAX_VALUE))
        );

        jTabbedPaneGuests.addTab("Stays", jPanel4);

        jMenu1.setText("Create");

        jMenuItemGuestCreate.setText("Guest");
        jMenuItemGuestCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemGuestCreateActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemGuestCreate);

        jMenuItemRoomCreation.setText("Room");
        jMenuItemRoomCreation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemRoomCreationActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemRoomCreation);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jTabbedPaneGuests, javax.swing.GroupLayout.PREFERRED_SIZE, 532, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPaneGuests, javax.swing.GroupLayout.PREFERRED_SIZE, 432, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        jTabbedPaneGuests.getAccessibleContext().setAccessibleName("Guests");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItemGuestCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemGuestCreateActionPerformed
//        JFrame guestCreation = new jFrameRoomCreation();
        jFrameGuestCreation.setVisible(true);
    }//GEN-LAST:event_jMenuItemGuestCreateActionPerformed

    private void jButtonCreateGuestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCreateGuestActionPerformed
        
        CreateGuestWorker createGuestWorker = new CreateGuestWorker();
        createGuestWorker.execute();
        
        jFrameGuestCreation.setVisible(false);
    }//GEN-LAST:event_jButtonCreateGuestActionPerformed
    
    private Guest getGuestFromCreateForm() {
        Guest g = new Guest();
        g.setName(jTextFieldGuestName.getText());
        g.setPassportNo(jTextFieldPassportNumber.getText());
        g.setEmail(jTextFieldEmail.getText());
        g.setPhone(jTextFieldPhone.getText());
        String dateStr = jTextFieldDateOfBirth.getText();
        LocalDate d = parseDate(dateStr);
        g.setDateOfBirth(d);
        return g;
    }

    private void jMenuItemRoomCreationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemRoomCreationActionPerformed
        jFrameRoomCreation.setVisible(true);
    }//GEN-LAST:event_jMenuItemRoomCreationActionPerformed

    private void jButtonSerachGuestByNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSerachGuestByNameActionPerformed
        log.debug("Search guest by name button clicked.");
        String name = jTextFieldSearchGuest.getText();
        FindGuestByNameWorker w = new FindGuestByNameWorker(name);
        w.execute();
    }//GEN-LAST:event_jButtonSerachGuestByNameActionPerformed

    private void jButtonRoomCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRoomCreateActionPerformed
        Room r = new Room();
        r.setNumber((String) jTextFieldRoomNumber.getText());
        r.setCapacity(Integer.parseInt(jTextFieldCapacity.getText()));
        r.setPricePerNight(new BigDecimal(jTextFieldPricePerNight.getText()));
        r.setType((RoomType) jComboBoxRoomType.getSelectedItem());
        r.setBathroom((Boolean) jRadioButton1.isSelected());

        //roomManager.createRoom(r);
        roomsModel.addRoom(r);
        jFrameRoomCreation.setVisible(false);
    }//GEN-LAST:event_jButtonRoomCreateActionPerformed

    private void jButtonDeleteSelectedGuestsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteSelectedGuestsActionPerformed
        DeleteGuestWorker w = new DeleteGuestWorker();
        w.execute();
    }//GEN-LAST:event_jButtonDeleteSelectedGuestsActionPerformed

    private void jButtonFindAllGuestsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFindAllGuestsActionPerformed
        findAllGuestsWorker = new FindAllGuestsWorker();
        findAllGuestsWorker.execute();
    }//GEN-LAST:event_jButtonFindAllGuestsActionPerformed

    private void jButtonTop3GuestsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTop3GuestsActionPerformed
        FindTop3GuestsWorker w = new FindTop3GuestsWorker();
        w.execute();
    }//GEN-LAST:event_jButtonTop3GuestsActionPerformed

    private void jTextFieldFindStayingGuestsByDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldFindStayingGuestsByDateActionPerformed

    }//GEN-LAST:event_jTextFieldFindStayingGuestsByDateActionPerformed

    private void jButtonFindStayingGuestsByDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFindStayingGuestsByDateActionPerformed
        LocalDate date = parseDate(jTextFieldFindStayingGuestsByDate.getText());
        if (date != null) {
            FindStayingGuestsByDateWorker w = new FindStayingGuestsByDateWorker(date);
            w.execute();
            jTextFieldFindStayingGuestsByDate.setText("");
        }
    }//GEN-LAST:event_jButtonFindStayingGuestsByDateActionPerformed

    private void jButtonFindGuestsForRoomByDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFindGuestsForRoomByDateActionPerformed
        LocalDate date = parseDate(jTextFieldFindGuestsForRoomByDate_date.getText());
        Room room = (Room) jComboBoxFindGuestsFormRoomByDate_room.getSelectedItem();
        if (date != null) {
            FindGuestsForRoomByDateWorker w = new FindGuestsForRoomByDateWorker(room, date);
            w.execute();
            jTextFieldFindGuestsForRoomByDate_date.setText("");
            jComboBoxFindGuestsFormRoomByDate_room.setSelectedItem(null);
        }
    }//GEN-LAST:event_jButtonFindGuestsForRoomByDateActionPerformed
    
    private LocalDate parseDate(String d) {
        try {
            return LocalDate.parse(d);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Wrong date format entered!");
        }
        return null;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                    
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(HotelApp.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(HotelApp.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(HotelApp.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(HotelApp.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new HotelApp().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonCreateGuest;
    private javax.swing.JButton jButtonDeleteSelectedGuests;
    private javax.swing.JButton jButtonFindAllGuests;
    private javax.swing.JButton jButtonFindGuestsForRoomByDate;
    private javax.swing.JButton jButtonFindStayingGuestsByDate;
    private javax.swing.JButton jButtonRoomCreate;
    private javax.swing.JButton jButtonSearchRoomNumber;
    private javax.swing.JButton jButtonSerachGuestByName;
    private javax.swing.JButton jButtonTop3Guests;
    private javax.swing.JComboBox jComboBoxFindGuestsFormRoomByDate_room;
    private javax.swing.JComboBox jComboBoxRoomType;
    private javax.swing.JFrame jFrameGuestCreation;
    private javax.swing.JFrame jFrameRoomCreation;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItemGuestCreate;
    private javax.swing.JMenuItem jMenuItemRoomCreation;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanelGuests;
    private javax.swing.JPanel jPanelRooms;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPaneGuests;
    private javax.swing.JTabbedPane jTabbedPaneGuests;
    private javax.swing.JTable jTableGuests;
    private javax.swing.JTable jTableRooms;
    private javax.swing.JTable jTableStays;
    private javax.swing.JTextField jTextFieldCapacity;
    private javax.swing.JTextField jTextFieldDateOfBirth;
    private javax.swing.JTextField jTextFieldEmail;
    private javax.swing.JTextField jTextFieldFindGuestsForRoomByDate_date;
    private javax.swing.JTextField jTextFieldFindStayingGuestsByDate;
    private javax.swing.JTextField jTextFieldGuestName;
    private javax.swing.JTextField jTextFieldPassportNumber;
    private javax.swing.JTextField jTextFieldPhone;
    private javax.swing.JTextField jTextFieldPricePerNight;
    private javax.swing.JTextField jTextFieldRoomNumber;
    private javax.swing.JTextField jTextFieldSearchGuest;
    private javax.swing.JTextField jTextFieldSearchRoomNumber;
    // End of variables declaration//GEN-END:variables
}
