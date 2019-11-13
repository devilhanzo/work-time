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
import org.worktime.admin.model.common.XEmployees;

/**
 *
 * @author PucK
 */
@ManagedBean(name = "listXEmpBean")
@SessionScoped
public class ListXEmpBean implements Serializable {

    private static final long serialVersionUID = 1L;
    @Resource(name = "jdbc/worktime")
    private DataSource ds;
    private Connection con;
    private PreparedStatement ps;
    private ResultSet result;
    private List<XEmployees> xEmployeeList;
    private String strSql;
    private String keyWord;
    
    public void preloadEmployeeList(ActionListener action){
        this.xEmployeeList = null;
    }

    public List<XEmployees> getXEmployeeList() throws SQLException {
        if(xEmployeeList == null){
            if (ds == null) {
                throw new SQLException("Can't get data source");
            }
            con = ds.getConnection();
            if (con == null) {
                throw new SQLException("Can't get database connection");
            }

            strSql = "SELECT Badgenumber,Name FROM USERINFO WHERE Badgenumber = ? AND (SSN IS NULL OR SSN LIKE 'st%' OR SSN = '00000' OR SSN = '')";
            ps = con.prepareStatement(strSql);
            ps.setString(1, keyWord);
            result = ps.executeQuery();
            xEmployeeList = new ArrayList<XEmployees>();
            while (result.next()) {
                XEmployees xemp = new XEmployees();
                xemp.setBadgenumber(result.getString("Badgenumber"));
                xemp.setName(result.getString("Name"));
                xEmployeeList.add(xemp);
            }
            result.close();
            result = null;
            ps.close();
            ps = null;
            con.close();
            con = null;
        }
        return xEmployeeList;
    }

    public void setXEmployeeList(List<XEmployees> xEmployeeList) {
        this.xEmployeeList = xEmployeeList;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }
    
}
