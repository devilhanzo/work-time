/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.worktime.department.report;

import com.zyztems.pthaipdf.jasperreports.engine.ThaiExporterManager;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.annotation.PostConstruct;
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
import org.worktime.admin.model.common.KkhDepts;
import org.worktime.admin.model.common.KkhEmployees;
import org.worktime.holiday.model.Holiday;
import org.worktime.report.common.util.ReportCalendar;
import org.worktime.report.model.common.CheckInOut;
import org.worktime.report.model.common.Mounthly;
import org.worktime.report.model.common.TimeTableModel;

/**
 *
 * @author PucK
 */
@ManagedBean(name = "departmentReportBean")
@SessionScoped
public class DepartmentReportBean implements Serializable {

    private static final long serialVersionUID = 1L;
    @Resource(name = "jdbc/worktime")
    DataSource ds;
    private Connection con;
    private PreparedStatement ps;
    private ResultSet result;
    JasperPrint jasperPrint;
    @ManagedProperty(value = "#{userLogin.sessionRefCode}")
    private String code;
//    @ManagedProperty(value = "#{userLogin.sessionNo}")
//    private String no;
    @ManagedProperty(value = "#{userLogin.sessionUname}")
    private String empname;
//    @ManagedProperty(value = "#{userLogin.sessionPosition}")
//    private String position;
//    @ManagedProperty(value = "#{userLogin.sessionDept}")
//    private String department;
    //private List<CheckInOut> checkInOutList;
    private String day;
    private String month;
    private String year;
    private String period;
    private String strSql;
    private String reportType;
    @ManagedProperty(value = "#{listKkhDeptBean.listKkhDept}")
    private List<KkhDepts> listKkhDept;
    @ManagedProperty(value = "#{holidayBean.holidayList}")
    private List<Holiday> holidayList;
    private List<TimeTableModel> timeTableList;
    private List<KkhEmployees> employeeList;
    private List<KkhEmployees> orgEmployeeList;
    private List<CheckInOut> checkInOutListMonth;
    private List<CheckInOut> checkInOutListDay;
    private List<Date> checkdateList;
    private List<Mounthly> mountlyList; 
    private Logger logger = Logger.getLogger(getClass().getName());
    private boolean holidayOt;
    private boolean weekendOt;
    private boolean showInComplete;
    private String deptname;
    private String tablename;
    private String start;
    private String end;
    private String prestart;
    private String preend;
    private String postend;
    private String poststart;
    private String minet;
    private String minot;
    private Date checkdate;
    private SimpleDateFormat regularFormat;
    private SimpleDateFormat weekDay;
    private SimpleDateFormat sqlDateFormat;
    private boolean sortDateAsc = true;
    private boolean sortNameAsc = false;
    private boolean sortNoAsc = false;
    private boolean sortNameAscD = false;
    private boolean sortNoAscD = false;
    private boolean orderByDate;
    private boolean orderByName;
    private boolean dayReportClick;
    private boolean monthReportClick;
    private Locale locale;
    private boolean by_session;
    private boolean by_org;
    private String org_deptid;
    private String org_deptname;

    public DepartmentReportBean() {
        this.strSql = "";
        this.locale = new Locale("th", "TH");
        Locale.setDefault(locale);
        this.month = ReportCalendar.getCurrentMonthNo();
        this.year = ReportCalendar.getCurrentXYear();
//        this.dayReportClick = false;
        this.monthReportClick = false;
        this.orderByDate = true;
        this.orderByName = false;
        this.regularFormat = new SimpleDateFormat("dd/MM/yyyy");
        this.weekDay = new SimpleDateFormat("E", locale);
        this.sqlDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    }

    @PostConstruct
    public void initSettings() throws SQLException {
        // get time table
        strSql = "SELECT * FROM wt_timetable WHERE ownercode = " + code + " ORDER BY tbid";
        if (ds == null) {
            throw new SQLException("Can't get data source");
        }
        con = ds.getConnection();
        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        ps = con.prepareStatement(strSql);
        result = ps.executeQuery();

        this.timeTableList = new ArrayList<TimeTableModel>();

        while (result.next()) {
            TimeTableModel tm = new TimeTableModel();
            tm.setTbid(result.getString("tbid"));
            tm.setTablename(result.getString("tablename"));
            tm.setOwncode(result.getString("ownercode"));
            tm.setStart(result.getString("start"));
            tm.setEnd(result.getString("end"));
            tm.setPrestart(result.getString("prestart"));
            tm.setPoststart(result.getString("poststart"));
            tm.setPreend(result.getString("preend"));
            tm.setPostend(result.getString("postend"));
            tm.setStartot(result.getString("startot"));
            tm.setEndot(result.getString("endot"));
            this.timeTableList.add(tm);
        }
        result.close();
        result = null;
        ps.close();
        ps = null;
        // get department setting
        this.holidayOt = false;
        this.weekendOt = false;
        this.showInComplete = false;
//        this.period = "1"; // default morning 08:00-16:00
        strSql = "SELECT * FROM own_department WHERE ownercode = " + code;
        ps = con.prepareStatement(strSql);
        result = ps.executeQuery();
        while (result.next()) {
            this.period = result.getString("default_tbid");
            this.deptname = result.getString("name");
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
            if ("1".equals(result.getString("showic"))) {
                this.showInComplete = true;
            } else {
                this.showInComplete = false;
            }
        }
        result.close();
        result = null;
        ps.close();
        ps = null;
        //get employee list
        employeeList = new ArrayList<KkhEmployees>();

        strSql = "SELECT ownemp.empcode AS code, kkhemp.no AS no , "
                + "CONCAT(kkhemp.name,' ',kkhemp.surname) AS empname , "
                + "kkhemp.position AS position FROM hip_time.own_employee ownemp "
                + "JOIN intranet.employee kkhemp ON ownemp.empcode = kkhemp.code "
               // + "WHERE ownemp.ownercode = " + code + " order by empname";
              //  + "WHERE kkhemp.expire ='0000-00-00' AND ownemp.ownercode = " + code ;
        + "WHERE ownemp.ownercode = " + code ;

        ps = con.prepareStatement(strSql);
        result = ps.executeQuery();
        while (result.next()) {
            KkhEmployees kkhemp = new KkhEmployees();
            kkhemp.setCode(result.getInt("code"));
            kkhemp.setNo(result.getString("no"));
            kkhemp.setEmpname(result.getString("empname"));
            kkhemp.setPosition(result.getString("position"));
            employeeList.add(kkhemp);
        }
        result.close();
        result = null;
        ps.close();
        ps = null;
        con.close();
        con = null;
    }
    

