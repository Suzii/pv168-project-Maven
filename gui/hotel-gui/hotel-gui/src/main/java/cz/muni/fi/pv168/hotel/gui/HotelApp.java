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
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
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

    public GuestsTableModel getGuestsModel() {
        return guestsModel;
    }

    public RoomsTableModel getRoomsModel() {
        return roomsModel;
    }

    public StaysTableModel getStaysModel() {
        return staysModel;
    }

    //find all workers
    private FindAllGuestsWorker findAllGuestsWorker;
    private FindAllRoomsWorker findAllRoomsWorker;
    private FindAllStaysWorker findAllStaysWorker;

    /**
     * Creates new form HotelApp
     */
    public HotelApp() {
        initComponents();
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
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

// ********************* WORKERS FOR DELETE*****************************
    private class DeleteGuestWorker extends SwingWorker<int[], Void> {

        @Override
        protected int[] doInBackground() {
            int[] selectedRows = jTableGuests.getSelectedRows();
            if (selectedRows.length >= 0) {
                for (int selectedRow : selectedRows) {
                    Guest g = guestsModel.getGuest(selectedRow);
                    guestManager.deleteGuest(g);
                }
                return selectedRows;
            }
            return null;
        }

        @Override
        protected void done() {
            try {
                int[] indexes = get();
                if (indexes != null && indexes.length != 0) {
                    guestsModel.deleteGuests(indexes);
                }
            } catch (ExecutionException ex) {
                //TODO
            } catch (InterruptedException ex) {
                //TODO
            }
        }
    }

    private class DeleteRoomWorker extends SwingWorker<int[], Void> {

        @Override
        protected int[] doInBackground() {
            int[] selectedRows = jTableRooms.getSelectedRows();
            if (selectedRows.length >= 0) {
                for (int selectedRow : selectedRows) {
                    Room r = roomsModel.getRoom(selectedRow);
                    roomManager.deleteRoom(r);
                }
                return selectedRows;
            }
            return null;
        }

        @Override
        protected void done() {
            try {
                int[] indexes = get();
                if (indexes != null && indexes.length != 0) {
                    roomsModel.deleteRooms(indexes);
                }
            } catch (ExecutionException ex) {
                //TODO
            } catch (InterruptedException ex) {
                //TODO
            }
        }
    }

    private class DeleteStayWorker extends SwingWorker<int[], Void> {

        @Override
        protected int[] doInBackground() {
            int[] selectedRows = jTableStays.getSelectedRows();
            if (selectedRows.length >= 0) {
                for (int selectedRow : selectedRows) {
                    Stay s = staysModel.getStay(selectedRow);
                    stayManager.deleteStay(s);
                }
                return selectedRows;
            }
            return null;
        }

        @Override
        protected void done() {
            try {
                int[] indexes = get();
                if (indexes != null && indexes.length != 0) {
                    staysModel.deleteStays(indexes);
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

// ********************* WORKERS FOR ROOM UTIL *****************************
// ********************* WORKERS FOR STAY UTIL *****************************
    private class FindStaysByDateWorker extends SwingWorker<List<Stay>, Integer> {

        private LocalDate from;
        private LocalDate to;

        public FindStaysByDateWorker(LocalDate from, LocalDate to) {
            this.from = from;
            this.to = to;
        }

        @Override
        protected List<Stay> doInBackground() throws Exception {
            List<Stay> result;
            result = stayManager.findStaysByDate(from, to);
            return result;
        }

        @Override
        protected void done() {
            try {
                staysModel.setStays(get());
            } catch (ExecutionException ex) {
                log.error("Exception thrown in doInBackground of FindStaysByDate: " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("doInBackground of FindStaysByDate interrupted: " + ex.getCause());
                throw new RuntimeException("Operation interrupted.. FindStaysByDate");
            }
        }
    }

    private class FindStaysForGuestWorker extends SwingWorker<List<Stay>, Integer> {

        private Guest guest;

        public FindStaysForGuestWorker(Guest guest) {
            this.guest = guest;
        }

        @Override
        protected List<Stay> doInBackground() throws Exception {
            List<Stay> result;
            result = stayManager.findAllStaysForGuest(guest);
            return result;
        }

        @Override
        protected void done() {
            try {
                staysModel.setStays(get());
            } catch (ExecutionException ex) {
                log.error("Exception thrown in doInBackground of FindStaysForGuest: " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("doInBackground of FindStaysForGuest interrupted: " + ex.getCause());
                throw new RuntimeException("Operation interrupted.. FindStaysForGuest");
            }
        }
    }

    private class FindStaysForRoomByDateWorker extends SwingWorker<List<Stay>, Integer> {

        private Room room;
        private LocalDate date;

        public FindStaysForRoomByDateWorker(Room room, LocalDate date) {
            this.room = room;
            this.date = date;
        }

        @Override
        protected List<Stay> doInBackground() throws Exception {
            List<Stay> result;
            result = stayManager.findStaysForRoomByDate(room, date);
            return result;
        }

        @Override
        protected void done() {
            try {
                staysModel.setStays(get());
            } catch (ExecutionException ex) {
                log.error("Exception thrown in doInBackground of FindStaysForRoomByDate: " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("doInBackground of FindStaysForRoomByDate interrupted: " + ex.getCause());
                throw new RuntimeException("Operation interrupted.. FindStaysForRoomByDate");
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
        jButtonFindAllRooms = new javax.swing.JButton();
        jButtonDeleteRoom = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableStays = new javax.swing.JTable();
        jButtonFindAllStays = new javax.swing.JButton();
        jButtonDeleteStay = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jButtonFindStaysByDate = new javax.swing.JButton();
        jButtonFindStaysForRoom = new javax.swing.JButton();
        jButtonFindStaysForGuest = new javax.swing.JButton();
        jTextFieldFindStaysByDate_from = new javax.swing.JTextField();
        jComboBoxFindStaysForRoom = new javax.swing.JComboBox();
        jComboBoxFindStaysForGuest = new javax.swing.JComboBox();
        jTextFieldFindStaysByDate_to = new javax.swing.JTextField();
        jTextFieldFindStaysForRoomByDate_date = new javax.swing.JTextField();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItemGuestCreate = new javax.swing.JMenuItem();
        jMenuItemRoomCreation = new javax.swing.JMenuItem();
        jMenuItemStayCreate = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();

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

        jButtonDeleteSelectedGuests.setText("Delete selected");
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
            .addComponent(jScrollPaneGuests)
            .addGroup(jPanelGuestsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelGuestsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelGuestsLayout.createSequentialGroup()
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
                        .addComponent(jLabel14)
                        .addGap(0, 456, Short.MAX_VALUE))))
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
                .addContainerGap(49, Short.MAX_VALUE))
        );

        jTabbedPaneGuests.addTab("Guests", jPanelGuests);

        jTableRooms.setModel(new RoomsTableModel());
        jScrollPane1.setViewportView(jTableRooms);

        jLabel8.setText("Search by room number:");

        jButtonSearchRoomNumber.setText("Search");

        jButtonFindAllRooms.setText("List all");
        jButtonFindAllRooms.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFindAllRoomsActionPerformed(evt);
            }
        });

        jButtonDeleteRoom.setText("Delete selected");
        jButtonDeleteRoom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteRoomActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelRoomsLayout = new javax.swing.GroupLayout(jPanelRooms);
        jPanelRooms.setLayout(jPanelRoomsLayout);
        jPanelRoomsLayout.setHorizontalGroup(
            jPanelRoomsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 609, Short.MAX_VALUE)
            .addGroup(jPanelRoomsLayout.createSequentialGroup()
                .addGroup(jPanelRoomsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelRoomsLayout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextFieldSearchRoomNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButtonSearchRoomNumber)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanelRoomsLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButtonFindAllRooms)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonDeleteRoom)))
                .addContainerGap())
        );
        jPanelRoomsLayout.setVerticalGroup(
            jPanelRoomsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelRoomsLayout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanelRoomsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonFindAllRooms)
                    .addComponent(jButtonDeleteRoom))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 113, Short.MAX_VALUE)
                .addGroup(jPanelRoomsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jTextFieldSearchRoomNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonSearchRoomNumber))
                .addContainerGap())
        );

        jTabbedPaneGuests.addTab("Rooms", jPanelRooms);

        jTableStays.setModel(new StaysTableModel());
        jScrollPane2.setViewportView(jTableStays);

        jButtonFindAllStays.setText("List all");
        jButtonFindAllStays.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFindAllStaysActionPerformed(evt);
            }
        });

        jButtonDeleteStay.setText("Delete selected");
        jButtonDeleteStay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteStayActionPerformed(evt);
            }
        });

        jLabel15.setText("Find stays by date");

        jLabel16.setText("Finds stays for room by date:");

        jLabel17.setText("Find stays for guest:");

        jButtonFindStaysByDate.setText("Search");
        jButtonFindStaysByDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFindStaysByDateActionPerformed(evt);
            }
        });

        jButtonFindStaysForRoom.setText("Search");
        jButtonFindStaysForRoom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFindStaysForRoomActionPerformed(evt);
            }
        });

        jButtonFindStaysForGuest.setText("Search");
        jButtonFindStaysForGuest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFindStaysForGuestActionPerformed(evt);
            }
        });

        //List<Room> rooms = AppCommons.getRoomManager().findAllRooms();
        for(Room r: rooms){
            jComboBoxFindStaysForRoom.addItem(r);
        }

        List<Guest> guests = AppCommons.getGuestManager().findAllGuests();
        for(Guest g: guests){
            jComboBoxFindStaysForGuest.addItem(g);
        }

        jTextFieldFindStaysByDate_to.setText("to");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 609, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jButtonFindAllStays)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonDeleteStay))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel15)
                    .addComponent(jLabel16)
                    .addComponent(jLabel17))
                .addGap(86, 86, 86)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jComboBoxFindStaysForGuest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonFindStaysForGuest))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextFieldFindStaysByDate_from, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBoxFindStaysForRoom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextFieldFindStaysForRoomByDate_date)
                            .addComponent(jTextFieldFindStaysByDate_to, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButtonFindStaysByDate, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jButtonFindStaysForRoom, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonFindAllStays)
                    .addComponent(jButtonDeleteStay))
                .addGap(22, 22, 22)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(jButtonFindStaysByDate)
                    .addComponent(jTextFieldFindStaysByDate_from, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldFindStaysByDate_to, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(jButtonFindStaysForRoom)
                    .addComponent(jComboBoxFindStaysForRoom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldFindStaysForRoomByDate_date, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(jButtonFindStaysForGuest)
                    .addComponent(jComboBoxFindStaysForGuest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 32, Short.MAX_VALUE))
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

        jMenuItemStayCreate.setText("Stay");
        jMenuItemStayCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemStayCreateActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemStayCreate);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPaneGuests)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPaneGuests)
        );

        jTabbedPaneGuests.getAccessibleContext().setAccessibleName("Guests");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItemGuestCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemGuestCreateActionPerformed
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
        
                new GuestCreationFrame(HotelApp.this);//.setVisible(true);
            }
        });
    }//GEN-LAST:event_jMenuItemGuestCreateActionPerformed

    private void jMenuItemRoomCreationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemRoomCreationActionPerformed
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new RoomCreationFrame(HotelApp.this).setVisible(true);
            }
        });
    }//GEN-LAST:event_jMenuItemRoomCreationActionPerformed

    private void jButtonSerachGuestByNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSerachGuestByNameActionPerformed
        log.debug("Search guest by name button clicked.");
        String name = jTextFieldSearchGuest.getText();
        FindGuestByNameWorker w = new FindGuestByNameWorker(name);
        w.execute();
    }//GEN-LAST:event_jButtonSerachGuestByNameActionPerformed


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

    private void jButtonFindStaysForRoomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFindStaysForRoomActionPerformed
        Room room = (Room) jComboBoxFindStaysForRoom.getSelectedItem();
        LocalDate date = parseDate(jTextFieldFindStaysForRoomByDate_date.getText());
        FindStaysForRoomByDateWorker w = new FindStaysForRoomByDateWorker(room, date);
        w.execute();
    }//GEN-LAST:event_jButtonFindStaysForRoomActionPerformed

    private void jButtonFindStaysByDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFindStaysByDateActionPerformed
        LocalDate from = parseDate(jTextFieldFindStaysByDate_from.getText());
        LocalDate to = parseDate(jTextFieldFindStaysByDate_to.getText());
        FindStaysByDateWorker w = new FindStaysByDateWorker(from, to);
        w.execute();

    }//GEN-LAST:event_jButtonFindStaysByDateActionPerformed

    private void jButtonFindStaysForGuestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFindStaysForGuestActionPerformed
        Guest guest = (Guest) jComboBoxFindStaysForGuest.getSelectedItem();
        FindStaysForGuestWorker w = new FindStaysForGuestWorker(guest);
        w.execute();
    }//GEN-LAST:event_jButtonFindStaysForGuestActionPerformed

    private void jButtonFindAllRoomsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFindAllRoomsActionPerformed
        findAllRoomsWorker = new FindAllRoomsWorker();
        findAllRoomsWorker.execute();
    }//GEN-LAST:event_jButtonFindAllRoomsActionPerformed

    private void jButtonDeleteRoomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteRoomActionPerformed
        DeleteRoomWorker w = new DeleteRoomWorker();
        w.execute();
    }//GEN-LAST:event_jButtonDeleteRoomActionPerformed

    private void jButtonDeleteStayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteStayActionPerformed
        DeleteStayWorker w = new DeleteStayWorker();
        w.execute();
    }//GEN-LAST:event_jButtonDeleteStayActionPerformed

    private void jButtonFindAllStaysActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFindAllStaysActionPerformed
        findAllStaysWorker = new FindAllStaysWorker();
        findAllStaysWorker.execute();
    }//GEN-LAST:event_jButtonFindAllStaysActionPerformed

    private void jMenuItemStayCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemStayCreateActionPerformed
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new StayCreationFrame(HotelApp.this).setVisible(true);
            }
        });
    }//GEN-LAST:event_jMenuItemStayCreateActionPerformed

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
    private javax.swing.JButton jButtonDeleteRoom;
    private javax.swing.JButton jButtonDeleteSelectedGuests;
    private javax.swing.JButton jButtonDeleteStay;
    private javax.swing.JButton jButtonFindAllGuests;
    private javax.swing.JButton jButtonFindAllRooms;
    private javax.swing.JButton jButtonFindAllStays;
    private javax.swing.JButton jButtonFindGuestsForRoomByDate;
    private javax.swing.JButton jButtonFindStayingGuestsByDate;
    private javax.swing.JButton jButtonFindStaysByDate;
    private javax.swing.JButton jButtonFindStaysForGuest;
    private javax.swing.JButton jButtonFindStaysForRoom;
    private javax.swing.JButton jButtonSearchRoomNumber;
    private javax.swing.JButton jButtonSerachGuestByName;
    private javax.swing.JButton jButtonTop3Guests;
    private javax.swing.JComboBox jComboBoxFindGuestsFormRoomByDate_room;
    private javax.swing.JComboBox jComboBoxFindStaysForGuest;
    private javax.swing.JComboBox jComboBoxFindStaysForRoom;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItemGuestCreate;
    private javax.swing.JMenuItem jMenuItemRoomCreation;
    private javax.swing.JMenuItem jMenuItemStayCreate;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanelGuests;
    private javax.swing.JPanel jPanelRooms;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPaneGuests;
    private javax.swing.JTabbedPane jTabbedPaneGuests;
    private javax.swing.JTable jTableGuests;
    private javax.swing.JTable jTableRooms;
    private javax.swing.JTable jTableStays;
    private javax.swing.JTextField jTextFieldFindGuestsForRoomByDate_date;
    private javax.swing.JTextField jTextFieldFindStayingGuestsByDate;
    private javax.swing.JTextField jTextFieldFindStaysByDate_from;
    private javax.swing.JTextField jTextFieldFindStaysByDate_to;
    private javax.swing.JTextField jTextFieldFindStaysForRoomByDate_date;
    private javax.swing.JTextField jTextFieldSearchGuest;
    private javax.swing.JTextField jTextFieldSearchRoomNumber;
    // End of variables declaration//GEN-END:variables
}
