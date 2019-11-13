/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.worktime.admin.common.manage;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionListener;
import javax.sql.DataSource;
import org.worktime.admin.model.common.FingerPrint;

/**
 *
 * @author Puck
 */
@ManagedBean(name = "empManageBean")
@SessionScoped
public class EmpManageBean implements Serializable {
    private static final long serialVersionUID = 1L;
    @Resource(name = "jdbc/worktime")
    private DataSource ds;
    private Connection con;
    private PreparedStatement ps;
    private ResultSet result;
    private List<FingerPrint> fingerPrintList;
    private String searchFinger;
    private String FingerPicked;
    private String listFinger;
    private String hip_no;
    private String hip_name;
    private String no;
    private String empname;
    private String department;
    private String keyWord;
    private String searchType;
    private int test;

    public EmpManageBean() {
        this.listFinger = "true";
        this.FingerPicked = "false";
        this.searchFinger = "false";
    }
    
    public void proloadEmp(ActionListener event){
        this.fingerPrintList = null;
    }

    public List<FingerPrint> getFingerPrintList() throws SQLException {
        if(fingerPrintList == null){
            if(ds == null){
                throw new SQLException("Can't get data source");
            }
            con = ds.getConnection();
            if (con == null) {
                throw new SQLException("Can't get database connection");
            }
            ps = con.prepareStatement("SELECT hip_info.Badgenumber AS hip_no, "
                    + "hip_info.Name AS hip_name ,"
                    + "kkhemp.no AS no,CONCAT(kkhemp.title,kkhemp.name,' ',"
                    + "kkhemp.surname) AS empname,"
                    + "date_format(kkhemp.expire, '%Y-%m-%d') AS expire,"
                    + "office.name As department FROM hip_time.USERINFO hip_info "
                    + "LEFT JOIN intranet.employee kkhemp "
                    + "ON hip_info.SSN = kkhemp.no "
                    + "LEFT JOIN intranet.lib_office office "
                    + "ON kkhemp.department = office.ref "
                    //+ "WHERE kkhemp.expire = '0000-00-00' "
                    + ("true".equals(searchFinger) ? whereCause() : "")
                    + "ORDER BY CONVERT(hip_info.Badgenumber,UNSIGNED INT)");
            result = ps.executeQuery();
            fingerPrintList = new ArrayList<FingerPrint>();
            while (result.next()) {
                FingerPrint fingerPrint = new FingerPrint();
                fingerPrint.setHip_no(result.getString("hip_no"));
                fingerPrint.setHip_name(result.getString("hip_name"));
                fingerPrint.setNo(result.getString("no"));
                fingerPrint.setEmpname(result.getString("empname"));
                fingerPrint.setDepartment(result.getString("department"));
                fingerPrint.setExpire("0000-00-00".equals(result.getString("expire")) 
                        || result.getString("expire") == null ? "":"ลาออก" );
                fingerPrintList.add(fingerPrint);
            }
            result.close();
            result = null;
            ps.close();
            ps = null;
            con.close();
            con = null;
        }
        return fingerPrintList;
    }
    
    public void saveFinger(ActionListener action) throws SQLException {
        
            if (ds == null) {
                throw new SQLException("Can't get data source");
            }
            con = ds.getConnection();
            if (con == null) {
                throw new SQLException("Can't get database connection");
            }
            // Update
            String sql = "UPDATE hip_time.USERINFO "
                    + "SET Name = ? ,"
                    + "SSN = ? "
                    + "WHERE Badgenumber = " + hip_no;
                    
            ps = con.prepareStatement(sql);
            ps.setString(1, hip_name);
            ps.setString(2, no);
            test = ps.executeUpdate();
            ps.close();
            ps = null;
            con.close();
            con = null;
            // reload user list
            fingerPrintList = null;
            listFinger = "true";
            searchFinger = "false";
        
    }

    public String getSearchFinger() {
        return searchFinger;
    }

    public void setSearchFinger(String searchFinger) {
        this.searchFinger = searchFinger;
    }

    public String getFingerPicked() {
        return FingerPicked;
    }

    public void setFingerPicked(String FingerPicked) {
        this.FingerPicked = FingerPicked;
    }

    public String getListFinger() {
        return listFinger;
    }

    public void setListFinger(String listFinger) {
        this.listFinger = listFinger;
    }

    public String getHip_no() {
        return hip_no;
    }

    public void setHip_no(String hip_no) {
        this.hip_no = hip_no;
    }

    public String getHip_name() {
        return hip_name;
    }

    public void setHip_name(String hip_name) {
        this.hip_name = hip_name;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getEmpname() {
        return empname;
    }

    public void setEmpname(String empname) {
        this.empname = empname;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }
    
    public String whereCause(){
        String type = "where hip_info.Badgenumber = '" + keyWord + "'";
        if ("1".equals(searchType)) {
            type = "where kkhemp.no like '%" + keyWord + "%'";
        }
        if ("2".equals(searchType)) {
            type = "where kkhemp.name like '%" + keyWord + "%'";
        }
        if ("3".equals(searchType)) {
            type = "where kkhemp.surname like '%" + keyWord + "%'";
        }
        if ("4".equals(searchType)) {
            type = "where hip_info.Name like '%" + keyWord + "%'";
        }
        return type;
    }
}