    public void kkhDeptIdChanged(ValueChangeEvent e) {
        this.org_deptid = e.getNewValue().toString();
        this.checkInOutListDay = null;
        this.checkInOutListMonth = null;
        for (int i = 0; i < listKkhDept.size(); i++) {
            if (listKkhDept.get(i).getKkhdeptid().equals(org_deptid)) {
                this.org_deptname = listKkhDept.get(i).getKkhdeptname();
                break;
            }
        }
    }

    public void loadTimeTable() {

        for (int i = 0; i < timeTableList.size(); i++) {
            if (timeTableList.get(i).getTbid().equals(period)) {
                tablename = timeTableList.get(i).getTablename();
                start = timeTableList.get(i).getStart();
                end = timeTableList.get(i).getEnd();
                prestart = timeTableList.get(i).getPrestart();
                poststart = timeTableList.get(i).getPoststart();
                preend = timeTableList.get(i).getPreend();
                postend = timeTableList.get(i).getPostend();
                minet = timeTableList.get(i).getStartot();
                minot = timeTableList.get(i).getEndot();
                break;
            }
        }

    }

    public void loadOrgEmployees() throws SQLException {
        orgEmployeeList = null;
        if (ds == null) {
            throw new SQLException("Can't get data source");
        }
        con = ds.getConnection();
        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        orgEmployeeList = new ArrayList<KkhEmployees>();

        strSql = "SELECT kkhemp.code AS code,kkhemp.no AS no,"
                + "CONCAT(kkhemp.name,' ',kkhemp.surname) AS empname, "
                + "kkhemp.position AS position FROM intranet.employee kkhemp "
               // + "WHERE kkhemp.department = '" + org_deptid + "' AND kkhemp.expire = '0000-00-00'";
        + "WHERE kkhemp.department = '" + org_deptid ;

        ps = con.prepareStatement(strSql);
        result = ps.executeQuery();
        while (result.next()) {
            KkhEmployees kkhemp = new KkhEmployees();
            kkhemp.setCode(result.getInt("code"));
            kkhemp.setNo(result.getString("no"));
            kkhemp.setEmpname(result.getString("empname"));
            kkhemp.setPosition(result.getString("position"));
            orgEmployeeList.add(kkhemp);
        }
        result.close();
        result = null;
        ps.close();
        ps = null;
        con.close();
        con = null;
    }

    public List<KkhEmployees> getEmployeeList() {
        return employeeList;
    }

