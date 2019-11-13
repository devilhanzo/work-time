/**
 * Project work-time
 * UserLogin.java
 *
 * Created on May 5, 2012, 0:44:46 AM
 *
 * Copyright(c) 2012 Khon Kaen Hospital, Information Technology.  All Rights Reserved.
 * This software is the proprietary information of , Information Technology.
 *
 */
package org.worktime.web.session;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.annotation.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionListener;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import org.apache.log4j.Logger;

/**
 *
 * @author Skulchai Chimrakkaeo
 * @contact
 *  email skulchai.ch@gmail.com
 *  phone 085-008-2248
 */
@ManagedBean
@SessionScoped
public class UserLogin implements Serializable {
    private static final long serialVersionUID = 1L;
    @Resource(name = "jdbc/worktime")
    private DataSource ds;
    private Connection con;
    private PreparedStatement ps;
    private ResultSet result;
    private static String reqURL;
    final String normalUserURL = "/work-time/report/report.jsf?type=personal_report";
    private String username;
    private String password;
    private String sessionUname;
    private String sessionRefCode;
    private String sessionNo;
    private String sessionPosition;
    private String sessionDept;
    private String sessionAdm;
    private String sessionDep;
    private String sessionOrg;
    private String SessionSuspended;
    private String valid;
    private String refcode;
    private String uname;
    private String position;
    private String department;
    private String no;
    private String adm;
    private String dep;
    private String org;
    private String suspended;
    private Logger logger = Logger.getLogger(getClass().getName());
    public UserLogin() {
        reqURL = "/work-time/";
        valid = "failure";
        refcode = "0";
        uname = "";
    }

    public String getSessionUname() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        sessionUname = (String) session.getAttribute("uName");
        return sessionUname;
    }

    public String getSessionRefCode() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        sessionRefCode = (String) session.getAttribute("referCode");
        return sessionRefCode;
    }


    public String getSessionDept() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        sessionDept = (String) session.getAttribute("department");
        return sessionDept;
    }

    public String getSessionNo() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        sessionNo = (String) session.getAttribute("no");
        return sessionNo;
    }

    public String getSessionPosition() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        sessionPosition = (String) session.getAttribute("position");
        return sessionPosition;
    }

    public String getSessionAdm() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        sessionAdm = (String) session.getAttribute("adm_priv");
        return sessionAdm;
    }

    public String getSessionDep() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        sessionDep = (String) session.getAttribute("dept_priv");
        return sessionDep;
    }

    public String getSessionOrg() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        sessionOrg = (String) session.getAttribute("org_priv");
        return sessionOrg;
    }

    public String getSessionSuspended() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        SessionSuspended = (String) session.getAttribute("priv_suspended");
        return SessionSuspended;
    }   
    

    public String getUname() {
        return uname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String checkValidUser() throws SQLException, IOException {

        if (username != null && password != null) {
            if (ds == null) {
                throw new SQLException("Can't get data source");
            }

            con = ds.getConnection();

            if (con == null) {
                throw new SQLException("Can't get database connection");
            }

            ps = con.prepareStatement("SELECT "
                    + "kkhemp.code AS kkhcode,kkhemp.no AS no,"
                    + "CONCAT(kkhemp.name,' ',kkhemp.surname) AS kkhname,"
                    + "kkhemp.position AS position,office.name AS department,"
//                    + "COALESCE(trustee.privilege,'1') AS privilege "
                    + "COALESCE(trustee.adm,'N') AS adm, "
                    + "COALESCE(trustee.dep,'N') AS dep, "
                    + "COALESCE(trustee.org,'N') AS org, "
                    + "COALESCE(trustee.suspended,'N') AS suspended "
                    + "FROM hip_time.trustee trustee "
                    + "RIGHT JOIN intranet.employee kkhemp "
                    + "ON trustee.kkhcode = kkhemp.code "
                    + "LEFT JOIN intranet.lib_office office "
                    + "ON kkhemp.department = office.ref "
                    + "WHERE kkhemp.expire = '0000-00-00' "
                    + "AND kkhemp.id = ? "
                    + "AND kkhemp.passwd = OLD_PASSWORD(?)");
            ps.setString(1, username);
            ps.setString(2, password);
            result = ps.executeQuery();
            while (result.next()) {

                refcode = result.getString("kkhcode");
                uname = result.getString("kkhname");
                position = result.getString("position");
                department = result.getString("department") == null ? "ไม่ระบุ" : result.getString("department");
                no = result.getString("no");
                adm = result.getString("adm");
                dep = result.getString("dep");
                org = result.getString("org");
                suspended = result.getString("suspended");
            }
            result.close();
            result = null;
            ps.close();
            ps = null;
            con.close();
            con = null;
            // Check refcode query from data base is not empty set (Got user)
            if (!"0".equals(refcode)) {
                // Check user has registered hip system or not
                if (isHipRegistered()) {
                    logger.info("User : " + username + " Logged in. refcode = " + refcode);
                    // Save user variable to session
                    setSessionVariables(uname, refcode, no, department, position,adm,dep,org,suspended);
                    // Check user privileges for redirect page
                    
                    if ("N".equals(suspended)) {
                        // // login.jsf form outcome : Not navigate
                        valid = "success";
                        if ("N".equals(dep) && "N".equals(org) && "N".equals(adm)) {
                            FacesContext.getCurrentInstance().getExternalContext().redirect(normalUserURL);
                                //Normal user login                           
                        } else {
                                //Redirect to user select menu
                            FacesContext.getCurrentInstance().getExternalContext().redirect(reqURL);                           
                        }
                    } else {
                        // login.jsf form outcome : Navigate to suspended.jsf
                        valid = "suspended";
                    }
                } else {
                    valid = "notregister";
                    refcode = "0";
                }
            } else {
                // login.jsf form outcome : Navigate to loginfail.jsf
                valid = "failure";
                //Failure Login Log here
                logger.info("Login failure for user :" + username + ", password : " + password);
            }
        }
        return valid;
    }

    public void setSessionVariables(String uname, String ref, String no, String dept, String pos,String adm_priv,String dept_priv,String org_priv,String priv_suspended) {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        session.setAttribute("uName", uname);
        session.setAttribute("referCode", ref);
        session.setAttribute("department", dept);
        session.setAttribute("position", pos);
        session.setAttribute("no", no);
        session.setAttribute("adm_priv", adm_priv);
        session.setAttribute("dept_priv", dept_priv);
        session.setAttribute("org_priv", org_priv);
        session.setAttribute("priv_suspended", priv_suspended);
    }
    // Set request url call from SessionFilter
    public static void setReqURL(String reqUrl) {
        reqURL = reqUrl;
    }
    // Redirect to normal user url call from suspended.jsf
    public void gotoUserPage(ActionListener actionEvent) throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().redirect(normalUserURL);
    }

    private boolean isHipRegistered() throws SQLException {

        if (ds == null) {
            throw new SQLException("Can't get data source");
        }

        con = ds.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        ps = con.prepareStatement("SELECT COUNT(*) AS count "
                + "FROM intranet.employee emp "
                + "JOIN hip_time.USERINFO uinfo ON emp.no = uinfo.SSN "
                + "WHERE emp.code =" + refcode);

        result = ps.executeQuery();
        int test = 0;
        while (result.next()) {
            test = result.getInt("count");
        }
        result.close();
        result = null;
        ps.close();
        ps = null;
        con.close();
        con = null;
        if (test > 0) {
            return true;
        } else {
            return false;
        }
    }
}
