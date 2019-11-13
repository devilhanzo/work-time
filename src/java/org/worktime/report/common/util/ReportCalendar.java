/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.worktime.report.common.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import org.worktime.calendar.model.Days;
import org.worktime.calendar.model.Months;
import org.worktime.calendar.model.Years;
import org.worktime.holiday.model.Holiday;

/**
 *
 * @author PucK
 */
public class ReportCalendar {

    public static SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    public static SimpleDateFormat regularFormat = new SimpleDateFormat("dd/MM/yyyy E",new Locale("th","TH"));
    public static SimpleDateFormat timeIct = new SimpleDateFormat("HH:mm:ss");
    public static SimpleDateFormat shortTimeIct = new SimpleDateFormat("HH:mm");
    public static SimpleDateFormat dayOfweekLong = new SimpleDateFormat("EEEE",new Locale("th","TH"));
    
    public static String holiday(List<Holiday> holidayList,Date checkdate) throws ParseException{
        Locale.setDefault(new Locale("th","TH"));
        String detail = "";        
        String strcheckdate = regularFormat.format(checkdate);
        for(int i = 0;i<holidayList.size();i++){
            if(strcheckdate.equals(holidayList.get(i).getHolidayDate())){
                if("วันหยุดราชการ".equals(holidayList.get(i).getHolidayType()) || 
                        "วันหยุดราชการชดเชย".equals(holidayList.get(i).getHolidayType())){
                    detail = "H";
                    break;
                }                
            }
        }
        return detail;
    }
    public static String long2Hour(long time){
        long hours = TimeUnit.MILLISECONDS.toHours(time);
        time -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(time);
        StringBuilder sb = new StringBuilder(64);
        sb.append(hours);
        sb.append(":");
        sb.append(minutes);
        return(sb.toString());
    }
    
    public static String dayOfWeekLong(Date checkdate) throws ParseException{ 
        Locale.setDefault(new Locale("th","TH"));
        return dayOfweekLong.format(checkdate);
    }
    
    public static long strShortTime2Long(String strShortTime) throws ParseException{
        Locale.setDefault(new Locale("th","TH"));
        shortTimeIct.setTimeZone(TimeZone.getTimeZone("ICT"));
        Date dateShortTime = shortTimeIct.parse(strShortTime);
        return dateShortTime.getTime();
    }

    public static String earlyTime(Date checkdate,Timestamp checkin,String start, String minet) throws ParseException {
        Locale.setDefault(new Locale("th","TH"));
        timeIct.setTimeZone(TimeZone.getTimeZone("ICT"));
        shortTimeIct.setTimeZone(TimeZone.getTimeZone("ICT"));
        Date datestart = timeIct.parse(start);
        Date dateminet = timeIct.parse(minet);
        long longstart = checkdate.getTime() + datestart.getTime();
        long longminet = dateminet.getTime();
        long longcheckin = checkin.getTime();
        if ((longstart - longminet) >= longcheckin) {
            return shortTimeIct.format(new Date((longstart) - longcheckin));
        } else {
            return "";
        }
    }

    public static String overTime(Date checkdate, Timestamp checkout, String end, String minot) throws ParseException {
        Locale.setDefault(new Locale("th","TH"));
        timeIct.setTimeZone(TimeZone.getTimeZone("ICT"));
        shortTimeIct.setTimeZone(TimeZone.getTimeZone("ICT"));        
        Date dateend = timeIct.parse(end);
        Date dateminot = timeIct.parse(minot);
        long longend = checkdate.getTime() + dateend.getTime();
        long longminot = dateminot.getTime();
        long longcheckout = checkout.getTime();
        if(longcheckout >= (longend + longminot)){
            return shortTimeIct.format(new Date(longcheckout - (longend)));
        } else {
            return "";
        }
    }

    public static String lateTime(Date checkdate ,Timestamp checkin, String start ) throws ParseException {
        Locale.setDefault(new Locale("th","TH"));
        timeIct.setTimeZone(TimeZone.getTimeZone("ICT"));
        shortTimeIct.setTimeZone(TimeZone.getTimeZone("ICT"));        
        Date datestart = timeIct.parse(start);
        long longstart = checkdate.getTime() + datestart.getTime();
        long longcheckin = checkin.getTime();
        if (longcheckin > longstart) {
            return shortTimeIct.format(longcheckin - longstart);
        } else {
            return "";
        }
    }

