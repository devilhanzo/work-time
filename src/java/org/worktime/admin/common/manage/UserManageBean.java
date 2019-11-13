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
import org.worktime.admin.model.common.SystemUser;

/**
 *
 * @author PucK
 */
@ManagedBean(name = "userManageBean")
@SessionScoped
public class UserManageBean implements Serializable {

    private static final long serialVersionUID = 1L;
    @Resource(name = "jdbc/worktime")
    private DataSource ds;
    private Connection con;
    private PreparedStatement ps;
    private ResultSet result;
    private List<SystemUser> systemUserList;
    private String listActiveUsers;
    private String searchUser;
    private String kkhEmpPicked;
    private int code;
    private String no;
    private String empname;
    private String department;
    private int test;
    private boolean adm_priv;
    private boolean dept_priv;
    private boolean org_priv;
    private boolean suspended_priv;

    public UserManageBean() {
        this.test = 0;
        this.code = 0;
        this.listActiveUsers = "true";
        this.searchUser = "false";
        this.kkhEmpPicked = "false";
    }

    public void preloadUser(ActionListener event) {
        this.systemUserList = null;
    }

    public List<SystemUser> getSystemUserList() throws SQLException {
        if (systemUserList == null) {
            if (ds == null) {
                throw new SQLException("Can't get data source");
            }
            con = ds.getConnection();
            if (con == null) {
                throw new SQLException("Can't get database connection");
            }
            ps = con.prepareStatement("SELECT distinct(kkhemp.code) AS code,"
                    + "kkhemp.no AS no,CONCAT(kkhemp.title,kkhemp.name,' ',"
                    + "kkhemp.surname) AS empname,"
                    + "trustee.adm AS adm, "
                    + "trustee.dep AS dep, "
                    + "trustee.org AS org, "
                    + "trustee.suspended AS suspended, "
                    + "office.name As department FROM hip_time.trustee trustee "
                    + "RIGHT JOIN intranet.employee kkhemp "
                    + "ON trustee.kkhcode = kkhemp.code "
                    + "LEFT JOIN intranet.lib_office office "
                    + "ON kkhemp.department = office.ref "
                    + "WHERE trustee.adm IS NOT NULL "
                    + "AND trustee.dep IS NOT NULL "
                    + "AND trustee.org IS NOT NULL "
                    + "AND trustee.suspended IS NOT NULL "
                    + "ORDER BY trustee.regdate ;");
            result = ps.executeQuery();
            systemUserList = new ArrayList<SystemUser>();
            while (result.next()) {
                SystemUser sysuser = new SystemUser();
                sysuser.setCode(result.getInt("code"));
                sysuser.setNo(result.getString("no"));
                sysuser.setEmpname(result.getString("empname"));
                sysuser.setDepartment(result.getString("department") == null ? "ไม่ระบุ" : result.getString("department"));
                sysuser.setAdm_priv(result.getString("adm"));
                sysuser.setDept_priv(result.getString("dep"));
                sysuser.setOrg_priv(result.getString("org"));
                sysuser.setSuspended_priv(result.getString("suspended"));
                systemUserList.add(sysuser);
            }
            result.close();
            result = null;
            ps.close();
            ps = null;
            con.close();
            con = null;
        }
        return systemUserList;
    }

