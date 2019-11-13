/**
 * Project work-time
 * Time.java
 *
 * Created on May 20, 2012, 9:08:22 AM
 *
 * Copyright(c) 2012 Khon Kaen Hospital, Information Technology.  All Rights Reserved.
 * This software is the proprietary information of Khon Kaen Hospital, Information Technology.
 *
 */
package org.worktime.department.setup.model;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author Skulchai Chimrakkaeo
 * @contact
 *  email skulchai.ch@gmail.com
 *  phone 085-008-2248
 */
public class Time implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private String tick;
    private String value;

    public Time() {
    }

    @Override
    public String toString() {
        return tick;
    }
    @XmlElement
    public String getTick() {
        return tick;
    }

    public void setTick(String tick) {
        this.tick = tick;
    }
    @XmlElement
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
    
}
