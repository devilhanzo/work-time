/**
 * Project work-time ListKkhDeptBean.java
 *
 * Created on May 1, 2012, 0:00:00 AM
 *
 * Copyright(c) 2012 Khon Kaen Hospital, Information Technology. All Rights
 * Reserved. This software is the proprietary information of , Information
 * Technology.
 *
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
import javax.sql.DataSource;
import org.worktime.admin.model.common.KkhDepts;

/**
 *
 * @author Skulchai Chimrakkaeo @contact email skulchai.ch@gmail.com phone
 * 085-008-2248
 */
@ManagedBean(name = "listKkhDeptBean")
@SessionScoped
public class ListKkhDeptBean implements Serializable {

    private static final long serialVersionUID = 1L;
    @Resource(name = "jdbc/worktime")
    private DataSource ds;
    private Connection con;
    private PreparedStatement ps;
    private ResultSet result;
    private List<KkhDepts> listKkhDept;

    public List<KkhDepts> getListKkhDept() throws SQLException {
        if (listKkhDept == null) {
            if (ds == null) {
                throw new SQLException("Can't get data source");
            }
            con = ds.getConnection();
            if (con == null) {
                throw new SQLException("Can't get database connection");
            }
            ps = con.prepareStatement(
                    "SELECT ref,name FROM intranet.lib_office WHERE LENGTH(ref) <= 6 ORDER BY ref");
            result = ps.executeQuery();
            listKkhDept = new ArrayList<KkhDepts>();
            while (result.next()) {
                KkhDepts kkhdept = new KkhDepts();
                kkhdept.setKkhdeptid(result.getString("ref"));
                String tmpName = result.getString("name");
                if (tmpName.length() > 50) {
                    kkhdept.setKkhdeptname(tmpName.substring(0, 50) + "..");
                } else {
                    kkhdept.setKkhdeptname(tmpName);
                }
                listKkhDept.add(kkhdept);
            }
            result.close();
            result = null;
            ps.close();
            ps = null;
            con.close();
            con = null;
        }
        return listKkhDept;
    }
}
