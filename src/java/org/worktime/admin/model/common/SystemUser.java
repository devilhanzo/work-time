/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.worktime.admin.model.common;

/**
 *
 * @author PucK
 */
public class SystemUser {
    private int code;
    private String no;
    private String empname;
    private String department;
    private String adm_priv;
    private String dept_priv;
    private String org_priv;
    private String suspended_priv;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
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

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getAdm_priv() {
        return adm_priv;
    }

    public void setAdm_priv(String adm_priv) {
        this.adm_priv = adm_priv;
    }

    public String getDept_priv() {
        return dept_priv;
    }

    public void setDept_priv(String dept_priv) {
        this.dept_priv = dept_priv;
    }

    public String getOrg_priv() {
        return org_priv;
    }

    public void setOrg_priv(String org_priv) {
        this.org_priv = org_priv;
    }

    public String getSuspended_priv() {
        return suspended_priv;
    }

    public void setSuspended_priv(String suspended_priv) {
        this.suspended_priv = suspended_priv;
    }
    
}
