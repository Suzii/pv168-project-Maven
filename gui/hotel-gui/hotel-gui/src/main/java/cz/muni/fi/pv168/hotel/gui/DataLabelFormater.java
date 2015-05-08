/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.hotel.gui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JFormattedTextField.AbstractFormatter;

/**
 *
 * @author pato
 */
public class DataLabelFormater extends AbstractFormatter {

    private String datePattern = "yyyy-MM-dd";
    private SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);

    @Override
    public Object stringToValue(String text) throws ParseException {
        return dateFormatter.parseObject(text);
    }

    @Override
    public String valueToString(Object value) throws ParseException {
        if (value != null) {
            /* LocalDate ld = (LocalDate)value;
             Instant instant = ld.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
             Date time = Date.from(instant);*/
            /*Date date = (Date) dateFormatter.parse(value.toString());
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);*/
            Calendar cal = (Calendar) value;
            return dateFormatter.format(cal.getTime());
        }

        return "";
    }
   /* public String toString(Object value) throws ParseException{
             LocalDate ld = (LocalDate)value;
             Instant instant = ld.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
             Date time = Date.from(instant);
            Date date = (Date) dateFormatter.parse(value.toString());
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            return dateFormatter.format(cal.getTime());
    }*/

}
