/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.project;

import java.util.List;

/**
 *
 * @author pato
 */
public interface RoomManager {
    /**
     * Stores new room into database. Id for the new grave is automatically
     * generated and stored into id attribute.
     * 
     * @param r room to be created.
     * @throws IllegalArgumentException when room is null, or room has already 
     * assigned id.
     * @throws  ServiceFailureException when db operation fails.
     */
      void createRoom(Room r);
      
    /**
     * Returns room with given id.
     * 
     * @param id primary key of requested room.
     * @return room with given id or null if such room does not exist.
     * @throws IllegalArgumentException when given id is null.
     * @throws  ServiceFailureException when db operation fails.
     */
      Room getRoomById(Long id);
      
    /**
     * Updates room in database.
     * 
     * @param r updated room to be stored into database.
     * @throws IllegalArgumentException when room is null, or room has null id, or has 0 or negative capacity or price,
     * or number is not in correct format
     * @throws  ServiceFailureException when db operation fails.
     */
      void updateRoom(Room r);
      
    /**
     * Deletes room from database. 
     * 
     * @param r room to be deleted from db.
     * @throws IllegalArgumentException when room is null, or room has null id, or does not exists.
     * @throws  ServiceFailureException when db operation fails.
     */
      void deleteRoom(Room r);
      
    /**
     * Returns list of all rooms in the database.
     * 
     * @return list of all rooms in database.
     * @throws  ServiceFailureException when db operation fails.
     */
      List<Room> findAllRooms();
      
      /**
       * Returns room with given number
       * 
       * @param n is number of room to be found.
       * @return room with given number or null when the room does not exist 
       * @throws IllegalArgumentException when given number is null.
       * @throws  ServiceFailureException when db operation fails.
       */
      Room findRoomByNumber(String n);  
}
