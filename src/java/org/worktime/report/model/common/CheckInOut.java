/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.worktime.report.model.common;

import java.sql.Timestamp;
import java.util.Date;

/**
 *
 * @author PucK
 */
public class CheckInOut {
    public String code;
    public String no;
    public String name;
    public String position;
    public java.sql.Timestamp checkin;
    public java.sql.Timestamp checkout;
    public String late;
    public String earlyout;
    public String earlytime;
    public String overtime;
    public String total;
    public Date checkdate;    
    public String remark;

    public Date getCheckdate() {
        return checkdate;
    }

    public void setCheckdate(Date checkdate) {
        this.checkdate = checkdate;
    }

    public Timestamp getCheckin() {
        return checkin;
    }

    public void setCheckin(Timestamp checkin) {
        this.checkin = checkin;
    }

    public Timestamp getCheckout() {
        return checkout;
    }

    public void setCheckout(Timestamp checkout) {
        this.checkout = checkout;
    }
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getEarlyout() {
        return earlyout;
    }

    public void setEarlyout(String earlyout) {
        this.earlyout = earlyout;
    }

    public String getEarlytime() {
        return earlytime;
    }

    public void setEarlytime(String earlytime) {
        this.earlytime = earlytime;
    }

    public String getLate() {
        return late;
    }

    public void setLate(String late) {
        this.late = late;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOvertime() {
        return overtime;
    }

    public void setOvertime(String overtime) {
        this.overtime = overtime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
    
}
