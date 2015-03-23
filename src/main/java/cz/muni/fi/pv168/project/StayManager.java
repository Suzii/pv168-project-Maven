/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.project;

import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author Zuzana
 */
public interface StayManager {
    
    /**
     * Stores new stay into database. Id for the new stay is automatically
     * generated and stored into id attribute.
     * 
     * @param stay stay to be created.
     * @throws IllegalArgumentException when stay is null, or stay has null id,
     * stay had an id which already exists, guest or room attribute is null or
     * not in the database, either expected or real end date is before start 
     * date or mini bar costs is negative.
     * @throws  ServiceFailureException when db operation fails.
     */
    void createStay(Stay stay);
    
    /**
     * Returns stay with given id.
     * 
     * @param id primary key of requested stay.
     * @return stay with given id or null if such stay does not exist.
     * @throws IllegalArgumentException when given id is null.
     * @throws  ServiceFailureException when db operation fails.
     */
    Stay getStayById(Long id);
    
    /**
     * Updates stay in database.
     * 
     * @param stay updated stay to be stored into database.
     * @throws IllegalArgumentException when stay is null, or stay has null id,
     * stay had an id which does not exist, guest or room attribute is null or
     * not in the database, either expected or real end date is before start
     * date or mini bar costs is negative.
     * @throws  ServiceFailureException when db operation fails.
     */
    void updateStay(Stay stay);
    
    /**
     * Deletes stay from database. 
     * 
     * @param stay stay to be deleted from db.
     * @throws IllegalArgumentException when stay is null, or stay has null id, or stay does not 
     * exists in db.
     * @throws  ServiceFailureException when db operation fails.
     */
    void deleteStay(Stay stay);
    
    /**
     * Returns list of all stays in the database.
     * 
     * @return list of all stays in database.
     * @throws  ServiceFailureException when db operation fails.
     */
    List<Stay> findAllStays();
    
    /**
     * Returns stays in hotel which are in concret date area.
     * 
     * @param from is date from which we want to find stays
     * @param to si date to which we want to find stays in hotel
     * @return stays which are in hotel in this date
     */
    List<Stay> findStaysByDate(LocalDate from, LocalDate to);
    
    /**
     * Returns a list of all guests that stayed at the hotel on given date.
     * 
     * @param date when guests to be retrieved stayed in the hotel
     * @return list of all guests staying at the hotel on specified date
     */
    List<Guest> findStayingGuestsByDate(LocalDate date);
    
    /**
     * Returns list of rooms that are fre from given date for specified number 
     * of days
     * 
     * @param date from which room should be free
     * @param len number of days for which room should be free
     * @return rooms that are free from specified date for specified number 
     * of days
     * @throws IllegalArgumentException if length is zero or negative
     */
    List<Room> findFreeRoomsByDateAndLen(LocalDate date, int len);
    
    /**
     * Returns list of stays of current guest in hotel for whole time.
     * 
     * @param guest is guest from whom we want to now stays in hotel for whole time
     * @return list of stays of current guest
     */
    List<Stay> findAllStaysForGuest(Guest guest);
    
    /**
     * Returns list of rooms which were occupied by given guest on given date
     * @param guest who stayed at room
     * @param date on which guest stayed at room
     * @return list of rooms guest stayed at on specified date
     * @throws IllegalArgumentException guest is null or his id is null (not in DB)
     */
    List<Room> findRoomsForGuestByDate(Guest guest, LocalDate date);
    
    /**
     * Returns stays in room at concret date
     * 
     * @param room is room in which we want to know stays
     * @param date is date in which we want to know stays in room
     * @return stays in room in concret date (should be always one stay ,only in the day when are changin guest in room should be two)
     */
    List<Stay> findStaysForRoomByDate(Room room, LocalDate date);
    
    /**
     * Returns list of all guests who stayed at room on given date.
     * 
     * @param room which guests should have stayed at
     * @param date date on which guests should have stayed on room
     * @return list of rooms guest stayed at on specified date
     * @throws IllegalArgumentException guest is null or his id is null (not in DB)
     */
    List<Guest> findGuestsForRoomByDate(Room room, LocalDate date);
    
    /**
     * Returns free rooms with some capacity from the date 
     * 
     * @param date form which we want free room
     * @param capacity which we want in room
     * @return free rooms with concret capacity form some date
     */
    List<Room> findFreeRoomByDateAndCapacity(LocalDate date, int capacity);
    
    /**
     * Return top 3 guests in hotel which have the most stays in hotel
     * 
     * @return sorted list of guest , sorted by most stays in hotel
     */
    List<Guest> findTop3Guests();
}
