/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.project;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;


//import java.sql.*;

/**
 *
 * @author pato
 */
public class RoomManagerImpl implements RoomManager{
    //Logger
    //private static final Logger logger = Logger.getLogger(GuestManagerImpl.class.getName());
    //DataSource
    private DataSource dataSource;


    public RoomManagerImpl(DataSource dataSource) { //asi takyto nazov metody
        this.dataSource = dataSource;
    }

    public void checkDataSource() {
        if (dataSource == null) {
            throw new IllegalArgumentException("DataSource is not ser");
        }
    }
      
    @Override
    public void createRoom(Room r) {
        checkDataSource();
        validate(r);
        if (r.getId() != null) {
            throw new IllegalArgumentException("room id already set");
        }

        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement(
                    "INSERT INTO ROOM (number, capacity, price_per_night, bathroom, room_type) "
                    + "VALUES(?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                st.setString(1, r.getNumber());
                st.setInt(2, r.getCapacity());
                st.setBigDecimal(3, r.getPricePerNight());
                st.setBoolean(4, r.hasBathroom());
                st.setString(5, r.getType().toString());
                int addedRows = st.executeUpdate();
                if (addedRows != 1) {
                    throw new ServiceFailureException("Internal Error: More rows inserted when one expected.");
                }
                ResultSet keyRS = st.getGeneratedKeys();
                r.setId(getKey(keyRS, r));
            }

        } catch (SQLException ex) {
            //logger.error("db connection problem", ex);
            throw new ServiceFailureException("Error when creatig new room", ex);
        }
    }
    
    private Long getKey(ResultSet keyRS, Room room) throws ServiceFailureException, SQLException {
        if (keyRS.next()) {
            if (keyRS.getMetaData().getColumnCount() != 1) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retriving failed when trying to insert guest " + room
                        + " - wrong key fields count: " + keyRS.getMetaData().getColumnCount());
            }
            Long result = keyRS.getLong(1);
            if (keyRS.next()) {
                throw new ServiceFailureException("Internal Error: Generated key"
                        + "retriving failed when trying to insert guest " + room
                        + " - more keys found");
            }
            return result;
        } else {
            throw new ServiceFailureException("Internal Error: Generated key"
                    + "retriving failed when trying to insert guest " + room
                    + " - no key found");
        }
    }    
    
    @Override
    public Room getRoomById(Long id) {
        checkDataSource();
        if (id == null) {
            throw new IllegalArgumentException("id must not be null");
        }
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("SELECT id, number, capacity, price_per_night, bathroom, room_type FROM room WHERE id = ?")) {
                st.setLong(1, id);
                return executeQueryForSingleRoom(st);
            }
        } catch (SQLException ex) {
            //logger.error("db connection problem", ex);
            throw new ServiceFailureException("Error when retrieving room with id " + id, ex);
        }
    }

    @Override
    public void updateRoom(Room r) {
        checkDataSource();
        validate(r);
        if (r.getId() == null) {
            throw new IllegalArgumentException("room id must not be null when updating");
        }
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("UPDATE room SET number = ?, capacity = ?, price_per_night = ?, bathroom = ?, room_type = ? WHERE id = ?")) { //zatvorka???
                st.setString(1, r.getNumber());
                st.setInt(2, r.getCapacity());
                st.setBigDecimal(3, r.getPricePerNight());
                st.setBoolean(4, r.hasBathroom());
                st.setString(5, r.getType().toString());
                st.setLong(6, r.getId());
                int updatedRows = st.executeUpdate();
                if (updatedRows != 1) {
                    throw new IllegalArgumentException("Internal Error: More rows updated when one was expected.");
                }
            }
        } catch (SQLException ex) {
            //logger.error("db connection problem", ex);
            throw new ServiceFailureException("Error when updating room" , ex);
        } 
    }

    @Override
    public void deleteRoom(Room r) {
        checkDataSource();
        validate(r);
        if (r.getId() == null) {
            throw new IllegalArgumentException("room id must not be null when deleting");
        }
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("DELETE FROM room WHERE id = ?")) {
                st.setLong(1, r.getId());
                int removerRows = st.executeUpdate();
                if (removerRows != 1) {
                    throw new ServiceFailureException("Internal Error: More rows removed when one was expected, id = " + r.getId());
                }
            }
        } catch (SQLException ex) {
            //logger.error("db connection problem", ex);
            throw new ServiceFailureException("Error when deleting room", ex);
        }
    }

    @Override
    public List<Room> findAllRooms() {
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("SELECT id, number, capacity, price_per_night, bathroom, room_type FROM room")) {
                return executeQueryForMultipleRooms(st);
            }
        } catch (SQLException ex) {
            //logger.error("db connection problem", ex);
            throw new ServiceFailureException("Error when retrieving all rooms", ex);
        }
    }

    @Override
    public Room findRoomByNumber(String n) {
        checkDataSource();
        if (n == null) {
            throw new IllegalArgumentException("name must not be null");
        }
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement st = conn.prepareStatement("SELECT id, number, capacity, price_per_night, bathroom, room_type FROM room WHERE number = ?")) {
                st.setString(1, n);
                return executeQueryForSingleRoom(st);
            }
        } catch (SQLException ex) {
            //logger.error("db connection problem", ex);
            throw new ServiceFailureException("Error when retrieving room with number" + n, ex);
        }
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
     * Validates passed room object. It must not be null, number must must not be
     * null nor empty, capacity and pricePerNight must be positive,
     *
     * Integrity of Id attribute needs to be checked separately since, different
     * states are expected for different methods.
     *
     * @param room room to be checked for validity
     */
    private static void validate(Room room) {
       try{ if (room == null) {
            throw new IllegalArgumentException("room is null");
        }
        if (room.getCapacity() <= 0) {
            throw new ValidationException("room must have positive capacity");
        }
        if (room.getNumber() == null) {
            throw new ValidationException("room number must not be null");
        }
        if (room.getNumber().equals("")) {
            throw new ValidationException("room number must not be empty");
        }
        if (room.getNumber().length() < 4) {
            throw new ValidationException("room number must have some format");
        }
        if (room.getPricePerNight().signum() < 0){ //testuje ci je to zaporne
            throw new ValidationException("price per night must be positive");
        }      
       }
       catch (ValidationException e){
           throw new IllegalArgumentException("Validation failed ",e);
       }
    }
}