    public static String earlyOutTime(Date checkdate,Timestamp checkout, String end) throws ParseException {
        Locale.setDefault(new Locale("th","TH"));
        timeIct.setTimeZone(TimeZone.getTimeZone("ICT"));
        shortTimeIct.setTimeZone(TimeZone.getTimeZone("ICT")); 
        Date dateend = timeIct.parse(end);
        long longend = checkdate.getTime() + dateend.getTime();
        long longcheckout = checkout.getTime();
        if (longend > longcheckout) {
            return shortTimeIct.format(longend - longcheckout);
        } else {
            return "";
        }
    }

    public static String totalWorkHour(Timestamp more, Timestamp less) throws ParseException {
        Locale.setDefault(new Locale("th","TH"));
        shortTimeIct.setTimeZone(TimeZone.getTimeZone("ICT"));
        long longmore = more.getTime();
        long longless = less.getTime();
        long longdiff = longmore - longless;
        Date timediff = new Date(longdiff);
        return shortTimeIct.format(timediff);
    }

    public static String preStart(Date checkdate, String prestart, String poststart) throws ParseException {
        Locale.setDefault(new Locale("th","TH"));
        timeIct.setTimeZone(TimeZone.getTimeZone("ICT"));
        Date dateprestart = (Date) timeIct.parse(prestart);
        Date datepoststart = (Date) timeIct.parse(poststart);
        long longprestart = checkdate.getTime() + dateprestart.getTime();
        long longpoststart = checkdate.getTime() + datepoststart.getTime();
        long longoneday = 24 * 1000 * 60 * 60; // 1 day in millis
        if (longprestart > longpoststart) {
            //-1day to prestart
            return timeStampFormat.format(new Date(longprestart - longoneday));
        } else {
            return timeStampFormat.format(longprestart);
        }

    }

    public static String postEnd(Date checkdate, String preend, String postend) throws ParseException {
        Locale.setDefault(new Locale("th","TH"));
        timeIct.setTimeZone(TimeZone.getTimeZone("ICT"));
        
        Date datepreend = (Date) timeIct.parse(preend);
        Date datepostend = (Date) timeIct.parse(postend);
        long longpreend = checkdate.getTime() + datepreend.getTime();
        long longpostend = checkdate.getTime() + datepostend.getTime();
        long longoneday = 24 * 1000 * 60 * 60; // 1 day in millis
        if (longpreend > longpostend) {
            //+1day to postend
            return timeStampFormat.format(new Date(longpostend + longoneday));
        } else {
            return timeStampFormat.format(longpostend);
        }
    }

    public static List<Days> getListDays(String month, String year) {
        List<Days> daylist = new ArrayList<Days>();
        String format = String.format("%%0%dd", 2);
        
        //return String.format(format, usercode);
        for (int i = 1; i <= getDaysOfMonth(month, year); i++) {
            Days ds = new Days();
            ds.setDay(String.valueOf(i));
            ds.setValue(String.format(format, i));
            daylist.add(ds);
        }
        return daylist;
    }

    public static List<Date> getListDateMonth(String month, String year) throws java.text.ParseException {
        List<Date> dates = new ArrayList<Date>();

        String str_date = "01/" + month + "/" + year;
        String end_date = String.valueOf(getDaysOfMonth(month, year)) + "/" + month + "/" + year;

        DateFormat formatter;
        formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date startDate = (Date) formatter.parse(str_date);
        Date endDate = (Date) formatter.parse(end_date);
        long interval = 24 * 1000 * 60 * 60; // 1 hour in millis
        long endTime = endDate.getTime(); // create your endtime here, possibly using Calendar or Date
        long curTime = startDate.getTime();
        while (curTime <= endTime) {
            dates.add(new Date(curTime));
            curTime += interval;
        }
        return dates;
    }

