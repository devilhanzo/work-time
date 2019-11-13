/**
 * Project work-time
 * SelectYears.java
 *
 * Created on May 1, 2012, 0:00:00 AM
 *
 * Copyright(c) 2012 Khon Kaen Hospital, Information Technology.  All Rights Reserved.
 * This software is the proprietary information of , Information Technology.
 *
 */
package org.worktime.calendar;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.worktime.calendar.model.Years;
import org.worktime.report.common.util.ReportCalendar;

/**
 *
 * @author Skulchai Chimrakkaeo
 * @contact
 *  email skulchai.ch@gmail.com
 *  phone 085-008-2248
 */
@ManagedBean
@SessionScoped
public class SelectYears implements Serializable {
    private static final long serialVersionUID = 1L;
    public SelectYears() {
        Locale locale = new Locale("th","TH");
        Locale.setDefault(locale);
    }
    
    public List<Years> getYearsList() throws SQLException {
        return ReportCalendar.getYearList();
    }    
}
