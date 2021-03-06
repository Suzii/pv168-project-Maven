/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.project;

import cz.muni.fi.pv168.project.common.DBUtils;
import java.sql.SQLException;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.After;

/**
 *
 * @author pato
 */
public class RoomManagerImplTest {

    private RoomManagerImpl manager;
    private DataSource ds;

    private static DataSource prepareDataSource() throws SQLException {
        BasicDataSource ds = new BasicDataSource();
        //we will use in memory database
        ds.setUrl("jdbc:derby:memory:guestmgr-test;create=true");
        return ds;
    }

    @Before
    public void setUp() throws SQLException {
        ds = prepareDataSource();
        manager = new RoomManagerImpl(ds);
        //create tables!!
        DBUtils.executeSqlScript(ds, GuestManager.class.getResourceAsStream("/createTables.sql"));
    }

    @After
    public void tearDown() throws SQLException {
        DBUtils.executeSqlScript(ds, GuestManager.class.getResourceAsStream("/dropTables.sql"));
    }

    /*
     @Before
     public void setUp() throws SQLException{
     BasicDataSource bds = new BasicDataSource();
     bds.setUrl("jdbc:derby:memory:RoomManagerTest;create=true");
     this.dataSource = bds;
     //create new empty table before every test
     try (Connection conn = bds.getConnection()) {
     conn.prepareStatement("CREATE TABLE ROOM (" 
     + "ID BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, "
     + "NUMBER VARCHAR(10) NOT NULL "
     + "CHECK(NUMBER = UPPER(NUMBER)), "
     + "CAPACITY INTEGER  NOT NULL"
     + " CHECK(CAPACITY > 0), "
     + "PRICE_PER_NIGHT DECIMAL(12,2) NOT NULL "
     + "CHECK(PRICE_PER_NIGHT > 0), "
     + "BATHROOM BOOLEAN NOT NULL, "
     + "ROOM_TYPE VARCHAR(255)) " 
     //+ "FOREIGNKEY(ROOM_TYPE) REFERENCES ROOM_TYPE(TYPE))"
     ).executeUpdate();
     /*conn.prepareStatement("CREATE TABLE ROOM_TYPE ("
     +"TYPE VARCHAR(255) PRIMARY KEY )"
     ).executeUpdate();*/
    /*       }
     manager = new RoomManagerImpl(bds);
     }

     @After
     public void tearDown() throws SQLException {
     DBUtils.executeSqlScript(dataSource,GuestManager.class.getResourceAsStream("/dropTables.sql"));
     }
     /*
     @After
     public void tearDown() throws SQLException {
     try (Connection con = dataSource.getConnection()) {
     con.prepareStatement("DROP TABLE ROOM").executeUpdate();
     }
     }
     */
    /**
     * Test of createRoom method, of class RoomManagerImpl.
     */
    @Test
    public void createRoom() {
        Room room = newRoom("A721", 2, new BigDecimal("10"), false, RoomType.STANDARD);
        manager.createRoom(room);

        Long roomId = room.getId();
        assertNotNull(roomId);
        Room result = manager.getRoomById(roomId);
        assertEquals(room, result);
        assertNotSame(room, result);
        assertDeepEquals(room, result);

    }

    @Test
    public void createRoom2() {
        // these variants should be ok
        Room room = newRoom("A101", 2, new BigDecimal("10"), false, RoomType.STANDARD); //100 nie je ale 101 ano
        manager.createRoom(room);
        Room result = manager.getRoomById(room.getId());
        assertNotNull(result);
/*
        room = newRoom("A102", 1, new BigDecimal("10"), false, RoomType.STANDARD); //minimalna capacita
        manager.createRoom(room);
        result = manager.getRoomById(room.getId());
        assertNotNull(result);
  */  }

    @Test
    public void getRoomById() {

        assertNull(manager.getRoomById(1l));

        Room room = newRoom("A721", 2, new BigDecimal("10"), false, RoomType.STANDARD);
        manager.createRoom(room);
        Long roomId = room.getId();

        Room result = manager.getRoomById(roomId);
        assertEquals(room, result);
        assertDeepEquals(room, result);
    }