    public List<CheckInOut> getCheckInOutListDay() throws ParseException, SQLException {
        if (checkInOutListDay == null) {
            if (by_org || by_session) {
                if (day != null && month != null && year != null && period != null) {
                    Locale.setDefault(locale);
                    checkdate = new Date(regularFormat.parse(day + "/" + month + "/" + ReportCalendar.yearConversion(year)).getTime());
                    loadTimeTable();
                    // Prepare sql where cause
                    String sqlprestart = ReportCalendar.preStart(checkdate, prestart, poststart);
                    String sqlpoststart = year + "-" + month + "-" + day + " " + poststart;
                    String sqlpreend = year + "-" + month + "-" + day + " " + preend;
                    String sqlpostend = ReportCalendar.postEnd(checkdate, preend, postend);
                    // Load Org Employee List Here
                    ///////////////////////////////
                    if (by_org) {
                        loadOrgEmployees();
                    }
                    // Initial Connection
                    if (ds == null) {
                        throw new SQLException("Can't get data source");
                    }
                    con = ds.getConnection();
                    if (con == null) {
                        throw new SQLException("Can't get database connection");
                    }

                    //gen employees list                
                    checkInOutListDay = new ArrayList<CheckInOut>();

                    if (by_session) {
                        for (int i = 0; i < employeeList.size(); i++) {
                            CheckInOut cio = new CheckInOut();
                            cio.setCheckdate(checkdate);
                            cio.setCode(String.valueOf(employeeList.get(i).getCode()));
                            cio.setNo(employeeList.get(i).getNo());
                            cio.setName(employeeList.get(i).getEmpname());
                            cio.setPosition(employeeList.get(i).getPosition());
                            checkInOutListDay.add(cio);
                        }
                    }
                    if (by_org) {
                        for (int i = 0; i < orgEmployeeList.size(); i++) {
                            CheckInOut cio = new CheckInOut();
                            cio.setCheckdate(checkdate);
                            cio.setCode(String.valueOf(orgEmployeeList.get(i).getCode()));
                            cio.setNo(orgEmployeeList.get(i).getNo());
                            cio.setName(orgEmployeeList.get(i).getEmpname());
                            cio.setPosition(orgEmployeeList.get(i).getPosition());
                            checkInOutListDay.add(cio);
                        }
                    }

                    //get checkin
                    for (int i = 0; i < checkInOutListDay.size(); i++) {
                        ps = con.prepareStatement("SELECT checktime FROM wt_checktime "
                                + "WHERE code = " + checkInOutListDay.get(i).getCode() + " AND checktime "
                                + "BETWEEN '" + sqlprestart + "' AND '" + sqlpoststart + "' "
                                + "ORDER BY checktime");
                        result = ps.executeQuery();
                        while (result.next()) {
                            java.sql.Timestamp tmpTime = result.getTimestamp("checktime");
                            checkInOutListDay.get(i).setCheckin(tmpTime);
                        }
                        result.close();
                        result = null;
                        ps.close();
                        ps = null;
                    }
                    //get checkout
                    for (int i = 0; i < checkInOutListDay.size(); i++) {
                        ps = con.prepareStatement("SELECT checktime FROM wt_checktime "
                                + "WHERE code = " + checkInOutListDay.get(i).getCode() + " AND checktime "
                                + "BETWEEN '" + sqlpreend + "' AND '" + sqlpostend + "' "
                                + "ORDER BY checktime DESC");
                        result = ps.executeQuery();
                        while (result.next()) {
                            java.sql.Timestamp tmpTime = result.getTimestamp("checktime");
                            checkInOutListDay.get(i).setCheckout(tmpTime);
                        }
                        result.close();
                        result = null;
                        ps.close();
                        ps = null;
                    }
                    // Close Connection.
                    con.close();
                    con = null;


                    for (int i = 0; i < checkInOutListDay.size(); i++) {
                        //check holiday
                        checkInOutListDay.get(i).setRemark(ReportCalendar.holiday(holidayList, checkInOutListDay.get(i).getCheckdate()));

                        if (!showInComplete) {
                            if (checkInOutListDay.get(i).getCheckin() != null && checkInOutListDay.get(i).getCheckout() != null) {
                                checkInOutListDay.get(i).setTotal(ReportCalendar.totalWorkHour(checkInOutListDay.get(i).getCheckout(), checkInOutListDay.get(i).getCheckin()));

                                checkInOutListDay.get(i).setLate(ReportCalendar.lateTime(checkInOutListDay.get(i).getCheckdate(), checkInOutListDay.get(i).getCheckin(), start));
                                if (weekendOt || holidayOt) {
                                    checkInOutListDay.get(i).setEarlytime("");
                                } else {
                                    checkInOutListDay.get(i).setEarlytime(ReportCalendar.earlyTime(checkInOutListDay.get(i).getCheckdate(), checkInOutListDay.get(i).getCheckin(), start, minet));
                                }

                                checkInOutListDay.get(i).setEarlyout(ReportCalendar.earlyOutTime(checkInOutListDay.get(i).getCheckdate(), checkInOutListDay.get(i).getCheckout(), end));
                                if (weekendOt && ("ส.".equals(weekDay.format(checkInOutListDay.get(i).getCheckdate())) || "อา.".equals(weekDay.format(checkInOutListDay.get(i).getCheckdate())))) {
                                    checkInOutListDay.get(i).setOvertime(checkInOutListDay.get(i).getTotal());
                                } else if (holidayOt && "H".equals(checkInOutListDay.get(i).getRemark())) {
                                    checkInOutListDay.get(i).setOvertime(checkInOutListDay.get(i).getTotal());
                                } else {
                                    checkInOutListDay.get(i).setOvertime(ReportCalendar.overTime(checkInOutListDay.get(i).getCheckdate(), checkInOutListDay.get(i).getCheckout(), end, minot));
                                }
                            } else {
                                checkInOutListDay.get(i).setCheckin(null);
                                checkInOutListDay.get(i).setCheckout(null);
                            }
                        } else {
                            //cal total work hour
                            if (checkInOutListDay.get(i).getCheckin() != null && checkInOutListDay.get(i).getCheckout() != null) {
                                checkInOutListDay.get(i).setTotal(ReportCalendar.totalWorkHour(checkInOutListDay.get(i).getCheckout(), checkInOutListDay.get(i).getCheckin()));
                            }
                            //calculate latetime and earlytime
                            if (checkInOutListDay.get(i).getCheckin() != null) {
                                checkInOutListDay.get(i).setLate(ReportCalendar.lateTime(checkInOutListDay.get(i).getCheckdate(), checkInOutListDay.get(i).getCheckin(), start));
                                if (weekendOt || holidayOt) {
                                    checkInOutListDay.get(i).setEarlytime("");
                                } else {
                                    checkInOutListDay.get(i).setEarlytime(ReportCalendar.earlyTime(checkInOutListDay.get(i).getCheckdate(), checkInOutListDay.get(i).getCheckin(), start, minet));
                                }
                            }
                            //calculate earlyout and overtime
                            if (checkInOutListDay.get(i).getCheckout() != null) {
                                checkInOutListDay.get(i).setEarlyout(ReportCalendar.earlyOutTime(checkInOutListDay.get(i).getCheckdate(), checkInOutListDay.get(i).getCheckout(), end));
                                if (weekendOt) {
                                    if ("ส.".equals(weekDay.format(checkInOutListDay.get(i).getCheckdate())) || "อา.".equals(weekDay.format(checkInOutListDay.get(i).getCheckdate()))) {
                                        if (!"".equals(checkInOutListDay.get(i).getTotal())) {
                                            checkInOutListDay.get(i).setOvertime(checkInOutListDay.get(i).getTotal());
                                        } else {
                                            checkInOutListDay.get(i).setOvertime("");
                                        }
                                    } else {
                                        checkInOutListDay.get(i).setOvertime(ReportCalendar.overTime(checkInOutListDay.get(i).getCheckdate(), checkInOutListDay.get(i).getCheckout(), end, minot));
                                    }
                                } else if (holidayOt) {
                                    if ("H".equals(checkInOutListDay.get(i).getRemark())) {
                                        if (!"".equals(checkInOutListDay.get(i).getTotal())) {
                                            checkInOutListDay.get(i).setOvertime(checkInOutListDay.get(i).getTotal());
                                        } else {
                                            checkInOutListDay.get(i).setOvertime("");
                                        }
                                    } else {
                                        checkInOutListDay.get(i).setOvertime(ReportCalendar.overTime(checkInOutListDay.get(i).getCheckdate(), checkInOutListDay.get(i).getCheckout(), end, minot));
                                    }
                                } else {
                                    checkInOutListDay.get(i).setOvertime(ReportCalendar.overTime(checkInOutListDay.get(i).getCheckdate(), checkInOutListDay.get(i).getCheckout(), end, minot));
                                }
                            }
                        }
                    }
                    // summary
                    if ("daily_sum".equals(reportType)) {
                        
                    }
                }
            }
        }
        return checkInOutListDay;

    }

