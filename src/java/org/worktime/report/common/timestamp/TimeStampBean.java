/**
 * Project work-time TimeStampBean.java
 *
 * Created on May 1, 2012, 0:00:00 AM
 *
 * Copyright(c) 2012 Khon Kaen Hospital, Information Technology. All Rights
 * Reserved. This software is the proprietary information of , Information
 * Technology.
 *
 */
package org.worktime.report.common.timestamp;

import com.zyztems.pthaipdf.jasperreports.engine.ThaiExporterManager;
import java.io.IOException;
import java.io.Serializable;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;
import javax.annotation.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionListener;
import javax.faces.event.ValueChangeEvent;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.log4j.Logger;
import org.worktime.report.common.util.ReportCalendar;
import org.worktime.report.model.common.TimeStamp;

/**
 *
 * @author Skulchai Chimrakkaeo @contact email skulchai.ch@gmail.com phone
 * 085-008-2248
 */
@ManagedBean
@SessionScoped
public class TimeStampBean implements Serializable {

    private static final long serialVersionUID = 1L;
    @Resource(name = "jdbc/worktime")
    private DataSource ds;
    private Connection con;
    private PreparedStatement ps;
    private ResultSet result;
    JasperPrint jasperPrint;
    private List<TimeStamp> timeStampList;
    @ManagedProperty(value = "#{userLogin.sessionRefCode}")
    private String code;
    @ManagedProperty(value = "#{userLogin.sessionNo}")
    private String no;
    @ManagedProperty(value = "#{userLogin.sessionUname}")
    private String empname;
    @ManagedProperty(value = "#{userLogin.sessionPosition}")
    private String position;
    @ManagedProperty(value = "#{userLogin.sessionDept}")
    private String department;
    private String month;
    private String year;
    private String empPicked = "false";
    private Logger logger = Logger.getLogger(getClass().getName());
    private String org_code;
    private String org_no;
    private String org_empname;
    private String org_position;
    private String org_department;
    private String noid_badgenumber;
    private String noid_name;
    private boolean by_session;
    private boolean by_org;
    private boolean by_org_noid;
    private String reportType;
    private String strSql;

    public TimeStampBean() {
        Locale locale = new Locale("th", "TH");
        Locale.setDefault(locale);
        this.month = ReportCalendar.getCurrentMonthNo();
        this.year = ReportCalendar.getCurrentXYear();
        this.strSql = "";
        this.by_session = false;
        this.by_org = false;
        this.by_org_noid = false;
    }

    public void preloadTimeStampList(ActionListener event) {
        this.timeStampList = null;
    }

