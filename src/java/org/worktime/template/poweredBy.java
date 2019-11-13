/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.worktime.template;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author PucK
 */
@ManagedBean
@SessionScoped
public class poweredBy implements Serializable {
    final String year = "2012";
    final String department = "ศูนย์เทคโนโลยีสารสนเทศและการสื่อสาร";
    final String company = "รพ.ขอนแก่น";
    final String phone = "1178,2000,2013";
    private String registeredTo;

    public String getRegisteredTo() {
        registeredTo = "Copyright © " + year + " Powered By " + department +
                " " + company + " โทร." + phone;
        return registeredTo;
    }
}