    public void setCheckInOutListDay(List<CheckInOut> checkInOutListDay) {
        this.checkInOutListDay = checkInOutListDay;
    }

    public List<CheckInOut> getCheckInOutListMonth() throws ParseException, SQLException {
        if (checkInOutListMonth == null) {
            if (by_org || by_session) {
                if (month != null && year != null && period != null) {
                    loadTimeTable();
                    // Load Org Employee List Here
                    if (by_org) {
                        loadOrgEmployees();
                    }
                    // Initial Connection
                    if (ds == null) {
                        throw new SQLException("Can't get data source");
                    }
                    con = ds.getConnection();
                    if (con == null) {
                        throw new SQLException("Can't get database connection");
                    }
                    //get date list
                    checkdateList = new ArrayList<Date>();
                    checkdateList = ReportCalendar.getListDateMonth(month, ReportCalendar.yearConversion(year));
                    //gen employees list                
                    checkInOutListMonth = new ArrayList<CheckInOut>();
                    if (by_session) {
                        for (int ii = 0; ii < checkdateList.size(); ii++) {
                            for (int i = 0; i < employeeList.size(); i++) {
                                CheckInOut cio = new CheckInOut();
                                cio.setCheckdate(checkdateList.get(ii));
                                cio.setCode(String.valueOf(employeeList.get(i).getCode()));
                                cio.setNo(employeeList.get(i).getNo());
                                cio.setName(employeeList.get(i).getEmpname());
                                checkInOutListMonth.add(cio);
                            }
                        }
                    }
                    if (by_org) {
                        for (int ii = 0; ii < checkdateList.size(); ii++) {
                            for (int i = 0; i < orgEmployeeList.size(); i++) {
                                CheckInOut cio = new CheckInOut();
                                cio.setCheckdate(checkdateList.get(ii));
                                cio.setCode(String.valueOf(orgEmployeeList.get(i).getCode()));
                                cio.setNo(orgEmployeeList.get(i).getNo());
                                cio.setName(orgEmployeeList.get(i).getEmpname());
                                checkInOutListMonth.add(cio);
                            }
                        }
                    }

                    //get checkin
                    for (int i = 0; i < checkInOutListMonth.size(); i++) {
                        String sqlprestart = ReportCalendar.preStart(checkInOutListMonth.get(i).getCheckdate(), prestart, poststart);
                        String sqlpoststart = sqlDateFormat.format(checkInOutListMonth.get(i).getCheckdate()) + " " + poststart;
                        ps = con.prepareStatement("SELECT checktime FROM wt_checktime "
                                + "WHERE code = " + checkInOutListMonth.get(i).getCode() + " AND checktime "
                                + "BETWEEN '" + sqlprestart + "' AND '" + sqlpoststart + "' "
                                + "ORDER BY checktime");
                        result = ps.executeQuery();
                        while (result.next()) {
                            java.sql.Timestamp tmpTime = result.getTimestamp("checktime");
                            checkInOutListMonth.get(i).setCheckin(tmpTime);
                        }
                        result.close();
                        result = null;
                        ps.close();
                        ps = null;
                    }
//                get checkout
                    for (int i = 0; i < checkInOutListMonth.size(); i++) {
                        String sqlpreend = sqlDateFormat.format(checkInOutListMonth.get(i).getCheckdate()) + " " + preend;
                        String sqlpostend = ReportCalendar.postEnd(checkInOutListMonth.get(i).getCheckdate(), preend, postend);
                        ps = con.prepareStatement("SELECT checktime FROM wt_checktime "
                                + "WHERE code = " + checkInOutListMonth.get(i).getCode() + " AND checktime "
                                + "BETWEEN '" + sqlpreend + "' AND '" + sqlpostend + "' "
                                + "ORDER BY checktime DESC");
                        result = ps.executeQuery();
                        while (result.next()) {
                            java.sql.Timestamp tmpTime = result.getTimestamp("checktime");
                            checkInOutListMonth.get(i).setCheckout(tmpTime);
                        }
                        result.close();
                        result = null;
                        ps.close();
                        ps = null;
                    }
                    // Close Connection.
                    con.close();
                    con = null;

                    ///////////////////////calculate all row of checkInOutListMonth//////////////////
                    
                    for (int i = 0; i < checkInOutListMonth.size(); i++) {
                        //check holiday
                        checkInOutListMonth.get(i).setRemark(ReportCalendar.holiday(holidayList, checkInOutListMonth.get(i).getCheckdate()));

                        if (!showInComplete) {
                            if (checkInOutListMonth.get(i).getCheckin() != null && checkInOutListMonth.get(i).getCheckout() != null) {
                                checkInOutListMonth.get(i).setTotal(ReportCalendar.totalWorkHour(checkInOutListMonth.get(i).getCheckout(), checkInOutListMonth.get(i).getCheckin()));
                                checkInOutListMonth.get(i).setLate(ReportCalendar.lateTime(checkInOutListMonth.get(i).getCheckdate(), checkInOutListMonth.get(i).getCheckin(), start));
                                if (weekendOt || holidayOt) {
                                    checkInOutListMonth.get(i).setEarlytime("");
                                } else {
                                    checkInOutListMonth.get(i).setEarlytime(ReportCalendar.earlyTime(checkInOutListMonth.get(i).getCheckdate(), checkInOutListMonth.get(i).getCheckin(), start, minet));
                                }
                                checkInOutListMonth.get(i).setEarlyout(ReportCalendar.earlyOutTime(checkInOutListMonth.get(i).getCheckdate(), checkInOutListMonth.get(i).getCheckout(), end));
                                if (weekendOt && ("ส.".equals(weekDay.format(checkInOutListMonth.get(i).getCheckdate())) || "อา.".equals(weekDay.format(checkInOutListMonth.get(i).getCheckdate())))) {
                                    checkInOutListMonth.get(i).setOvertime(checkInOutListMonth.get(i).getTotal());
                                } else if (holidayOt && "H".equals(checkInOutListMonth.get(i).getRemark())) {
                                    checkInOutListMonth.get(i).setOvertime(checkInOutListMonth.get(i).getTotal());
                                } else {
                                    checkInOutListMonth.get(i).setOvertime(ReportCalendar.overTime(checkInOutListMonth.get(i).getCheckdate(), checkInOutListMonth.get(i).getCheckout(), end, minot));
                                }
                            } else {
                                checkInOutListMonth.get(i).setCheckin(null);
                                checkInOutListMonth.get(i).setCheckout(null);
                            }
                        } else {
                            //cal total work hour
                            if (checkInOutListMonth.get(i).getCheckin() != null && checkInOutListMonth.get(i).getCheckout() != null) {
                                checkInOutListMonth.get(i).setTotal(ReportCalendar.totalWorkHour(checkInOutListMonth.get(i).getCheckout(), checkInOutListMonth.get(i).getCheckin()));
                            }
                            //calculate latetime and earlytime
                            if (checkInOutListMonth.get(i).getCheckin() != null) {
                                checkInOutListMonth.get(i).setLate(ReportCalendar.lateTime(checkInOutListMonth.get(i).getCheckdate(), checkInOutListMonth.get(i).getCheckin(), start));
                                if (weekendOt || holidayOt) {
                                    checkInOutListMonth.get(i).setEarlytime("");
                                } else {
                                    checkInOutListMonth.get(i).setEarlytime(ReportCalendar.earlyTime(checkInOutListMonth.get(i).getCheckdate(), checkInOutListMonth.get(i).getCheckin(), start, minet));
                                }
                            }
                            //calculate earlyout and overtime
                            if (checkInOutListMonth.get(i).getCheckout() != null) {
                                checkInOutListMonth.get(i).setEarlyout(ReportCalendar.earlyOutTime(checkInOutListMonth.get(i).getCheckdate(), checkInOutListMonth.get(i).getCheckout(), end));
                                if (weekendOt) {
                                    if ("ส.".equals(weekDay.format(checkInOutListMonth.get(i).getCheckdate())) || "อา.".equals(weekDay.format(checkInOutListMonth.get(i).getCheckdate()))) {
                                        if (!"".equals(checkInOutListMonth.get(i).getTotal())) {
                                            checkInOutListMonth.get(i).setOvertime(checkInOutListMonth.get(i).getTotal());
                                        } else {
                                            checkInOutListMonth.get(i).setOvertime("");
                                        }
                                    } else {
                                        checkInOutListMonth.get(i).setOvertime(ReportCalendar.overTime(checkInOutListMonth.get(i).getCheckdate(), checkInOutListMonth.get(i).getCheckout(), end, minot));
                                    }
                                } else if (holidayOt) {
                                    if ("H".equals(checkInOutListMonth.get(i).getRemark())) {
                                        if (!"".equals(checkInOutListMonth.get(i).getTotal())) {
                                            checkInOutListMonth.get(i).setOvertime(checkInOutListMonth.get(i).getTotal());
                                        } else {
                                            checkInOutListMonth.get(i).setOvertime("");
                                        }
                                    } else {
                                        checkInOutListMonth.get(i).setOvertime(ReportCalendar.overTime(checkInOutListMonth.get(i).getCheckdate(), checkInOutListMonth.get(i).getCheckout(), end, minot));
                                    }
                                } else {
                                    checkInOutListMonth.get(i).setOvertime(ReportCalendar.overTime(checkInOutListMonth.get(i).getCheckdate(), checkInOutListMonth.get(i).getCheckout(), end, minot));
                                }
                            }
                        }
                    }
                    
                    // summary   
                    mountlyList = new ArrayList<Mounthly>();
                    if ("mountly_sum".equals(reportType)) {
                        for (int i = 0; i < employeeList.size(); i++) {
                                Mounthly mmm = new Mounthly();
                              //  mmm.setCheckdate(checkdateList.get(ii));
                                mmm.setCode(String.valueOf(employeeList.get(i).getCode()));
                                mmm.setNo(employeeList.get(i).getNo());
                                mmm.setName(employeeList.get(i).getEmpname());
                                mmm.setPosition(employeeList.get(i).getPosition());
                                mmm.setD01("ม");
                                mmm.setD02("ม");
                                mmm.setD03("ม");
                                mmm.setD04("ป");
                                mmm.setD05("ม");
                                mmm.setD06("");
                                mmm.setD07("");
                                mmm.setD08("");
                                mmm.setD09("");
                                mmm.setD10("");
                                mmm.setD11("");
                                mmm.setD12("");
                                mmm.setD13("");
                                mmm.setD14("");
                                mmm.setD15("");
                                mmm.setD16("");
                                mmm.setD17("");
                                mmm.setD18("");
                                mmm.setD19("");
                                mmm.setD20("");
                                mmm.setD21("");
                                mmm.setD22("");
                                mmm.setD23("");
                                mmm.setD24("");
                                mmm.setD25("");
                                mmm.setD26("");
                                mmm.setD27("");
                                mmm.setD28("");
                                mmm.setD29("");
                                mmm.setD30("");
                                mmm.setD31("");
                                mountlyList.add(mmm);
                            }
                    }
                }
            }
        }
        return checkInOutListMonth;
    }