    public List<TimeStamp> getTimeStampList() throws SQLException {
        if (timeStampList == null) {
            if (by_org || by_org_noid || by_session) {
                Timestamp sqlTimstamp;
                Date dateConverter;
//                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy E");
//                DateFormat timeFormat = new SimpleDateFormat("HH:mm");

                if (ds == null) {
                    throw new SQLException("Can't get data source");
                }

                con = ds.getConnection();

                if (con == null) {
                    throw new SQLException("Can't get database connection");
                }
                if (by_session) {
                    strSql = "SELECT "
                            + "checktime.CHECKTIME AS checktime, "
                            + "checktime.VERIFYCODE AS verifycode,"
                            + "scanner.MachineAlias AS scanner "
                            + "FROM intranet.employee kkhemp "
                            + "JOIN USERINFO uinfo "
                            + "ON kkhemp.no = uinfo.SSN "
                            + "JOIN CHECKINOUT checktime "
                            + "ON uinfo.Badgenumber = checktime.Badgenumber "
                            + "JOIN MACHINE scanner "
                            + "ON checktime.SENSORID =  scanner.MachineNumber "
                            + "WHERE kkhemp.code = " + code + " "
                            + "AND checktime.CHECKTIME like '" + year + "-" + month + "%' "
                            + "ORDER BY checktime.CHECKTIME ";
                }
                if (by_org) {
                    strSql = "SELECT "
                            + "checktime.CHECKTIME AS checktime, "
                            + "checktime.VERIFYCODE AS verifycode,"
                            + "scanner.MachineAlias AS scanner "
                            + "FROM intranet.employee kkhemp "
                            + "JOIN USERINFO uinfo "
                            + "ON kkhemp.no = uinfo.SSN "
                            + "JOIN CHECKINOUT checktime "
                            + "ON uinfo.Badgenumber = checktime.Badgenumber "
                            + "JOIN MACHINE scanner "
                            + "ON checktime.SENSORID =  scanner.MachineNumber "
                            + "WHERE kkhemp.code = " + org_code + " "
                            + "AND checktime.CHECKTIME like '" + year + "-" + month + "%' "
                            + "ORDER BY checktime.CHECKTIME";
                }
                if (by_org_noid) {
                    strSql = "SELECT "
                            + "checktime.CHECKTIME AS checktime, "
                            + "checktime.VERIFYCODE AS verifycode,"
                            + "scanner.MachineAlias AS scanner "
                            + "FROM USERINFO uinfo "
                            + "JOIN CHECKINOUT checktime "
                            + "ON uinfo.Badgenumber = checktime.Badgenumber "
                            + "JOIN MACHINE scanner "
                            + "ON checktime.SENSORID =  scanner.MachineNumber "
                            + "WHERE uinfo.Badgenumber = " + noid_badgenumber + " "
                            + "AND checktime.CHECKTIME like '" + year + "-" + month + "%' "
                            + "ORDER BY checktime.CHECKTIME";
                }
                ps = con.prepareStatement(strSql);
                result = ps.executeQuery();

                timeStampList = new ArrayList<TimeStamp>();

                while (result.next()) {
                    TimeStamp ts = new TimeStamp();
                    sqlTimstamp = result.getTimestamp("checktime");
                    dateConverter = new Date(sqlTimstamp.getTime());
                    ts.setCheckdate(dateConverter);
                    ts.setVerifycode(result.getString("verifycode"));
                    ts.setScanner(result.getString("scanner"));
                    timeStampList.add(ts);
                }
                result.close();
                result = null;
                ps.close();
                ps = null;
                con.close();
                con = null;
            }
        }
        return timeStampList;
    }

    public void print(ActionListener actionEvent) throws JRException, IOException {
        String jasperPath;
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("month", month);
        params.put("year", ReportCalendar.yearConversion(year));
        if (by_session) {
            params.put("no_label", "เลขที่เงินเดือน");
            params.put("no", no);
            params.put("empname", empname);
            params.put("position", position);
            params.put("department", department);
            params.put("reporterlb", null);
            params.put("reporter", null);
        }
        if (by_org) {
            params.put("no_label", "เลขที่เงินเดือน");
            params.put("no", org_no);
            params.put("empname", org_empname);
            params.put("position", org_position);
            params.put("department", org_department);
            params.put("reporterlb", "ผู้พิมพ์");
            params.put("reporter", empname);
        }
        if (by_org_noid) {
            params.put("no_label", "เลขที่ในระบบ");
            params.put("no", noid_badgenumber);
            params.put("empname", noid_name);
            params.put("position", "-");
            params.put("department", "-");
            params.put("reporterlb", "ผู้พิมพ์");
            params.put("reporter", empname);
        }
        jasperPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/report/jasper/TimeStamp.jasper");
        JRBeanCollectionDataSource timeStampDS = new JRBeanCollectionDataSource(timeStampList);
        jasperPrint = JasperFillManager.fillReport(jasperPath, params, timeStampDS);
        jasperPrint.setLocaleCode("th_TH");
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        HttpServletResponse httpServletResponse = (HttpServletResponse) externalContext.getResponse();
        httpServletResponse.reset();
        ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
        ThaiExporterManager.exportReportToPdfStream(jasperPrint, servletOutputStream);
        facesContext.responseComplete();
        logger.debug("User " + empname + " Print Report");
        logger.info("User " + empname + " Print Report");
    }

