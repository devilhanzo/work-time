/**
 * Project work-time ListKkhEmpBean.java
 *
 * Created on May 1, 2012, 0:00:00 AM
 *
 * Copyright(c) 2012 Khon Kaen Hospital, Information Technology. All Rights
 * Reserved. This software is the proprietary information of , Information
 * Technology.
 *
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
import javax.faces.event.ValueChangeEvent;
import javax.sql.DataSource;
import org.worktime.admin.model.common.KkhEmployees;

/**
 *
 * @author Skulchai Chimrakkaeo @contact email skulchai.ch@gmail.com phone
 * 085-008-2248
 */
@ManagedBean(name = "listKkhEmpBean")
@SessionScoped
public class ListKkhEmpBean implements Serializable {

    private static final long serialVersionUID = 1L;
    @Resource(name = "jdbc/worktime")
    private DataSource ds;
    private Connection con;
    private PreparedStatement ps;
    private ResultSet result;
    private List<KkhEmployees> kkhEmployeeList;
    private String searchType;
    private String keyWord;
    private String kkhDeptId;
    private String sqlStatement;

    public void kkhDeptIdChanged(ValueChangeEvent e) {
        this.kkhDeptId = e.getNewValue().toString();
        this.kkhEmployeeList = null;
    }
    
    public void preloadEmployeeList(ActionListener action){
        this.kkhEmployeeList = null;
    }
    public List<KkhEmployees> getKkhEmployeeList() throws SQLException {
        if (kkhEmployeeList == null) {
            if (ds == null) {
                throw new SQLException("Can't get data source");
            }
            con = ds.getConnection();
            if (con == null) {
                throw new SQLException("Can't get database connection");
            }
            if ("".equals(keyWord) || keyWord == null) {
                sqlStatement = "SELECT emp.code AS code,emp.no AS no, "
                        + "CONCAT(emp.title,emp.name,' ',emp.surname) AS empname,"
                        + "emp.position AS position,office.name AS department "
                        + "FROM intranet.employee emp "
                        + "JOIN intranet.lib_office office ON emp.department = office.ref "
                        + "WHERE emp.expire = '0000-00-00' AND emp.department = ? "
                        + "ORDER BY empname";
            } else {
                sqlStatement = "SELECT emp.code AS code,emp.no AS no, "
                        + "CONCAT(emp.title,emp.name,' ',emp.surname) AS empname,"
                        + "emp.position AS position,office.name AS department "
                        + "FROM intranet.employee emp "
                        + "LEFT JOIN intranet.lib_office office ON emp.department = office.ref "
                        + "WHERE emp.expire = '0000-00-00' "
                        + "AND " + checkSearchType() + " like ? "
                        + "ORDER BY empname";
            }
            ps = con.prepareStatement(sqlStatement);
            if ("".equals(keyWord) || keyWord == null) {
                ps.setString(1, kkhDeptId);
            } else {
                ps.setString(1, "%"+keyWord+"%");
            }
            
            result = ps.executeQuery();
            kkhEmployeeList = new ArrayList<KkhEmployees>();
            while (result.next()) {
                KkhEmployees kkhemp = new KkhEmployees();
                kkhemp.setCode(result.getInt("code"));
                kkhemp.setNo(result.getString("no"));
                kkhemp.setEmpname(result.getString("empname"));
                kkhemp.setPosition(result.getString("position"));
                kkhemp.setDepartment(result.getString("department") == null ? "ไม่ระบุ" : result.getString("department"));
                kkhEmployeeList.add(kkhemp);
            }
            result.close();
            result = null;
            ps.close();
            ps = null;
            con.close();
            con = null;
        }
        return kkhEmployeeList;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public String getKkhDeptId() {
        return kkhDeptId;
    }

    public void setKkhDeptId(String kkhDeptId) {
        this.kkhDeptId = kkhDeptId;
    }

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    public String checkSearchType() {
        String type = "emp.name";
        if ("1".equals(searchType)) {
            type = "emp.surname";
        }
        if ("2".equals(searchType)) {
            type = "emp.no";
        }
        return type;
    }
}