    public void setCheckInOutListMonth(List<CheckInOut> checkInOutListMonth) {
        this.checkInOutListMonth = checkInOutListMonth;
    }

    public List<Holiday> getHolidayList() {
        return holidayList;
    }

    public void setHolidayList(List<Holiday> holidayList) {
        this.holidayList = holidayList;
    }

    public void dayList2Null(ActionListener action) {
        this.checkInOutListDay = null;
    }

    public void monthList2Null(ActionListener action) {
        this.checkInOutListMonth = null;
    }

    // Value Change Event
    public void monthChanged(ValueChangeEvent event) {
        this.month = event.getNewValue().toString();
    }

    public void yearChanged(ValueChangeEvent event) {
        this.year = event.getNewValue().toString();
    }

    public void print(ActionListener actionEvent) throws JRException, IOException, ParseException, SQLException {
        getCheckInOutListMonth();
        JRBeanCollectionDataSource checkInOutDS = null;
        String jasperPath = "";
        HashMap<String, Object> params = new HashMap<String, Object>();
        if (by_org) {
            params.put("department", org_deptname);
        }
        if (by_session) {
            params.put("department", deptname);
        }
        params.put("month", month);
        params.put("year", ReportCalendar.yearConversion(year));
        params.put("reporter", empname);
        if (showInComplete) {
            params.put("remark_label", "หมายเหตุ");
            params.put("showincomplete", "ข้อมูลลงเวลาจะสมบูรณ์เมื่อลงเวลาครบทั้งเข้า และออก");
        } else {
            params.put("remark_label", "");
            params.put("showincomplete", "");
        }

        params.put("tablename", tablename);
        if ("day".equals(reportType)) {
            params.put("day", Integer.parseInt(day));
            checkInOutDS = new JRBeanCollectionDataSource(checkInOutListDay);
            jasperPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/report/jasper/DepartmentDay.jasper");
        }
        if ("daily_sum".equals(reportType)) {
            params.put("day", Integer.parseInt(day));
            checkInOutDS = new JRBeanCollectionDataSource(checkInOutListDay);
            jasperPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/report/jasper/DepartmentDailySum.jasper");
        }
        if ("month".equals(reportType)) {

            checkInOutDS = new JRBeanCollectionDataSource(checkInOutListMonth);
            if (orderByDate) {
                jasperPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/report/jasper/DepartmentMonthByDate.jasper");
            }
            if (orderByName) {
                jasperPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/report/jasper/DepartmentMonthByName.jasper");
            }
        }
        if ("mountly_sum".equals(reportType)) {

            checkInOutDS = new JRBeanCollectionDataSource(mountlyList);
            
                jasperPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/report/jasper/Department_Mountly_Sum.jasper");
            
        }
        jasperPrint = JasperFillManager.fillReport(jasperPath, params, checkInOutDS);
        jasperPrint.setLocaleCode("th_TH");
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        HttpServletResponse httpServletResponse = (HttpServletResponse) externalContext.getResponse();
        httpServletResponse.reset();
        ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
        ThaiExporterManager.exportReportToPdfStream(jasperPrint, servletOutputStream);
        facesContext.responseComplete();
        logger.info("User Print Report");
    }

