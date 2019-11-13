/**
 * Project work-time
 * TimeTableBean.java
 *
 * Created on May 20, 2012, 1:30:56 PM
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
@ManagedBean(name = "timeTableBean")
@SessionScoped
public class TimeTableBean implements Serializable {

    private static final long serialVersionUID = 1L;
    @Resource(name = "jdbc/worktime")
    private DataSource ds;
    @ManagedProperty(value = "#{userLogin.sessionRefCode}")
    private String code;
    private Connection con;
    private String sqlStatement;
    private PreparedStatement ps;
    private ResultSet result;
    private String tbid;
    private String tablename;
    private String start;
    private String end;
    private String prestart;
    private String poststart;
    private String preend;
    private String postend;
    private String startot;
    private String endot;
    private List<TimeTable> timeTableList;
    private String addBtDisable;
    private String deleteBtDisable;
    private String saveBTDisable;
    public TimeTableBean() {
        this.sqlStatement = "";
        this.addBtDisable = "false";
        this.deleteBtDisable = "true";
        this.saveBTDisable = "true";
    }

    @PostConstruct
    public void initSettings() throws SQLException {
        initTableList();
    }

    public void initTableList() throws SQLException {
        if (ds == null) {
            throw new SQLException("Can't get data source");
        }
        con = ds.getConnection();
        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        this.timeTableList = new ArrayList<TimeTable>();
        sqlStatement = "SELECT tbid,tablename FROM wt_timetable WHERE ownercode = " + code;
        ps = con.prepareStatement(sqlStatement);
        result = ps.executeQuery();
        while (result.next()) {
            TimeTable tt = new TimeTable();
            tt.setTbid(result.getString("tbid"));
            tt.setTablename(result.getString("tablename"));
            this.timeTableList.add(tt);
        }
        result.close();
        ps.close();
        con.close();
    }
    
    public void loadTemplate(ActionListener action) throws SQLException {
        //Loop copy record from template to user table
        if (ds == null) {
            throw new SQLException("Can't get data source");
        }
        con = ds.getConnection();
        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        sqlStatement = "DELETE FROM wt_timetable WHERE ownercode = " + code +";";
        ps = con.prepareStatement(sqlStatement);
        ps.executeUpdate();
        ps.close();
        sqlStatement = "CREATE TEMPORARY TABLE temp_timetable AS SELECT * FROM wt_timetable WHERE ownercode = 0;";
        ps = con.prepareStatement(sqlStatement);
        ps.executeUpdate();
        ps.close();
        sqlStatement = "UPDATE temp_timetable SET ownercode = " + code + " ,tbid = 0 WHERE ownercode = 0;";
        ps = con.prepareStatement(sqlStatement);
        ps.executeUpdate();
        ps.close();
        sqlStatement = "INSERT INTO wt_timetable SELECT * FROM temp_timetable;";
        ps = con.prepareStatement(sqlStatement);
        ps.executeUpdate();
        ps.close();
        sqlStatement = "DROP TEMPORARY TABLE temp_timetable;";
        ps = con.prepareStatement(sqlStatement);
        ps.executeUpdate();
        ps.close();
        con.close();
        initTableList();
    }
    
    public void deleteAll(ActionListener action) throws SQLException {
        //Delete all row in user table
        if (ds == null) {
            throw new SQLException("Can't get data source");
        }
        con = ds.getConnection();
        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        sqlStatement = "DELETE FROM wt_timetable WHERE ownercode = " + code;
        ps = con.prepareStatement(sqlStatement);
        ps.executeUpdate();
        ps.close();
        con.close();
    }

    public void newTable(ActionListener action) {
        this.tbid = "0";
        this.tablename = "";
        this.start = "00:00:00";
        this.end = "00:00:00";
        this.prestart = "00:00:00";
        this.poststart = "00:00:00";
        this.preend = "00:00:00";
        this.postend = "00:00:00";
        this.startot = "00:00:00";
        this.endot = "00:00:00";
        this.deleteBtDisable = "true";
        this.saveBTDisable = "false";
    }

    public void deleteTable(ActionListener action) throws SQLException {
        if (ds == null) {
            throw new SQLException("Can't get data source");
        }
        con = ds.getConnection();
        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        sqlStatement = "DELETE FROM wt_timetable WHERE tbid = " + tbid;
        ps = con.prepareStatement(sqlStatement);
        ps.executeUpdate();
        ps.close();
        con.close();
        //refresh time table
        timeTableList = null;
        initTableList();
        this.deleteBtDisable = "true";
        this.saveBTDisable = "true";
        //clear form value
        this.tablename = "";
        this.start = "00:00:00";
        this.end = "00:00:00";
        this.prestart = "00:00:00";
        this.poststart = "00:00:00";
        this.preend = "00:00:00";
        this.postend = "00:00:00";
        this.startot = "00:00:00";
        this.endot = "00:00:00";
    }

    public void saveTable(ActionListener action) throws SQLException {
        //save tabledetail
        if (ds == null) {
            throw new SQLException("Can't get data source");
        }
        con = ds.getConnection();
        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        sqlStatement = "INSERT INTO wt_timetable (tbid,ownercode,tablename,"
                + "start,end,prestart,poststart,preend,postend,startot,endot) "
                + "VALUES (" + tbid + ",'" + code + "','" + tablename + "','" + start + "',"
                + "'" + end + "','" + prestart + "','" + poststart + "','" + preend + "',"
                + "'" + postend + "','" + startot + "','" + endot + "') "
                + "ON DUPLICATE KEY UPDATE "
                + "ownercode = '" + code + "',"
                + "tablename = '" + tablename + "',"
                + "start = '" + start + "',"
                + "end = '" + end + "',"
                + "prestart = '" + prestart + "',"
                + "poststart = '" + poststart + "',"
                + "preend = '" + preend + "',"
                + "postend = '" + postend + "',"
                + "startot = '" + startot + "',"
                + "endot = '" + endot + "'";
        ps = con.prepareStatement(sqlStatement);
        ps.executeUpdate();
        ps.close();
        con.close();
        this.deleteBtDisable = "true";
        this.saveBTDisable = "true";
        //refresh time table
        timeTableList = null;
        initTableList();
    }
   

    public void selectTable(ActionListener action) throws SQLException {
        if (ds == null) {
            throw new SQLException("Can't get data source");
        }
        con = ds.getConnection();
        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        sqlStatement = "SELECT * FROM wt_timetable WHERE tbid = " + tbid;
        ps = con.prepareStatement(sqlStatement);
        result = ps.executeQuery();
        while (result.next()) {
            this.tablename = result.getString("tablename");
            this.start = result.getString("start");
            this.end = result.getString("end");
            this.prestart = result.getString("prestart");
            this.poststart = result.getString("poststart");
            this.preend = result.getString("preend");
            this.postend = result.getString("postend");
            this.startot = result.getString("startot");
            this.endot = result.getString("endot");
        }
        result.close();
        ps.close();
        con.close();
        this.deleteBtDisable = "false";
        this.saveBTDisable = "false";
    }

    public void startChanged(ValueChangeEvent event) {
        this.start = event.getNewValue().toString();
    }

    public void endChanged(ValueChangeEvent event) {
        this.end = event.getNewValue().toString();
    }

    public void prestartChanged(ValueChangeEvent event) {
        this.prestart = event.getNewValue().toString();
    }

    public void poststartChanged(ValueChangeEvent event) {
        this.poststart = event.getNewValue().toString();
    }

    public void preendChanged(ValueChangeEvent event) {
        this.preend = event.getNewValue().toString();
    }

    public void postendChanged(ValueChangeEvent event) {
        this.postend = event.getNewValue().toString();
    }

    public void startotChanged(ValueChangeEvent event) {
        this.startot = event.getNewValue().toString();
    }

    public void endotChanged(ValueChangeEvent event) {
        this.endot = event.getNewValue().toString();
    }

    public List<TimeTable> getTimeTableList() {
        return timeTableList;
    }

    public void setTimeTableList(List<TimeTable> timeTableList) {
        this.timeTableList = timeTableList;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTbid() {
        return tbid;
    }

    public void setTbid(String tbid) {
        this.tbid = tbid;
    }

    public String getTablename() {
        return tablename;
    }

    public void setTablename(String tablename) {
        this.tablename = tablename;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getPrestart() {
        return prestart;
    }

    public void setPrestart(String prestart) {
        this.prestart = prestart;
    }

    public String getPoststart() {
        return poststart;
    }

    public void setPoststart(String poststart) {
        this.poststart = poststart;
    }

    public String getPreend() {
        return preend;
    }

    public void setPreend(String preend) {
        this.preend = preend;
    }

    public String getPostend() {
        return postend;
    }

    public void setPostend(String postend) {
        this.postend = postend;
    }

    public String getStartot() {
        return startot;
    }

    public void setStartot(String startot) {
        this.startot = startot;
    }

    public String getEndot() {
        return endot;
    }

    public void setEndot(String endot) {
        this.endot = endot;
    }

    public String getAddBtDisable() {
        return addBtDisable;
    }

    public void setAddBtDisable(String addBtDisable) {
        this.addBtDisable = addBtDisable;
    }

    public String getDeleteBtDisable() {
        return deleteBtDisable;
    }

    public void setDeleteBtDisable(String deleteBtDisable) {
        this.deleteBtDisable = deleteBtDisable;
    }

    public String getSaveBTDisable() {
        return saveBTDisable;
    }

    public void setSaveBTDisable(String saveBTDisable) {
        this.saveBTDisable = saveBTDisable;
    }
 
}
