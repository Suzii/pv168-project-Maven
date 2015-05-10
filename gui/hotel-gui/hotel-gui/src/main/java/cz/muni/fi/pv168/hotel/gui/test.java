/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.hotel.gui;

/**
 *
 * @author Zuzana
 */
public class test {

    public static void main(String args[]) {
        try {
            AppCommons.getStayManager().findTop3Guests();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
