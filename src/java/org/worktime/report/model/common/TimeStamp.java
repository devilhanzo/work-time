 /**
 * Project work-time
 * TimeStampBean.java
 *
 * Created on May 1, 2012, 0:00:00 AM
 *
 * Copyright(c) 2012 Khon Kaen Hospital, Information Technology.  All Rights Reserved.
 * This software is the proprietary information of , Information Technology.
 *
 */
package org.worktime.report.model.common;

import java.util.Date;

/**
 *
 * @author Skulchai Chimrakkaeo
 * @contact
 *  email skulchai.ch@gmail.com
 *  phone 085-008-2248
 */
public class TimeStamp {
    public String strtime;
    public Date checkdate;
    public String scanner;
    public String verifycode;
   
    public String getScanner() {
        return scanner;
    }

    public void setScanner(String scanner) {
        this.scanner = scanner;
    }

    public String getStrtime() {
        return strtime;
    }

    public void setStrtime(String strtime) {
        this.strtime = strtime;
    }

    public Date getCheckdate() {
        return checkdate;
    }

    public void setCheckdate(Date checkdate) {
        this.checkdate = checkdate;
    }
    
    public String getVerifycode() {
        return verifycode;
    }

    public void setVerifycode(String verifycode) {
        this.verifycode = verifycode;
    }
    
}
