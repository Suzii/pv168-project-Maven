/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/*  -------------------- JDBC notes -------------------------
 * 
 * Prepared statement do  try zatvorky, potom netreba finally,
 * po skonceni try bolku sa automaticky zavola close.
 * "Try with resources"
 * 
 * Transakcie - nemozeme pouzivat rovnaky connection na vsetky transakcie.
 * ak je tam viac uzivatelov/ vlakien, kazde musi mat vlastny connection
 * NESMIEME pouzivat jeden
 * 
 * Pouzivat DATA SOURCE + data pooling
 * (spojenie sa do poolu vracia potomcou close, nezavrie sa vsak skutoce, 
 * je dostuypne pre dalsich uzivatelov)
 */
package cz.muni.fi.pv168.project;

import cz.muni.fi.pv168.project.common.DBUtils;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import javax.sql.DataSource;
import org.junit.Rule;
import org.junit.internal.runners.statements.ExpectException;
import org.junit.rules.ExpectedException;
import org.apache.commons.dbcp2.BasicDataSource;

/**
 * @author Zuzana
 */
public class StayManagerImplTest {

    private StayManagerImpl manager;
    private GuestManagerImpl guestManager;
    private RoomManagerImpl roomManager;
    private StayBuilder stayBuilder;
    private GuestBuilder guestBuilder;
    private RoomBuilder roomBuilder;
    private Guest goodGuest;
    private Room goodRoom;

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
        //create tables!!
        DBUtils.executeSqlScript(ds, StayManager.class.getResourceAsStream("/createTables.sql"));
        manager = new StayManagerImpl(ds);
        guestManager = new GuestManagerImpl(ds);
        roomManager = new RoomManagerImpl(ds);
        stayBuilder = new StayBuilder();
        guestBuilder = new GuestBuilder();
        roomBuilder = new RoomBuilder();

        //guest already inDB
        goodGuest = guestBuilder.build();
        guestManager.createGuest(goodGuest);

