/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.worktime.report.model.common;

/**
 *
 * @author PucK
 */
public class TimeTableModel {
    private String tbid;
    private String owncode;
    private String tablename;
    private String start;
    private String end;
    private String prestart;
    private String poststart;
    private String preend;
    private String postend;
    private String startot;
    private String endot;

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getEndot() {
        return endot;
    }

    public void setEndot(String endot) {
        this.endot = endot;
    }

    public String getOwncode() {
        return owncode;
    }

    public void setOwncode(String owncode) {
        this.owncode = owncode;
    }

    public String getPostend() {
        return postend;
    }

    public void setPostend(String postend) {
        this.postend = postend;
    }

    public String getPoststart() {
        return poststart;
    }

    public void setPoststart(String poststart) {
        this.poststart = poststart;
    }

    public String getPreend() {
        return preend;
    }

    public void setPreend(String preend) {
        this.preend = preend;
    }

    public String getPrestart() {
        return prestart;
    }

    public void setPrestart(String prestart) {
        this.prestart = prestart;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getStartot() {
        return startot;
    }

    public void setStartot(String startot) {
        this.startot = startot;
    }

    public String getTablename() {
        return tablename;
    }

    public void setTablename(String tablename) {
        this.tablename = tablename;
    }

    public String getTbid() {
        return tbid;
    }

    public void setTbid(String tbid) {
        this.tbid = tbid;
    }
    
}