    public void loadPrivileges(ActionListener action) throws SQLException {
        if (code != 0) {
            if (ds == null) {
                throw new SQLException("Can't get data source");
            }
            con = ds.getConnection();
            if (con == null) {
                throw new SQLException("Can't get database connection");
            }
            ps = con.prepareStatement("SELECT "
                    + "COALESCE(trustee.adm,'N') AS adm, "
                    + "COALESCE(trustee.dep,'N') As dep, "
                    + "COALESCE(trustee.org,'N') As org, "
                    + "COALESCE(trustee.suspended,'N') As suspended "
                    + "FROM hip_time.trustee trustee RIGHT JOIN intranet.employee "
                    + "kkhemp ON trustee.kkhcode = kkhemp.code "
                    + "WHERE kkhemp.expire = '0000-00-00'  "
                    + "AND kkhemp.code = " + code);
            result = ps.executeQuery();
            adm_priv = false;
            dept_priv = false;
            org_priv = false;
            suspended_priv = false;
            while (result.next()) {
                if ("Y".equals(result.getString("adm"))) {
                    this.adm_priv = true;
                } else {
                    this.adm_priv = false;
                }
                if ("Y".equals(result.getString("dep"))) {
                    this.dept_priv = true;
                } else {
                    this.dept_priv = false;
                }
                if ("Y".equals(result.getString("org"))) {
                    this.org_priv = true;
                } else {
                    this.org_priv = false;
                }
                if ("Y".equals(result.getString("suspended"))) {
                    this.suspended_priv = true;
                } else {
                    this.suspended_priv = false;
                }
            }
            result.close();
            result = null;
            ps.close();
            ps = null;
            con.close();
            con = null;
        }
    }

    public void savePrivileges(ActionListener action) throws SQLException {
        if (code != 0) {
            if (ds == null) {
                throw new SQLException("Can't get data source");
            }
            con = ds.getConnection();
            if (con == null) {
                throw new SQLException("Can't get database connection");
            }
            // Update
            String sql = "INSERT INTO trustee (uid,kkhcode,suspended,adm,org,dep,regdate) "
                    + "VALUES (0,'" + code + "',"
                    + "'" + (suspended_priv ? "Y":"N") + "',"
                    + "'" + (adm_priv ? "Y":"N") + "',"
                    + "'" + (org_priv ? "Y":"N") + "',"
                    + "'" + (dept_priv ? "Y":"N") + "',"
                    + "DATE(NOW()) ) "
                    + "ON DUPLICATE KEY UPDATE "
                    + "suspended = '" + (suspended_priv ? "Y":"N") + "',"
                    + "adm = '" + (adm_priv ? "Y":"N") + "',"
                    + "org = '" + (org_priv ? "Y":"N") + "',"
                    + "dep = '" + (dept_priv ? "Y":"N") + "'";
            ps = con.prepareStatement(sql);
            test = ps.executeUpdate();
            ps.close();
            ps = null;
            con.close();
            con = null;
            // reload user list
            systemUserList = null;
            listActiveUsers = "true";
            searchUser = "false";
        }
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getListActiveUsers() {
        return listActiveUsers;
    }

    public void setListActiveUsers(String listActiveUsers) {
        this.searchUser = "false";
        this.listActiveUsers = listActiveUsers;
    }

    public String getSearchUser() {
        return searchUser;
    }

    public void setSearchUser(String searchUser) {
        this.listActiveUsers = "false";
        this.searchUser = searchUser;
    }

    public String getKkhEmpPicked() {
        return kkhEmpPicked;
    }

    public void setKkhEmpPicked(String kkhEmpPicked) {
        this.kkhEmpPicked = kkhEmpPicked;
    }

    public String getEmpname() {
        return empname;
    }

    public void setEmpname(String empname) {
        this.empname = empname;
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

    public boolean isAdm_priv() {
        return adm_priv;
    }

    public void setAdm_priv(boolean adm_priv) {
        this.adm_priv = adm_priv;
    }

    public boolean isDept_priv() {
        return dept_priv;
    }

    public void setDept_priv(boolean dept_priv) {
        this.dept_priv = dept_priv;
    }

    public boolean isOrg_priv() {
        return org_priv;
    }

    public void setOrg_priv(boolean org_priv) {
        this.org_priv = org_priv;
    }

    public boolean isSuspended_priv() {
        return suspended_priv;
    }

    public void setSuspended_priv(boolean suspended_priv) {
        this.suspended_priv = suspended_priv;
    }
    
}
