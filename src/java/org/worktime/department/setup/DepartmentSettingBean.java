/**
 * Project work-time
 * DepartmentNameBean.java
 *
 * Created on May 20, 2012, 11:49:18 AM
 *
 * Copyright(c) 2012 Khon Kaen Hospital, Information Technology.  All Rights Reserved.
 * This software is the proprietary information of Khon Kaen Hospital, Information Technology.
 *
 */
package org.worktime.department.setup;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.faces.event.ValueChangeEvent;
import javax.sql.DataSource;
import org.worktime.department.setup.model.TimeTable;

/**
 *
 * @author Skulchai Chimrakkaeo
 * @contact
 *  email skulchai.ch@gmail.com
 *  phone 085-008-2248
 */
@ManagedBean (name="departmentSettingBean")
@SessionScoped
public class DepartmentSettingBean implements Serializable {
    private static final long serialVersionUID = 1L;
    @Resource(name = "jdbc/worktime")
    private DataSource ds;
    private Connection con;
    private PreparedStatement ps;
    private ResultSet result;
    @ManagedProperty(value = "#{userLogin.sessionRefCode}")
    private String code;
    private String departmentName;
    private String sqlStatement;
    private int test;
    private List<TimeTable> timeTableList;
    private String default_tbid;
    private boolean weekendOt;
    private boolean holidayOt;
    private boolean showInComplete;
    public DepartmentSettingBean() {
        this.test = 0;
        this.sqlStatement = "";
    }
    
    @PostConstruct
    public void initSettings() throws SQLException{
        if (ds == null) {
            throw new SQLException("Can't get data source");
        }

        con = ds.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        // init department option
        this.weekendOt = false;
        this.holidayOt = false;
        this.showInComplete = false;
        sqlStatement = "SELECT * FROM own_department WHERE ownercode = " + code;

        ps = con.prepareStatement(sqlStatement);
        result = ps.executeQuery();

        while (result.next()) {
            this.departmentName = result.getString("name");
            this.default_tbid = result.getString("default_tbid");
            if ("1".equals(result.getString("weekendot"))) {
                this.weekendOt = true;
            } else {
                this.weekendOt = false;
            }
            if ("1".equals(result.getString("holidayot"))) {
                this.holidayOt = true;
            } else {
                this.holidayOt = false;
            }
            if ("1".equals(result.getString("showic"))){
                this.showInComplete = true;
            } else {
                this.showInComplete = false;
            }
        }

        result.close();
        result = null;
        ps.close();
        ps = null;
        // get time table
        sqlStatement = "SELECT tbid,tablename FROM wt_timetable WHERE ownercode = " + code;
        timeTableList = new ArrayList<TimeTable>();
        ps = con.prepareStatement(sqlStatement);
        result = ps.executeQuery();
        while (result.next()){
            TimeTable tt = new TimeTable();
            tt.setTbid(result.getString("tbid"));
            tt.setTablename(result.getString("tablename"));
            timeTableList.add(tt);
        }
        result.close();
        result = null;
        ps.close();
        ps = null; 
        con.close();
        con = null;
    }
    
    public void saveSettings(ActionEvent action) throws SQLException{
        this.test = 0;
        if (ds == null) {
            throw new SQLException("Can't get data source");
        }
        con = ds.getConnection();
        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        ps = con.prepareStatement("INSERT INTO own_department "
                + "(ownercode,name,default_tbid,weekendot,holidayot,showic) VALUES "
                + "('" + code + "','" + departmentName + "','" + default_tbid + "','" + (weekendOt ? "1" : "0") + "',"
                + "'" + (holidayOt ? "1" : "0") + "','" + (showInComplete ? "1" : "0") + "') "
                + "ON DUPLICATE KEY UPDATE "
                + "name = '" + departmentName + "',"
                + "default_tbid = '" + default_tbid + "',"
                + "weekendot = '" + (weekendOt ? "1" : "0") + "',"
                + "holidayot = '" + (holidayOt ? "1" : "0") + "',"
                + "showic = '" + (showInComplete ? "1" : "0") + "'");
        test = ps.executeUpdate();
        ps.close();
        ps = null;
        con.close();
        con = null;
        initSettings();
    }
    
    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) throws SQLException {
        this.departmentName = departmentName;
        if (ds == null) {
            throw new SQLException("Can't get data source");
        }

        con = ds.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        sqlStatement = "INSERT INTO own_department (ownercode,name) VALUES ('"+code+"','"+departmentName+"') ON DUPLICATE KEY UPDATE name = '" + departmentName + "'";

        ps = con.prepareStatement(sqlStatement);
        this.test = ps.executeUpdate();

        ps.close();
        ps = null;
        con.close();
        con = null;
    }
// ValueChangeEvent
    public void defaultTableChanged(ValueChangeEvent event){
        this.default_tbid = event.getNewValue().toString();
    }
// Only Getter
    
    public List<TimeTable> getTimeTableList() {
        return timeTableList;
    }

    public void setTimeTableList(List<TimeTable> timeTableList) {
        this.timeTableList = timeTableList;
    }
    
    public void clearTest(ActionListener event){
        this.test = 0;
    }
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getTest() {
        return test;
    }

    public void setTest(int test) {
        this.test = test;
    }

    public String getDefault_tbid() {
        return default_tbid;
    }

    public void setDefault_tbid(String default_tbid) {
        this.default_tbid = default_tbid;
    }

    public boolean isHolidayOt() {
        return holidayOt;
    }

    public void setHolidayOt(boolean holidayOt) {
        this.holidayOt = holidayOt;
    }

    public boolean isShowInComplete() {
        return showInComplete;
    }

    public void setShowInComplete(boolean showInComplete) {
        this.showInComplete = showInComplete;
    }

    public boolean isWeekendOt() {
        return weekendOt;
    }

    public void setWeekendOt(boolean weekendOt) {
        this.weekendOt = weekendOt;
    }
    
}