    public static int getDaysOfMonth(String month, String year) {
        Calendar calendar = Calendar.getInstance();
        int yyyy = Integer.parseInt(year);
        int mm = Integer.parseInt(month);
        int date = 1;
        int mmmm = 0;
        switch (mm) {
            case 1:
                mmmm = Calendar.JANUARY;
                break;
            case 2:
                mmmm = Calendar.FEBRUARY;
                break;
            case 3:
                mmmm = Calendar.MARCH;
                break;
            case 4:
                mmmm = Calendar.APRIL;
                break;
            case 5:
                mmmm = Calendar.MAY;
                break;
            case 6:
                mmmm = Calendar.JUNE;
                break;
            case 7:
                mmmm = Calendar.JULY;
                break;
            case 8:
                mmmm = Calendar.AUGUST;
                break;
            case 9:
                mmmm = Calendar.SEPTEMBER;
                break;
            case 10:
                mmmm = Calendar.OCTOBER;
                break;
            case 11:
                mmmm = Calendar.NOVEMBER;
                break;
            case 12:
                mmmm = Calendar.DECEMBER;
                break;
        }

        calendar.set(yyyy, mmmm, date);
        int days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        return days;
    }

    public static List<Years> getYearList() {
        int systemBeginYear = 2554;
        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        List<Years> years = new ArrayList<Years>();
        for (int i = currentYear; i >= systemBeginYear; i--) {
            Years ys = new Years();
            ys.setChristianyear(String.valueOf(i - 543));
            ys.setBuddhismyear(String.valueOf(i));
            years.add(ys);
        }
        return years;
    }

    public static List<Months> getThaiMonthList() {
        List<Months> monthList = new ArrayList<Months>();
        Months m1 = new Months();
        m1.setName("มกราคม");
        m1.setNo("01");
        monthList.add(m1);
        Months m2 = new Months();
        m2.setName("กุมภาพันธ์");
        m2.setNo("02");
        monthList.add(m2);
        Months m3 = new Months();
        m3.setName("มีนาคม");
        m3.setNo("03");
        monthList.add(m3);
        Months m4 = new Months();
        m4.setName("เมษายน");
        m4.setNo("04");
        monthList.add(m4);
        Months m5 = new Months();
        m5.setName("พฤษภาคม");
        m5.setNo("05");
        monthList.add(m5);
        Months m6 = new Months();
        m6.setName("มิถุนายน");
        m6.setNo("06");
        monthList.add(m6);
        Months m7 = new Months();
        m7.setName("กรกฎาคม");
        m7.setNo("07");
        monthList.add(m7);
        Months m8 = new Months();
        m8.setName("สิงหาคม");
        m8.setNo("08");
        monthList.add(m8);
        Months m9 = new Months();
        m9.setName("กันยายน");
        m9.setNo("09");
        monthList.add(m9);
        Months m10 = new Months();
        m10.setName("ตุลาคม");
        m10.setNo("10");
        monthList.add(m10);
        Months m11 = new Months();
        m11.setName("พฤศจิกายน");
        m11.setNo("11");
        monthList.add(m11);
        Months m12 = new Months();
        m12.setName("ธันวาคม");
        m12.setNo("12");
        monthList.add(m12);
        return monthList;
    }

    public static String yearConversion(String year) {
        String strYear;
        int intYear;
        if ("".equals(year) || year == null) {
            strYear = year;
        } else {
            intYear = Integer.parseInt(year) + 543;
            strYear = String.valueOf(intYear);
        }
        return strYear;
    }

    public static String getCurrentDay() {
        Date date = new Date();
        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat("dd");
        return sdf.format(date);
    }

    public static String getCurrentMonthNo() {
        Date date = new Date();
        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat("MM");
        return sdf.format(date);
    }

    public static String getCurrentTHYear() {
        String currentYear;
        Calendar cal = Calendar.getInstance();
        int curYear = cal.get(Calendar.YEAR);
        currentYear = String.valueOf(curYear);
        return currentYear;
    }

    public static String getCurrentXYear() {
        String currentYear;
        Calendar cal = Calendar.getInstance();
        int curYear = cal.get(Calendar.YEAR);
        currentYear = String.valueOf(curYear - 543);
        return currentYear;
    }

    public static String getCurrentDate() {
        String currentDate;
        DateFormat dateFormat = new SimpleDateFormat("dd/MMMM/yyyy E",new Locale("th","TH"));
        Calendar cal = Calendar.getInstance();
        currentDate = dateFormat.format(cal.getTime());
        return currentDate;
    }
}
