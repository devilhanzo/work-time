/**
 * Project work-time
 * EmployeeListSelectBean.java
 *
 * Created on May 16, 2012, 6:53:56 PM
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
import org.worktime.department.setup.model.EmployeeSelect;

/**
 *
 * @author Skulchai Chimrakkaeo
 * @contact
 *  email skulchai.ch@gmail.com
 *  phone 085-008-2248
 */
@ManagedBean(name = "employeeListSelectBean")
@SessionScoped
public class EmployeeListSelectBean implements Serializable {

    private static final long serialVersionUID = 1L;
    @Resource(name = "jdbc/worktime")
    private DataSource ds;
    private Connection con;
    private PreparedStatement ps;
    private ResultSet result;
    @ManagedProperty(value = "#{userLogin.sessionRefCode}")
    private String code;
    private List<EmployeeSelect> employees;
    private String kkhDeptId;
    private String searchType;
    private String keyWord;
    private String sqlStatement = "";
    private String[] selectedCode;

    @PostConstruct
    public void initSettings() throws SQLException {
        if (ds == null) {
            throw new SQLException("Can't get data source");
        }

        con = ds.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        sqlStatement = "SELECT empcode FROM own_employee WHERE ownercode = " + code;

        ps = con.prepareStatement(sqlStatement);
        result = ps.executeQuery();

        int rowcount = 0;
        if (result.last()) {
            rowcount = result.getRow();
            result.beforeFirst();
        }
        this.selectedCode = new String[rowcount];
        int i = 0;
        while (result.next()) {
            this.selectedCode[i] = result.getString("empcode");
            i++;
        }

        result.close();
        result = null;
        ps.close();
        ps = null;
        con.close();
        con = null;
    }

    public String[] getSelectedCode() {
        return selectedCode;
    }

    public void setSelectedCode(String[] selectedCode) throws SQLException {
        this.selectedCode = selectedCode;
        this.employees = null;
        if (ds == null) {
            throw new SQLException("Can't get data source");
        }

        con = ds.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        sqlStatement = "DELETE FROM own_employee WHERE ownercode = " + code;
        ps = con.prepareStatement(sqlStatement);
        ps.executeUpdate();
        ps.close();
        ps = null;
        sqlStatement = "INSERT INTO own_employee (ownercode,empcode) VALUES ";
        for (int i = 0; i < selectedCode.length; i++) {
            sqlStatement += "('" + code + "','" + selectedCode[i] + "'"
                    + ((i + 1) == selectedCode.length ? ");" : "),");
        }
        ps = con.prepareStatement(sqlStatement);
        ps.executeUpdate();
        ps.close();
        ps = null;
        con.close();
        con = null;
    }

    public void kkhDeptIdChanged(ValueChangeEvent e) {
        this.kkhDeptId = e.getNewValue().toString();
        this.employees = null;
    }

    public void preloadEmpList(ActionListener event) {
        this.employees = null;
    }

    public String extendedSql() {
        String extendedSql = "";
        if (selectedCode.length > 0) {
            extendedSql = " OR kkhemp.code IN (";
            for (int i = 0; i < selectedCode.length; i++) {
                extendedSql += "'" + selectedCode[i] + "'"
                        + ((i + 1) == selectedCode.length ? ")" : ",");
            }
        }
        return extendedSql;
    }

    public List<EmployeeSelect> getEmployees() throws SQLException {

        if (employees == null) {
            if (ds == null) {
                throw new SQLException("Can't get data source");
            }

            con = ds.getConnection();

            if (con == null) {
                throw new SQLException("Can't get database connection");
            }

            if ("".equals(keyWord) || keyWord == null) {
                sqlStatement = "SELECT kkhemp.code AS code,CONCAT(kkhemp.no,'-',kkhemp.name,' ',kkhemp.surname) AS noempname "
                        + "FROM hip_time.USERINFO AS uinfo "
                        + "JOIN intranet.employee kkhemp ON uinfo.SSN = kkhemp.no AND uinfo.SSN != '' "
                       // + "WHERE kkhemp.expire = '0000-00-00' and ( kkhemp.department = " + kkhDeptId + extendedSql() + ")";
                 + "WHERE kkhemp.department = " + kkhDeptId + extendedSql();

            } else {
                sqlStatement = "SELECT kkhemp.code AS code,CONCAT(kkhemp.no,'-',kkhemp.name,' ',kkhemp.surname) AS noempname "
                        + "FROM hip_time.USERINFO AS uinfo "
                        + "JOIN intranet.employee kkhemp ON uinfo.SSN = kkhemp.no AND uinfo.SSN != '' "
                        //+ "WHERE kkhemp.expire = '0000-00-00' and " + checkSearchType() + " like '%" + keyWord + "%'" + extendedSql();
                        + "WHERE " + checkSearchType() + " like '%" + keyWord + "%'" + extendedSql();
            }
            ps = con.prepareStatement(sqlStatement);
            result = ps.executeQuery();

            employees = new ArrayList<EmployeeSelect>();

            while (result.next()) {
                EmployeeSelect es = new EmployeeSelect();
                es.setCode(result.getString("code"));
                es.setNoempname(result.getString("noempname"));
                employees.add(es);

            }
            result.close();
            result = null;
            ps.close();
            ps = null;
            con.close();
            con = null;
        }
        return employees;
    }

    public void setEmployees(List<EmployeeSelect> employees) {
        this.employees = employees;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getKkhDeptId() {
        return kkhDeptId;
    }

    public void setKkhDeptId(String kkhDeptId) {
        this.kkhDeptId = kkhDeptId;
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

    public String checkSearchType() {
        String type = "kkhemp.name";
        if ("1".equals(searchType)) {
            type = "kkhemp.surname";
        }
        if ("2".equals(searchType)) {
            type = "kkhemp.no";
        }
        return type;
    }
}