    public void pdf(ActionListener actionEvent) throws JRException, IOException {
        JRBeanCollectionDataSource checkInOutDS = null;
        String jasperPath = "";
        String filename = "";
        HashMap<String, Object> params = new HashMap<String, Object>();
        if (by_org) {
            params.put("department", org_deptname);
            filename = org_deptname;
                    }
        if (by_session) {
            params.put("department", deptname);
            filename = deptname;
        }
        params.put("month", month);
        params.put("year", ReportCalendar.yearConversion(year));
        params.put("reporter", empname);
        if (showInComplete) {
            params.put("remark_label", "หมายเหตุ");
            params.put("showincomplete", "ข้อมูลลงเวลาจะสมบูรณ์เมื่อลงเวลาครบทั้งเข้า และออก");
        } else {
            params.put("remark_label", "");
            params.put("showincomplete", "");
        }

        params.put("tablename", tablename);
        if ("day".equals(reportType)) {
            params.put("day", Integer.parseInt(day));
            checkInOutDS = new JRBeanCollectionDataSource(checkInOutListDay);
            jasperPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/report/jasper/DepartmentDay.jasper");
        }
        if ("daily_sum".equals(reportType)) {
            params.put("day", day);
            checkInOutDS = new JRBeanCollectionDataSource(checkInOutListDay);
            jasperPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/report/jasper/DepartmentDailySum.jasper");
        }
        if ("month".equals(reportType)) {

            checkInOutDS = new JRBeanCollectionDataSource(checkInOutListMonth);
            if (orderByDate) {
                jasperPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/report/jasper/DepartmentMonthByDate.jasper");
            }
            if (orderByName) {
                jasperPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/report/jasper/DepartmentMonthByName.jasper");
            }
        }
        jasperPrint = JasperFillManager.fillReport(jasperPath, params, checkInOutDS);
        jasperPrint.setLocaleCode("th_TH");
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        HttpServletResponse httpServletResponse = (HttpServletResponse) externalContext.getResponse();
        httpServletResponse.reset();
        httpServletResponse.addHeader("Content-disposition", "attachment; filename=" + "DepartmentMonth"
                + ReportCalendar.yearConversion(year) + "-" + month + (day == null ? "" : "-" + day) + "_" + java.net.URLEncoder.encode(filename, "UTF-8") + ".pdf");
        ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
        ThaiExporterManager.exportReportToPdfStream(jasperPrint, servletOutputStream);
        facesContext.responseComplete();
        logger.info("User Download Report");
    }

