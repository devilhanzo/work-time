/**
 * Project work-time
 * UserSetup.java
 *
 * Created on May 6, 2012, 3:57:02 PM
 *
 * Copyright(c) 2012 Khon Kaen Hospital , Information Technology.  All Rights Reserved.
 * This software is the proprietary information of Khon Kaen Hospital , Information Technology.
 *
 */
package org.worktime.personal.setting;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.sql.DataSource;

/**
 *
 * @author Skulchai Chimrakkaeo
 * @contact
 *  email skulchai.ch@gmail.com
 *  phone 085-008-2248
 */
@ManagedBean
@SessionScoped
public class PersonalSettingBean implements Serializable {

    private static final long serialVersionUID = 1L;
    @Resource(name = "jdbc/worktime")
    private DataSource ds;
    private Connection con;
    private PreparedStatement ps;
    private ResultSet result;
    @ManagedProperty(value = "#{userLogin.sessionRefCode}")
    private String code;
    private boolean holidayOt;
    private boolean weekendOt;
    private boolean showInComplete;
    private String default_tbid;
    
    private int sqlUpdateTest;

    public PersonalSettingBean() {
        this.default_tbid = "1";
        this.weekendOt = false;
        this.holidayOt = false;
        this.showInComplete = false;
    }

    @PostConstruct
    public void initSettings() throws SQLException {
        if (ds == null) {
            throw new SQLException("Can't get data source");
        }
        con = ds.getConnection();
        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        ps = con.prepareStatement("SELECT * FROM usetting WHERE ownercode = " + code);
        result = ps.executeQuery();

        while (result.next()) {
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
        con.close();
        con = null;
    }

    public void saveSetting(ActionEvent action) throws SQLException {
        sqlUpdateTest = 0;
        if (ds == null) {
            throw new SQLException("Can't get data source");
        }
        con = ds.getConnection();
        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        ps = con.prepareStatement("INSERT INTO usetting "
                + "(ownercode,default_tbid,weekendot,holidayot,showic) VALUES "
                + "('" + code + "','" + default_tbid + "','" + (weekendOt ? "1" : "0") + "',"
                + "'" + (holidayOt ? "1" : "0") + "','" + (showInComplete ? "1" : "0") + "') "
                + "ON DUPLICATE KEY UPDATE "
                + "default_tbid = '" + default_tbid + "',"
                + "weekendot = '" + (weekendOt ? "1" : "0") + "',"
                + "holidayot = '" + (holidayOt ? "1" : "0") + "',"
                + "showic = '" + (showInComplete ? "1" : "0") + "'");
        sqlUpdateTest = ps.executeUpdate();
        ps.close();
        ps = null;
        con.close();
        con = null;
        initSettings();
    }
    //Action Event
    public void clearSqlUpdateTest(ActionEvent action){
        this.sqlUpdateTest = 0;
    }
    //Value changed listerner

    public void default_tbidChanged(ValueChangeEvent event) {
        this.default_tbid = event.getNewValue().toString();
    }
    //getter and setter

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isShowInComplete() {
        return showInComplete;
    }

    public void setShowInComplete(boolean showInComplete) {
        this.showInComplete = showInComplete;
    }

    public boolean isHolidayOt() {
        return holidayOt;
    }

    public void setHolidayOt(boolean holidayOt) {
        this.holidayOt = holidayOt;
    }

    public boolean isWeekendOt() {
        return weekendOt;
    }

    public void setWeekendOt(boolean weekendOt) {
        this.weekendOt = weekendOt;
    }

    public String getDefault_tbid() {
        return default_tbid;
    }

    public void setDefault_tbid(String default_tbid) {
        this.default_tbid = default_tbid;
    }

    public int getSqlUpdateTest() {
        return sqlUpdateTest;
    }

    public void setSqlUpdateTest(int sqlUpdateTest) {
        this.sqlUpdateTest = sqlUpdateTest;
    }
    
}
