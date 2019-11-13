/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.worktime.holiday.model;

/**
 *
 * @author PucK
 */
public class Holiday {
    private String holidayDate;
    private String holidayDetail;
    private String holidayType;
    private String holidayURL;

    public String getHolidayDate() {
        return holidayDate;
    }

    public void setHolidayDate(String holidayDate) {
        this.holidayDate = holidayDate;
    }
    
    public String getHolidayDetail() {
        return holidayDetail;
    }

    public void setHolidayDetail(String holidayDetail) {
        this.holidayDetail = holidayDetail;
    }

    public String getHolidayType() {
        return holidayType;
    }

    public void setHolidayType(String holidayType) {
        this.holidayType = holidayType;
    }

    public String getHolidayURL() {
        return holidayURL;
    }

    public void setHolidayURL(String holidayURL) {
        this.holidayURL = holidayURL;
    }
    
}