    public String noSortDay() {
        if (sortNoAscD) {
            Collections.sort(checkInOutListDay, new Comparator() {

                @Override
                public int compare(Object t, Object t1) {
                    CheckInOut c = (CheckInOut) t;
                    CheckInOut c2 = (CheckInOut) t1;
                    return c2.getNo().compareToIgnoreCase(c.getNo());
                }
            });
            sortNoAscD = false;
        } else {
            Collections.sort(checkInOutListDay, new Comparator() {

                @Override
                public int compare(Object t, Object t1) {
                    CheckInOut c = (CheckInOut) t;
                    CheckInOut c2 = (CheckInOut) t1;
                    return c.getNo().compareToIgnoreCase(c2.getNo());
                }
            });
            sortNoAscD = true;
        }
        return null;
    }

    public String nameSortDay() {
        if (sortNameAscD) {
            Collections.sort(checkInOutListDay, new Comparator() {

                @Override
                public int compare(Object t, Object t1) {
                    CheckInOut c = (CheckInOut) t;
                    CheckInOut c2 = (CheckInOut) t1;
                    return c2.getName().compareToIgnoreCase(c.getName());
                }
            });
            sortNameAscD = false;
        } else {
            Collections.sort(checkInOutListDay, new Comparator() {

                @Override
                public int compare(Object t, Object t1) {
                    CheckInOut c = (CheckInOut) t;
                    CheckInOut c2 = (CheckInOut) t1;
                    return c.getName().compareToIgnoreCase(c2.getName());
                }
            });
            sortNameAscD = true;
        }
        return null;
    }

