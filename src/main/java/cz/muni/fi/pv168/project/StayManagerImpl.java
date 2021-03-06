/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.project;

import cz.muni.fi.pv168.project.common.ServiceFailureException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Checkovat ci je izba praznda , dorobit metodu na to +logging + presne kde
 * pisat ten uvodny logger
 *
 * @author Zuzana
 */
public class StayManagerImpl implements StayManager {

    private static GuestManager guestManager;
    private static RoomManager roomManager;

    //Logger
    private final static Logger logger = LoggerFactory.getLogger(GuestManagerImpl.class);
    //DataSource
    private final DataSource dataSource;

    public StayManagerImpl(DataSource dataSource) {
        if (dataSource == null) {
            throw new IllegalArgumentException("DataSource is not set");
        }
        this.dataSource = dataSource;
        this.guestManager = new GuestManagerImpl(dataSource);
        this.roomManager = new RoomManagerImpl(dataSource);
    }

    //Zuzana
    @Override
    public void createStay(Stay stay) {
        logger.debug("Creating stay : " + stay);
        validate(stay);
        if (stay.getId() != null) {
            throw new IllegalArgumentException("stay id already set");
        }

        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement(
                    "INSERT INTO STAY (start_date, expected_end_date, real_end_date, guest_id, room_id, minibar_costs) "
                    + "VALUES(?,?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                st.setDate(1, Date.valueOf(stay.getStartDate()));
                st.setDate(2, (stay.getExpectedEndDate() != null) ? Date.valueOf(stay.getExpectedEndDate()) : null);
                st.setDate(3, (stay.getRealEndDate() != null) ? Date.valueOf(stay.getRealEndDate()) : null);
                st.setLong(4, stay.getGuest().getId());
                st.setLong(5, stay.getRoom().getId());
                st.setBigDecimal(6, stay.getMinibarCosts());
                int addedRows = st.executeUpdate();
                if (addedRows != 1) {
                    throw new ServiceFailureException("Internal Error: More rows inserted when one expected.");
                }
                ResultSet keyRS = st.getGeneratedKeys();
                stay.setId(getKey(keyRS, stay));
            }

        } catch (SQLException ex) {
            logger.error("db connection problem when creating stay: " + stay, ex);
            throw new ServiceFailureException("Error when creatig new stay", ex);
        }

    }

    private Long getKey(ResultSet keyRS, Stay stay) throws ServiceFailureException, SQLException {
        if (keyRS.next()) {
            if (keyRS.getMetaData().getColumnCount() != 1) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retriving failed when trying to insert stay " + stay
                        + " - wrong key fields count: " + keyRS.getMetaData().getColumnCount());
            }
            Long result = keyRS.getLong(1);
            if (keyRS.next()) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retriving failed when trying to insert stay " + stay
                        + " - more keys found");
            }
            return result;
        } else {
            throw new ServiceFailureException("Internal Error: Generated key"
                    + "retriving failed when trying to insert stay " + stay
                    + " - no key found");
        }
    }

    //Zuzana
    @Override
    public Stay getStayById(Long id) {
        logger.debug("Getting stay by id : " + id);
        if (id == null) {
            throw new IllegalArgumentException("id must not be null");
        }
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement(
                    "SELECT id, start_date, expected_end_date, real_end_date, guest_id, room_id, minibar_costs "
                    + "FROM stay "
                    + "WHERE id = ?")) {
                st.setLong(1, id);
                return executeQueryForSingleStay(st);
            }
        } catch (SQLException ex) {
            logger.error("db connection problem when retrieving stay by id. Id: " + id, ex);
            throw new ServiceFailureException("Error when retrieving stay with id " + id, ex);
        }
    }

    //Zuzana
    @Override
    public void updateStay(Stay stay) {
        logger.debug("Updating stay : " + stay);
        validate(stay);
        if (stay.getId() == null) {
            throw new IllegalArgumentException("stay id must not be null when updating");
        }
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement(
                    "UPDATE stay "
                    + "SET start_date = ?, expected_end_date = ?, real_end_date = ?, guest_id = ?, room_id = ?, minibar_costs = ? "
                    + "WHERE id = ?")) {
                st.setDate(1, Date.valueOf(stay.getStartDate()));
                st.setDate(2, (stay.getExpectedEndDate() != null) ? Date.valueOf(stay.getExpectedEndDate()) : null);
                st.setDate(3, (stay.getRealEndDate() != null) ? Date.valueOf(stay.getRealEndDate()) : null);
                st.setLong(4, stay.getGuest().getId());
                st.setLong(5, stay.getRoom().getId());
                st.setBigDecimal(6, stay.getMinibarCosts());
                st.setLong(7, stay.getId());
                int updatedRows = st.executeUpdate();
                if (updatedRows != 1) {
                    throw new IllegalArgumentException("Internal Error: More rows updated when one was expected.");
                }
            }
        } catch (SQLException ex) {
            logger.error("db connection problem when updating stay: " + stay, ex);
            throw new ServiceFailureException("Error when updating stay", ex);
        }
    }

    //pato  
    @Override
    public void deleteStay(Stay stay) {
        logger.debug("Deleting stay : " + stay);
        validate(stay);
        if (stay.getId() == null) {
            throw new IllegalArgumentException("stay id must not be null when deleting");
        }
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("DELETE FROM stay WHERE id = ?")) {
                st.setLong(1, stay.getId());
                int removedRows = st.executeUpdate();
                if (removedRows != 1) {
                    throw new IllegalArgumentException("Internal Error: More rows removed when one was expected, id = " + stay.getId());
                }
            }
        } catch (SQLException ex) {
            logger.error("db connection problem when deleting stay: " + stay, ex);
            throw new ServiceFailureException("Error when deleting stay", ex);
        }
    }

    //Zuzana
    @Override
    public List<Stay> findAllStays() {
        logger.debug("Finding all stays. ");
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement(
                    "SELECT id, start_date, expected_end_date, real_end_date, guest_id, room_id, minibar_costs "
                    + "FROM stay")) {
                return executeQueryForMultipleStays(st);
            }
        } catch (SQLException ex) {
            logger.error("db connection problem when retrieving all stays", ex);
            throw new ServiceFailureException("Error when retrieving all stays", ex);
        }
    }

    // pato - ocekovat porovnavacky este!
    @Override
    public List<Stay> findStaysByDate(LocalDate from, LocalDate to) {
        logger.debug("Finding stays by date from : " + from + " to: " + to);
        if (from == null || to == null) {
            throw new IllegalArgumentException("date must not be null.");
        }
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement(
                    "SELECT DISTINCT id, start_date, expected_end_date, real_end_date, guest_id, room_id, minibar_costs "
                    + "FROM stay "
                    + "WHERE (start_date >= ?"
                    + "AND start_date <= ?) "
                    + "OR (expected_end_date >= ? "
                    + "AND expected_end_date <= ? )")) {
                st.setDate(1, Date.valueOf(from));
                st.setDate(2, Date.valueOf(to));
                st.setDate(3, Date.valueOf(from));
                st.setDate(4, Date.valueOf(to));
                return executeQueryForMultipleStays(st);
            }
        } catch (SQLException ex) {
            logger.error("db connection problem when retrieving stays by date from: " + from + " to: " + to, ex);
            throw new ServiceFailureException("Error when retrieving guests by date from: " + from + " to: " + to, ex);
        }
    }

    /**
     * Zuzana - robit cez JOIN alebo pouzit vytiahnute id a metodu getGuestById?
     */
    @Override
    public List<Guest> findStayingGuestsByDate(LocalDate date) {
        logger.debug("Finding staying guests by date: " + date);
        if (date == null) {
            throw new IllegalArgumentException("date must not be null");
        }
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement(
                    "SELECT DISTINCT guest_id AS id, name, passport_no, email, phone, date_of_birth "
                    + "FROM stay JOIN guest ON (guest.id = stay.guest_id)"
                    + "WHERE start_date <= ? "
                    + "AND (expected_end_date IS NULL OR expected_end_date >= ?) "
                    + "AND (real_end_date IS NULL OR real_end_date >= ?)")) {
                st.setDate(1, Date.valueOf(date));
                st.setDate(2, Date.valueOf(date));
                st.setDate(3, Date.valueOf(date));
                return executeQueryForMultipleGuests(st);
            }
        } catch (SQLException ex) {
            logger.error("db connection problem when retrieving guests by date: " + date, ex);
            throw new ServiceFailureException("Error when retrieving guests by dat: " + date, ex);
        }
    }

    //Zuzana
    //"SELECT room_id AS id, number, capacity, price_per_night, bathroom, room_type, start_date, real_end_date, expected_end_date "
    @Override
    public List<Room> findFreeRoomsByDateAndLen(LocalDate date, int len) {
        logger.debug("Finding free rooms by date : " + date + " and length: " + len);
        if (date == null) {
            throw new IllegalArgumentException("date must not be null");
        }
        if (len <= 0) {
            throw new IllegalArgumentException("length must be positive");
        }
        Date dateFrom = Date.valueOf(date);
        Date dateTo = Date.valueOf(date.plusDays(len));
        //logger.debug("Date from: " + dateFrom + "\n Date to: " + dateTo);
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement(
                    "SELECT  DISTINCT id, number, capacity, price_per_night, bathroom, room_type "
                    + "FROM room "
                    + "WHERE id NOT IN ("
                    + "SELECT room_id "
                    + "FROM stay "
                    + "WHERE (start_date < ?) AND (expected_end_date IS NULL OR expected_end_date > ?) AND (real_end_date IS NULL OR real_end_date > ?))")) {
                st.setDate(1, dateTo);
                st.setDate(2, dateFrom);
                st.setDate(3, dateFrom);
                return executeQueryForMultipleRooms(st);
            }
        } catch (SQLException ex) {
            logger.error("db connection problem when retrieving rooms by date: " + date + "and len: " + len, ex);
            throw new ServiceFailureException("Error when retrieving rooms by date: " + date + "and len: " + len, ex);
        }
    }

    //pato
    @Override
    public List<Stay> findAllStaysForGuest(Guest guest) {
        logger.debug("Finding all stays for guest : " + guest);
        validate(guest);
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement(
                    "SELECT DISTINCT id, start_date, expected_end_date, real_end_date, guest_id, room_id, minibar_costs "
                    + "FROM stay "
                    + "WHERE guest_id = ?")) {
                st.setLong(1, guest.getId());
                return executeQueryForMultipleStays(st);
            }
        } catch (SQLException ex) {
            logger.error("db connection problem when retrieving stays by guest: " + guest, ex);
            throw new ServiceFailureException("Error when retrieving stays by guest: " + guest, ex);
        }
    }

    //Zuzana
    @Override
    public List<Room> findRoomsForGuestByDate(Guest guest, LocalDate date) {
        logger.debug("Finding rooms for guest : " + guest + " by date: " + date);
        validate(guest);
        if (date == null) {
            throw new IllegalArgumentException("date must not be null");
        }
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement(
                    "SELECT DISTINCT room_id AS id, number, capacity, price_per_night, bathroom, room_type "
                    + "FROM stay JOIN room ON (room.id = stay.room_id) JOIN guest ON (guest.id = stay.guest_id)"
                    + "WHERE stay.guest_id = ? "
                    + "AND start_date <= ? "
                    + "AND (expected_end_date IS NULL OR expected_end_date >= ?) "
                    + "AND (real_end_date IS NULL OR real_end_date >= ?)")) {
                st.setLong(1, guest.getId());
                st.setDate(2, Date.valueOf(date));
                st.setDate(3, Date.valueOf(date));
                st.setDate(4, Date.valueOf(date));
                return executeQueryForMultipleRooms(st);
            }
        } catch (SQLException ex) {
            logger.error("db connection problem when retrieving rooms for guest: " + guest + " by date: " + date, ex);
            throw new ServiceFailureException("Error when retrieving rooms for guest: " + guest + " by date: " + date, ex);
        }
    }

    // pato
    @Override
    public List<Stay> findStaysForRoomByDate(Room room, LocalDate date) {
        logger.debug("Finding stays for room : " + room + " by date: " + date);
        validate(room);
        if (date == null) {
            throw new IllegalArgumentException("date must not be null.");
        }
        Date dateFrom = Date.valueOf(date);
        logger.debug("Finding stays by room " + room + " and date: " + date);
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement(
                    "SELECT DISTINCT id, start_date, expected_end_date, real_end_date, guest_id, room_id, minibar_costs "
                    + "FROM stay "
                    + "WHERE ((start_date <= ?) AND (real_end_date IS NOT NULL AND real_end_date >= ?) OR (real_end_date IS NULL AND (expected_end_date IS NOT NULL AND expected_end_date >= ?))) "
                    + " AND room_id = ? ")) {
                st.setDate(1, dateFrom);
                st.setDate(2, dateFrom);
                st.setDate(3, dateFrom);
                st.setLong(4, room.getId());
                return executeQueryForMultipleStays(st);
            }
        } catch (SQLException ex) {
            logger.error("db connection problem when retrieving stays by room " + room + " and date: " + date, ex);
            throw new ServiceFailureException("Error when retrieving stays by room " + room + " and date: " + date, ex);
        }
    }

    //Zuzana
    @Override
    public List<Guest> findGuestsForRoomByDate(Room room, LocalDate date) {
        logger.debug("Finding guests for room : " + room + " by date: " + date);
        validate(room);
        if (date == null) {
            throw new IllegalArgumentException("date must not be null");
        }
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement(
                    "SELECT DISTINCT guest_id AS id, name, passport_no, email, phone, date_of_birth "
                    + "FROM stay JOIN room ON (room.id = stay.room_id) JOIN guest ON (guest.id = stay.guest_id)"
                    + "WHERE stay.room_id = ? "
                    + "AND start_date <= ? "
                    + "AND (expected_end_date IS NULL OR expected_end_date >= ?) "
                    + "AND (real_end_date IS NULL OR real_end_date >= ?)")) {
                st.setLong(1, room.getId());
                st.setDate(2, Date.valueOf(date));
                st.setDate(3, Date.valueOf(date));
                st.setDate(4, Date.valueOf(date));
                return executeQueryForMultipleGuests(st);
            }
        } catch (SQLException ex) {
            logger.error("db connection problem when retrieving guests for room: " + room + " by date: " + date, ex);
            throw new ServiceFailureException("Error when retrieving guests for room: " + room + " by date: " + date, ex);
        }
    }

    /*
     * pato nie je to asi az tak good metoda
     * chyba tam ta dlzka najde to napr len pre jeden den volnu alebo current date?
     * checknut porovnavanie date  
     */
    @Override
    public List<Room> findFreeRoomByDateAndCapacity(LocalDate date, int capacity) {
        logger.debug("Finding room by date: " + date + " and capacity: " + capacity);
        if (date == null) {
            throw new IllegalArgumentException("date must not be null.");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be positive.");
        }
        Date dateFrom = Date.valueOf(date);
        Date dateTo = Date.valueOf(date.plusDays(1));

        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement(
                    "SELECT  DISTINCT id, number, capacity, price_per_night, bathroom, room_type "
                    + "FROM room "
                    + "WHERE capacity = ? AND id NOT IN ("
                    + "SELECT room_id "
                    + "FROM stay "
                    + "WHERE (start_date < ?) AND (expected_end_date IS NULL OR expected_end_date > ?) AND (real_end_date IS NULL OR real_end_date > ?))")) {

                st.setInt(1, capacity);
                st.setDate(2, dateTo);
                st.setDate(3, dateFrom);
                st.setDate(4, dateFrom);
                return executeQueryForMultipleRooms(st);
            }
        } catch (SQLException ex) {
            logger.error("db connection problem when retrieving rooms by date: " + date + " and capacity: " + capacity, ex);
            throw new ServiceFailureException("Error when retrieving rooms by date: " + date + " and capacity: " + capacity, ex);
        }
    }

    @Override
    public List<Guest> findTop3Guests() {
        logger.debug("Finding top 3 guests. ");
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement(
                    "SELECT guest_id AS id, name, passport_no, email, phone, date_of_birth ,count(guest_id) as countStays"
                    + " FROM stay join guest on (guest_id = guest.id) GROUP BY guest_id,name, passport_no, email, phone, date_of_birth"
                    + " ORDER BY countStays DESC"
            )) {
                List<Guest> guests = executeQueryForMultipleGuests(st);
                List<Guest> top3 = new ArrayList<Guest>();
                if (guests.size() > 0) {
                    top3.add(guests.get(0));
                }
                if (guests.size() > 1) {
                    top3.add(guests.get(1));
                }
                if (guests.size() > 2) {
                    top3.add(guests.get(2));
                }
                return top3;
            }
        } catch (SQLException ex) {
            logger.error("db connection problem when retrieving top 3 guests ", ex);
            throw new ServiceFailureException("Error when retrieving top 3 guests ", ex);
        }
    }

    static Stay executeQueryForSingleStay(PreparedStatement st) throws SQLException, ServiceFailureException {
        ResultSet rs = st.executeQuery();
        Stay result = null;
        if (rs.next()) {
            result = rowToStay(rs);
            if (rs.next()) {
                throw new ServiceFailureException(
                        "Internal error: More entities with same id found "
                        + "(source id: " + result.getId() + ", found " + result + " and " + rowToStay(rs));
            }
        }
        return result;
    }

    static List<Stay> executeQueryForMultipleStays(PreparedStatement st) throws SQLException, ServiceFailureException {
        ResultSet rs = st.executeQuery();
        List<Stay> result = new ArrayList<Stay>();
        while (rs.next()) {
            result.add(rowToStay(rs));
        }
        return result;
    }

    static Guest executeQueryForSingleGuest(PreparedStatement st) throws SQLException, ServiceFailureException {
        ResultSet rs = st.executeQuery();
        Guest result = null;
        if (rs.next()) {
            result = rowToGuest(rs);
            if (rs.next()) {
                throw new ServiceFailureException(
                        "Internal error: More entities with same id found "
                        + "(source id: " + result.getId() + ", found " + result + " and " + rowToGuest(rs));
            }
        }
        return result;
    }

    static List<Guest> executeQueryForMultipleGuests(PreparedStatement st) throws SQLException, ServiceFailureException {
        ResultSet rs = st.executeQuery();
        List<Guest> result = new ArrayList<Guest>();
        while (rs.next()) {
            result.add(rowToGuest(rs));
        }
        return result;
    }

    static Room executeQueryForSingleRoom(PreparedStatement st) throws SQLException, ServiceFailureException {
        ResultSet rs = st.executeQuery();
        Room result = null;
        if (rs.next()) {
            result = rowToRoom(rs);
            if (rs.next()) {
                throw new IllegalArgumentException(
                        "Internal error: More entities with same id found "
                        + "(source id: " + result.getId() + ", found " + result + " and " + rowToRoom(rs));
            }
        }
        return result;
    }

    static List<Room> executeQueryForMultipleRooms(PreparedStatement st) throws SQLException, ServiceFailureException {
        ResultSet rs = st.executeQuery();
        List<Room> result = new ArrayList<Room>();
        while (rs.next()) {
            result.add(rowToRoom(rs));
        }
        return result;
    }

    private static Stay rowToStay(ResultSet rs) throws SQLException {
        Stay result = new Stay();
        result.setId(rs.getLong("id"));
        result.setStartDate(rs.getDate("start_date").toLocalDate());
        result.setExpectedEndDate((rs.getDate("expected_end_date") != null) ? rs.getDate("expected_end_date").toLocalDate() : null);
        result.setRealEndDate((rs.getDate("real_end_date") != null) ? rs.getDate("real_end_date").toLocalDate() : null);
        Long guestId = rs.getLong("guest_id");
        result.setGuest(guestManager.getGuestById(guestId));
        Long roomId = rs.getLong("room_id");
        result.setRoom(roomManager.getRoomById(roomId));
        result.setMinibarCosts(rs.getBigDecimal("minibar_costs"));

        return result;
    }

    private static Guest rowToGuest(ResultSet rs) throws SQLException {
        Guest result = new Guest();
        result.setId(rs.getLong("id"));
        result.setName(rs.getString("name"));
        result.setPassportNo(rs.getString("passport_no"));
        result.setEmail(rs.getString("email"));
        result.setPhone(rs.getString("phone"));
        result.setDateOfBirth(rs.getDate("date_of_birth").toLocalDate());
        return result;
    }

    private static Room rowToRoom(ResultSet rs) throws SQLException {
        Room result = new Room();
        result.setId(rs.getLong("id"));
        result.setNumber(rs.getString("number"));
        result.setCapacity(rs.getInt("capacity"));
        result.setPricePerNight(rs.getBigDecimal("price_per_night"));
        result.setBathroom(rs.getBoolean("bathroom"));
        result.setType(RoomType.valueOf(rs.getString("room_type"))); // praca enum
        return result;
    }

    /**
     * Validates passed stay object. It must not be null, guest, room, startDate
     * attributes must not be null. Minibar costs may not be negative. If
     * specified both, real and expected end dates must be strictly greater then
     * start date.
     *
     * Integrity of Id attribute needs to be checked separately since, different
     * states are expected for different methods.
     *
     * @param stay stay to be checked for validity
     */
    private static void validate(Stay stay) {
        if (stay == null) {
            throw new IllegalArgumentException("stay is null");
        }
        validate(stay.getGuest());
        validate(stay.getRoom());
        if (stay.getMinibarCosts().signum() == -1) {
            throw new IllegalArgumentException("minibar cost can not be negative");
        }
        if (stay.getStartDate() == null) {
            throw new IllegalArgumentException("start date of stay must not be null");
        }
        LocalDate start = stay.getStartDate();
        LocalDate exEnd = stay.getExpectedEndDate();
        LocalDate rEnd = stay.getRealEndDate();
        //expected End date specified and not greater then start date
        if ((exEnd != null) && (start.compareTo(exEnd) == 1)) {
            throw new IllegalArgumentException("Expected end date must be after start date");
        }
        //real end date specified and not greater then start date
        if ((rEnd != null) && (start.compareTo(rEnd) == 1)) {
            throw new IllegalArgumentException("Real end date must be after start date");
        }
    }

    /**
     * Validates passed room object. It must not be null, number must must not
     * be null nor empty, capacity and pricePerNight must be positive, Room ID
     * must not be null since any stay manipulates only rooms already in DB.
     *
     * @param room room to be checked for validity
     */
    private static void validate(Room room) {
        if (room == null) {
            throw new IllegalArgumentException("room is null");
        }
        if (room.getId() == null) {
            throw new IllegalArgumentException("room id must not be null");
        }
        if (room.getCapacity() <= 0) {
            throw new IllegalArgumentException("room must have positive capacity");
        }
        if (room.getNumber() == null) {
            throw new IllegalArgumentException("room number must not be null");
        }
        if (room.getNumber().equals("")) {
            throw new IllegalArgumentException("room number must not be empty");
        }
        if (room.getNumber().length() < 4) {
            throw new IllegalArgumentException("room number must have some format");
        }
        if (room.getPricePerNight().signum() < 1) { //testuje ci je to zaporne
            throw new IllegalArgumentException("price per night must be positive");
        }
    }

    /**
     * Validates passed guest object. It must not be null, name must must not be
     * null nor empty. Guest ID must not be null since any stay manipulates only
     * guests already in DB.
     *
     * @param guest guest to be checked for validity
     */
    private static void validate(Guest guest) {
        if (guest == null) {
            throw new IllegalArgumentException("guest is null");
        }
        if (guest.getId() == null) {
            throw new IllegalArgumentException("guest id must not be null");
        }
        if (guest.getName() == null) {
            throw new IllegalArgumentException("guest name must not be null");
        }
        if (guest.getName().equals("")) {
            throw new IllegalArgumentException("guest name must not be empty");
        }
    }

}
