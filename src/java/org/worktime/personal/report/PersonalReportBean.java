/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.worktime.personal.report;

import com.zyztems.pthaipdf.jasperreports.engine.ThaiExporterManager;
import java.io.IOException;
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
import org.worktime.holiday.model.Holiday;
import org.worktime.report.common.util.ReportCalendar;
import org.worktime.report.model.common.CheckInOut;
import org.worktime.report.model.common.TimeTableModel;

/**
 *
 * @author PucK
 */
@ManagedBean(name = "personalReportBean")
@SessionScoped
public class PersonalReportBean {

    private static final long serialVersionUID = 1L;
    @Resource(name = "jdbc/worktime")
    private DataSource ds;
    private Connection con;
    private PreparedStatement ps;
    private ResultSet result;
    JasperPrint jasperPrint;
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
    @ManagedProperty(value = "#{holidayBean.holidayList}")
    private List<Holiday> holidayList;
    private List<CheckInOut> checkInOutListMonth;
    private List<CheckInOut> checkInOutListDay;
    private List<TimeTableModel> timeTableList;
    private List<Date> checkdateList;
    private String reportType;
    private String period;
    private String day;
    private String month;
    private String year;
    private String strSql;
    private Logger logger = Logger.getLogger(getClass().getName());
    private String tablename;
    private String start;
    private String end;
    private String prestart;
    private String poststart;
    private String preend;
    private String postend;
    private String minot;
    private String minet;
    private Date checkdate;
    //no setter getter
    private long longSumWorkHour;
    private long longSumEarlyTime;
    private long longSumOverTime;
    private boolean holidayOt;
    private boolean weekendOt;
    private boolean showInComplete;
    //private int totalWorkDay;
    private int totalCheckIn;
    private int totalCheckOut;
    private int totalLate;
    private int totalEarlyOut;
    private String totalWorkHour;
    private String totalOverTime;
    private String totalEarlyTime;
//    private SimpleDateFormat regularFormat = new SimpleDateFormat("dd/MM/yyyy");
//    private SimpleDateFormat weekDay = new SimpleDateFormat("E", new Locale("th", "TH"));
//    private SimpleDateFormat sqlDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private Locale locale;
    private SimpleDateFormat regularFormat;
    private SimpleDateFormat weekDay;
    private SimpleDateFormat sqlDateFormat;
    private boolean dayReportClick;
    private boolean monthReportClick;
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

    public PersonalReportBean() {
        this.strSql = "";
        this.locale = new Locale("th", "TH");
        Locale.setDefault(locale);
        this.month = ReportCalendar.getCurrentMonthNo();
        this.year = ReportCalendar.getCurrentXYear();
        this.dayReportClick = false;
        this.monthReportClick = false;
        this.regularFormat = new SimpleDateFormat("dd/MM/yyyy");
        this.weekDay = new SimpleDateFormat("E", locale);
        this.sqlDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    }

    @PostConstruct
    public void initSettings() throws SQLException {
        //get personal timetable
        strSql = "SELECT * FROM wt_timetable WHERE ownercode = 0 ORDER BY tbid";
        if (ds == null) {
            throw new SQLException("Can't get data source");
        }
        con = ds.getConnection();
        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        ps = con.prepareStatement(strSql);
        result = ps.executeQuery();

        timeTableList = new ArrayList<TimeTableModel>();
// Load user Time Table
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
            timeTableList.add(tm);
        }
        result.close();
        result = null;
        ps.close();
        ps = null;
        //get user settting
        this.holidayOt = false;
        this.weekendOt = false;
        this.showInComplete = false;
        this.period = "1"; // default morning 08:00-16:00
        strSql = "SELECT * FROM usetting WHERE ownercode = " + code;
        ps = con.prepareStatement(strSql);
        result = ps.executeQuery();
// Load user Setting
        while (result.next()) {
            this.period = result.getString("default_tbid");
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
        con.close();
        con = null;
    }
    // Action Listener

    public void setNullGetItemDay(ActionListener action) {
        this.checkInOutListDay = null;
    }

    public void setNullGetItemMonth(ActionListener action) {
        this.checkInOutListMonth = null;
    }
    // Value Change Event

    public void monthChanged(ValueChangeEvent event) {
        this.month = event.getNewValue().toString();
    }

    public void yearChanged(ValueChangeEvent event) {
        this.year = event.getNewValue().toString();
    }

