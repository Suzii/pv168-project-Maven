/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.project;

import java.math.BigDecimal;
import java.util.Objects;
import java.time.LocalDate;

/**
 *
 * @author Zuzana
 */
public class Stay {
    private Long id;
    private LocalDate startDate;
    private LocalDate expectedEndDate;
    private LocalDate realEndDate;
    private Guest guest;
    private Room room;
    private BigDecimal minibarCosts;
    
    public Guest getGuest() {
        return guest;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getExpectedEndDate() {
        return expectedEndDate;
    }

    public void setExpectedEndDate(LocalDate expectedEndDate) {
        this.expectedEndDate = expectedEndDate;
    }

    public LocalDate getRealEndDate() {
        return realEndDate;
    }

    public void setRealEndDate(LocalDate realEndDate) {
        this.realEndDate = realEndDate;
    }

    public BigDecimal getMinibarCosts() {
        return minibarCosts;
    }

    public void setMinibarCosts(BigDecimal minibarCosts) {
        this.minibarCosts = minibarCosts;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Stay other = (Stay) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Stay{" + "id=" + id + ", startDate=" + startDate + ", expectedEndDate=" + expectedEndDate + ", realEndDate=" + realEndDate + '}';
    }
}
