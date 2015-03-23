/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.project;

import java.util.List;

/**
 *
 * @author Zuzana
 */
public interface GuestManager {
    
    /**
     * Stores new guest into database. Id for the new guest is automatically
     * generated and stored into id attribute.
     * 
     * @param guest guest to be created.
     * @throws IllegalArgumentException when guest is null, guest has already 
     * assigned id, guest has empty name or it is null.
     * @throws  ServiceFailureException when db operation fails.
     */
    void createGuest(Guest guest);
    
    /**
     * Returns guest with given id.
     * 
     * @param id primary key of requested guest.
     * @return guest with given id or null if such guest does not exist.
     * @throws IllegalArgumentException when given id is null.
     * @throws  ServiceFailureException when db operation fails.
     */
    Guest getGuestById(Long id);
    
    /**
     * Updates guest in database.
     * 
     * @param guest updated guest to be stored into database.
     * @throws IllegalArgumentException when guest is null, or guest has null id,
     * guest had an id which does not exist, name is null or empty.
     * @throws  ServiceFailureException when db operation fails.
     */
    void updateGuest(Guest guest);
    
    /**
     * Deletes guest from database. 
     * 
     * @param guest guest to be deleted from db.
     * @throws IllegalArgumentException when guest is null, or guest has null id.
     * @throws  ServiceFailureException when db operation fails.
     */
    void deleteGuest(Guest guest);
    
    /**
     * Returns list of all guests in the database.
     * 
     * @return list of all guests in database.
     * @throws  ServiceFailureException when db operation fails.
     */
    List<Guest> findAllGuests();
    
    /**
     * Returns list of all guests whose name matches the parameter passed.
     * 
     * @param name name of guests to retrieve from database
     * @return list of all guests with specified name
     */
    List<Guest> findGuestByName(String name);
}