    @Test
    public void getAllRooms() {

        assertTrue(manager.findAllRooms().isEmpty());

        Room r1 = newRoom("A721", 2, new BigDecimal("10"), false, RoomType.STANDARD);
        Room r2 = newRoom("B721", 2, new BigDecimal("80"), true, RoomType.STUDIO);

        manager.createRoom(r1);
        manager.createRoom(r2);

        List<Room> expected = Arrays.asList(r1, r2);
        List<Room> actual = manager.findAllRooms();

        Collections.sort(actual, idComparator);
        Collections.sort(expected, idComparator);

        assertEquals(expected, actual);
        assertDeepEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createRoomWithNonExistingRoom() {
        manager.createRoom(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createRoomWithNonExistingID() {
        Room room = newRoom("A721", 2, new BigDecimal("10"), false, RoomType.STANDARD);
        room.setId(1l);
        manager.createRoom(room);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createRoomWithNumberNull() {
        Room room = newRoom(null, 2, new BigDecimal("10"), false, RoomType.STANDARD);
        manager.createRoom(room);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createRoomWithWrongCapacity() {
        Room room = newRoom("A721", 0, new BigDecimal("10"), false, RoomType.STANDARD);
        manager.createRoom(room);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createRoomWithWrongPricePetNightNegative() {
        Room room = newRoom("A721", 0, new BigDecimal("-0.5"), false, RoomType.STANDARD);
        manager.createRoom(room);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createRoomWithWrongPricePetNightZero() {
        Room room = newRoom("A721", 0, new BigDecimal("0"), false, RoomType.STANDARD);
        manager.createRoom(room);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createRoomWithWrongNumber() {
        Room room = newRoom("1", 0, new BigDecimal("0"), false, RoomType.STANDARD);
        manager.createRoom(room);
    }

    // BigDecimalScale
    @Test
    public void updateRoom() {
        Room room = newRoom("A721", 2, new BigDecimal("10").setScale(2), false, RoomType.STANDARD);
        Room r2 = newRoom("B721", 2, new BigDecimal("80").setScale(2), true, RoomType.STUDIO);
        manager.createRoom(room);
        manager.createRoom(r2);
        Long roomId = room.getId();
        Room result;
        

        room = manager.getRoomById(roomId);
        room.setNumber("A121");
        manager.updateRoom(room);
        result = manager.getRoomById(roomId);
        assertDeepEquals(room, result);
                
        /*assertEquals("A121", room.getNumber());
        assertEquals(new BigDecimal("10").setScale(2), room.getPricePerNight());
        assertEquals(2, room.getCapacity());
        assertEquals(false, room.hasBathroom());
        assertEquals(RoomType.STANDARD, room.getType());*/

        room = manager.getRoomById(roomId);
        room.setPricePerNight(new BigDecimal("9").setScale(2));
        manager.updateRoom(room);
        result = manager.getRoomById(roomId);
        assertDeepEquals(room, result);
       /*assertEquals("A121", room.getNumber());
        assertEquals(new BigDecimal("9").setScale(2), room.getPricePerNight());
        assertEquals(2, room.getCapacity());
        assertEquals(false, room.hasBathroom());
        assertEquals(RoomType.STANDARD, room.getType());*/

        room = manager.getRoomById(roomId);
        room.setCapacity(3);
        manager.updateRoom(room);
        result = manager.getRoomById(roomId);
        assertDeepEquals(room, result);
        /*assertEquals("A121", room.getNumber());
        assertEquals(new BigDecimal("9").setScale(2), room.getPricePerNight());
        assertEquals(3, room.getCapacity());
        assertEquals(false, room.hasBathroom());
        assertEquals(RoomType.STANDARD, room.getType());*/

        room = manager.getRoomById(roomId);
        room.setBathroom(true);
        manager.updateRoom(room);
        result = manager.getRoomById(roomId);
        assertDeepEquals(room, result);
        
        /*assertEquals("A121", room.getNumber());
        assertEquals(new BigDecimal("9").setScale(2), room.getPricePerNight());
        assertEquals(3, room.getCapacity());
        assertEquals(true, room.hasBathroom());
        assertEquals(RoomType.STANDARD, room.getType());*/

        room = manager.getRoomById(roomId);
        room.setType(RoomType.APARTMENT);
        manager.updateRoom(room);
        result = manager.getRoomById(roomId);
        assertDeepEquals(room, result);
       
        /*assertEquals("A121", room.getNumber());
        assertEquals(new BigDecimal("9").setScale(2), room.getPricePerNight());
        assertEquals(3, room.getCapacity());
        assertEquals(true, room.hasBathroom());
        assertEquals(RoomType.APARTMENT, room.getType());*/

        // Check if updates didn't affected other records
        assertDeepEquals(r2, manager.getRoomById(r2.getId()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateRoomWithNonExistingRoom() {
        Room room = newRoom("A721", 2, new BigDecimal("10"), false, RoomType.STANDARD);
        manager.createRoom(room);
        manager.updateRoom(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateRoomWithIdNull() {
        Room room = newRoom("A721", 2, new BigDecimal("10"), false, RoomType.STANDARD);
        manager.createRoom(room);
        room.setId(null);
        manager.updateRoom(room);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateRoomWithNonExistingId() {
        Room room = newRoom("A721", 2, new BigDecimal("10"), false, RoomType.STANDARD);
        manager.createRoom(room);
        Long roomId = room.getId();
        room.setId(roomId + 1);
        manager.updateRoom(room);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateRoomWithWrongNumber() {
        Room room = newRoom("A721", 2, new BigDecimal("10"), false, RoomType.STANDARD);
        manager.createRoom(room);
        room.setNumber("-1");
        manager.updateRoom(room);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateRoomWithNullNumber() {
        Room room = newRoom("A721", 2, new BigDecimal("10"), false, RoomType.STANDARD);
        manager.createRoom(room);
        room.setNumber(null);
        manager.updateRoom(room);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateRoomWithWrongCapacity() {
        Room room = newRoom("A721", 2, new BigDecimal("10"), false, RoomType.STANDARD);
        manager.createRoom(room);
        room.setCapacity(0);
        manager.updateRoom(room);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateRoomWithNegativePricePerNight() {
        Room room = newRoom("A721", 2, new BigDecimal("10"), false, RoomType.STANDARD);
        manager.createRoom(room);
        room.setPricePerNight(new BigDecimal("-10"));
        manager.updateRoom(room);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateRoomWithZeroPricePerNight() {
        Room room = newRoom("A721", 2, new BigDecimal("10"), false, RoomType.STANDARD);
        manager.createRoom(room);
        room.setPricePerNight(new BigDecimal("0"));
        manager.updateRoom(room);
    }

    /**
     * Test of deleteRoom method, of class RoomManagerImpl.
     */
    @Test
    public void deleteRoom() {
        Room r1 = newRoom("A721", 2, new BigDecimal("50"), false, RoomType.STANDARD);
        Room r2 = newRoom("B721", 2, new BigDecimal("80"), true, RoomType.STUDIO);

        manager.createRoom(r1);
        manager.createRoom(r2);

        assertNotNull(manager.getRoomById(r1.getId()));
        assertNotNull(manager.getRoomById(r2.getId()));

        manager.deleteRoom(r1);

        assertNull(manager.getRoomById(r1.getId()));
        assertNotNull(manager.getRoomById(r2.getId()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteRoomWithNonExistingRoom() {
        Room room = newRoom("A721", 2, new BigDecimal("0"), false, RoomType.STANDARD);
        manager.deleteRoom(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteRoomWithIdNull() {
        Room room = newRoom("A721", 2, new BigDecimal("0"), false, RoomType.STANDARD);
        room.setId(null);
        manager.deleteRoom(room);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteRoomWithNonExistingId() {
        Room room = newRoom("A721", 2, new BigDecimal("0"), false, RoomType.STANDARD);
        room.setId(1l);
        manager.deleteRoom(room);
    }

    /**
     * Test of findRoomByNumber method, of class RoomManagerImpl.
     */
    @Test
    public void findRoomByNumber() {
        assertNull(manager.findRoomByNumber("1"));

        Room room = newRoom("A721", 2, new BigDecimal("10"), false, RoomType.STANDARD);
        manager.createRoom(room);
        Room result = manager.findRoomByNumber("A721");

        assertEquals(room, result);
        assertDeepEquals(room, result);

    }

    @Test(expected = IllegalArgumentException.class)
    public void findRoomWithWrongNullNumber() {
        assertNull(manager.findRoomByNumber("1"));
        Room room = newRoom("A721", 2, new BigDecimal("10"), false, RoomType.STANDARD);
        manager.createRoom(room);
        manager.findRoomByNumber(null);
    }

    private static Room newRoom(String num, int capacity, BigDecimal costs, boolean bath, RoomType type) {
        Room room = new Room();
        room.setNumber(num);
        room.setCapacity(capacity);
        room.setPricePerNight(costs);
        room.setBathroom(bath);
        room.setType(type);
        return room;
    }

    private void assertDeepEquals(List<Room> expectedList, List<Room> actualList) {
        for (int i = 0; i < expectedList.size(); i++) {
            Room expected = expectedList.get(i);
            Room actual = actualList.get(i);
            assertDeepEquals(expected, actual);
        }
    }

    private void assertDeepEquals(Room expected, Room actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getCapacity(), actual.getCapacity());
        assertEquals(expected.getNumber(), actual.getNumber());
        assertEquals(expected.getPricePerNight().setScale(2), actual.getPricePerNight());
        assertEquals(expected.hasBathroom(), actual.hasBathroom());
        assertEquals(expected.getType(), actual.getType());
    }

    private static Comparator<Room> idComparator = new Comparator<Room>() {

        @Override
        public int compare(Room o1, Room o2) {
            return Long.valueOf(o1.getId()).compareTo(Long.valueOf(o2.getId()));
        }
    };

}