    public void loadTimeTable() throws SQLException {
        if (by_org) {

            
        }
        
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

    public List<CheckInOut> getCheckInOutListDay() throws ParseException, SQLException {
        if (checkInOutListDay == null) {
            if (day != null && month != null && year != null && period != null) {
//                Locale.setDefault(new Locale("th", "TH"));
                checkdate = new Date(regularFormat.parse(day + "/" + month + "/" + ReportCalendar.yearConversion(year)).getTime());
                loadTimeTable();
                // Prepare sql where cause
                String sqlprestart = ReportCalendar.preStart(checkdate, prestart, poststart);
                String sqlpoststart = year + "-" + month + "-" + day + " " + poststart;
                String sqlpreend = year + "-" + month + "-" + day + " " + preend;
                String sqlpostend = ReportCalendar.postEnd(checkdate, preend, postend);
                //gen list
                checkInOutListDay = new ArrayList<CheckInOut>();
                CheckInOut cio = new CheckInOut();
                cio.setCheckdate(checkdate);
                checkInOutListDay.add(cio);
                //get checkin
                if (ds == null) {
                    throw new SQLException("Can't get data source");
                }
                con = ds.getConnection();
                if (con == null) {
                    throw new SQLException("Can't get database connection");
                }
                ps = con.prepareStatement("SELECT checktime FROM wt_checktime "
                        + "WHERE code = " + code + " AND checktime "
                        + "BETWEEN '" + sqlprestart + "' AND '" + sqlpoststart + "' "
                        + "ORDER BY checktime");
                result = ps.executeQuery();
                while (result.next()) {
                    java.sql.Timestamp tmpTime = result.getTimestamp("checktime");

                    checkInOutListDay.get(0).setCheckin(tmpTime);
                }
                result.close();
                result = null;
                ps.close();
                ps = null;
                //get checkout
                ps = con.prepareStatement("SELECT checktime FROM wt_checktime "
                        + "WHERE code = " + code + " AND checktime "
                        + "BETWEEN '" + sqlpreend + "' AND '" + sqlpostend + "' "
                        + "ORDER BY checktime DESC");
                result = ps.executeQuery();
                while (result.next()) {
                    java.sql.Timestamp tmpTime = result.getTimestamp("checktime");

                    checkInOutListDay.get(0).setCheckout(tmpTime);
                }
                result.close();
                result = null;
                ps.close();
                ps = null;
                con.close();
                con = null;

                ///////////////////////calculate 1 row of checkInOutListDay at index 0///////////////////////////////////////


                //check holiday
                checkInOutListDay.get(0).setRemark(ReportCalendar.holiday(holidayList, checkInOutListDay.get(0).getCheckdate()));
                //cal total work hour
                if (checkInOutListDay.get(0).getCheckin() != null && checkInOutListDay.get(0).getCheckout() != null) {
                    checkInOutListDay.get(0).setTotal(ReportCalendar.totalWorkHour(checkInOutListDay.get(0).getCheckout(), checkInOutListDay.get(0).getCheckin()));
                }
                //calculate latetime and earlytime
                if (checkInOutListDay.get(0).getCheckin() != null) {
                    checkInOutListDay.get(0).setLate(ReportCalendar.lateTime(checkInOutListDay.get(0).getCheckdate(), checkInOutListDay.get(0).getCheckin(), start));
                    checkInOutListDay.get(0).setEarlytime(ReportCalendar.earlyTime(checkInOutListDay.get(0).getCheckdate(), checkInOutListDay.get(0).getCheckin(), start, minet));
                }
                //calculate earlyout and overtime
                if (checkInOutListDay.get(0).getCheckout() != null) {
                    checkInOutListDay.get(0).setEarlyout(ReportCalendar.earlyOutTime(checkInOutListDay.get(0).getCheckdate(), checkInOutListDay.get(0).getCheckout(), end));
                    checkInOutListDay.get(0).setOvertime(ReportCalendar.overTime(checkInOutListDay.get(0).getCheckdate(), checkInOutListDay.get(0).getCheckout(), end, minot));
                }
            }
        }
        return checkInOutListDay;
    }

    public List<CheckInOut> getCheckInOutListMonth() throws ParseException, SQLException {
        if (checkInOutListMonth == null) {
            if (by_org || by_org_noid || by_session) {
                if (month != null && year != null && period != null) {

                    longSumWorkHour = 0L;
                    longSumEarlyTime = 0L;
                    longSumOverTime = 0L;
                    this.totalCheckIn = 0;
                    this.totalCheckOut = 0;
                    this.totalLate = 0;
                    this.totalEarlyOut = 0;
//                Locale.setDefault(new Locale("th", "TH"));
                    loadTimeTable();
                    checkdateList = ReportCalendar.getListDateMonth(month, ReportCalendar.yearConversion(year));
                    checkInOutListMonth = new ArrayList<CheckInOut>();
                    // add date range in month
                    for (int i = 0; i < checkdateList.size(); i++) {
                        CheckInOut ciom = new CheckInOut();
                        ciom.setCheckdate(new Date(checkdateList.get(i).getTime()));
                        checkInOutListMonth.add(ciom);
                    }
                    //get checkin
                    if (ds == null) {
                        throw new SQLException("Can't get data source");
                    }
                    con = ds.getConnection();
                    if (con == null) {
                        throw new SQLException("Can't get database connection");
                    }

                    for (int i = 0; i < checkInOutListMonth.size(); i++) {
                        String sqlprestart = ReportCalendar.preStart(checkInOutListMonth.get(i).getCheckdate(), prestart, poststart);
                        String sqlpoststart = sqlDateFormat.format(checkInOutListMonth.get(i).getCheckdate()) + " " + poststart;
                        if (by_session) {
                            strSql = "SELECT checktime FROM wt_checktime "
                                    + "WHERE code = " + code + " AND checktime "
                                    + "BETWEEN '" + sqlprestart + "' AND '" + sqlpoststart + "' "
                                    + "ORDER BY checktime";
                        }
                        if (by_org) {
                            strSql = "SELECT checktime FROM wt_checktime "
                                    + "WHERE code = " + org_code + " AND checktime "
                                    + "BETWEEN '" + sqlprestart + "' AND '" + sqlpoststart + "' "
                                    + "ORDER BY checktime";
                        }
                        if (by_org_noid) {
                            strSql = "SELECT checktime FROM noid_checktime "
                                    + "WHERE badgenumber = " + noid_badgenumber + " AND checktime "
                                    + "BETWEEN '" + sqlprestart + "' AND '" + sqlpoststart + "' "
                                    + "ORDER BY checktime";
                        }
                        ps = con.prepareStatement(strSql);
                        result = ps.executeQuery();
                        while (result.next()) {
                            checkInOutListMonth.get(i).setCheckin(result.getTimestamp("checktime"));
                        }
                        result.close();
                        result = null;
                        ps.close();
                        ps = null;
                    }
                    con.close();
                    con = null;
                    //get checkout
                    if (ds == null) {
                        throw new SQLException("Can't get data source");
                    }
                    con = ds.getConnection();
                    if (con == null) {
                        throw new SQLException("Can't get database connection");
                    }
                    for (int i = 0; i < checkInOutListMonth.size(); i++) {
                        String sqlpreend = sqlDateFormat.format(checkInOutListMonth.get(i).getCheckdate()) + " " + preend;
                        String sqlpostend = ReportCalendar.postEnd(checkInOutListMonth.get(i).getCheckdate(), preend, postend);
                        if (by_session) {
                            strSql = "SELECT checktime FROM wt_checktime "
                                    + "WHERE code = " + code + " AND checktime "
                                    + "BETWEEN '" + sqlpreend + "' AND '" + sqlpostend + "' "
                                    + "ORDER BY checktime DESC";
                        }
                        if (by_org) {
                            strSql = "SELECT checktime FROM wt_checktime "
                                    + "WHERE code = " + org_code + " AND checktime "
                                    + "BETWEEN '" + sqlpreend + "' AND '" + sqlpostend + "' "
                                    + "ORDER BY checktime DESC";
                        }
                        if (by_org_noid) {
                            strSql = "SELECT checktime FROM noid_checktime "
                                    + "WHERE badgenumber = " + noid_badgenumber + " AND checktime "
                                    + "BETWEEN '" + sqlpreend + "' AND '" + sqlpostend + "' "
                                    + "ORDER BY checktime DESC";
                        }
                        ps = con.prepareStatement(strSql);
                        result = ps.executeQuery();
                        while (result.next()) {
                            checkInOutListMonth.get(i).setCheckout(result.getTimestamp("checktime"));
                        }
                        result.close();
                        result = null;
                        ps.close();
                        ps = null;
                    }
                    con.close();
                    con = null;

                    for (int i = 0; i < checkInOutListMonth.size(); i++) {
                        //check holiday
                        checkInOutListMonth.get(i).setRemark(ReportCalendar.holiday(holidayList, checkInOutListMonth.get(i).getCheckdate()));

                        if (!showInComplete) {
                            if (checkInOutListMonth.get(i).getCheckin() != null && checkInOutListMonth.get(i).getCheckout() != null) {
                                totalCheckIn++;
                                totalCheckOut++;

                                checkInOutListMonth.get(i).setTotal(ReportCalendar.totalWorkHour(checkInOutListMonth.get(i).getCheckout(), checkInOutListMonth.get(i).getCheckin()));
                                //sum work hour
//                                if (!"".equals(checkInOutListMonth.get(i).getTotal())) {
                                longSumWorkHour = longSumWorkHour + ReportCalendar.strShortTime2Long(checkInOutListMonth.get(i).getTotal());
//                                }
                                checkInOutListMonth.get(i).setLate(ReportCalendar.lateTime(checkInOutListMonth.get(i).getCheckdate(), checkInOutListMonth.get(i).getCheckin(), start));
                                if (weekendOt || holidayOt) {
                                    checkInOutListMonth.get(i).setEarlytime("");
                                } else {
                                    checkInOutListMonth.get(i).setEarlytime(ReportCalendar.earlyTime(checkInOutListMonth.get(i).getCheckdate(), checkInOutListMonth.get(i).getCheckin(), start, minet));
                                }
                                //sum latetime and early time
                                if (!"".equals(checkInOutListMonth.get(i).getLate())) {
                                    totalLate++;
                                }
                                if (!"".equals(checkInOutListMonth.get(i).getEarlytime())) {
                                    longSumEarlyTime = longSumEarlyTime + ReportCalendar.strShortTime2Long(checkInOutListMonth.get(i).getEarlytime());
                                }
                                checkInOutListMonth.get(i).setEarlyout(ReportCalendar.earlyOutTime(checkInOutListMonth.get(i).getCheckdate(), checkInOutListMonth.get(i).getCheckout(), end));
                                if (weekendOt && ("ส.".equals(weekDay.format(checkInOutListMonth.get(i).getCheckdate())) || "อา.".equals(weekDay.format(checkInOutListMonth.get(i).getCheckdate())))) {
                                    checkInOutListMonth.get(i).setOvertime(checkInOutListMonth.get(i).getTotal());
                                } else if (holidayOt && "H".equals(checkInOutListMonth.get(i).getRemark())) {
                                    checkInOutListMonth.get(i).setOvertime(checkInOutListMonth.get(i).getTotal());
                                } else {
                                    checkInOutListMonth.get(i).setOvertime(ReportCalendar.overTime(checkInOutListMonth.get(i).getCheckdate(), checkInOutListMonth.get(i).getCheckout(), end, minot));
                                }
                                //sum earlyout and overtime                                
                                if (!"".equals(checkInOutListMonth.get(i).getEarlyout())) {
                                    totalEarlyOut++;
                                }
                                if (!"".equals(checkInOutListMonth.get(i).getOvertime())) {
                                    longSumOverTime = longSumOverTime + ReportCalendar.strShortTime2Long(checkInOutListMonth.get(i).getOvertime());
                                }
                            } else {
                                checkInOutListMonth.get(i).setCheckin(null);
                                checkInOutListMonth.get(i).setCheckout(null);
                            }
                            //////////////
                        } else {//Show Incomplete
                            //cal total work hour if checkin and checkout
                            if (checkInOutListMonth.get(i).getCheckin() != null && checkInOutListMonth.get(i).getCheckout() != null) {
                                checkInOutListMonth.get(i).setTotal(ReportCalendar.totalWorkHour(checkInOutListMonth.get(i).getCheckout(), checkInOutListMonth.get(i).getCheckin()));
                                //sum work hour
//                                if (!"".equals(checkInOutListMonth.get(i).getTotal())) {
                                longSumWorkHour = longSumWorkHour + ReportCalendar.strShortTime2Long(checkInOutListMonth.get(i).getTotal());
//                                }
                            }
                            //calculate latetime and earlytime if checkin
                            if (checkInOutListMonth.get(i).getCheckin() != null) {
                                checkInOutListMonth.get(i).setLate(ReportCalendar.lateTime(checkInOutListMonth.get(i).getCheckdate(), checkInOutListMonth.get(i).getCheckin(), start));
                                if (weekendOt || holidayOt) {
                                    checkInOutListMonth.get(i).setEarlytime("");
                                } else {
                                    checkInOutListMonth.get(i).setEarlytime(ReportCalendar.earlyTime(checkInOutListMonth.get(i).getCheckdate(), checkInOutListMonth.get(i).getCheckin(), start, minet));
                                }
                                //sum latetime and early time
                                totalCheckIn++;
                                if (!"".equals(checkInOutListMonth.get(i).getLate())) {
                                    totalLate++;
                                }
                                if (!"".equals(checkInOutListMonth.get(i).getEarlytime())) {
                                    longSumEarlyTime = longSumEarlyTime + ReportCalendar.strShortTime2Long(checkInOutListMonth.get(i).getEarlytime());
                                }
                            }
                            //calculate earlyout and overtime if checkout
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
                                //sum earlyout and overtime
                                totalCheckOut++;
                                if (!"".equals(checkInOutListMonth.get(i).getEarlyout())) {
                                    totalEarlyOut++;
                                }
                                if (!"".equals(checkInOutListMonth.get(i).getOvertime())) {
                                    if (checkInOutListMonth.get(i).getOvertime() != null) {
                                        longSumOverTime = longSumOverTime + ReportCalendar.strShortTime2Long(checkInOutListMonth.get(i).getOvertime());
                                    }
                                }
                            }
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

    public void setCheckInOutListDay(List<CheckInOut> checkInOutListDay) {
        this.checkInOutListDay = checkInOutListDay;
    }

    public void print(ActionListener actionEvent) throws JRException, IOException {
        JRBeanCollectionDataSource checkInOutDS = null;
        String jasperPath = "";
        HashMap<String, Object> params = new HashMap<String, Object>();
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
        params.put("month", month);
        params.put("year", ReportCalendar.yearConversion(year));
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
            jasperPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/report/jasper/PersonalDay.jasper");
        }
        if ("month".equals(reportType)) {
            params.put("totalcheckin", totalCheckIn);
            params.put("totalcheckout", totalCheckOut);
            params.put("totallate", totalLate);
            params.put("totalearlyout", totalEarlyOut);
            params.put("totalearlytime", totalEarlyTime);
            params.put("totalovertime", totalOverTime);
            params.put("totalworkhour", totalWorkHour);
            checkInOutDS = new JRBeanCollectionDataSource(checkInOutListMonth);
            jasperPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/report/jasper/PersonalMonth.jasper");
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
            params.put("no_label", "เลขที่ในระบบ");
            params.put("no", noid_badgenumber);
            params.put("empname", noid_name);
            params.put("position", "-");
            params.put("department", "-");
            params.put("reporterlb", "ผู้พิมพ์");
            params.put("reporter", empname);
            filename = noid_badgenumber;
        }
        params.put("month", month);
        params.put("year", ReportCalendar.yearConversion(year));
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
            jasperPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/report/jasper/PersonalDay.jasper");
        }
        if ("month".equals(reportType)) {
            params.put("totalcheckin", totalCheckIn);
            params.put("totalcheckout", totalCheckOut);
            params.put("totallate", totalLate);
            params.put("totalearlyout", totalEarlyOut);
            params.put("totalearlytime", totalEarlyTime);
            params.put("totalovertime", totalOverTime);
            params.put("totalworkhour", totalWorkHour);
            checkInOutDS = new JRBeanCollectionDataSource(checkInOutListMonth);
            jasperPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/report/jasper/PersonalMonth.jasper");
        }
        jasperPrint = JasperFillManager.fillReport(jasperPath, params, checkInOutDS);
        jasperPrint.setLocaleCode("th_TH");
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        HttpServletResponse httpServletResponse = (HttpServletResponse) externalContext.getResponse();
        httpServletResponse.reset();
        httpServletResponse.addHeader("Content-disposition", "attachment; filename=" + "Personal-" + reportType
                + ReportCalendar.yearConversion(year) + "-" + month + (day == null ? "" : "-" + day) + "_" + java.net.URLEncoder.encode(filename, "UTF-8") + ".pdf");
        ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
        ThaiExporterManager.exportReportToPdfStream(jasperPrint, servletOutputStream);
        facesContext.responseComplete();
        logger.info("User Download Report PDF");
    }

    // Only Getter
    public int getTotalCheckIn() {
        return totalCheckIn;
    }

    public int getTotalCheckOut() {
        return totalCheckOut;
    }

    public int getTotalEarlyOut() {
        return totalEarlyOut;
    }

    public int getTotalLate() {
        return totalLate;
    }

    public String getTotalWorkHour() {
        this.totalWorkHour = ReportCalendar.long2Hour(longSumWorkHour);
        return totalWorkHour;
    }

    public String getTotalEarlyTime() {
        this.totalEarlyTime = ReportCalendar.long2Hour(longSumEarlyTime);
        return totalEarlyTime;
    }

    public String getTotalOverTime() {
        this.totalOverTime = ReportCalendar.long2Hour(longSumOverTime);
        return totalOverTime;
    }

    // Getter And Setter
    public String getCode() {
        return code;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
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

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
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

    public boolean isShowInComplete() {
        return showInComplete;
    }
}
