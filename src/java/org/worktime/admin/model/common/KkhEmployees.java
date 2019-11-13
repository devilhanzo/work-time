/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.worktime.admin.model.common;

/**
 *
 * @author PucK
 */
public class KkhEmployees {
    private int code;
    private String no;
    private String empname;
    private String position;
    private String department;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
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
}
