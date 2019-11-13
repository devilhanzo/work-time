/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.worktime.calendar;

import java.io.Serializable;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ValueChangeEvent;
import org.worktime.calendar.model.Days;
import org.worktime.report.common.util.ReportCalendar;

/**
 *
 * @author PucK
 */
@ManagedBean
@SessionScoped
public class SelectDays implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Days> days;
    private String day;
    private String month;
    private String year;

    public SelectDays() {
        this.day = ReportCalendar.getCurrentDay();
        this.month = ReportCalendar.getCurrentMonthNo();
        this.year = ReportCalendar.getCurrentXYear();
    }
    
    public void monthChanged(ValueChangeEvent e){
        this.month = e.getNewValue().toString();
    }
    
    public void yearChanged(ValueChangeEvent e){
        this.year = e.getNewValue().toString();
    }
    
    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
    
    public List<Days> getDays() {
        this.days = ReportCalendar.getListDays(month, ReportCalendar.yearConversion(year));
        return days;
    }

    public void setDays(List<Days> days) {
        this.days = days;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
    
}