    public String noSortMonth() {
        if (sortNoAsc) {
            Collections.sort(checkInOutListMonth, new Comparator() {

                @Override
                public int compare(Object t, Object t1) {
                    CheckInOut c = (CheckInOut) t;
                    CheckInOut c2 = (CheckInOut) t1;
                    return c2.getNo().compareToIgnoreCase(c.getNo());
                }
            });
            sortNoAsc = false;
        } else {
            Collections.sort(checkInOutListMonth, new Comparator() {

                @Override
                public int compare(Object t, Object t1) {
                    CheckInOut c = (CheckInOut) t;
                    CheckInOut c2 = (CheckInOut) t1;
                    return c.getNo().compareToIgnoreCase(c2.getNo());
                }
            });
            sortNoAsc = true;
        }
        return null;
    }

    public String nameSortMonth() {
        if (sortNameAsc) {
            Collections.sort(checkInOutListMonth, new Comparator() {

                @Override
                public int compare(Object t, Object t1) {
                    CheckInOut c = (CheckInOut) t;
                    CheckInOut c2 = (CheckInOut) t1;
                    return c2.getName().compareToIgnoreCase(c.getName());
                }
            });
            sortNameAsc = false;
        } else {
            Collections.sort(checkInOutListMonth, new Comparator() {

                @Override
                public int compare(Object t, Object t1) {
                    CheckInOut c = (CheckInOut) t;
                    CheckInOut c2 = (CheckInOut) t1;
                    return c.getName().compareToIgnoreCase(c2.getName());
                }
            });
            sortNameAsc = true;
        }
        return null;
    }

    public String dateSortMonth() {
        if (sortDateAsc) {
            Collections.sort(checkInOutListMonth, new Comparator() {

                @Override
                public int compare(Object t, Object t1) {
                    CheckInOut c = (CheckInOut) t;
                    CheckInOut c2 = (CheckInOut) t1;
                    return c2.getCheckdate().compareTo(c.getCheckdate());
                }
            });
            sortDateAsc = false;
        } else {
            Collections.sort(checkInOutListMonth, new Comparator() {

                @Override
                public int compare(Object t, Object t1) {
                    CheckInOut c = (CheckInOut) t;
                    CheckInOut c2 = (CheckInOut) t1;
                    return c.getCheckdate().compareTo(c2.getCheckdate());
                }
            });
            sortDateAsc = true;
        }
        return null;
    }

    public List<TimeTableModel> getTimeTableList() {
        return timeTableList;
    }

    public void setTimeTableList(List<TimeTableModel> timeTableList) {
        this.timeTableList = timeTableList;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEmpname() {
        return empname;
    }

    public void setEmpname(String empname) {
        this.empname = empname;
    }

//    public String getNo() {
//        return no;
//    }
//
//    public void setNo(String no) {
//        this.no = no;
//    }
//
//    public String getPosition() {
//        return position;
//    }
//
//    public void setPosition(String position) {
//        this.position = position;
//    }
    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
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

//    public String getDepartment() {
//        return department;
//    }
//
//    public void setDepartment(String department) {
//        this.department = department;
//    }
    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }
    // only getter

    public boolean isHolidayOt() {
        return holidayOt;
    }

    public boolean isShowInComplete() {
        return showInComplete;
    }

    public boolean isWeekendOt() {
        return weekendOt;
    }

    public String getDeptname() {
        return deptname;
    }

    public boolean isDayReportClick() {
        return dayReportClick;
    }

    public void setDayReportClick(boolean dayReportClick) {
        this.dayReportClick = dayReportClick;
    }

    public boolean isMonthReportClick() {
        return monthReportClick;
    }

    public void setMonthReportClick(boolean monthReportClick) {
        this.monthReportClick = monthReportClick;
    }

    public void setOrderByDate(boolean orderByDate) {
        this.orderByDate = orderByDate;
    }

    public void setOrderByName(boolean orderByName) {
        this.orderByName = orderByName;
    }

    public boolean isBy_org() {
        return by_org;
    }

    public void setBy_org(boolean by_org) {
        this.by_org = by_org;
    }

    public boolean isBy_session() {
        return by_session;
    }

    public void setBy_session(boolean by_session) {
        this.by_session = by_session;
    }

    public String getOrg_deptid() {
        return org_deptid;
    }

    public void setOrg_deptid(String org_deptid) {
        this.org_deptid = org_deptid;
    }

    public List<KkhDepts> getListKkhDept() {
        return listKkhDept;
    }

    public void setListKkhDept(List<KkhDepts> listKkhDept) {
        this.listKkhDept = listKkhDept;
    }
}
