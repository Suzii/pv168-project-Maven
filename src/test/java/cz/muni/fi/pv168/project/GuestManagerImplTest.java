/*
 * Comparator pri liste!!!! terba pouzit
 * 
 */
package cz.muni.fi.pv168.project;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.time.LocalDate;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import cz.muni.fi.pv168.project.common.DBUtils;
import org.junit.After;

/**
 *
 * @author Zuzana
 */
public class GuestManagerImplTest {

    private GuestManager manager;
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
        manager = new GuestManagerImpl(ds);
        //create tables!!
        DBUtils.executeSqlScript(ds,GuestManager.class.getResourceAsStream("/createTables.sql"));
    }
    
    @After
    public void tearDown() throws SQLException {
        DBUtils.executeSqlScript(ds,GuestManager.class.getResourceAsStream("/dropTables.sql"));
    }

    @Test
    public void createGuest() {
        Guest guest = newGuest("Johnny English", "001", "johnny@english.uk", "123", LocalDate.of(1970, 1, 1));
        manager.createGuest(guest);

        Long guestId = guest.getId();
        assertNotNull(guestId);
        Guest result = manager.getGuestById(guestId);
        assertEquals(guest, result);
        assertNotSame(guest, result);
        assertDeepEquals(guest, result);
    }

    @Test
    public void getGuestById() {
        assertNull(manager.getGuestById(1l));

        Guest guest = newGuest("Johnny English", "001", "johnny@english.uk", "123", LocalDate.of(1970, 1, 1));
        manager.createGuest(guest);
        Long guestId = guest.getId();
        Guest result = manager.getGuestById(guestId);
        assertDeepEquals(guest, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getGuestByIdWithIdNul() {
        manager.getGuestById(null);
    }

    @Test
    public void findAllGuests() {
        assertTrue(manager.findAllGuests().isEmpty());

        Guest g1 = newGuest("Johnny English", "001", "johnny@english.uk", "123", LocalDate.of(1970, 1, 1));
        Guest g2 = newGuest("James Bond", "007", "james@bond.uk", "234", LocalDate.of(1970, 1, 3));

        manager.createGuest(g2);
        manager.createGuest(g1);

        List<Guest> expected = Arrays.asList(g1, g2);
        List<Guest> actual = manager.findAllGuests();

        assertEquals(expected, actual);
        assertDeepEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createGuestWithGuestNull() {
        manager.createGuest(null);
    }

    @Test
    public void createGuest2() {
        //passport may be null
        Guest guest = newGuest("Johnny English", null, "johnny@english.uk", "123", LocalDate.of(1970, 1, 1));
        manager.createGuest(guest);

        Guest result = manager.getGuestById(guest.getId());
        assertNotNull(result);
        assertNull(result.getPassportNo());

        //passport number may be empty
        guest = newGuest("Johnny English", "", "johnny@english.uk", "123", LocalDate.of(1970, 1, 1));
        manager.createGuest(guest);
        result = manager.getGuestById(guest.getId());
        assertNotNull(result);
        assertEquals(result.getPassportNo(), "");

        //email may be null
        guest = newGuest("Johnny English", "001", null, "123", LocalDate.of(1970, 1, 1));
        manager.createGuest(guest);
        result = manager.getGuestById(guest.getId());
        assertNotNull(result);
        assertNull(result.getEmail());

        //phone number may be empty
        guest = newGuest("Johnny English", "001", "johnny@english.uk", "", LocalDate.of(1970, 1, 1));
        manager.createGuest(guest);
        result = manager.getGuestById(guest.getId());
        assertNotNull(result);
        assertEquals(result.getPhone(), "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createGuestWithIdAlreadySet() {
        Guest guest = newGuest("Johnny English", "001", "johnny@english.uk", "123", LocalDate.of(1970, 1, 1));
        guest.setId(1l);
        manager.createGuest(guest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createGuestWithNameNull() {
        Guest guest = newGuest(null, "002", "johnny@english.uk", "123", LocalDate.of(1970, 1, 1));
        manager.createGuest(guest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createGuestWithEmptyName() {
        Guest guest = newGuest("", "002", "johnny@english.uk", "123", LocalDate.of(1970, 1, 1));
        manager.createGuest(guest);
    }

    @Test
    public void updateGuest() {
        Guest g1 = newGuest("Johnny English", "001", "johnny@english.uk", "123", LocalDate.of(1970, 1, 1));
        Guest g2 = newGuest("James Bond", "007", "james@bond.uk", "234", LocalDate.of(1970, 1, 3));
        manager.createGuest(g2);
        manager.createGuest(g1);
        Long guestId = g1.getId();

        Guest guest = manager.getGuestById(guestId);
        guest.setEmail("secret@agent.com");
        g1.setEmail("secret@agent.com");
        manager.updateGuest(g1);
        assertDeepEquals(g1, guest);

        guest = manager.getGuestById(guestId);
        guest.setPassportNo("999");
        g1.setPassportNo("999");
        manager.updateGuest(guest);
        assertDeepEquals(g1, guest);

        guest = manager.getGuestById(guestId);
        guest.setPhone("888");
        g1.setPhone("888");
        manager.updateGuest(guest);
        assertDeepEquals(g1, guest);

        guest = manager.getGuestById(guestId);
        guest.setName("No Name");
        g1.setName("No Name");
        manager.updateGuest(guest);
        assertDeepEquals(g1, guest);
        assertDeepEquals(g1, manager.getGuestById(guestId));

        //check effects on other records
        assertDeepEquals(g2, manager.getGuestById(g2.getId()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateGuestWithGuestNull() {
        manager.updateGuest(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateGuestWithIdNull() {
        Guest guest = newGuest("Johnny English", "001", "johnny@english.uk", "123", LocalDate.of(1970, 1, 1));
        manager.createGuest(guest);
        Long guestId = guest.getId();

        guest = manager.getGuestById(guestId);
        guest.setId(null);
        manager.updateGuest(guest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateGuestWithWrongId() {
        Guest guest = newGuest("Johnny English", "001", "johnny@english.uk", "123", LocalDate.of(1970, 1, 1));
        manager.createGuest(guest);
        Long guestId = guest.getId();

        guest = manager.getGuestById(guestId);
        guest.setId(guestId + 1);
        manager.updateGuest(guest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateGuestWithNameNull() {
        Guest guest = newGuest("Johnny English", "001", "johnny@english.uk", "123", LocalDate.of(1970, 1, 1));
        manager.createGuest(guest);
        Long guestId = guest.getId();

        guest = manager.getGuestById(guestId);
        guest.setName(null);
        manager.updateGuest(guest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateGuestWithEmptyName() {
        Guest guest = newGuest("Johnny English", "001", "johnny@english.uk", "123", LocalDate.of(1970, 1, 1));
        manager.createGuest(guest);
        Long guestId = guest.getId();
        guest = manager.getGuestById(guestId);
        guest.setName("");
        manager.updateGuest(guest);
    }

    @Test
    public void deleteGuest() {
        Guest guest = newGuest("Johnny English", "001", "johnny@english.uk", "123", LocalDate.of(1970, 1, 1));
        Guest poorGuest = newGuest("James Bond", "007", "james@bond.uk", "234", LocalDate.of(1970, 1, 3));

        manager.createGuest(guest);
        manager.createGuest(poorGuest);

        assertNotNull(manager.getGuestById(guest.getId()));
        assertNotNull(manager.getGuestById(poorGuest.getId()));

        manager.deleteGuest(poorGuest);

        assertNotNull(manager.getGuestById(guest.getId()));
        assertNull(manager.getGuestById(poorGuest.getId()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteGuestWithGuestNull() {
        manager.deleteGuest(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteGuestWithIdNull() {
        Guest guest = newGuest("Johnny English", "001", "johnny@english.uk", "123", LocalDate.of(1970, 1, 1));
        manager.createGuest(guest);
        Long guestId = guest.getId();

        guest = manager.getGuestById(guestId);
        guest.setId(null);
        manager.deleteGuest(guest);

    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteGuestWithWrongId() {
        Guest guest = newGuest("Johnny English", "001", "johnny@english.uk", "123", LocalDate.of(1970, 1, 1));
        manager.createGuest(guest);
        Long guestId = guest.getId();

        guest = manager.getGuestById(guestId);
        guest.setId(10l);
        manager.deleteGuest(guest);
    }

    @Test
    public void findGuestByName() {
        assertTrue(manager.findGuestByName("Achmed Muhamad Alii").isEmpty());

        Guest guest = newGuest("Johnny English", "001", "johnny@english.uk", "123", LocalDate.of(1970, 1, 1));
        Guest g2 = newGuest("James Bond", "007", "james@bond.uk", "234", LocalDate.of(1970, 1, 2));
        Guest g3 = newGuest("James Bond", "008", "james_clone@bond.uk", "345", LocalDate.of(1970, 1, 3));

        manager.createGuest(guest);
        manager.createGuest(g2);
        manager.createGuest(g3);

        List<Guest> actual = Arrays.asList(g2, g3);
        List<Guest> expected = manager.findGuestByName("James Bond");

        assertTrue(expected.size() == 2);
        assertTrue(manager.findGuestByName("Johnny English").size() == 1);
        assertTrue(manager.findGuestByName("Achmed Muhamad Alii").isEmpty());

        assertDeepEquals(actual, expected);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findGuestByNameWithNameNull() {
        manager.findGuestByName(null);
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

    private void assertDeepEquals(Guest expected, Guest actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getPassportNo(), actual.getPassportNo());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getPhone(), actual.getPhone());
        assertEquals(expected.getDateOfBirth(), actual.getDateOfBirth());
    }

    private void assertDeepEquals(List<Guest> expectedList, List<Guest> actualList) {
        expectedList.sort(idComparator);
        actualList.sort(idComparator);

        for (int i = 0; i < expectedList.size(); i++) {
            Guest expected = expectedList.get(i);
            Guest actual = actualList.get(i);
            assertDeepEquals(expected, actual);
        }
    }
    private static Comparator<Guest> idComparator = new Comparator<Guest>() {
        @Override
        public int compare(Guest g1, Guest g2) {
            return Long.valueOf(g1.getId()).compareTo(Long.valueOf(g2.getId()));
        }
    };
}
