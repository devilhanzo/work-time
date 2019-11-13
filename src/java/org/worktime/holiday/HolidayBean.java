/**
 * Project work-time HolidayBean.java
 *
 * Created on May 1, 2012, 0:04:46 AM
 *
 * Copyright(c) 2012 Khon Kaen Hospital, Information Technology. All Rights
 * Reserved. This software is the proprietary information of , Information
 * Technology.
 *
 */
package org.worktime.holiday;

import java.io.Serializable;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.annotation.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.sql.DataSource;
import org.worktime.holiday.model.Holiday;
import org.worktime.report.common.util.ReportCalendar;

/**
 *
 * @author Skulchai Chimrakkaeo @contact email skulchai.ch@gmail.com phone
 * 085-008-2248
 */
@ManagedBean(name = "holidayBean")
@SessionScoped
public class HolidayBean implements Serializable {

    private static final long serialVersionUID = 1L;
    @Resource(name = "jdbc/worktime")
    private DataSource ds;
    private Connection con;
    private PreparedStatement ps;
    private ResultSet result;
    private List<Holiday> holidayList;
    private String currentYear;
    private String currentDate;

    public HolidayBean() {
        Locale locale = new Locale("th", "TH");
        Locale.setDefault(locale);
    }

    public String getCurrentDate() {
        currentDate = ReportCalendar.getCurrentDate();
        return currentDate;
    }

    public String getCurrentYear() {
        currentYear = ReportCalendar.getCurrentTHYear();
        return currentYear;
    }

    @SuppressWarnings("empty-statement")
    public List<Holiday> getHolidayList() throws SQLException {
        if (holidayList == null) {
            Timestamp sqlTimstamp;
            Date dateConverter;
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy E",new Locale("th","TH"));

            if (ds == null) {
                throw new SQLException("Can't get data source");
            }
            //get database connection
            con = ds.getConnection();

            if (con == null) {
                throw new SQLException("Can't get database connection");
            }
            ps = con.prepareStatement("SELECT date,detail,type,url "
                    + "FROM intranet.holiday WHERE date LIKE '" + ReportCalendar.getCurrentXYear() + "%' "
                    + "ORDER BY date;");

            result = ps.executeQuery();

            holidayList = new ArrayList<Holiday>();

            while (result.next()) {
                Holiday holiday = new Holiday();
                sqlTimstamp = result.getTimestamp("date");
                dateConverter = new Date(sqlTimstamp.getTime());
                holiday.setHolidayDate(dateFormat.format(dateConverter));
                holiday.setHolidayDetail(result.getString("detail"));
                holiday.setHolidayType(result.getString("type"));
                holiday.setHolidayURL(result.getString("url"));
                
                
                holidayList.add(holiday);
            }
            result.close();
            result = null;
            ps.close();
            ps = null;
            con.close();
            con = null;
        }
        return holidayList;
    }
}
