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
import java.util.List;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
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

    @Override
    public void createStay(Stay stay) {
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

    @Override
    public Stay getStayById(Long id) {
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

    @Override
    public void updateStay(Stay stay) {
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

    @Override
    public void deleteStay(Stay stay) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Stay> findAllStays() {
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

    @Override
    public List<Stay> findStaysByDate(LocalDate from, LocalDate to) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Guest> findStayingGuestsByDate(LocalDate date) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Room> findFreeRoomsByDateAndLen(LocalDate date, int len) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Stay> findAllStaysForGuest(Guest guest) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Room> findRoomsForGuestByDate(Guest guest, LocalDate date) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Stay> findStaysForRoomByDate(Room room, LocalDate date) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Guest> findGuestsForRoomByDate(Room room, LocalDate date) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Room> findFreeRoomByDateAndCapacity(LocalDate date, int capacity) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Guest> findTop3Guests() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
