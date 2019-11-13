/**
 * Project work-time
 * TimeParser.java
 *
 * Created on May 20, 2012, 8:59:32 AM
 *
 * Copyright(c) 2012 Khon Kaen Hospital, Information Technology.  All Rights Reserved.
 * This software is the proprietary information of Khon Kaen Hospital, Information Technology.
 *
 */
package org.worktime.department.setup;

import java.net.URL;
import java.util.List;
import javax.faces.FacesException;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.worktime.department.setup.model.Time;

/**
 *
 * @author Skulchai Chimrakkaeo
 * @contact
 *  email skulchai.ch@gmail.com
 *  phone 085-008-2248
 */
@ManagedBean 
@ApplicationScoped
public class TimeParser {
    private List<Time> timesList;
    
    @XmlRootElement(name = "times")
    private static final class TimesHolder {
        
        private List<Time> times;
        
        @XmlElement(name = "time")
        public List<Time> getTimes() {
            return times;
        }
        
        @SuppressWarnings("unused")
        public void setTimes(List<Time> times) {
            this.times = times;
        }
    }
    
    public synchronized List<Time> getTimesList() {
        if (timesList == null) {
            ClassLoader ccl = Thread.currentThread().getContextClassLoader();
            URL resource = ccl.getResource("org/worktime/department/setup/model/onedayhalfhourtimetick.xml");
            JAXBContext context;
            try {
                context = JAXBContext.newInstance(TimesHolder.class);
                TimesHolder capitalsHolder = (TimesHolder) context.createUnmarshaller().unmarshal(resource);
                timesList = capitalsHolder.getTimes();
            } catch (JAXBException e) {
                throw new FacesException(e.getMessage(), e);
            }
        }
        
        return timesList;
    }
}
