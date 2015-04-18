/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.project;

import cz.muni.fi.pv168.project.common.ServiceFailureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import javax.sql.DataSource;

import java.util.ArrayList;
import java.util.List;
//import java.util.logging.Logger;

import javax.sql.rowset.serial.SerialException;

/**
 *
 * @author Zuzana
 */
public class GuestManagerImpl implements GuestManager {

    //Logger
    private final static Logger logger = LoggerFactory.getLogger(GuestManagerImpl.class);
    //DataSource
    private final DataSource dataSource;

    public GuestManagerImpl(DataSource dataSource) {
        if (dataSource == null) {
            throw new IllegalArgumentException("DataSource is not set");
        }
        this.dataSource = dataSource;
    }

    @Override
    public void createGuest(Guest guest) {
        logger.debug("Creating guest : " + guest);
        validate(guest);
        if (guest.getId() != null) {
            throw new IllegalArgumentException("guest id already set");
        }

        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement(
                    "INSERT INTO GUEST (name, passport_no, email, phone, date_of_birth) "
                    + "VALUES(?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                st.setString(1, guest.getName());
                st.setString(2, guest.getPassportNo());
                st.setString(3, guest.getEmail());
                st.setString(4, guest.getPhone());
                Date d = null;
                LocalDate dd = guest.getDateOfBirth();
                if (dd != null) {
                    d = Date.valueOf(dd);
                }
                st.setDate(5, d);
                int addedRows = st.executeUpdate();
                if (addedRows != 1) {
                    throw new ServiceFailureException("Internal Error: More rows inserted when one expected.");
                }
                ResultSet keyRS = st.getGeneratedKeys();
                guest.setId(getKey(keyRS, guest));
            }

        } catch (SQLException ex) {
            logger.error("db connection problem when creating guest: " + guest, ex);
            throw new ServiceFailureException("Error when creatig new guest", ex);
        }
    }

    private Long getKey(ResultSet keyRS, Guest guest) throws ServiceFailureException, SQLException {
        if (keyRS.next()) {
            if (keyRS.getMetaData().getColumnCount() != 1) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retriving failed when trying to insert guest " + guest
                        + " - wrong key fields count: " + keyRS.getMetaData().getColumnCount());
            }
            Long result = keyRS.getLong(1);
            if (keyRS.next()) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retriving failed when trying to insert guest " + guest
                        + " - more keys found");
            }
            return result;
        } else {
            throw new ServiceFailureException("Internal Error: Generated key"
                    + "retriving failed when trying to insert guest " + guest
                    + " - no key found");
        }
    }

    @Override
    public Guest getGuestById(Long id) throws ServiceFailureException {
        logger.debug("Getting guest by id : " + id);
        if (id == null) {
            throw new IllegalArgumentException("id must not be null");
        }
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("SELECT id, name, passport_no, email, phone, date_of_birth FROM guest WHERE id = ?")) {
                st.setLong(1, id);
                return executeQueryForSingleGuest(st);
            }
        } catch (SQLException ex) {
            logger.error("db connection problem when retrieving guest by id. Id: " + id, ex);
            throw new ServiceFailureException("Error when retrieving guest with id " + id, ex);
        }
    }

    @Override
    public void updateGuest(Guest guest) {
        logger.debug("Updating guest : " + guest);
        validate(guest);
        if (guest.getId() == null) {
            throw new IllegalArgumentException("guest id must not be null when updating");
        }
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("UPDATE guest SET name = ?, passport_no = ?, email = ?, phone = ?, date_of_birth = ? WHERE id = ?")) {
                st.setString(1, guest.getName());
                st.setString(2, guest.getPassportNo());
                st.setString(3, guest.getEmail());
                st.setString(4, guest.getPhone());
                st.setDate(5, Date.valueOf(guest.getDateOfBirth()));
                st.setLong(6, guest.getId());
                int updatedRows = st.executeUpdate();
                if (updatedRows != 1) {
                    throw new IllegalArgumentException("Internal Error: More rows updated when one was expected.");
                }
            }
        } catch (SQLException ex) {
            logger.error("db connection problem when updating guest: " + guest, ex);
            throw new ServiceFailureException("Error when updating guest", ex);
        }
    }

    @Override
    public void deleteGuest(Guest guest) {
        logger.debug("Deleting guest : " + guest);
        validate(guest);
        if (guest.getId() == null) {
            throw new IllegalArgumentException("guest id must not be null when deleting");
        }
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("DELETE FROM guest WHERE id = ?")) {
                st.setLong(1, guest.getId());
                int removedRows = st.executeUpdate();
                if (removedRows != 1) {
                    throw new IllegalArgumentException("Internal Error: More rows removed when one was expected, id = " + guest.getId());
                }
            }
        } catch (SQLException ex) {
            logger.error("db connection problem when deleting guest: " + guest, ex);
            throw new ServiceFailureException("Error when deleting guest", ex);
        }
    }

    @Override
    public List<Guest> findAllGuests() {
        logger.debug("Finding all guests");
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("SELECT id, name, passport_no, email, phone, date_of_birth FROM guest")) {
                return executeQueryForMultipleGuests(st);
            }
        } catch (SQLException ex) {
            logger.error("db connection problem when retrieving all guests", ex);
            throw new ServiceFailureException("Error when retrieving all guests", ex);
        }
    }

    @Override
    public List<Guest> findGuestByName(String name) {
        logger.debug("Finding guest by name : " + name);
        if (name == null) {
            throw new IllegalArgumentException("name must not be null");
        }
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("SELECT id, name, passport_no, email, phone, date_of_birth FROM guest WHERE name = ?")) {
                st.setString(1, name);
                return executeQueryForMultipleGuests(st);
            }
        } catch (SQLException ex) {
            logger.error("db connection problem when looking for guest by name: " + name, ex);
            throw new ServiceFailureException("Error when retrieving guests with name" + name, ex);
        }
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

    private static Guest rowToGuest(ResultSet rs) throws SQLException {
        Guest result = new Guest();
        result.setId(rs.getLong("id"));
        result.setName(rs.getString("name"));
        result.setPassportNo(rs.getString("passport_no"));
        result.setEmail(rs.getString("email"));
        result.setPhone(rs.getString("phone"));
        Date d = rs.getDate("date_of_birth");
        if (d != null) {
            result.setDateOfBirth(d.toLocalDate());
        }
        return result;
    }

    /**
     * Validates passed guest object. It must not be null, name must must not be
     * null nor empty.
     *
     * Integrity of Id attribute needs to be checked separately since, different
     * states are expected for different methods.
     *
     * @param guest guest to be checked for validity
     */
    private static void validate(Guest guest) {
        if (guest == null) {
            throw new IllegalArgumentException("guest is null");
        }
        if (guest.getName() == null) {
            throw new IllegalArgumentException("guest name must not be null");
        }
        if (guest.getName().equals("")) {
            throw new IllegalArgumentException("guest name must not be empty");
        }
    }
}