    public void pdf(ActionListener actionEvent) throws JRException, IOException {
        String jasperPath;
        String filename = "";
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("month", month);
        params.put("year", ReportCalendar.yearConversion(year));
        if (by_session) {
            params.put("no_label", "เลขที่เงินเดือน");
            params.put("no", no);
            params.put("empname", empname);
            params.put("position", position);
            params.put("department", department);
            params.put("reporterlb", null);
            params.put("reporter", null);
            filename = no;
        }
        if (by_org) {
            params.put("no_label", "เลขที่เงินเดือน");
            params.put("no", org_no);
            params.put("empname", org_empname);
            params.put("position", org_position);
            params.put("department", org_department);
            params.put("reporterlb", "ผู้พิมพ์");
            params.put("reporter", empname);
            filename = org_no;
        }
        if (by_org_noid) {
            params.put("no_label", "เลขที่เงินเดือน");
            params.put("no", noid_badgenumber);
            params.put("empname", noid_name);
            params.put("position", "-");
            params.put("department", "-");
            params.put("reporterlb", "ผู้พิมพ์");
            params.put("reporter", empname);
            filename = noid_badgenumber;
        }
        jasperPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/report/jasper/TimeStamp.jasper");
        JRBeanCollectionDataSource timeStampDS = new JRBeanCollectionDataSource(timeStampList);
        jasperPrint = JasperFillManager.fillReport(jasperPath, params, timeStampDS);
        jasperPrint.setLocaleCode("th_TH");
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        HttpServletResponse httpServletResponse = (HttpServletResponse) externalContext.getResponse();
        httpServletResponse.reset();
        httpServletResponse.addHeader("Content-disposition", "attachment; filename=" + "TimeStamp"
                + ReportCalendar.yearConversion(year) + "-" + month + "_" + java.net.URLEncoder.encode(filename, "UTF-8") + ".pdf");
        ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
        ThaiExporterManager.exportReportToPdfStream(jasperPrint, servletOutputStream);
        facesContext.responseComplete();
        logger.debug("User " + empname + " Download PDF Report");
        logger.info("User " + empname + " Download PDF Report");
    }

    public void monthChanged(ValueChangeEvent e) {
        this.month = e.getNewValue().toString();
    }

    public void yearChanged(ValueChangeEvent e) {
        this.year = e.getNewValue().toString();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEmpPicked() {
        return empPicked;
    }

    public void setEmpPicked(String empPicked) {
        this.empPicked = empPicked;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
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

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getOrg_code() {
        return org_code;
    }

    public void setOrg_code(String org_code) {
        this.org_code = org_code;
    }

    public String getOrg_department() {
        return org_department;
    }

    public void setOrg_department(String org_department) {
        this.org_department = org_department;
    }

    public String getOrg_empname() {
        return org_empname;
    }

    public void setOrg_empname(String org_empname) {
        this.org_empname = org_empname;
    }

    public String getOrg_no() {
        return org_no;
    }

    public void setOrg_no(String org_no) {
        this.org_no = org_no;
    }

    public String getOrg_position() {
        return org_position;
    }

    public void setOrg_position(String org_position) {
        this.org_position = org_position;
    }

    public String getNoid_badgenumber() {
        return noid_badgenumber;
    }

    public void setNoid_badgenumber(String noid_badgenumber) {
        this.noid_badgenumber = noid_badgenumber;
    }

    public String getNoid_name() {
        return noid_name;
    }

    public void setNoid_name(String noid_name) {
        this.noid_name = noid_name;
    }

    public boolean isBy_org() {
        return by_org;
    }

    public void setBy_org(boolean by_org) {
        this.by_org = by_org;
    }

    public boolean isBy_org_noid() {
        return by_org_noid;
    }

    public void setBy_org_noid(boolean by_org_noid) {
        this.by_org_noid = by_org_noid;
    }

    public boolean isBy_session() {
        return by_session;
    }

    public void setBy_session(boolean by_session) {
        this.by_session = by_session;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }
}