        //room already inDB
        goodRoom = roomBuilder.build();
        roomManager.createRoom(goodRoom);
    }
    
    @After
    public void tearDown() throws SQLException {
        System.out.println("Tabes droppped");
        DBUtils.executeSqlScript(ds,GuestManager.class.getResourceAsStream("/dropTables.sql"));
    }

    /**
     * Pato
     */
    @Test
    public void createStay() {

        Stay stay = stayBuilder
                .guest(goodGuest)
                .room(goodRoom)
                .build();
        manager.createStay(stay);

        Long stayId = stay.getId();
        assertNotNull(stayId);
        Stay result = manager.getStayById(stayId);
        assertEquals(stay, result);
        assertNotSame(stay, result);
        assertDeepEquals(stay, result);
    }

    /**
     * Pato
     *
     * Ked sa vytvara stay treba overit ci uz tam niekto nebyva !!!!!!!!!!!!!!!
     */
    @Test(expected = IllegalArgumentException.class)
    public void createStayWithNonExistingRoom() {
        Room notInDbRoom = roomBuilder
                .number("C001")
                .build();
        Stay stay = stayBuilder
                .guest(goodGuest)
                .room(notInDbRoom)
                .build();
        // room not in db room
        manager.createStay(stay);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createStayWithNonExistingGuest() {
        Guest guestNotInDB = guestBuilder
                .name("Poor Guest")
                .build();

        Stay stay = stayBuilder
                .guest(guestNotInDB)
                .room(goodRoom)
                .build();
        //Given guest not in DB
        manager.createStay(stay);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createStayWithStartDateNull() {
        Stay stay = stayBuilder
                .guest(goodGuest)
                .room(goodRoom)
                .startDate(null)
                .build();
        manager.createStay(stay);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createStayWithWrongEndDate() {
        //endDate is earlier then startdate
        Stay stay = stayBuilder
                .guest(goodGuest)
                .room(goodRoom)
                .startDate(LocalDate.of(2015, 1, 1))
                .expectedEndDate(LocalDate.of(2014, 1, 12))
                .build();

        manager.createStay(stay);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createStayWithWrongMinibarCosts() {
        Stay stay = stayBuilder
                .guest(goodGuest)
                .room(goodRoom)
                .minibarCosts(new BigDecimal("-5.00"))
                .build();

        manager.createStay(stay);
    }

    /**
     * Zuzana
     */
    @Test
    public void getStayById() {
        assertNull(manager.getStayById(1l));

        Stay stay = stayBuilder
                .guest(goodGuest)
                .room(goodRoom)
                .build();
        manager.createStay(stay);
        Long stayId = stay.getId();
        Stay result = manager.getStayById(stayId);
        assertEquals(stay, result);
        assertDeepEquals(stay, result);
    }

    /**
     * Zuzana
     */
    @Test(expected = IllegalArgumentException.class)
    public void getStayByIdWithIdNull() {
        Stay stay = stayBuilder
                .guest(goodGuest)
                .room(goodRoom)
                .build();
        manager.createStay(stay);
        stay.setId(null);
        manager.getStayById(stay.getId());
    }

    /**
     * Zuzana
     */
    @Test
    public void updateStay() {
        Guest g1 = guestBuilder.build();
        Guest g2 = guestBuilder
                .name("James Bond")
                .passportNo("234")
                .build();
        Room r1 = roomBuilder.build();
        Room r2 = roomBuilder
                .number("A222")
                .type(RoomType.APARTMENT)
                .build();
        Stay s1 = stayBuilder
                .guest(g1)
                .room(r1)
                .build();

        Stay s2 = stayBuilder
                .startDate(LocalDate.of(2015, 2, 20))
                .expectedEndDate(LocalDate.of(2015, 3, 1))
                .guest(g2)
                .room(r2)
                .build();

        guestManager.createGuest(g1);
        guestManager.createGuest(g2);
        roomManager.createRoom(r1);
        roomManager.createRoom(r2);
        manager.createStay(s1);
        manager.createStay(s2);
        Long stayId = s1.getId();

        Stay stay = manager.getStayById(stayId);
        stay.setStartDate(LocalDate.of(2015, 1, 2));
        s1.setStartDate(LocalDate.of(2015, 1, 2));
        manager.updateStay(stay);
        assertDeepEquals(s1, stay);

        stay = manager.getStayById(stayId);
        stay.setExpectedEndDate(LocalDate.of(2015, 1, 5));
        s1.setExpectedEndDate(LocalDate.of(2015, 1, 5));
        manager.updateStay(stay);
        assertDeepEquals(s1, stay);

        stay = manager.getStayById(stayId);
        stay.setRealEndDate(LocalDate.of(2015, 1, 5));
        s1.setRealEndDate(LocalDate.of(2015, 1, 5));
        manager.updateStay(stay);
        assertDeepEquals(s1, stay);

        stay = manager.getStayById(stayId);
        stay.setGuest(g2);
        s1.setGuest(g2);
        manager.updateStay(stay);
        assertDeepEquals(s1, stay);

        stay = manager.getStayById(stayId);
        stay.setRoom(r2);
        s1.setRoom(r2);
        manager.updateStay(stay);
        assertDeepEquals(s1, stay);

        stay = manager.getStayById(stayId);
        stay.setMinibarCosts(new BigDecimal("1.10"));
        s1.setMinibarCosts(new BigDecimal("1.10"));
        manager.updateStay(stay);
        assertDeepEquals(s1, stay);

        assertDeepEquals(s1, manager.getStayById(stayId));

        //check effect on other record
        assertDeepEquals(s2, manager.getStayById(s2.getId()));
    }

    /**
     * Zuzana
     */
    @Test(expected = IllegalArgumentException.class)
    public void updateStayWithStayNull() {
        manager.updateStay(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateStayWithStayIdNull() {
        Stay stay = stayBuilder
                .guest(goodGuest)
                .room(goodRoom)
                .build();

        manager.updateStay(stay);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateStayWithWrongStayId() {
        Stay stay = stayBuilder
                .guest(goodGuest)
                .room(goodRoom)
                .build();
        manager.createStay(stay);
        stay = manager.getStayById(stay.getId());
        stay.setId(stay.getId() + 1);

        manager.updateStay(stay);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateStayWithGuestNull() {
        Stay stay = stayBuilder
                .guest(goodGuest)
                .room(goodRoom)
                .build();
        manager.createStay(stay);
        stay.setGuest(null);
        manager.updateStay(stay);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateStayWithNonExistingGuest() {
        Stay stay = stayBuilder
                .guest(goodGuest)
                .room(goodRoom)
                .build();
        manager.createStay(stay);
        Guest guest = guestBuilder.build();
        stay.setGuest(guest);
        manager.updateStay(stay);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateStayWithRoomNull() {
        Stay stay = stayBuilder
                .guest(goodGuest)
                .room(goodRoom)
                .build();
        manager.createStay(stay);
        stay.setRoom(null);
        manager.updateStay(stay);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateStayWithNonExistingRoom() {
        Stay stay = stayBuilder
                .guest(goodGuest)
                .room(goodRoom)
                .build();
        manager.createStay(stay);
        Room room = roomBuilder.build();
        stay.setRoom(room);
        manager.updateStay(stay);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateStayWithWrongExpectedEndDate() {
        Stay stay = stayBuilder
                .guest(goodGuest)
                .room(goodRoom)
                .build();
        manager.createStay(stay);
        stay.setStartDate(LocalDate.of(2015, 1, 5));
        stay.setExpectedEndDate(LocalDate.of(2015, 1, 4));
        manager.updateStay(stay);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateStayWithWrongRealEndDate() {
        //Real end date must not be before start date
        Stay stay = stayBuilder
                .guest(goodGuest)
                .room(goodRoom)
                .build();
        manager.createStay(stay);
        stay.setStartDate(LocalDate.of(2015, 1, 5));
        stay.setExpectedEndDate(LocalDate.of(2015, 1, 6));
        stay.setRealEndDate(LocalDate.of(2015, 1, 4));
        manager.updateStay(stay);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateStayWithNegativeMinibarCosts() {
        //Real end date must not be before start date
        Stay stay = stayBuilder
                .guest(goodGuest)
                .room(goodRoom)
                .build();
        manager.createStay(stay);
        stay.setMinibarCosts(new BigDecimal("-1.00"));
        manager.updateStay(stay);
    }

    /**
     * Pato
     */
    @Test
    public void deleteStay() {
        Guest g0 = guestBuilder.build();
        Guest g1 = guestBuilder
                .name("Vin Diesel")
                .passportNo("888")
                .build();

        Room r1 = roomBuilder.build();
        Room r2 = roomBuilder
                .number("A001")
                .build();

        Stay s1 = stayBuilder
                .startDate(LocalDate.of(2015, 1, 1))
                .expectedEndDate(LocalDate.of(2015, 1, 3))
                .realEndDate(LocalDate.of(2015, 1, 3))
                .guest(g1)
                .room(r1)
                .build();
        Stay s2 = stayBuilder
                .startDate(LocalDate.of(2015, 1, 2))
                .expectedEndDate(LocalDate.of(2015, 1, 4))
                .realEndDate(LocalDate.of(2015, 1, 4))
                .guest(g0)
                .room(r2)
                .build();
        guestManager.createGuest(g1);
        guestManager.createGuest(g0);
        roomManager.createRoom(r1);
        roomManager.createRoom(r2);
        manager.createStay(s1);
        manager.createStay(s2);


        assertNotNull(manager.getStayById(s1.getId()));
        assertNotNull(manager.getStayById(s2.getId()));

        manager.deleteStay(s1);

        assertNull(manager.getStayById(s1.getId()));
        assertNotNull(manager.getStayById(s2.getId()));
    }

    /**
     * Pato
     */
    @Test(expected = IllegalArgumentException.class)
    public void deleteStayWithStayNull() {
        manager.deleteStay(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteStayWithStayIdNull() {
        Stay stay = stayBuilder
                .guest(goodGuest)
                .room(goodRoom)
                .build();
        manager.deleteStay(stay);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteStayWithWrongId() {
        Stay stay = stayBuilder
                .id(1l)
                .guest(goodGuest)
                .room(goodRoom)
                .build();
        manager.deleteStay(stay);
    }

    /**
     * Zuzana
     */
    @Test
    public void findAllStays() {
        assertTrue(manager.findAllStays().isEmpty());

        Guest g1 = guestBuilder.build();
        Guest g2 = guestBuilder
                .name("James Bond")
                .passportNo("234")
                .build();
        Room r1 = roomBuilder.build();
        Room r2 = roomBuilder
                .number("A222")
                .type(RoomType.APARTMENT)
                .build();
        Stay s1 = stayBuilder
                .guest(g1)
                .room(r1)
                .build();

        Stay s2 = stayBuilder
                .startDate(LocalDate.of(2015, 2, 20))
                .expectedEndDate(LocalDate.of(2015, 3, 1))
                .guest(g2)
                .room(r2)
                .build();

        guestManager.createGuest(g1);
        guestManager.createGuest(g2);
        roomManager.createRoom(r1);
        roomManager.createRoom(r2);
        manager.createStay(s1);
        manager.createStay(s2);

        List<Stay> expected = Arrays.asList(s1, s2);
        List<Stay> actual = manager.findAllStays();

        //assertEquals(expected, actual);
        assertDeepEqualsStays(expected, actual);
    }

    /**
     * Pato ako presne brat ten cas?????
     */
    @Test
    public void findStaysByDate() {
        Guest g0 = guestBuilder.build();
        Guest g1 = guestBuilder
                .name("Vin Diesel")
                .passportNo("888")
                .build();
        Guest g2 = guestBuilder
                .name("Chuck Norris")
                .passportNo("999")
                .build();
        Guest g3 = guestBuilder
                .name("James Clark")
                .passportNo("060")
                .build();

        Guest g4 = guestBuilder
                .name("Harry Potter")
                .passportNo("723")
                .build();

        Room r1 = roomBuilder.build();
        Room r2 = roomBuilder
                .number("A001")
                .build();
        Room r3 = roomBuilder
                .number("A002")
                .build();

        Stay s1 = stayBuilder
                .startDate(LocalDate.of(2015, 1, 1))
                .expectedEndDate(LocalDate.of(2015, 1, 3))
                .realEndDate(LocalDate.of(2015, 1, 3))
                .guest(g1)
                .room(r1)
                .build();
        Stay s2 = stayBuilder
                .startDate(LocalDate.of(2015, 1, 2))
                .expectedEndDate(LocalDate.of(2015, 1, 4))
                .realEndDate(LocalDate.of(2015, 1, 4))
                .guest(g3)
                .room(r2)
                .build();
        Stay s3 = stayBuilder
                .startDate(LocalDate.of(2015, 1, 3))
                .expectedEndDate(LocalDate.of(2015, 1, 6))
                .realEndDate(LocalDate.of(2015, 1, 6))
                .guest(g2)
                .room(r1)
                .build();
        Stay s4 = stayBuilder
                .startDate(LocalDate.of(2015, 1, 4))
                .expectedEndDate(LocalDate.of(2015, 1, 5))
                .realEndDate(LocalDate.of(2015, 1, 5))
                .guest(g4)
                .room(r2)
                .build();

        Stay s5 = stayBuilder
                .startDate(LocalDate.of(2015, 1, 5))
                .expectedEndDate(LocalDate.of(2015, 1, 8))
                .realEndDate(LocalDate.of(2015, 1, 8))
                .guest(g1)
                .room(r2)
                .build();
        Stay s6 = stayBuilder
                .startDate(LocalDate.of(2015, 1, 6))
                .expectedEndDate(LocalDate.of(2015, 1, 8))
                .realEndDate(LocalDate.of(2015, 1, 8))
                .guest(g1)
                .room(r1)
                .build();

        guestManager.createGuest(g0);
        guestManager.createGuest(g1);
        guestManager.createGuest(g2);
        guestManager.createGuest(g3);
        guestManager.createGuest(g4);
        roomManager.createRoom(r1);
        roomManager.createRoom(r2);
        roomManager.createRoom(r3);
        manager.createStay(s1);
        manager.createStay(s2);
        manager.createStay(s3);
        manager.createStay(s4);
        manager.createStay(s5);
        manager.createStay(s6);

        List<Stay> expected = Arrays.asList(s1, s2, s3);
        List<Stay> actual = manager.findStaysByDate(LocalDate.of(2015, 1, 1), LocalDate.of(2015, 1, 3));
        //assertEquals(expected, actual);
        assertDeepEqualsStays(expected, actual);

        expected = Arrays.asList(s1);
        actual = manager.findStaysByDate(LocalDate.of(2014, 1, 3), LocalDate.of(2015, 1, 1));
        //assertEquals(expected, actual);
        assertDeepEqualsStays(expected, actual);

        expected = Arrays.asList(s5, s6);
        actual = manager.findStaysByDate(LocalDate.of(2015, 1, 7), LocalDate.of(2015, 1, 9));
        //assertEquals(expected, actual);
        assertDeepEqualsStays(expected, actual);

        assertTrue(manager.findStaysByDate(LocalDate.of(2014, 1, 1), LocalDate.of(2014, 1, 12)).isEmpty());
        assertEquals(1, manager.findStaysByDate(LocalDate.of(2015, 1, 2), LocalDate.of(2015, 1, 2)).size());
        assertEquals(2, manager.findStaysByDate(LocalDate.of(2015, 1, 6), LocalDate.of(2015, 1, 6)).size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void findStaysByDateWithWrongAttributes() {
        manager.findStayingGuestsByDate(null);
    }

    /**
     * Zuzana
     */
    @Test
    public void findStayingGuestsByDate() {
        Guest g1 = guestBuilder.build();
        Guest g2 = guestBuilder
                .name("Batman")
                .passportNo("999")
                .build();
        Guest g3 = guestBuilder
                .name("James Clark")
                .passportNo("060")
                .build();

        Room r1 = roomBuilder.build();
        Room r2 = roomBuilder
                .number("A001")
                .build();
        Room r3 = roomBuilder
                .number("A002")
                .build();

        Stay s1 = stayBuilder
                .startDate(LocalDate.of(2015, 1, 1))
                .expectedEndDate(LocalDate.of(2015, 1, 3))
                .realEndDate(LocalDate.of(2015, 1, 3))
                .guest(g1)
                .room(r1)
                .build();

        Stay s2 = stayBuilder
                .startDate(LocalDate.of(2015, 1, 2))
                .expectedEndDate(LocalDate.of(2015, 1, 3))
                .realEndDate(LocalDate.of(2015, 1, 3))
                .guest(g2)
                .room(r2)
                .build();

        Stay s3 = stayBuilder
                .startDate(LocalDate.of(2015, 1, 3))
                .expectedEndDate(LocalDate.of(2015, 1, 5))
                .realEndDate(LocalDate.of(2015, 1, 5))
                .guest(g3)
                .room(r3)
                .build();

        guestManager.createGuest(g1);
        guestManager.createGuest(g2);
        guestManager.createGuest(g3);
        roomManager.createRoom(r1);
        roomManager.createRoom(r2);
        roomManager.createRoom(r3);
        manager.createStay(s1);
        manager.createStay(s2);
        manager.createStay(s3);

        List<Guest> expected = Arrays.asList(g1, g2);
        List<Guest> actual = manager.findStayingGuestsByDate(LocalDate.of(2015, 1, 2));

        //assertEquals(expected, actual);
        assertDeepEqualsGuests(expected, actual);

        assertTrue(manager.findStayingGuestsByDate(LocalDate.of(2015, 1, 10)).isEmpty());
        assertEquals(1, manager.findStayingGuestsByDate(LocalDate.of(2015, 1, 4)).size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void findStayingGuestsByDateWithDateNull() {
        manager.findStayingGuestsByDate(null);
    }

    /**
     * Zuzana
     */
    @Test
    public void findFreeRoomsByDateAndLen() {
        Guest g1 = guestBuilder.build();

        Room r1 = roomBuilder
                .number("A001")
                .build();
        Room r2 = roomBuilder
                .number("A002")
                .build();
        Room r3 = roomBuilder
                .number("A003")
                .build();

        Room r4 = roomBuilder
                .number("A004")
                .build();

        Stay s1 = stayBuilder
                .startDate(LocalDate.of(2015, 1, 1))
                .expectedEndDate(LocalDate.of(2015, 1, 4))
                .realEndDate(null)
                .guest(g1)
                .room(r1)
                .build();

        Stay s2 = stayBuilder
                .startDate(LocalDate.of(2015, 1, 2))
                .expectedEndDate(LocalDate.of(2015, 1, 3))
                .realEndDate(LocalDate.of(2015, 1, 3))
                .guest(g1)
                .room(r2)
                .build();

        Stay s3 = stayBuilder
                .startDate(LocalDate.of(2015, 1, 3))
                .expectedEndDate(LocalDate.of(2015, 1, 5))
                .realEndDate(null)
                .guest(g1)
                .room(r3)
                .build();

        Stay s4 = stayBuilder
                .startDate(LocalDate.of(2015, 1, 5))
                .expectedEndDate(LocalDate.of(2015, 1, 8))
                .realEndDate(null)
                .guest(g1)
                .room(r4)
                .build();

        guestManager.createGuest(g1);
        roomManager.createRoom(r1);
        roomManager.createRoom(r2);
        roomManager.createRoom(r3);
        roomManager.createRoom(r4);
        manager.createStay(s1);
        manager.createStay(s2);
        manager.createStay(s3);
        manager.createStay(s4);

        List<Room> expected = Arrays.asList(r2, r4, goodRoom);
        List<Room> actual = manager.findFreeRoomsByDateAndLen(LocalDate.of(2015, 1, 3), 2);
        //assertEquals(expected, actual);
        assertDeepEqualsRooms(expected, actual);

        expected = Arrays.asList(r1, r2, r3, goodRoom);
        actual = manager.findFreeRoomsByDateAndLen(LocalDate.of(2015, 1, 5), 5);
        //assertEquals(expected, actual);
        assertDeepEqualsRooms(expected, actual);

        expected = Arrays.asList(r4, goodRoom);
        actual = manager.findFreeRoomsByDateAndLen(LocalDate.of(2015, 1, 2), 2);
        //assertEquals(expected, actual);
        assertDeepEqualsRooms(expected, actual);

        expected = Arrays.asList(r1, r2, r3, r4, goodRoom);
        actual = manager.findFreeRoomsByDateAndLen(LocalDate.of(2015, 1, 10), 5);
        //assertEquals(expected, actual);
        assertDeepEqualsRooms(expected, actual);

        assertTrue(manager.findFreeRoomsByDateAndLen(LocalDate.of(2015, 1, 1), 10).size() == 1);
    }

    /**
     * Zuzana
     */
    @Test(expected = IllegalArgumentException.class)
    public void findFreeRoomsByDateAndLenWithDatenull() {
        manager.findFreeRoomsByDateAndLen(null, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findFreeRoomsByDateAndLenWithLenZero() {
        manager.findFreeRoomsByDateAndLen(LocalDate.of(2015, 1, 1), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findFreeRoomsByDateAndLenWithLenNegative() {
        manager.findFreeRoomsByDateAndLen(LocalDate.of(2015, 1, 1), -5);
    }

    /**
     * Pato
     */
    @Test
    public void findAllStaysForGuest() {
        Guest g0 = guestBuilder.build();
        Guest g1 = guestBuilder
                .name("Vin Diesel")
                .passportNo("888")
                .build();
        Guest g2 = guestBuilder
                .name("Chuck Norris")
                .passportNo("999")
                .build();
        Guest g3 = guestBuilder
                .name("James Clark")
                .passportNo("060")
                .build();

        Guest g4 = guestBuilder
                .name("Harry Potter")
                .passportNo("723")
                .build();

        Room r1 = roomBuilder.build();
        Room r2 = roomBuilder
                .number("A001")
                .build();
        Room r3 = roomBuilder
                .number("A002")
                .build();

        Stay s1 = stayBuilder
                .startDate(LocalDate.of(2015, 1, 1))
                .expectedEndDate(LocalDate.of(2015, 1, 3))
                .realEndDate(LocalDate.of(2015, 1, 3))
                .guest(g1)
                .room(r1)
                .build();
        Stay s2 = stayBuilder
                .startDate(LocalDate.of(2015, 1, 2))
                .expectedEndDate(LocalDate.of(2015, 1, 4))
                .realEndDate(LocalDate.of(2015, 1, 4))
                .guest(g3)
                .room(r2)
                .build();
        Stay s3 = stayBuilder
                .startDate(LocalDate.of(2015, 1, 3))
                .expectedEndDate(LocalDate.of(2015, 1, 6))
                .realEndDate(LocalDate.of(2015, 1, 6))
                .guest(g2)
                .room(r1)
                .build();
        Stay s4 = stayBuilder
                .startDate(LocalDate.of(2015, 1, 4))
                .expectedEndDate(LocalDate.of(2015, 1, 5))
                .realEndDate(LocalDate.of(2015, 1, 5))
                .guest(g4)
                .room(r2)
                .build();

        Stay s5 = stayBuilder
                .startDate(LocalDate.of(2015, 1, 5))
                .expectedEndDate(LocalDate.of(2015, 1, 8))
                .realEndDate(LocalDate.of(2015, 1, 8))
                .guest(g1)
                .room(r2)
                .build();
        Stay s6 = stayBuilder
                .startDate(LocalDate.of(2015, 1, 6))
                .expectedEndDate(LocalDate.of(2015, 1, 8))
                .realEndDate(LocalDate.of(2015, 1, 8))
                .guest(g1)
                .room(r1)
                .build();

        guestManager.createGuest(g0);
        guestManager.createGuest(g1);
        guestManager.createGuest(g2);
        guestManager.createGuest(g3);
        guestManager.createGuest(g4);
        roomManager.createRoom(r1);
        roomManager.createRoom(r2);
        roomManager.createRoom(r3);
        manager.createStay(s1);
        manager.createStay(s2);
        manager.createStay(s3);
        manager.createStay(s4);
        manager.createStay(s5);
        manager.createStay(s6);

        List<Stay> expected = Arrays.asList();
        List<Stay> actual = manager.findAllStaysForGuest(g0);
        //assertEquals(expected, actual);
        assertDeepEqualsStays(expected, actual);

        assertTrue(manager.findAllStaysForGuest(g0).isEmpty());

        expected = Arrays.asList(s5, s6, s1);
        actual = manager.findAllStaysForGuest(g1);
        //assertEquals(expected, actual);
        assertDeepEqualsStays(expected, actual);

        assertEquals(3, manager.findAllStaysForGuest(g1).size());

        expected = Arrays.asList(s3);
        actual = manager.findAllStaysForGuest(g2);
        //assertEquals(expected, actual);
        assertDeepEqualsStays(expected, actual);

        assertEquals(1, manager.findAllStaysForGuest(g4).size());


    }

    @Test(expected = IllegalArgumentException.class)
    public void findAllStaysForGuestWithGuestNull() {
        manager.findAllStaysForGuest(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findAllStaysForGuestWithGuestIdNull() {
        goodGuest.setId(null);
        manager.findAllStaysForGuest(goodGuest);
    }

    /**
     * Zuzana
     */
    @Test
    public void findRoomsForGuestByDate() {
        Guest g0 = guestBuilder.build();
        Guest g1 = guestBuilder
                .name("Vin Diesel")
                .passportNo("888")
                .build();
        Guest g2 = guestBuilder
                .name("Chuck Norris")
                .passportNo("999")
                .build();
        Guest g3 = guestBuilder
                .name("James Clark")
                .passportNo("060")
                .build();

        Guest g4 = guestBuilder
                .name("Harry Potter")
                .passportNo("723")
                .build();

        Room r1 = roomBuilder.build();
        Room r2 = roomBuilder
                .number("A001")
                .build();
        Room r3 = roomBuilder
                .number("A002")
                .build();

        Stay s1 = stayBuilder
                .startDate(LocalDate.of(2015, 1, 1))
                .expectedEndDate(LocalDate.of(2015, 1, 3))
                .realEndDate(LocalDate.of(2015, 1, 3))
                .guest(g1)
                .room(r1)
                .build();
        Stay s2 = stayBuilder
                .startDate(LocalDate.of(2015, 1, 2))
                .expectedEndDate(LocalDate.of(2015, 1, 4))
                .realEndDate(LocalDate.of(2015, 1, 4))
                .guest(g3)
                .room(r2)
                .build();
        Stay s3 = stayBuilder
                .startDate(LocalDate.of(2015, 1, 3))
                .expectedEndDate(LocalDate.of(2015, 1, 6))
                .realEndDate(LocalDate.of(2015, 1, 6))
                .guest(g2)
                .room(r1)
                .build();
        Stay s4 = stayBuilder
                .startDate(LocalDate.of(2015, 1, 4))
                .expectedEndDate(LocalDate.of(2015, 1, 5))
                .realEndDate(LocalDate.of(2015, 1, 5))
                .guest(g4)
                .room(r2)
                .build();

        Stay s5 = stayBuilder
                .startDate(LocalDate.of(2015, 1, 5))
                .expectedEndDate(LocalDate.of(2015, 1, 8))
                .realEndDate(LocalDate.of(2015, 1, 8))
                .guest(g1)
                .room(r2)
                .build();
        Stay s6 = stayBuilder
                .startDate(LocalDate.of(2015, 1, 6))
                .expectedEndDate(LocalDate.of(2015, 1, 8))
                .realEndDate(LocalDate.of(2015, 1, 8))
                .guest(g1)
                .room(r1)
                .build();

        guestManager.createGuest(g0);
        guestManager.createGuest(g1);
        guestManager.createGuest(g2);
        guestManager.createGuest(g3);
        guestManager.createGuest(g4);
        roomManager.createRoom(r1);
        roomManager.createRoom(r2);
        roomManager.createRoom(r3);
        manager.createStay(s1);
        manager.createStay(s2);
        manager.createStay(s3);
        manager.createStay(s4);
        manager.createStay(s5);
        manager.createStay(s6);

        List<Room> expected = Arrays.asList(r1);
        List<Room> actual = manager.findRoomsForGuestByDate(g1, LocalDate.of(2015, 1, 1));
        //assertEquals(expected, actual);
        assertDeepEqualsRooms(expected, actual);

        expected = Arrays.asList(r1);
        actual = manager.findRoomsForGuestByDate(g1, LocalDate.of(2015, 1, 3));
        //assertEquals(expected, actual);
        assertDeepEqualsRooms(expected, actual);

        expected = Arrays.asList(r1, r2);
        actual = manager.findRoomsForGuestByDate(g1, LocalDate.of(2015, 1, 7));
        //assertEquals(expected, actual);
        assertDeepEqualsRooms(expected, actual);

        expected = Arrays.asList(r2);
        actual = manager.findRoomsForGuestByDate(g4, LocalDate.of(2015, 1, 4));
        //assertEquals(expected, actual);
        assertDeepEqualsRooms(expected, actual);

        assertTrue(manager.findRoomsForGuestByDate(g0, LocalDate.of(2015, 1, 5)).isEmpty());
    }

    /**
     * Zuzana
     */
    @Test(expected = IllegalArgumentException.class)
    public void findRoomsForGuestByDateWithGuestNull() {
        manager.findRoomsForGuestByDate(null, LocalDate.of(2015, 1, 1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void findRoomsForGuestByDateWithGuestIdNull() {
        //his id will be null
        Guest guest = guestBuilder.build();
        manager.findRoomsForGuestByDate(guest, LocalDate.of(2015, 1, 1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void findRoomsForGuestByDateWithDateNull() {
        //date is null
        manager.findRoomsForGuestByDate(goodGuest, null);
    }

    /**
     * Pato realEndDate moze byt aj null? zo zaciatku ? asi spravit testy
     */
    @Test
    public void findStaysForRoomByDate() {
        Guest g0 = guestBuilder.build();
        Guest g1 = guestBuilder
                .name("Vin Diesel")
                .passportNo("888")
                .build();
        Guest g2 = guestBuilder
                .name("Chuck Norris")
                .passportNo("999")
                .build();
        Guest g3 = guestBuilder
                .name("James Clark")
                .passportNo("060")
                .build();

        Guest g4 = guestBuilder
                .name("Harry Potter")
                .passportNo("723")
                .build();

        Room r1 = roomBuilder.build();
        Room r2 = roomBuilder
                .number("A001")
                .build();

        Stay s1 = stayBuilder
                .startDate(LocalDate.of(2015, 1, 1))
                .expectedEndDate(LocalDate.of(2015, 1, 3))
                .realEndDate(LocalDate.of(2015, 1, 3))
                .guest(g1)
                .room(r1)
                .build();
        Stay s2 = stayBuilder
                .startDate(LocalDate.of(2015, 1, 2))
                .expectedEndDate(LocalDate.of(2015, 1, 4))
                .realEndDate(LocalDate.of(2015, 1, 4))
                .guest(g3)
                .room(r2)
                .build();
        Stay s3 = stayBuilder
                .startDate(LocalDate.of(2015, 1, 3))
                .expectedEndDate(LocalDate.of(2015, 1, 6))
                .realEndDate(LocalDate.of(2015, 1, 6))
                .guest(g2)
                .room(r1)
                .build();
        Stay s4 = stayBuilder
                .startDate(LocalDate.of(2015, 1, 4))
                .expectedEndDate(LocalDate.of(2015, 1, 5))
                .realEndDate(LocalDate.of(2015, 1, 5))
                .guest(g4)
                .room(r2)
                .build();

        guestManager.createGuest(g0);
        guestManager.createGuest(g1);
        guestManager.createGuest(g2);
        guestManager.createGuest(g3);
        guestManager.createGuest(g4);
        roomManager.createRoom(r1);
        roomManager.createRoom(r2);
        manager.createStay(s1);
        manager.createStay(s2);
        manager.createStay(s3);
        manager.createStay(s4);

        List<Stay> expected = Arrays.asList(s1);
        List<Stay> actual = manager.findStaysForRoomByDate(r1, LocalDate.of(2015, 1, 1));
        //assertEquals(expected, actual);
        assertDeepEqualsStays(expected, actual);

        expected = Arrays.asList(s3, s1);
        actual = manager.findStaysForRoomByDate(r1, LocalDate.of(2015, 1, 3));
        //assertEquals(expected, actual);
        assertDeepEqualsStays(expected, actual);

        expected = Arrays.asList(s4);
        actual = manager.findStaysForRoomByDate(r2, LocalDate.of(2015, 1, 5));
        //assertEquals(expected, actual);
        assertDeepEqualsStays(expected, actual);

        expected = Arrays.asList(s2, s4);
        actual = manager.findStaysForRoomByDate(r2, LocalDate.of(2015, 1, 4));
        //assertEquals(expected, actual);
        assertDeepEqualsStays(expected, actual);

        assertTrue(manager.findStaysForRoomByDate(r1, LocalDate.of(2014, 1, 5)).isEmpty());

        expected = Arrays.asList(s3);
        actual = manager.findStaysForRoomByDate(r1, LocalDate.of(2015, 1, 5));
        //assertEquals(expected, actual);
        assertDeepEqualsStays(expected, actual);

        assertTrue(manager.findStaysForRoomByDate(r2, LocalDate.of(2014, 1, 1)).isEmpty());
        assertEquals(1, manager.findGuestsForRoomByDate(r2, LocalDate.of(2015, 1, 2)).size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void findStaysForRoomByDateWithRoomNull() {
        manager.findStaysForRoomByDate(null, LocalDate.MIN);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findStaysForRoomByDateWithRoomIdNull() {
        Room room = roomBuilder.build();
        manager.findStaysForRoomByDate(room, LocalDate.MIN);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findStaysForRoomByDateWithDateNull() {
        manager.findStaysForRoomByDate(goodRoom, null);
    }

    /**
     * Zuzana
     */
    @Test
    public void findGuestsForRoomByDate() {
        Guest g0 = guestBuilder.build();
        Guest g1 = guestBuilder
                .name("Vin Diesel")
                .passportNo("888")
                .build();
        Guest g2 = guestBuilder
                .name("Chuck Norris")
                .passportNo("999")
                .build();
        Guest g3 = guestBuilder
                .name("James Clark")
                .passportNo("060")
                .build();

        Guest g4 = guestBuilder
                .name("Harry Potter")
                .passportNo("723")
                .build();

        Room r1 = roomBuilder.build();
        Room r2 = roomBuilder
                .number("A001")
                .build();
        Room r3 = roomBuilder
                .number("A002")
                .build();

        Stay s1 = stayBuilder
                .startDate(LocalDate.of(2015, 1, 1))
                .expectedEndDate(LocalDate.of(2015, 1, 3))
                .realEndDate(LocalDate.of(2015, 1, 3))
                .guest(g1)
                .room(r1)
                .build();
        Stay s2 = stayBuilder
                .startDate(LocalDate.of(2015, 1, 2))
                .expectedEndDate(LocalDate.of(2015, 1, 4))
                .realEndDate(LocalDate.of(2015, 1, 4))
                .guest(g3)
                .room(r2)
                .build();
        Stay s3 = stayBuilder
                .startDate(LocalDate.of(2015, 1, 3))
                .expectedEndDate(LocalDate.of(2015, 1, 6))
                .realEndDate(LocalDate.of(2015, 1, 6))
                .guest(g2)
                .room(r1)
                .build();
        Stay s4 = stayBuilder
                .startDate(LocalDate.of(2015, 1, 4))
                .expectedEndDate(LocalDate.of(2015, 1, 5))
                .realEndDate(LocalDate.of(2015, 1, 5))
                .guest(g4)
                .room(r2)
                .build();

        Stay s5 = stayBuilder
                .startDate(LocalDate.of(2015, 1, 5))
                .expectedEndDate(LocalDate.of(2015, 1, 8))
                .realEndDate(LocalDate.of(2015, 1, 8))
                .guest(g1)
                .room(r2)
                .build();
        Stay s6 = stayBuilder
                .startDate(LocalDate.of(2015, 1, 6))
                .expectedEndDate(LocalDate.of(2015, 1, 8))
                .realEndDate(LocalDate.of(2015, 1, 8))
                .guest(g1)
                .room(r1)
                .build();

        guestManager.createGuest(g0);
        guestManager.createGuest(g1);
        guestManager.createGuest(g2);
        guestManager.createGuest(g3);
        guestManager.createGuest(g4);
        roomManager.createRoom(r1);
        roomManager.createRoom(r2);
        roomManager.createRoom(r3);
        manager.createStay(s1);
        manager.createStay(s2);
        manager.createStay(s3);
        manager.createStay(s4);
        manager.createStay(s5);
        manager.createStay(s6);

        List<Guest> expected = Arrays.asList(g1);
        List<Guest> actual = manager.findGuestsForRoomByDate(r1, LocalDate.of(2015, 1, 1));
        //assertEquals(expected, actual);
        assertDeepEqualsGuests(expected, actual);

        expected = Arrays.asList(g1, g2);
        actual = manager.findGuestsForRoomByDate(r1, LocalDate.of(2015, 1, 3));
        //assertEquals(expected, actual);
        assertDeepEqualsGuests(expected, actual);

        expected = Arrays.asList(g1);
        actual = manager.findGuestsForRoomByDate(r1, LocalDate.of(2015, 1, 8));
        //assertEquals(expected, actual);
        assertDeepEqualsGuests(expected, actual);

        expected = Arrays.asList(g3, g4);
        actual = manager.findGuestsForRoomByDate(r2, LocalDate.of(2015, 1, 4));
        //assertEquals(expected, actual);
        assertDeepEqualsGuests(expected, actual);

        assertTrue(manager.findGuestsForRoomByDate(r2, LocalDate.of(2015, 1, 1)).isEmpty());
        assertEquals(2, manager.findGuestsForRoomByDate(r2, LocalDate.of(2015, 1, 5)).size());

        assertTrue(manager.findGuestsForRoomByDate(r3, LocalDate.of(2015, 1, 1)).isEmpty());
    }

    /**
     * Zuzana
     */
    @Test(expected = IllegalArgumentException.class)
    public void findGuestsForRoomByDateWithRoomNull() {
        manager.findGuestsForRoomByDate(null, LocalDate.of(2015, 1, 1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void findGuestsForRoomByDateWithRoomIdNull() {
        //his id will be null
        Room room = roomBuilder.build();
        manager.findGuestsForRoomByDate(room, LocalDate.of(2015, 1, 1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void findGuestsForRoomByDateWithDateNull() {
        manager.findGuestsForRoomByDate(goodRoom, null);
    }

    /**
     * Pato ako riesit hranicne datumy????
     */
    @Test
    public void findFreeRoomByDateAndCapacity() {
        Guest g0 = guestBuilder.build();
        Guest g1 = guestBuilder
                .name("Vin Diesel")
                .passportNo("888")
                .build();
        Guest g2 = guestBuilder
                .name("Chuck Norris")
                .passportNo("999")
                .build();
        Guest g3 = guestBuilder
                .name("James Clark")
                .passportNo("060")
                .build();

        Guest g4 = guestBuilder
                .name("Harry Potter")
                .passportNo("723")
                .build();

       // Room r1 = roomBuilder.build();
        Room r1 = goodRoom;
        Room r2 = roomBuilder
                .number("A001")
                .capacity(3)
                .build();
        Room r3 = roomBuilder
                .number("A002")
                .capacity(2)
                .build();

        Stay s1 = stayBuilder
                .startDate(LocalDate.of(2015, 1, 1))
                .expectedEndDate(LocalDate.of(2015, 1, 3))
                .realEndDate(LocalDate.of(2015, 1, 3))
                .guest(g1)
                .room(r1)
                .build();
        Stay s2 = stayBuilder
                .startDate(LocalDate.of(2015, 1, 2))
                .expectedEndDate(LocalDate.of(2015, 1, 4))
                .realEndDate(LocalDate.of(2015, 1, 4))
                .guest(g3)
                .room(r2)
                .build();
        Stay s3 = stayBuilder
                .startDate(LocalDate.of(2015, 1, 3))
                .expectedEndDate(LocalDate.of(2015, 1, 6))
                .realEndDate(LocalDate.of(2015, 1, 6))
                .guest(g2)
                .room(r1)
                .build();
        Stay s4 = stayBuilder
                .startDate(LocalDate.of(2015, 1, 4))
                .expectedEndDate(LocalDate.of(2015, 1, 5))
                .realEndDate(LocalDate.of(2015, 1, 5))
                .guest(g4)
                .room(r2)
                .build();

        Stay s5 = stayBuilder
                .startDate(LocalDate.of(2015, 1, 5))
                .expectedEndDate(LocalDate.of(2015, 1, 8))
                .realEndDate(LocalDate.of(2015, 1, 8))
                .guest(g1)
                .room(r2)
                .build();
        Stay s6 = stayBuilder
                .startDate(LocalDate.of(2015, 1, 6))
                .expectedEndDate(LocalDate.of(2015, 1, 8))
                .realEndDate(LocalDate.of(2015, 1, 8))
                .guest(g1)
                .room(r1)
                .build();

        guestManager.createGuest(g0);
        guestManager.createGuest(g1);
        guestManager.createGuest(g2);
        guestManager.createGuest(g3);
        guestManager.createGuest(g4);
        //roomManager.createRoom(r1);
        roomManager.createRoom(r2);
        roomManager.createRoom(r3);
        manager.createStay(s1);
        manager.createStay(s2);
        manager.createStay(s3);
        manager.createStay(s4);
        manager.createStay(s5);
        manager.createStay(s6);

        List<Room> expected = Arrays.asList(r2);
        List<Room> actual = manager.findFreeRoomByDateAndCapacity(LocalDate.of(2015, 1, 1), 3);
        //assertEquals(expected, actual);
        assertDeepEqualsRooms(expected, actual);

        expected = Arrays.asList(r3);
        actual = manager.findFreeRoomByDateAndCapacity(LocalDate.of(2015, 1, 3), 2);
        //assertEquals(expected, actual);
        assertDeepEqualsRooms(expected, actual);

        expected = Arrays.asList(r3, r1);
        actual = manager.findFreeRoomByDateAndCapacity(LocalDate.of(2015, 1, 8), 2);
        //assertEquals(expected, actual);
        assertDeepEqualsRooms(expected, actual);

        expected = Arrays.asList();
        actual = manager.findFreeRoomByDateAndCapacity(LocalDate.of(2015, 1, 7), 3);
        //assertEquals(expected, actual);
        assertDeepEqualsRooms(expected, actual);

        assertTrue(manager.findFreeRoomByDateAndCapacity(LocalDate.of(2015, 1, 5), 3).isEmpty());
        assertEquals(1, manager.findFreeRoomByDateAndCapacity(LocalDate.of(2015, 1, 5), 2).size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void findFreeRoomByDateAndCapacityWithDateNull() {
        manager.findFreeRoomByDateAndCapacity(null, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findFreeRoomByDateAndCapacityWithCapacityZero() {
        manager.findFreeRoomByDateAndCapacity(LocalDate.of(2015, 1, 1), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findFreeRoomByDateAndCapacityWithNegativeCapacity() {
        manager.findFreeRoomByDateAndCapacity(LocalDate.of(2015, 1, 1), -5);
    }
    
    /**
     * Pato
     */
    @Test
    public void findTop3Guests() {
        Guest g0 = guestBuilder.build();
        Guest g1 = guestBuilder
                .name("Vin Diesel")
                .passportNo("888")
                .build();
        Guest g2 = guestBuilder
                .name("Chuck Norris")
                .passportNo("999")
                .build();
        Guest g3 = guestBuilder
                .name("James Clark")
                .passportNo("060")
                .build();


        Room r1 = roomBuilder.build();
        Room r2 = roomBuilder
                .number("A001")
                .capacity(3)
                .build();

        Stay s1 = stayBuilder
                .startDate(LocalDate.of(2015, 1, 1))
                .expectedEndDate(LocalDate.of(2015, 1, 3))
                .realEndDate(LocalDate.of(2015, 1, 3))
                .guest(g0)
                .room(r1)
                .build();
        Stay s2 = stayBuilder
                .startDate(LocalDate.of(2015, 1, 2))
                .expectedEndDate(LocalDate.of(2015, 1, 4))
                .realEndDate(LocalDate.of(2015, 1, 4))
                .guest(g3)
                .room(r2)
                .build();
        Stay s3 = stayBuilder
                .startDate(LocalDate.of(2015, 1, 3))
                .expectedEndDate(LocalDate.of(2015, 1, 6))
                .realEndDate(LocalDate.of(2015, 1, 6))
                .guest(g2)
                .room(r1)
                .build();
        Stay s4 = stayBuilder
                .startDate(LocalDate.of(2015, 1, 4))
                .expectedEndDate(LocalDate.of(2015, 1, 5))
                .realEndDate(LocalDate.of(2015, 1, 5))
                .guest(g0)
                .room(r2)
                .build();

        Stay s5 = stayBuilder
                .startDate(LocalDate.of(2015, 1, 5))
                .expectedEndDate(LocalDate.of(2015, 1, 8))
                .realEndDate(LocalDate.of(2015, 1, 8))
                .guest(g2)
                .room(r2)
                .build();
        Stay s6 = stayBuilder
                .startDate(LocalDate.of(2015, 1, 6))
                .expectedEndDate(LocalDate.of(2015, 1, 8))
                .realEndDate(LocalDate.of(2015, 1, 8))
                .guest(g0)
                .room(r2)
                .build();

        guestManager.createGuest(g0);
        guestManager.createGuest(g1);
        guestManager.createGuest(g2);
        guestManager.createGuest(g3);

        roomManager.createRoom(r1);
        roomManager.createRoom(r2);

        manager.createStay(s1);

        List<Guest> expected = Arrays.asList(g0);
        List<Guest> actual = manager.findTop3Guests();
        assertDeepEqualsCustomers(expected, actual);

        assertEquals(1, manager.findTop3Guests().size());

        manager.createStay(s2);
        manager.createStay(s3);
        manager.createStay(s4);
        manager.createStay(s5);
        manager.createStay(s6);

        expected = Arrays.asList(g0, g2, g3);
        actual = manager.findTop3Guests();
        assertDeepEqualsCustomers(expected, actual);

    }

    private static Stay newStay(LocalDate startDate, LocalDate expEndDate, LocalDate realEndDate, Guest guest, Room room, BigDecimal minibarCosts) {
        Stay stay = new Stay();
        stay.setStartDate(startDate);
        stay.setExpectedEndDate(expEndDate);
        stay.setRealEndDate(realEndDate);
        stay.setGuest(guest);
        stay.setRoom(room);
        stay.setMinibarCosts(minibarCosts);
        return stay;
    }

    private static Guest newGuest(String name, String passportNo, String email, String phone, LocalDate dateOfBirth) {
        Guest guest = new Guest();
        guest.setName(name);
        guest.setPassportNo(passportNo);
        guest.setEmail(email);
        guest.setPhone(phone);
        guest.setDateOfBirth(dateOfBirth);
        return guest;
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

    /**
     * Builder for Stay. Guest and Room HAVE TO be created by their own
     * respective managers and assigned to stay before (creating) storing stay
     * itself.
     */
    class StayBuilder {

        private Long id;
        private LocalDate startDate = LocalDate.of(2015, 1, 1);
        private LocalDate expectedEndDate = LocalDate.of(2015, 1, 3);
        private LocalDate realEndDate = null;
        private Guest guest = null;//new GuestBuilder().build();
        private Room room = null;//new RoomBuilder().build();
        private BigDecimal minibarCosts = new BigDecimal("100.50");

        public StayBuilder guest(Guest guest) {
            this.guest = guest;
            return this;
        }

        public StayBuilder room(Room room) {
            this.room = room;
            return this;
        }

        public StayBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public StayBuilder startDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public StayBuilder expectedEndDate(LocalDate expectedEndDate) {
            this.expectedEndDate = expectedEndDate;
            return this;
        }

        public StayBuilder realEndDate(LocalDate realEndDate) {
            this.realEndDate = realEndDate;
            return this;
        }

        public StayBuilder minibarCosts(BigDecimal minibarCosts) {
            this.minibarCosts = minibarCosts;
            return this;
        }

        public Stay build() {
            return newStay(startDate, expectedEndDate, realEndDate, guest, room, minibarCosts);
        }
    }

    class GuestBuilder {

        private Long id;
        private String name = "Johnny English";
        private String passportNo = "222";
        private String email = "johnny@english.uk";
        private String phone = "+00221133";
        private LocalDate dateOfBirth = LocalDate.of(1990, 2, 3);

        public GuestBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public GuestBuilder name(String name) {
            this.name = name;
            return this;
        }

        public GuestBuilder passportNo(String passportNo) {
            this.passportNo = passportNo;
            return this;
        }

        public GuestBuilder email(String email) {
            this.email = email;
            return this;
        }

        public GuestBuilder setPhone(String phone) {
            this.phone = phone;
            return this;
        }

        public GuestBuilder setDateOfBirth(LocalDate dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
            return this;
        }

        public Guest build() {
            return newGuest(name, passportNo, email, phone, dateOfBirth);
        }
    }

    class RoomBuilder {

        private Long id;
        private String number = "A721";
        private int capacity = 2;
        private BigDecimal pricePerNight = new BigDecimal("50.00");
        private boolean bathroom = false;
        private RoomType type = RoomType.STANDARD;

        public RoomBuilder type(RoomType type) {
            this.type = type;
            return this;
        }

        public RoomBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public RoomBuilder number(String number) {
            this.number = number;
            return this;
        }

        public RoomBuilder capacity(int capacity) {
            this.capacity = capacity;
            return this;
        }

        public RoomBuilder pricePerNight(BigDecimal pricePerNight) {
            this.pricePerNight = pricePerNight;
            return this;
        }

        public RoomBuilder bathroom(boolean bathroom) {
            this.bathroom = bathroom;
            return this;
        }

        public Room build() {
            return newRoom(number, capacity, pricePerNight, bathroom, type);
        }
    }

    private void assertDeepEqualsGuests(List<Guest> expectedList, List<Guest> actualList) {
        expectedList.sort(idComparatorGuest);
        actualList.sort(idComparatorGuest);
        assertEquals(expectedList, actualList);
        for (int i = 0; i < expectedList.size(); i++) {
            Guest expected = expectedList.get(i);
            Guest actual = actualList.get(i);
            assertDeepEquals(expected, actual);
        }
    }

    private void assertDeepEqualsCustomers(List<Guest> expectedList, List<Guest> actualList) {
        assertEquals(expectedList, actualList );
        for (int i = 0; i < expectedList.size(); i++) {
            Guest expected = expectedList.get(i);
            Guest actual = actualList.get(i);
            assertDeepEquals(expected, actual);
        }
    }

    private void assertDeepEqualsRooms(List<Room> expectedList, List<Room> actualList) {
        expectedList.sort(idComparatorRoom);
        actualList.sort(idComparatorRoom);
        assertEquals(expectedList, actualList);
        for (int i = 0; i < expectedList.size(); i++) {
            Room expected = expectedList.get(i);
            Room actual = actualList.get(i);
            assertDeepEquals(expected, actual);
        }
    }

    private void assertDeepEqualsStays(List<Stay> expectedList, List<Stay> actualList) {
        expectedList.sort(idComparatorStay);
        actualList.sort(idComparatorStay);
        assertEquals(expectedList, actualList);
        for (int i = 0; i < expectedList.size(); i++) {
            Stay expected = expectedList.get(i);
            Stay actual = actualList.get(i);
            assertDeepEquals(expected, actual);
        }
    }
    private static Comparator<Guest> idComparatorGuest = new Comparator<Guest>() {
        @Override
        public int compare(Guest g1, Guest g2) {
            return Long.valueOf(g1.getId()).compareTo(Long.valueOf(g2.getId()));
        }
    };
    private static Comparator<Room> idComparatorRoom = new Comparator<Room>() {
        @Override
        public int compare(Room o1, Room o2) {
            return Long.valueOf(o1.getId()).compareTo(Long.valueOf(o2.getId()));
        }
    };
    private static Comparator<Stay> idComparatorStay = new Comparator<Stay>() {
        @Override
        public int compare(Stay o1, Stay o2) {
            return Long.valueOf(o1.getId()).compareTo(Long.valueOf(o2.getId()));
        }
    };

    private void assertDeepEquals(Guest expected, Guest actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getPassportNo(), actual.getPassportNo());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getPhone(), actual.getPhone());
        assertEquals(expected.getDateOfBirth(), actual.getDateOfBirth());
    }

    private void assertDeepEquals(Room expected, Room actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getCapacity(), actual.getCapacity());
        assertEquals(expected.getNumber(), actual.getNumber());
        assertEquals(expected.getPricePerNight(), actual.getPricePerNight());
        assertEquals(expected.hasBathroom(), actual.hasBathroom());
        assertEquals(expected.getType(), actual.getType());
    }

    private void assertDeepEquals(Stay expected, Stay actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getStartDate(), actual.getStartDate());
        assertEquals(expected.getRealEndDate(), actual.getRealEndDate());
        assertEquals(expected.getExpectedEndDate(), actual.getExpectedEndDate());
        assertEquals(expected.getGuest(), actual.getGuest()); //??? ako skontrolovat dalsie entity .. deep alebo porovnat iba ID alebo??
        assertEquals(expected.getRoom(), actual.getRoom());
        assertEquals(expected.getMinibarCosts(), actual.getMinibarCosts());
    }
}
