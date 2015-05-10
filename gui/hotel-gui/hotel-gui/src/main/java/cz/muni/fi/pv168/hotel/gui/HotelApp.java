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
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.concurrent.ExecutionException;
import javax.management.RuntimeErrorException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.text.DateFormatter;
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
    private static GuestManager guestManager = AppCommons.getGuestManager();
    private static RoomManager roomManager = AppCommons.getRoomManager();
    private static StayManager stayManager = AppCommons.getStayManager();
    private GuestsTableModel guestsModel;
    private RoomsTableModel roomsModel;
    private StaysTableModel staysModel;
    private JDatePickerImpl datePicker;
    private JDatePickerImpl datePickerWithRoom;
    private JDatePickerImpl datePickerRL;
    private JDatePickerImpl datePickerRG;
    private JDatePickerImpl datePickerRC;
    private JDatePickerImpl datePickerfrom;
    private JDatePickerImpl datePickerto;
    private JDatePickerImpl datePickerstayForRoom;

//comboboxes
    private List<JComboBox> comboGuests = new ArrayList<>();

    private List<JComboBox> comboRooms = new ArrayList<>();
    //comboModel
    private DefaultComboBoxModel guestsComboBoxModel = new DefaultComboBoxModel();
    private DefaultComboBoxModel roomsComboBoxModel = new DefaultComboBoxModel();

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

        //set combobox models
        setComboBoxModelGuests();
        setComboBoxModelRooms();

        //initialize combobox data
        refreshComboBoxesGuests();
        refreshComboBoxesRooms();
        //initialize datePickers
        initializeDatePickers();

        warning(Locale.getDefault().toString());
    }

    public DefaultComboBoxModel getGuestsComboBoxModel() {
        return guestsComboBoxModel;
    }

    public DefaultComboBoxModel getRoomsComboBoxModel() {
        return roomsComboBoxModel;
    }

    public void refreshComboBoxesGuests() {
        log.debug("Refreshing combobox model fog guests.");
        GuestsComboWorker w = new GuestsComboWorker();
        w.execute();
    }

    public void refreshComboBoxesRooms() {
        log.debug("Refreshing combobox model fog rooms.");
        RoomsComboWorker w = new RoomsComboWorker();
        w.execute();
    }

    private void setComboBoxModelGuests() {
        jComboBoxFindRoomsForGuestByDate_guest.setModel(guestsComboBoxModel);
        jComboBoxFindStaysForGuest.setModel(guestsComboBoxModel);
    }

    private void setComboBoxModelRooms() {
        jComboBoxFindGuestsFormRoomByDate_room.setModel(roomsComboBoxModel);
        jComboBoxFindStaysForRoom.setModel(roomsComboBoxModel);
    }

    private void initializeDatePickers() {
        datePicker = setDatePickerStay();
        datePicker.setVisible(true);
        datePicker.setBounds(263, 340, 150, 30);
        datePicker.setLocale(Locale.getDefault());
        jPanelGuests.add(datePicker);

        datePickerWithRoom = setDatePickerStay();
        datePickerWithRoom.setVisible(true);
        datePickerWithRoom.setBounds(213, 380, 150, 30);
        datePickerWithRoom.setLocale(Locale.getDefault());
        jPanelGuests.add(datePickerWithRoom);

        datePickerRL = setDatePickerStay();
        datePickerRL.setVisible(true);
        datePickerRL.setBounds(290, 350, 150, 30);
        datePickerRL.setLocale(Locale.getDefault());
        jPanelRooms.add(datePickerRL);

        datePickerRC = setDatePickerStay();
        datePickerRC.setVisible(true);
        datePickerRC.setBounds(290, 430, 150, 30);
        datePickerRC.setLocale(Locale.getDefault());
        jPanelRooms.add(datePickerRC);

        datePickerRG = setDatePickerStay();
        datePickerRG.setVisible(true);
        datePickerRG.setBounds(410, 390, 150, 30);
        datePickerRG.setLocale(Locale.getDefault());
        jPanelRooms.add(datePickerRG);

        datePickerfrom = setDatePickerStay();
        datePickerfrom.setVisible(true);
        datePickerfrom.setBounds(210, 300, 150, 30);
        datePickerfrom.setLocale(Locale.getDefault());
        jPanel4.add(datePickerfrom);

        datePickerto = setDatePickerStay();
        datePickerto.setVisible(true);
        datePickerto.setBounds(370, 300, 150, 30);
        datePickerto.setLocale(Locale.getDefault());
        jPanel4.add(datePickerto);

        datePickerstayForRoom = setDatePickerStay();
        datePickerstayForRoom.setVisible(true);
        datePickerstayForRoom.setBounds(370, 350, 150, 30);
        datePickerstayForRoom.setLocale(Locale.getDefault());
        jPanel4.add(datePickerstayForRoom);
    }

    // ********************* WORKERS FOR COMBOBOX MODEL UPDATE *****************************
    private class GuestsComboWorker extends SwingWorker<List<Guest>, Integer> {

        @Override
        protected List<Guest> doInBackground() throws Exception {
            return guestManager.findAllGuests();
        }

        @Override
        protected void done() {
            try {
                List<Guest> guests = get();
                guestsComboBoxModel.removeAllElements();
                for (Guest g : guests) {
                    guestsComboBoxModel.addElement(g);
                }
            } catch (ExecutionException ex) {
                log.error("Exception thrown in doInBackground of GuestsComboWorker: " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("doInBackground of GuestsComboWorker interrupted: " + ex.getCause());
                throw new RuntimeException("Operation interrupted.. GuestsComboWorker");
            }
        }
    }

    private class RoomsComboWorker extends SwingWorker<List<Room>, Integer> {

        @Override
        protected List<Room> doInBackground() throws Exception {
            return roomManager.findAllRooms();
        }

        @Override
        protected void done() {
            try {
                List<Room> rooms = get();
                roomsComboBoxModel.removeAllElements();
                for (Room r : rooms) {
                    roomsComboBoxModel.addElement(r);
                }
            } catch (ExecutionException ex) {
                log.error("Exception thrown in doInBackground of RoomsComboWorker: " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("doInBackground of RoomsComboWorker interrupted: " + ex.getCause());
                throw new RuntimeException("Operation interrupted.. RoomsComboWorker");
            }
        }
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
                refreshComboBoxesGuests();
            } catch (ExecutionException ex) {
                log.error("Exception thrown in doInBackground of DeleteGuestWorker: " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("doInBackground of DeleteGuestWorker interrupted: " + ex.getCause());
                throw new RuntimeException("Operation interrupted.. DeleteGuestWorker");
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
                refreshComboBoxesRooms();
            } catch (ExecutionException ex) {
                log.error("Exception thrown in doInBackground of DeleteRoomWorker: " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("doInBackground of DeleteRoomWorker interrupted: " + ex.getCause());
                throw new RuntimeException("Operation interrupted.. DeleteRoomWorker");
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
                log.error("Exception thrown in doInBackground of DeleteStayWorker: " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("doInBackground of DeleteStayWorker interrupted: " + ex.getCause());
                throw new RuntimeException("Operation interrupted.. DeleteStayWorker");
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
                ex.printStackTrace();
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
    private class FindRoomByNumberWorker extends SwingWorker<List<Room>, Integer> {

        private String number;

        public FindRoomByNumberWorker(String number) {
            this.number = number;
        }

        @Override
        protected List<Room> doInBackground() throws Exception {
            List<Room> result = new ArrayList<Room>();
            result.add(roomManager.findRoomByNumber(number));
            return result;
        }

        @Override
        protected void done() {
            try {
                roomsModel.setRooms(get());
            } catch (ExecutionException ex) {
                log.error("Exception thrown in doInBackground of FindRoomByNumberWorker: " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("doInBackground of FindRoomByNumberWorker interrupted: " + ex.getCause());
                throw new RuntimeException("Operation interrupted.. FindRoomByNumberWorker");
            }
        }
    }

    private class FindFreeRoomsByDateAndLenWorker extends SwingWorker<List<Room>, Integer> {

        private LocalDate date;
        private int len;

        public FindFreeRoomsByDateAndLenWorker(LocalDate date, int len) {
            this.date = date;
            this.len = len;
        }

        @Override
        protected List<Room> doInBackground() throws Exception {
            List<Room> result;
            result = stayManager.findFreeRoomsByDateAndLen(date, len);
            return result;
        }

        @Override
        protected void done() {
            try {
                roomsModel.setRooms(get());
            } catch (ExecutionException ex) {
                log.error("Exception thrown in doInBackground of FindFreeRoomsByDateAndLenWorker: " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("doInBackground of FindFreeRoomsByDateAndLenWorker interrupted: " + ex.getCause());
                throw new RuntimeException("Operation interrupted.. FindFreeRoomsByDateAndLenWorker");
            }
        }
    }

    private class FindRoomsForGuestByDateWorker extends SwingWorker<List<Room>, Integer> {

        private Guest guest;
        private LocalDate date;

        public FindRoomsForGuestByDateWorker(Guest guest, LocalDate date) {
            this.guest = guest;
            this.date = date;
        }

        @Override
        protected List<Room> doInBackground() throws Exception {
            List<Room> result;
            result = stayManager.findRoomsForGuestByDate(guest, date);
            return result;
        }

        @Override
        protected void done() {
            try {
                roomsModel.setRooms(get());
            } catch (ExecutionException ex) {
                log.error("Exception thrown in doInBackground of FindRoomsForGuestByDateWorker: " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("doInBackground of FindRoomsForGuestByDateWorker interrupted: " + ex.getCause());
                throw new RuntimeException("Operation interrupted.. FindRoomsForGuestByDateWorker");
            }
        }
    }

    private class FindFreeRoomByDateAndCapacityWorker extends SwingWorker<List<Room>, Integer> {

        private LocalDate date;
        private int capacity;

        public FindFreeRoomByDateAndCapacityWorker(LocalDate date, int capacity) {
            this.date = date;
            this.capacity = capacity;
        }

        @Override
        protected List<Room> doInBackground() throws Exception {
            List<Room> result;
            result = stayManager.findFreeRoomByDateAndCapacity(date, capacity);
            return result;
        }

        @Override
        protected void done() {
            try {
                roomsModel.setRooms(get());
            } catch (ExecutionException ex) {
                log.error("Exception thrown in doInBackground of FindFreeRoomByDateAndCapacityWorker: " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("doInBackground of FindFreeRoomByDateAndCapacityWorker interrupted: " + ex.getCause());
                throw new RuntimeException("Operation interrupted.. FindFreeRoomByDateAndCapacityWorker");
            }
        }
    }
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
        jButtonFindStayingGuestsByDate = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        jComboBoxFindGuestsFormRoomByDate_room = new javax.swing.JComboBox();
        jButtonFindGuestsForRoomByDate = new javax.swing.JButton();
        jButtonUpdateSelectedGuest = new javax.swing.JButton();
        jPanelRooms = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableRooms = new javax.swing.JTable();
        jLabel8 = new javax.swing.JLabel();
        jTextFieldSearchRoomNumber = new javax.swing.JTextField();
        jButtonSearchRoomNumber = new javax.swing.JButton();
        jButtonFindAllRooms = new javax.swing.JButton();
        jButtonDeleteRoom = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jTextFieldFindFreeRoomsByDateAndLen_Len = new javax.swing.JTextField();
        jButtonFindFreeRoomsByDateAndLen = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jButtonFindRoomsForGuestByDate = new javax.swing.JButton();
        jButtonFindFreeRoomByDateAndCapacity = new javax.swing.JButton();
        jComboBoxFindRoomsForGuestByDate_guest = new javax.swing.JComboBox();
        jTextFieldFindFreeRoomsByDateAndCapacity_capacity = new javax.swing.JTextField();
        jButtonUpdateSelectedRoom = new javax.swing.JButton();
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
        jComboBoxFindStaysForRoom = new javax.swing.JComboBox();
        jComboBoxFindStaysForGuest = new javax.swing.JComboBox();
        jButtonUpdateSelectedStay = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItemGuestCreate = new javax.swing.JMenuItem();
        jMenuItemRoomCreation = new javax.swing.JMenuItem();
        jMenuItemStayCreate = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTableGuests.setModel(new GuestsTableModel());
        jScrollPaneGuests.setViewportView(jTableGuests);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("texts"); // NOI18N
        jButtonSerachGuestByName.setText(bundle.getString("SEARCH")); // NOI18N
        jButtonSerachGuestByName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSerachGuestByNameActionPerformed(evt);
            }
        });

        jLabel7.setText(bundle.getString("SEARCH BY NAME:")); // NOI18N

        jButtonDeleteSelectedGuests.setText(bundle.getString("DELETE SELECTED")); // NOI18N
        jButtonDeleteSelectedGuests.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteSelectedGuestsActionPerformed(evt);
            }
        });

        jButtonFindAllGuests.setText(bundle.getString("LIST ALL")); // NOI18N
        jButtonFindAllGuests.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFindAllGuestsActionPerformed(evt);
            }
        });

        jButtonTop3Guests.setText(bundle.getString("LIST TOP 3")); // NOI18N
        jButtonTop3Guests.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTop3GuestsActionPerformed(evt);
            }
        });

        jLabel13.setText(bundle.getString("FIND STAYING GUESTS BY DATE:")); // NOI18N

        jButtonFindStayingGuestsByDate.setText(bundle.getString("SEARCH")); // NOI18N
        jButtonFindStayingGuestsByDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFindStayingGuestsByDateActionPerformed(evt);
            }
        });

        jLabel14.setText(bundle.getString("FIND GUESTS FOR ROOM BY DATE:")); // NOI18N

        List<Room> rooms = AppCommons.getRoomManager().findAllRooms();
        for(Room r: rooms){
            jComboBoxFindGuestsFormRoomByDate_room.addItem(r);
        }

        jButtonFindGuestsForRoomByDate.setText(bundle.getString("SERACH")); // NOI18N
        jButtonFindGuestsForRoomByDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFindGuestsForRoomByDateActionPerformed(evt);
            }
        });

        jButtonUpdateSelectedGuest.setText(bundle.getString("UPDATE SELECTED")); // NOI18N
        jButtonUpdateSelectedGuest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUpdateSelectedGuestActionPerformed(evt);
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
                                .addGroup(jPanelGuestsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel13))
                                .addGap(34, 34, 34)
                                .addComponent(jTextFieldSearchGuest)
                                .addGap(52, 52, 52))
                            .addGroup(jPanelGuestsLayout.createSequentialGroup()
                                .addComponent(jLabel14)
                                .addGap(94, 410, Short.MAX_VALUE)))
                        .addGroup(jPanelGuestsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButtonFindStayingGuestsByDate, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jButtonSerachGuestByName, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jButtonFindGuestsForRoomByDate, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelGuestsLayout.createSequentialGroup()
                        .addGroup(jPanelGuestsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanelGuestsLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jComboBoxFindGuestsFormRoomByDate_room, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanelGuestsLayout.createSequentialGroup()
                                .addComponent(jButtonFindAllGuests)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButtonTop3Guests)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButtonUpdateSelectedGuest)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButtonDeleteSelectedGuests)))
                .addContainerGap())
        );
        jPanelGuestsLayout.setVerticalGroup(
            jPanelGuestsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGuestsLayout.createSequentialGroup()
                .addComponent(jScrollPaneGuests, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanelGuestsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonFindAllGuests)
                    .addComponent(jButtonDeleteSelectedGuests)
                    .addComponent(jButtonTop3Guests)
                    .addComponent(jButtonUpdateSelectedGuest))
                .addGap(18, 18, 18)
                .addGroup(jPanelGuestsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldSearchGuest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonSerachGuestByName)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelGuestsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(jButtonFindStayingGuestsByDate))
                .addGroup(jPanelGuestsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelGuestsLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanelGuestsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel14)
                            .addComponent(jButtonFindGuestsForRoomByDate))
                        .addContainerGap(65, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelGuestsLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jComboBoxFindGuestsFormRoomByDate_room, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(59, 59, 59))))
        );

        jTabbedPaneGuests.addTab("Guests", jPanelGuests);

        jTableRooms.setModel(new RoomsTableModel());
        jScrollPane1.setViewportView(jTableRooms);

        jLabel8.setText(bundle.getString("SEARCH BY ROOM NUMBER:")); // NOI18N

        jButtonSearchRoomNumber.setText(bundle.getString("SEARCH")); // NOI18N
        jButtonSearchRoomNumber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSearchRoomNumberActionPerformed(evt);
            }
        });

        jButtonFindAllRooms.setText(bundle.getString("LIST ALL")); // NOI18N
        jButtonFindAllRooms.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFindAllRoomsActionPerformed(evt);
            }
        });

        jButtonDeleteRoom.setText(bundle.getString("DELETE SELECTED")); // NOI18N
        jButtonDeleteRoom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteRoomActionPerformed(evt);
            }
        });

        jLabel1.setText(bundle.getString("FIND FREE ROOMS BY DATE AND LENGTH:")); // NOI18N

        jButtonFindFreeRoomsByDateAndLen.setText(bundle.getString("SEARCH")); // NOI18N
        jButtonFindFreeRoomsByDateAndLen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFindFreeRoomsByDateAndLenActionPerformed(evt);
            }
        });

        jLabel2.setText(bundle.getString("FIND ROOMS FOR GUEST BY DATE:")); // NOI18N

        jLabel3.setText(bundle.getString("FIND FREE ROOM BY DATE AND CAPACITY:")); // NOI18N

        jButtonFindRoomsForGuestByDate.setText(bundle.getString("SEARCH")); // NOI18N
        jButtonFindRoomsForGuestByDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFindRoomsForGuestByDateActionPerformed(evt);
            }
        });

        jButtonFindFreeRoomByDateAndCapacity.setText(bundle.getString("SEARCH")); // NOI18N
        jButtonFindFreeRoomByDateAndCapacity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFindFreeRoomByDateAndCapacityActionPerformed(evt);
            }
        });

        jComboBoxFindRoomsForGuestByDate_guest.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jTextFieldFindFreeRoomsByDateAndCapacity_capacity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldFindFreeRoomsByDateAndCapacity_capacityActionPerformed(evt);
            }
        });

        jButtonUpdateSelectedRoom.setText(bundle.getString("UPDATE SELECTED")); // NOI18N
        jButtonUpdateSelectedRoom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUpdateSelectedRoomActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelRoomsLayout = new javax.swing.GroupLayout(jPanelRooms);
        jPanelRooms.setLayout(jPanelRoomsLayout);
        jPanelRoomsLayout.setHorizontalGroup(
            jPanelRoomsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
            .addGroup(jPanelRoomsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelRoomsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelRoomsLayout.createSequentialGroup()
                        .addComponent(jButtonFindAllRooms)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonUpdateSelectedRoom)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonDeleteRoom))
                    .addGroup(jPanelRoomsLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(51, 51, 51)
                        .addComponent(jComboBoxFindRoomsForGuestByDate_guest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonFindRoomsForGuestByDate))
                    .addGroup(jPanelRoomsLayout.createSequentialGroup()
                        .addGroup(jPanelRoomsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanelRoomsLayout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jTextFieldFindFreeRoomsByDateAndCapacity_capacity, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanelRoomsLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel8)
                                .addGap(67, 67, 67)
                                .addComponent(jTextFieldSearchRoomNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanelRoomsLayout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jTextFieldFindFreeRoomsByDateAndLen_Len, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
                        .addGroup(jPanelRoomsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButtonSearchRoomNumber, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jButtonFindFreeRoomsByDateAndLen, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jButtonFindFreeRoomByDateAndCapacity, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addContainerGap())
        );
        jPanelRoomsLayout.setVerticalGroup(
            jPanelRoomsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelRoomsLayout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanelRoomsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonFindAllRooms)
                    .addComponent(jButtonDeleteRoom)
                    .addComponent(jButtonUpdateSelectedRoom))
                .addGap(18, 18, 18)
                .addGroup(jPanelRoomsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jTextFieldSearchRoomNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonSearchRoomNumber))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelRoomsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextFieldFindFreeRoomsByDateAndLen_Len, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonFindFreeRoomsByDateAndLen))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelRoomsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jButtonFindRoomsForGuestByDate)
                    .addComponent(jComboBoxFindRoomsForGuestByDate_guest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelRoomsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jButtonFindFreeRoomByDateAndCapacity)
                    .addComponent(jTextFieldFindFreeRoomsByDateAndCapacity_capacity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPaneGuests.addTab("Rooms", jPanelRooms);

        jTableStays.setModel(new StaysTableModel());
        jScrollPane2.setViewportView(jTableStays);

        jButtonFindAllStays.setText(bundle.getString("LIST ALL")); // NOI18N
        jButtonFindAllStays.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFindAllStaysActionPerformed(evt);
            }
        });

        jButtonDeleteStay.setText(bundle.getString("DELETE SELECTED")); // NOI18N
        jButtonDeleteStay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteStayActionPerformed(evt);
            }
        });

        jLabel15.setText(bundle.getString("FIND STAYS BY DATE")); // NOI18N

        jLabel16.setText(bundle.getString("FINDS STAYS FOR ROOM BY DATE:")); // NOI18N

        jLabel17.setText(bundle.getString("FIND STAYS FOR GUEST:")); // NOI18N

        jButtonFindStaysByDate.setText(bundle.getString("SEARCH")); // NOI18N
        jButtonFindStaysByDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFindStaysByDateActionPerformed(evt);
            }
        });

        jButtonFindStaysForRoom.setText(bundle.getString("SEARCH")); // NOI18N
        jButtonFindStaysForRoom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFindStaysForRoomActionPerformed(evt);
            }
        });

        jButtonFindStaysForGuest.setText(bundle.getString("SEARCH")); // NOI18N
        jButtonFindStaysForGuest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFindStaysForGuestActionPerformed(evt);
            }
        });

        //List<Room> rooms = AppCommons.getRoomManager().findAllRooms();
        for(Room r: rooms){
            jComboBoxFindStaysForRoom.addItem(r);
        }
        jComboBoxFindStaysForRoom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxFindStaysForRoomActionPerformed(evt);
            }
        });

        List<Guest> guests = AppCommons.getGuestManager().findAllGuests();
        for(Guest g: guests){
            jComboBoxFindStaysForGuest.addItem(g);
        }

        jButtonUpdateSelectedStay.setText(bundle.getString("UPDATE SELECTED")); // NOI18N
        jButtonUpdateSelectedStay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUpdateSelectedStayActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 638, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jButtonFindAllStays)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonUpdateSelectedStay)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonDeleteStay)
                .addGap(8, 8, 8))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel15)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jComboBoxFindStaysForGuest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addGap(40, 40, 40)
                        .addComponent(jComboBoxFindStaysForRoom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonFindStaysForGuest, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButtonFindStaysByDate, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButtonFindStaysForRoom, javax.swing.GroupLayout.Alignment.TRAILING))
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
                    .addComponent(jButtonDeleteStay)
                    .addComponent(jButtonUpdateSelectedStay))
                .addGap(22, 22, 22)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(jButtonFindStaysByDate))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(jButtonFindStaysForRoom)
                    .addComponent(jComboBoxFindStaysForRoom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(jButtonFindStaysForGuest)
                    .addComponent(jComboBoxFindStaysForGuest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 48, Short.MAX_VALUE))
        );

        jTabbedPaneGuests.addTab("Stays", jPanel4);

        jMenu1.setText(bundle.getString("CREATE")); // NOI18N

        jMenuItemGuestCreate.setText(bundle.getString("GUEST")); // NOI18N
        jMenuItemGuestCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemGuestCreateActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemGuestCreate);

        jMenuItemRoomCreation.setText(bundle.getString("ROOM")); // NOI18N
        jMenuItemRoomCreation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemRoomCreationActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemRoomCreation);

        jMenuItemStayCreate.setText(bundle.getString("STAY")); // NOI18N
        jMenuItemStayCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemStayCreateActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemStayCreate);

        jMenuBar1.add(jMenu1);

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
                new GuestCreationFrame(HotelApp.this, null, -1, java.util.ResourceBundle.getBundle("texts").getString("CREATE"));//.setVisible(true);
            }
        });
    }//GEN-LAST:event_jMenuItemGuestCreateActionPerformed

    private void jMenuItemRoomCreationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemRoomCreationActionPerformed
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new RoomCreationFrame(HotelApp.this, null, -1, java.util.ResourceBundle.getBundle("texts").getString("CREATE"));//.setVisible(true);
            }
        });
    }//GEN-LAST:event_jMenuItemRoomCreationActionPerformed

    private void jButtonSerachGuestByNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSerachGuestByNameActionPerformed
        log.debug(java.util.ResourceBundle.getBundle("texts").getString("SEARCH GUEST BY NAME BUTTON CLICKED."));
        String name = jTextFieldSearchGuest.getText();
        if (name == null || name.trim().length() == 0) {
            warning(java.util.ResourceBundle.getBundle("texts").getString("FILL IN THE NAME."));
            return;
        }
        FindGuestByNameWorker w = new FindGuestByNameWorker(name);
        w.execute();
        jTextFieldSearchGuest.setText("");
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

    private void jButtonFindStayingGuestsByDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFindStayingGuestsByDateActionPerformed
        //TODO picker
        LocalDate date = transformDate((Date) datePicker.getModel().getValue());
        if (date == null) {
            warning(java.util.ResourceBundle.getBundle("texts").getString("SELECT THE DATE!"));
            return;
        }
        FindStayingGuestsByDateWorker w = new FindStayingGuestsByDateWorker(date);
        w.execute();
        datePicker.getModel().setValue(null);
        //TODO
        //datePicker.getModel()

    }//GEN-LAST:event_jButtonFindStayingGuestsByDateActionPerformed

    private void jButtonFindGuestsForRoomByDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFindGuestsForRoomByDateActionPerformed
        //TODO picker
        LocalDate date = transformDate((Date) datePickerWithRoom.getModel().getValue());
        Room room = (Room) jComboBoxFindGuestsFormRoomByDate_room.getSelectedItem();
        if (date == null || room == null) {
            warning(java.util.ResourceBundle.getBundle("texts").getString("SPECIFY DATE AND ROOM"));
            return;
        }

        FindGuestsForRoomByDateWorker w = new FindGuestsForRoomByDateWorker(room, date);
        w.execute();
        //TODO
        datePickerWithRoom.getModel().setValue(null);
        jComboBoxFindGuestsFormRoomByDate_room.setSelectedItem(null);
    }//GEN-LAST:event_jButtonFindGuestsForRoomByDateActionPerformed

    private void jButtonFindStaysForRoomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFindStaysForRoomActionPerformed
        Room room = (Room) jComboBoxFindStaysForRoom.getSelectedItem();
        LocalDate date = transformDate((Date) datePickerstayForRoom.getModel().getValue());

        if (room == null || date == null) {
            warning(java.util.ResourceBundle.getBundle("texts").getString("SPECIFY DATE AND ROOM!"));
            return;
        }
        FindStaysForRoomByDateWorker w = new FindStaysForRoomByDateWorker(room, date);
        w.execute();
        jComboBoxFindStaysForRoom.setSelectedItem(null);
        //TODO picker null
        datePickerstayForRoom.getModel().setValue(null);
    }//GEN-LAST:event_jButtonFindStaysForRoomActionPerformed

    private void jButtonFindStaysByDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFindStaysByDateActionPerformed
        LocalDate from = transformDate((Date) datePickerfrom.getModel().getValue());

        LocalDate to = transformDate((Date) datePickerto.getModel().getValue());

        if (from == null || to == null) {
            warning(java.util.ResourceBundle.getBundle("texts").getString("SPECIFY FROM AND TO DATES!"));
            return;
        }
        FindStaysByDateWorker w = new FindStaysByDateWorker(from, to);
        w.execute();
        //TODO null pickers
        datePickerfrom.getModel().setValue(null);
        datePickerto.getModel().setValue(null);

    }//GEN-LAST:event_jButtonFindStaysByDateActionPerformed

    private void jButtonFindStaysForGuestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFindStaysForGuestActionPerformed
        Guest guest = (Guest) jComboBoxFindStaysForGuest.getSelectedItem();
        if (guest == null) {
            warning(java.util.ResourceBundle.getBundle("texts").getString("SPECIFY GUEST!"));
            return;
        }
        FindStaysForGuestWorker w = new FindStaysForGuestWorker(guest);
        w.execute();
        jComboBoxFindStaysForGuest.setSelectedItem(null);
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
                new StayCreationFrame(HotelApp.this, null, -1, java.util.ResourceBundle.getBundle("texts").getString("CREATE"));//.setVisible(true);
            }
        });
    }//GEN-LAST:event_jMenuItemStayCreateActionPerformed

    private void jButtonSearchRoomNumberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSearchRoomNumberActionPerformed
        String number = jTextFieldSearchRoomNumber.getText();
        if (number == null || number.trim().length() == 0) {
            warning(java.util.ResourceBundle.getBundle("texts").getString("SPECIFY ROOM NUMBER!"));
            return;
        }

        FindRoomByNumberWorker w = new FindRoomByNumberWorker(number);
        w.execute();
        jTextFieldSearchRoomNumber.setText("");
    }//GEN-LAST:event_jButtonSearchRoomNumberActionPerformed

    private void jButtonFindFreeRoomsByDateAndLenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFindFreeRoomsByDateAndLenActionPerformed
        LocalDate date = transformDate((Date) datePickerRL.getModel().getValue());
        String l = jTextFieldFindFreeRoomsByDateAndLen_Len.getText();
        int len = 1;
        if (date == null || l == null || l.trim().length() == 0) {
            warning(java.util.ResourceBundle.getBundle("texts").getString("SPECIFY DATE AND LENGTH!"));
            return;
        }
        try {
            len = Integer.parseInt(l);
            if (len <= 0) {
                throw new IllegalArgumentException(java.util.ResourceBundle.getBundle("texts").getString("LENGTH MUST BE POSITIVE!"));
            }
        } catch (Exception ex) {
            warning(ex.getMessage());
            return;
        }
        FindFreeRoomsByDateAndLenWorker w = new FindFreeRoomsByDateAndLenWorker(date, len);
        w.execute();
        jTextFieldFindFreeRoomsByDateAndLen_Len.setText("");
        datePickerRL.getModel().setValue(null);
        //TODO pickeer null
    }//GEN-LAST:event_jButtonFindFreeRoomsByDateAndLenActionPerformed

    private void jButtonFindFreeRoomByDateAndCapacityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFindFreeRoomByDateAndCapacityActionPerformed
        LocalDate date = transformDate((Date) datePickerRC.getModel().getValue());
        String c = jTextFieldFindFreeRoomsByDateAndCapacity_capacity.getText();
        int cap = 1;
        if (date == null || c == null || c.trim().length() == 0) {
            warning(java.util.ResourceBundle.getBundle("texts").getString("SPECIFY DATE AND CAPACITY!"));
            return;
        }
        try {
            cap = Integer.parseInt(c);
            if (cap <= 0) {
                throw new IllegalArgumentException(java.util.ResourceBundle.getBundle("texts").getString("CAPACITY MUST BE POSITIVE!"));
            }
        } catch (Exception ex) {
            warning(ex.getMessage());
            return;
        }

        FindFreeRoomByDateAndCapacityWorker w = new FindFreeRoomByDateAndCapacityWorker(date, cap);
        w.execute();
        //TODO null picker
        jTextFieldFindFreeRoomsByDateAndCapacity_capacity.setText("");
        datePickerRC.getModel().setValue(null);
    }//GEN-LAST:event_jButtonFindFreeRoomByDateAndCapacityActionPerformed

    private void jButtonFindRoomsForGuestByDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFindRoomsForGuestByDateActionPerformed
        // Date d = 
        LocalDate date = transformDate((Date) datePickerRG.getModel().getValue());
        Guest g = (Guest) jComboBoxFindRoomsForGuestByDate_guest.getSelectedItem();
        if (date == null || g == null) {
            warning(java.util.ResourceBundle.getBundle("texts").getString("SPECIFY DATE AND GUEST"));
            return;
        }
        FindRoomsForGuestByDateWorker w = new FindRoomsForGuestByDateWorker(g, date);
        w.execute();
        jComboBoxFindRoomsForGuestByDate_guest.setSelectedItem(null);
        //TODO picker
        datePickerRG.getModel().setValue(null);
    }//GEN-LAST:event_jButtonFindRoomsForGuestByDateActionPerformed

    private void jButtonUpdateSelectedGuestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUpdateSelectedGuestActionPerformed
        if (jTableGuests.getSelectedRowCount() != 1) {
            warning(java.util.ResourceBundle.getBundle("texts").getString("ONE ROW HAS TO BE SELECTED"));
            return;
        }
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                int selected = jTableGuests.getSelectedRow();
                JFrame frame = new GuestCreationFrame(HotelApp.this, guestsModel.getGuest(selected), selected, java.util.ResourceBundle.getBundle("texts").getString("UPDATE"));//.setVisible(true);
            }
        });
    }//GEN-LAST:event_jButtonUpdateSelectedGuestActionPerformed

    private void jButtonUpdateSelectedRoomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUpdateSelectedRoomActionPerformed
        if (jTableRooms.getSelectedRowCount() != 1) {
            warning(java.util.ResourceBundle.getBundle("texts").getString("ONE ROW HAS TO BE SELECTED"));
            return;
        }
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                int selected = jTableRooms.getSelectedRow();
                JFrame frame = new RoomCreationFrame(HotelApp.this, roomsModel.getRoom(selected), selected, java.util.ResourceBundle.getBundle("texts").getString("UPDATE"));//.setVisible(true);
            }
        });
    }//GEN-LAST:event_jButtonUpdateSelectedRoomActionPerformed

    private void jButtonUpdateSelectedStayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUpdateSelectedStayActionPerformed
        if (jTableStays.getSelectedRowCount() != 1) {
            warning(java.util.ResourceBundle.getBundle("texts").getString("ONE ROW HAS TO BE SELECTED"));
            return;
        }
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                int selected = jTableStays.getSelectedRow();
                JFrame frame = new StayCreationFrame(HotelApp.this, staysModel.getStay(selected), selected, java.util.ResourceBundle.getBundle("texts").getString("UPDATE"));//.setVisible(true);
            }
        });
    }//GEN-LAST:event_jButtonUpdateSelectedStayActionPerformed

    private void jComboBoxFindStaysForRoomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxFindStaysForRoomActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBoxFindStaysForRoomActionPerformed

    private void jTextFieldFindFreeRoomsByDateAndCapacity_capacityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldFindFreeRoomsByDateAndCapacity_capacityActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldFindFreeRoomsByDateAndCapacity_capacityActionPerformed

    public JDatePickerImpl setDatePickerBirth() {
        UtilDateModel model = new UtilDateModel();
        model.setDate(1954, 01, 01);

        // Need this...
        Properties p = new Properties();
        p.put("text.today", java.util.ResourceBundle.getBundle("texts").getString("TODAY"));
        p.put("text.month", java.util.ResourceBundle.getBundle("texts").getString("MONTH"));
        p.put("text.year", java.util.ResourceBundle.getBundle("texts").getString("YEAR"));
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        // Don't know about the formatter, but there it is...
        JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DataLabelFormater());
        return datePicker;
    }

    public JDatePickerImpl setDatePickerStay() {
        UtilDateModel model = new UtilDateModel();
        model.setDate(2014, 01, 01);
        // Need this...
        Properties p = new Properties();
        p.put("text.today", java.util.ResourceBundle.getBundle("texts").getString("TODAY"));
        p.put("text.month", java.util.ResourceBundle.getBundle("texts").getString("MONTH"));
        p.put("text.year", java.util.ResourceBundle.getBundle("texts").getString("YEAR"));
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        // Don't know about the formatter, but there it is...
        JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DataLabelFormater());
        return datePicker;
    }

    private LocalDate parseDate(String d) {
        try {
            return LocalDate.parse(d);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, java.util.ResourceBundle.getBundle("texts").getString("WRONG DATE FORMAT ENTERED!"));
        }
        return null;
    }

    public LocalDate transformDate(Date d) {
        if (d == null) {
            return null;
        }
        LocalDate ld = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        String dateStr = ld.toString();
        return parseDate(dateStr);
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
    private javax.swing.JButton jButtonFindFreeRoomByDateAndCapacity;
    private javax.swing.JButton jButtonFindFreeRoomsByDateAndLen;
    private javax.swing.JButton jButtonFindGuestsForRoomByDate;
    private javax.swing.JButton jButtonFindRoomsForGuestByDate;
    private javax.swing.JButton jButtonFindStayingGuestsByDate;
    private javax.swing.JButton jButtonFindStaysByDate;
    private javax.swing.JButton jButtonFindStaysForGuest;
    private javax.swing.JButton jButtonFindStaysForRoom;
    private javax.swing.JButton jButtonSearchRoomNumber;
    private javax.swing.JButton jButtonSerachGuestByName;
    private javax.swing.JButton jButtonTop3Guests;
    private javax.swing.JButton jButtonUpdateSelectedGuest;
    private javax.swing.JButton jButtonUpdateSelectedRoom;
    private javax.swing.JButton jButtonUpdateSelectedStay;
    private javax.swing.JComboBox jComboBoxFindGuestsFormRoomByDate_room;
    private javax.swing.JComboBox jComboBoxFindRoomsForGuestByDate_guest;
    private javax.swing.JComboBox jComboBoxFindStaysForGuest;
    private javax.swing.JComboBox jComboBoxFindStaysForRoom;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JMenu jMenu1;
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
    private javax.swing.JTextField jTextFieldFindFreeRoomsByDateAndCapacity_capacity;
    private javax.swing.JTextField jTextFieldFindFreeRoomsByDateAndLen_Len;
    private javax.swing.JTextField jTextFieldSearchGuest;
    private javax.swing.JTextField jTextFieldSearchRoomNumber;
    // End of variables declaration//GEN-END:variables
}
